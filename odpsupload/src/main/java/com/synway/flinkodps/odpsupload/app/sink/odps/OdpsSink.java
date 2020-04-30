package com.synway.flinkodps.odpsupload.app.sink.odps;

import com.aliyun.odps.*;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.data.RecordWriter;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TunnelException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synway.flinkodps.common.utils.ParseUtil;
import com.synway.flinkodps.odpsupload.app.model.OdpsInfo;
import com.synway.flinkodps.odpsupload.app.model.SessionInfo;
import com.synway.flinkodps.odpsupload.dal.odps.OdpsDal;
import com.synway.flinkodps.odpsupload.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.sink
 * @date:2020/4/26
 */
@Slf4j
public class OdpsSink extends RichSinkFunction<OdpsInfo> implements CheckpointedFunction {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    //线程池
    private static volatile ExecutorService executorService;
    //线程池锁
    private static final String threadPoolLock = "ODPS:THREAD:LOCK";
    //数据库操作
    private OdpsDal dbBase;
    //配置属性
    private Properties prop;
    //数据状态
    private transient ListState<RecordAccumulator> recordAccumulatorState;
    private ListStateDescriptor<RecordAccumulator> recordAccumulatorListStateDescriptor;
    //保存所有的数据累加器
    private static Map<String, RecordAccumulator> recordAccumulators = Maps.newConcurrentMap();
    //session状态
    private transient ListState<SessionInfo> sessionState;
    private ListStateDescriptor<SessionInfo> sessionStateDiscriptor;
    //保存所有的uploadSession
    private static Map<String, SessionInfo> uploadSessions = Maps.newConcurrentMap();


    public OdpsSink(Properties prop) {
        this.prop = prop;
        dbBase = OdpsDal.build();

        recordAccumulatorListStateDescriptor = new ListStateDescriptor("accumulator-state", TypeInformation.of(new TypeHint<RecordAccumulator>() {
        }));

        sessionStateDiscriptor = new ListStateDescriptor("session-state", TypeInformation.of(new TypeHint<SessionInfo>() {
        }));

        //线程池初始化
        int poolSize = ParseUtil.parseInt(prop.get("pool.size"), 5);
        if (Objects.isNull(executorService)) {
            synchronized (threadPoolLock) {
                if (Objects.isNull(executorService)) {
                    executorService = new ThreadPoolExecutor(2, poolSize, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
                }
            }
        }
    }

    @Override
    public void invoke(OdpsInfo odpsInfo, Context context) throws Exception {

        RecordAccumulator accumulator = getAccumulator(odpsInfo);
        appendData(accumulator, odpsInfo);
        if (accumulator.isFull()) {
            send(accumulator);
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {
        log.info("start snapshot {}...", functionSnapshotContext.getCheckpointId());
        recordAccumulatorState.clear();
        long expireTime = ParseUtil.parseLong(prop.getProperty("batch.timeout"), 9600000);
        for (RecordAccumulator accumulator : recordAccumulators.values()) {
            //过期的发送
            if (accumulator.isExpire(expireTime)) {
                send(accumulator);
            } else {
                //没过期放入状态中
                recordAccumulatorState.add(accumulator);
            }
        }

        sessionState.clear();


        for (SessionInfo session : uploadSessions.values()) {
            sessionState.add(session);
        }

        log.info("snapshot end,accumulator:{},session:{}", recordAccumulators.size(), uploadSessions.size());
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        log.info("start initialize...");
        recordAccumulatorState = context.getOperatorStateStore().getListState(recordAccumulatorListStateDescriptor);
        sessionState = context.getOperatorStateStore().getListState(sessionStateDiscriptor);
        int accumulatorCounter = 0;
        int sessionCounter = 0;
        if (context.isRestored()) {
            //恢复数据累加器
            Iterator<RecordAccumulator> accumulatorIterator = recordAccumulatorState.get().iterator();
            while (accumulatorIterator.hasNext()) {
                accumulatorCounter++;
                RecordAccumulator recordAccumulator = accumulatorIterator.next();
                String sessionFlag = getSessionFlag(recordAccumulator.getTableName(), recordAccumulator.getProject());
                recordAccumulators.put(sessionFlag, recordAccumulator);
            }

            //恢复sessin
            Iterator<SessionInfo> sessionInfoIterator = sessionState.get().iterator();
            while (sessionInfoIterator.hasNext()) {
                sessionCounter++;
                SessionInfo sessionInfo = sessionInfoIterator.next();
                String sessionFlag = getSessionFlag(sessionInfo.getTableName(), sessionInfo.getProject());
                uploadSessions.put(sessionFlag, sessionInfo);
            }

        }
        log.info("initialize end.accumulator:{},session:{}.", accumulatorCounter, sessionCounter);
    }

    private boolean send(RecordAccumulator accumulator) {
        boolean ret = true;
        SessionInfo session = null;

        try {
            session = getSession(accumulator.getTableName(), accumulator.getProject());
            if (Objects.isNull(session)) {
                throw new TunnelException("session获取失败");
            }

            //发送数据
            session.getNewBlockId();
            //将数据拆分成多份发送
            List<RecordAccumulator> recordAccumulators = splitAccumulator(accumulator);

            List<Callable<Boolean>> threadList = Lists.newArrayList();

            for (RecordAccumulator recordAccumulator : recordAccumulators) {
                threadList.add(new UploadThread(session, recordAccumulator, prop.getProperty("split.str")));
            }

            List<Future<Boolean>> results = Lists.newArrayList();

            for (Callable<Boolean> thread : threadList) {
                results.add(executorService.submit(thread));
            }

            for (Future<Boolean> result : results) {
                result.get();
            }

            session.getUploadSession().commit();

        } catch (InterruptedException | ExecutionException | TunnelException e) {
            log.error("send data error:{}",e.getMessage());
            ret = false;
        } catch (IOException e) {
            log.error("send data io error:{}",e.getMessage());
            //io错误上传的session不再使用
            session.abandon();
            ret = false;
        }finally {
            //发送完成移除累加器数据
            recordAccumulators.remove(getSessionFlag(accumulator.getTableName(), accumulator.getProject()));
            return ret;
        }
    }

    //统计方法


    /**
     * 重试
     */
    private void redo(RecordAccumulator accumulator) {
        String brokerList = prop.getProperty("redo.broker.list");
        String redoTopic = prop.getProperty("redo.topic");
        for (AccumulatorData data : accumulator.getData()) {
            OdpsInfo odpsInfo = new OdpsInfo();
            odpsInfo.setProject(accumulator.getProject());
            odpsInfo.setTableId(accumulator.getTableId());
            odpsInfo.setTableName(accumulator.getTableName());
            odpsInfo.setTableComment(accumulator.getTableComment());
            odpsInfo.setColCount(accumulator.getColCount());
            odpsInfo.setEtlRule(accumulator.getEtlRule());
            odpsInfo.setFields(accumulator.getFields());
            odpsInfo.setTransState(accumulator.getTransState());
            odpsInfo.setSys(accumulator.getSys());
            odpsInfo.setCount(data.getData().size());
            odpsInfo.setCreateDate(System.currentTimeMillis());
            odpsInfo.setDataDate(System.currentTimeMillis());
            odpsInfo.setData(data.getData());
            odpsInfo.setRedoTime(data.getRedoTime() + 1);
            //发送到kafka
            KafkaUtils.sendData(brokerList, accumulator.getTableName(), redoTopic, odpsInfo);
        }
    }

    /**
     * 拆分聚集的数据
     */
    private List<RecordAccumulator> splitAccumulator(RecordAccumulator accumulator) {
        List<RecordAccumulator> ret = Lists.newArrayList();

        for (AccumulatorData accumulatorData : accumulator.getData()) {
            RecordAccumulator recordAccumulator = new RecordAccumulator();
            recordAccumulator.setProject(accumulator.getProject());
            recordAccumulator.setTableId(accumulator.getTableId());
            recordAccumulator.setTableName(accumulator.getTableName());
            recordAccumulator.setTableComment(accumulator.getTableComment());
            recordAccumulator.setColCount(accumulator.getColCount());
            recordAccumulator.setEtlRule(accumulator.getEtlRule());
            recordAccumulator.setFields(accumulator.getFields());
            recordAccumulator.setTransState(accumulator.getTransState());
            recordAccumulator.setSys(accumulator.getSys());
            recordAccumulator.setUpdateTime(System.currentTimeMillis());
            recordAccumulator.setBatchSize(accumulator.getBatchSize());
            recordAccumulator.append(accumulatorData);

            ret.add(recordAccumulator);
        }

        return ret;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    /**
     * session标识
     */
    private String getSessionFlag(String tableName, String project) {
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(project)) {
            return null;
        }
        return String.format("%s_%s", tableName, project);
    }

    /**
     * 获取数据累加器
     */
    private RecordAccumulator getAccumulator(OdpsInfo odpsInfo) {
        String sessionFlag = getSessionFlag(odpsInfo.getTableName(), odpsInfo.getProject());
        RecordAccumulator accumulator;
        if (recordAccumulators.containsKey(sessionFlag)) {
            accumulator = recordAccumulators.get(sessionFlag);
        } else {
            accumulator = new RecordAccumulator();

            accumulator.setProject(odpsInfo.getProject());
            accumulator.setTableName(odpsInfo.getTableName());
            accumulator.setTableId(odpsInfo.getTableId());
            accumulator.setTableComment(odpsInfo.getTableComment());
            accumulator.setColCount(odpsInfo.getColCount());
            accumulator.setEtlRule(odpsInfo.getEtlRule());
            accumulator.setFields(odpsInfo.getFields());
            accumulator.setTransState(odpsInfo.getTransState());
            accumulator.setSys(odpsInfo.getSys());
            accumulator.setUpdateTime(System.currentTimeMillis());
            accumulator.setBatchSize(ParseUtil.parseInt(prop.getProperty("batch.size"), 100000));

            recordAccumulators.put(sessionFlag, accumulator);
        }

        return accumulator;
    }

    /**
     * 累加数据
     */
    private void appendData(RecordAccumulator accumulator, OdpsInfo odpsInfo) {
        AccumulatorData accumulatorData = new AccumulatorData();
        accumulatorData.setPartition(odpsInfo.getPartition());
        accumulatorData.setOffset(odpsInfo.getOffset());
        accumulatorData.setData(odpsInfo.getData());
        accumulatorData.setRedoTime(odpsInfo.getRedoTime());
        accumulator.append(accumulatorData);
    }

    /**
     * 创建session
     */
    private SessionInfo getSession(String tableName, String project) {
        String sessionFlag = getSessionFlag(tableName, project);
        if (uploadSessions.containsKey(sessionFlag)) {
            SessionInfo sessionInfo = uploadSessions.get(sessionFlag);
            if (isValidSession(sessionInfo)) {
                return sessionInfo;
            }
        }
        return createSession(tableName, project);
    }

    /**
     * 创建session
     */
    private SessionInfo createSession(String tableName, String project) {
        try {
            SessionInfo sessionInfo = new SessionInfo();

            Odps odps = dbBase.createOdps();
            String partition = getPartition();

            TableTunnel tunnel = new TableTunnel(odps);
            tunnel.setEndpoint(prop.getProperty("tunnel.url"));

            TableTunnel.UploadSession uploadSession;
            if (partition.length() == 0) {
                uploadSession = tunnel
                        .createUploadSession(project, tableName);
            } else {
                PartitionSpec partitionSpec = new PartitionSpec(partition);
                uploadSession = tunnel.createUploadSession(project,
                        tableName, partitionSpec);
            }

            sessionInfo.setUploadSession(uploadSession);
            sessionInfo.setCreateTime(System.currentTimeMillis());
            sessionInfo.setProject(project);
            sessionInfo.setTableName(tableName);
            uploadSessions.put(getSessionFlag(tableName, project), sessionInfo);
            return sessionInfo;
        } catch (TunnelException e) {
            log.error("create new upload session error:{}", e.getErrorMsg() + "." + e.getMessage());
            return null;
        }
    }

    /**
     * 是否是有效的session
     */
    private boolean isValidSession(SessionInfo sessionInfo) {
        int blockCount = ParseUtil.parseInt(prop.getProperty("block.count"), 10000);
        if (sessionInfo.getCurrentBlockId() >= blockCount) {
            return false;
        }

        long sessionTime = ParseUtil.parseLong(prop.getProperty("session.timeout"), 9600000);
        if (System.currentTimeMillis() - sessionInfo.getCreateTime() > sessionTime) {
            return false;
        }

        return true;
    }

    /**
     * 获取odps分区
     */
    private String getPartition() {
        String partitionFlag = prop.getProperty("partition.flag");
        return String.format("%s='%s'", partitionFlag, sdf.format(new Date()));
    }

    private final class UploadThread implements Callable<Boolean> {

        private SessionInfo sessionInfo;
        private RecordAccumulator recordAccumulator;
        private final String splitStr;

        public UploadThread(SessionInfo sessionInfo, RecordAccumulator recordAccumulator, String splitStr) {
            this.sessionInfo = sessionInfo;
            this.recordAccumulator = recordAccumulator;
            this.splitStr = splitStr;
        }

        /**
         * 检查数据列数舒服正确
         */
        private boolean checkOdpsRecordData(String[] arrStr, TableSchema schema) {
            int columnCount = schema.getColumns().size();
            int arrCount = arrStr.length;

            return columnCount == arrCount;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                TableTunnel.UploadSession uploadSession = sessionInfo.getUploadSession();
                TableSchema schema = uploadSession.getSchema();
                Record record = uploadSession.newRecord();
                RecordWriter writer = uploadSession.openBufferedWriter();
                for (AccumulatorData data : recordAccumulator.getData()) {
                    for (String line : data.getData()) {
                        String[] arrStr = line.split(splitStr, -1);
                        if (!checkOdpsRecordData(arrStr, schema)) {
                            log.error("data check failed, schema:{}, arrStr:{}", schema.getColumns(), arrStr);
                            continue;
                        }
                        int columnCount = schema.getColumns().size();
                        for (int j = 0; j < columnCount; j++) {
                            String strTemp = arrStr[j];

                            if (StringUtils.isEmpty(strTemp)) {
                                continue;
                            }

                            Column column = schema.getColumn(j);
                            //2017-07-04 yzb增加 为了观察capture_time超长问题
                            if (column.getName().toLowerCase().equals("capture_time")) {
                                if (strTemp.length() > 10) {
                                    log.error("capture_time超长.partition:{},offset:{}.{}", data.getPartition(), data.getOffset(), line);
                                    continue;
                                }
                            }

                            OdpsType odpsType = column.getTypeInfo().getOdpsType();

                            switch (odpsType) {
                                case BIGINT:
                                    record.setBigint(j, ParseUtil.parseLong(strTemp, 0));
                                    break;
                                case BOOLEAN:
                                    record.setBoolean(j, ParseUtil.parseBoolen(strTemp, false));
                                    break;
                                case DATETIME:
                                    record.setDatetime(j, ParseUtil.parseDate(strTemp, null));
                                case DOUBLE:
                                    record.setDouble(j, ParseUtil.parseDouble(strTemp, 0));
                                    break;
                                default:
                                    record.setString(j, strTemp);
                                    break;
                            }
                            writer.write(record);
                        }
                    }
                }
                writer.close();
            } catch (TunnelException e) {
                log.error("recordWriter write data tunnel failed.{}.partition:{},offset:{}", e.getErrorMsg(), recordAccumulator.getData().stream().map(r -> r.getPartition()).collect(Collectors.toList()), recordAccumulator.getData().stream().map(r -> r.getOffset()).collect(Collectors.toList()));
                redo(recordAccumulator);
                return false;
            } catch (IOException e) {
                log.error("recordWriter write data io failed.{}.partition:{},offset:{}", e.getMessage(), recordAccumulator.getData().stream().map(r -> r.getPartition()).collect(Collectors.toList()), recordAccumulator.getData().stream().map(r -> r.getOffset()).collect(Collectors.toList()));
                //io错误的话整个session会持续错误，直接停止不再处理
                sessionInfo.abandon();
                redo(recordAccumulator);
                return false;
            }
            return true;
        }
    }
}
