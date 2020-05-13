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
import com.synway.flinkodps.odpsupload.app.model.StdOdpsStatInfo;
import com.synway.flinkodps.odpsupload.dal.DbBase;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.impl.OraStatDal;
import com.synway.flinkodps.odpsupload.dal.odps.OdpsDal;
import com.synway.flinkodps.odpsupload.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    //累加器锁
    private static final String accumulatorLock = "ODPS:ACCUMULATOR:LOCK:";
    //数据库操作
    private OdpsDal odpsDal;
    //统计数据
    private DbBase oraStatDal;
    //配置属性
    private Properties prop;
    //数据状态
    private transient ListState<RecordAccumulator> recordAccumulatorState;
    private ListStateDescriptor<RecordAccumulator> recordAccumulatorListStateDescriptor;
    //保存所有的数据累加器
    private static Map<String, RecordAccumulator> recordAccumulators = Maps.newConcurrentMap();
    //session状态
    private transient ListState<SessionInfo> sessionState;
    private ListStateDescriptor<SessionInfo> infoListStateDescriptor;
    //保存所有的uploadSession
    private static Map<String, SessionInfo> uploadSessions = Maps.newConcurrentMap();

    /**
     * 统计时使用的机器标识
     */
    private static String machineTag;

    private static final String MACHINE_TAG_LOCK = "MACHILE_LOCK";
    //异常系统表
    private static final String STD_EXCEPTION_SYSINFO_TABLE = "EXCEPTIONDICTIONARY";

    public OdpsSink(Properties prop) {
        this.prop = prop;

        recordAccumulatorListStateDescriptor = new ListStateDescriptor("accumulator-state", TypeInformation.of(new TypeHint<RecordAccumulator>() {
        }));

        infoListStateDescriptor = new ListStateDescriptor("session-state", TypeInformation.of(new TypeHint<SessionInfo>() {
        }));
    }

    @Override
    public void invoke(OdpsInfo odpsInfo, Context context) throws Exception {
        String lock = accumulatorLock + getSessionFlag(odpsInfo.getTableName(), odpsInfo.getProject());

        RecordAccumulator accumulator;
        //防止多个subTask重复发送
        synchronized (lock){
            accumulator = getAccumulator(odpsInfo);
            appendData(accumulator, odpsInfo);
            if(accumulator.isFull()){
                //满了移除累加器
                recordAccumulators.remove(getSessionFlag(accumulator.getTableName(), accumulator.getProject()));
            }
        }

        if (accumulator.isFull()) {
            log.info("{} is full:{}",accumulator.getTableName()+","+accumulator.getProject(),accumulator.getAppendCount().get());
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
        sessionState = context.getOperatorStateStore().getListState(infoListStateDescriptor);
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

    private long send(RecordAccumulator accumulator) {
        long writeCount = 0L;

        SessionInfo session = getSession(accumulator.getTableName(), accumulator.getProject());
        if (Objects.isNull(session)) {
            log.error("project:{},table:{} failed get upload session.", accumulator.getProject(), accumulator.getTableName());
            redo(accumulator);
        }
        //发送数据
        session.getNewBlockId();

        //将数据拆分成多份发送
        List<RecordAccumulator> recordAccumulators = splitAccumulator(accumulator);

        List<UploadThread> threadList = Lists.newArrayList();

        for (RecordAccumulator recordAccumulator : recordAccumulators) {
            threadList.add(new UploadThread(session, recordAccumulator, prop.getProperty("split.str")));
        }

        List<Future<Long>> results = Lists.newArrayList();

        for (UploadThread thread : threadList) {
            results.add(executorService.submit(thread));
        }

        int i = 0;

        for (Future<Long> result : results) {
            UploadThread uploadThread = threadList.get(i);
            try {
                writeCount += result.get();
            } catch (InterruptedException | ExecutionException e) {
                //每一部分出错值单独重试
                log.error("single data send error add to redo:{}", e.getMessage());
                redo(uploadThread.getRecordAccumulator());
            }
            i++;
        }

        boolean commitRet = commit(session, accumulator, writeCount, 0L);

        //统计信息
        StdOdpsStatInfo stdOdpsStatInfo = new StdOdpsStatInfo();
        stdOdpsStatInfo.setTableName(accumulator.getTableName());
        stdOdpsStatInfo.setDataSource(accumulator.getSys());
        String tableId = accumulator.getTableId();
        String objEngName = "";
        if (!StringUtils.isEmpty(tableId)) {
            objEngName = tableId.split("@")[0];
        }
        stdOdpsStatInfo.setObjEngName(objEngName);
        stdOdpsStatInfo.setStateType(commitRet ? 1 : 0);
        stdOdpsStatInfo.setRowCount(writeCount);

        String statBrokerList = prop.getProperty("stat.broker.list");
        String statRedoTopic = prop.getProperty("stat.topic");

        KafkaUtils.sendData(statBrokerList, statRedoTopic, objEngName, stdOdpsStatInfo);

        return commitRet ? writeCount : 0;
    }

    private boolean commit(SessionInfo session, RecordAccumulator accumulator, long writeCount, long time) {
        try {
            session.getUploadSession().commit();
        } catch (TunnelException e) {
            //网络异常阻塞重试
            log.error("project:{}, table:{} session commit error, redo time {} :{}", accumulator.getProject(), accumulator.getTableName(), ++time, e.getMessage());
            try {
                Thread.sleep(time * 5);
            } catch (InterruptedException e1) {
                redo(accumulator);
                return false;
            }
            return commit(session, accumulator, writeCount, time);
        } catch (IOException e) {
            //io异常直接重试
            redo(accumulator);
            return false;
        }
        log.info("project:{}, table:{} commit success. count:{}", accumulator.getProject(), accumulator.getTableName(), writeCount);
        return true;
    }

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

        int poolSize = ParseUtil.parseInt(prop.get("pool.size"), 5);

        double batchNum = Math.ceil(accumulator.getData().size() * 1.0 / poolSize);

        RecordAccumulator recordAccumulator = getNewRecordAccumulator(accumulator);

        for (AccumulatorData accumulatorData : accumulator.getData()) {
            if (recordAccumulator.getData().size() >= batchNum) {
                ret.add(recordAccumulator);
                recordAccumulator = getNewRecordAccumulator(accumulator);
            } else {
                recordAccumulator.append(accumulatorData);
            }
        }

        ret.add(recordAccumulator);

        return ret;
    }

    private RecordAccumulator getNewRecordAccumulator(RecordAccumulator accumulator) {
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

        return recordAccumulator;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        //线程池初始化
        int poolSize = ParseUtil.parseInt(prop.get("pool.size"), 5);
        if (Objects.isNull(executorService)) {
            synchronized (threadPoolLock) {
                if (Objects.isNull(executorService)) {
                    executorService = new ThreadPoolExecutor(2, poolSize, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
                }
            }
        }

        //数据访问层初始化
        odpsDal = OdpsDal.build();
        oraStatDal = OraStatDal.build();


        //机器信息初始化
  /*      if (StringUtils.isEmpty(machineTag)) {
            synchronized (MACHINE_TAG_LOCK) {
                if (StringUtils.isEmpty(machineTag)) {
                    machineTag = getMachineTag();
                }
            }
        }*/
    }

    @Override
    public void close() throws Exception {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
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
     * 创建上传的session
     */
    private TableTunnel.UploadSession createUploadSession(TableTunnel tunnel, String project, String tableName, String partition, long retryTime) {
        try {
            if (partition.length() == 0) {
                return tunnel.createUploadSession(project, tableName);
            } else {
                PartitionSpec partitionSpec = new PartitionSpec(partition);
                return tunnel.createUploadSession(project, tableName, partitionSpec);
            }
        } catch (TunnelException e) {
            //Tunnel异常递归重新获取
            log.error("创建uploadSession失败，开始第{}次重试", ++retryTime);
            try {
                Thread.sleep(retryTime * 5);
            } catch (InterruptedException e1) {
                log.error("create session error:" + e1.getMessage());
                return null;
            }
            return createUploadSession(tunnel, project, tableName, partition, retryTime);
        }
    }

    /**
     * 创建session
     */
    private SessionInfo createSession(String tableName, String project) {

        SessionInfo sessionInfo = new SessionInfo();

        Odps odps = odpsDal.createOdps();
        String partition = getPartition();

        TableTunnel tunnel = new TableTunnel(odps);
        tunnel.setEndpoint(prop.getProperty("tunnel.url"));

        TableTunnel.UploadSession uploadSession = createUploadSession(tunnel, project, tableName, partition, 0L);
        if (Objects.isNull(uploadSession)) {
            return null;
        }
        sessionInfo.setUploadSession(uploadSession);
        sessionInfo.setCreateTime(System.currentTimeMillis());
        sessionInfo.setProject(project);
        sessionInfo.setTableName(tableName);
        uploadSessions.put(getSessionFlag(tableName, project), sessionInfo);
        return sessionInfo;
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

    private final class UploadThread implements Callable<Long> {

        private SessionInfo sessionInfo;
        private RecordAccumulator recordAccumulator;
        private final String splitStr;

        public UploadThread(SessionInfo sessionInfo, RecordAccumulator recordAccumulator, String splitStr) {
            this.sessionInfo = sessionInfo;
            this.recordAccumulator = recordAccumulator;
            this.splitStr = splitStr;
        }

        public RecordAccumulator getRecordAccumulator() {
            return recordAccumulator;
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
        public Long call() {
            long writeCount = 0L;
            try {
                TableTunnel.UploadSession uploadSession = sessionInfo.getUploadSession();
                TableSchema schema = uploadSession.getSchema();
                Record record = uploadSession.newRecord();

                long openBufferTime = 0L;
                RecordWriter writer = openRecordWriter(uploadSession, openBufferTime);

                for (AccumulatorData data : recordAccumulator.getData()) {
                    for (String line : data.getData()) {
                        boolean oneRet = writeOne(line, schema, writer, record, data.getPartition(), data.getOffset());
                        if (oneRet) {
                            writeCount++;
                        }
                    }
                }

                writer.close();
            } catch (IOException e) {
                log.error("recordWriter write data io failed.{}.partition:{},offset:{}", e.getMessage(), recordAccumulator.getData().stream().map(r -> r.getPartition()).collect(Collectors.toList()), recordAccumulator.getData().stream().map(r -> r.getOffset()).collect(Collectors.toList()));
                //io错误的话整个session会持续错误，直接停止不再处理
                sessionInfo.abandon();
                redo(recordAccumulator);
            }

            return writeCount;
        }


        //打开recordWriter通道
        private RecordWriter openRecordWriter(TableTunnel.UploadSession uploadSession, long retryTime) {
            RecordWriter writer;
            try {
                writer = uploadSession.openBufferedWriter();
            } catch (TunnelException e) {
                //tunnel异常表示网络有问题别的表也不会正常打开通道这里做递归获取直至成功
                log.error("open buffered writer error.start {} retry...", ++retryTime);
                writer = openRecordWriter(uploadSession, retryTime);
            }
            return writer;
        }

        /**
         * 写入一行数据
         */
        private boolean writeOne(String line, TableSchema schema, RecordWriter writer, Record record, int partition, long offset) throws IOException {
            String[] arrStr = line.split(splitStr, -1);

            if (!checkOdpsRecordData(arrStr, schema)) {
                log.error("data check failed, schema:{}, arrStr:{}", schema.getColumns(), arrStr);
                return false;
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
                        log.error("capture_time超长.partition:{},offset:{}.{}", partition, offset, line);
                        return false;
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

            return true;
        }
    }

    /**
     * 统计相关代码
     */
    private String getMachineTag() {
        String localIp = getIp();
        String programTag = prop.getProperty("program.tag");

        StringBuilder sb = new StringBuilder();
        sb.append("select ESI_GUID from ")
                .append(STD_EXCEPTION_SYSINFO_TABLE)
                .append(" where ESI_IP = '")
                .append(localIp)
                .append("' and ESI_PROTAG = '")
                .append(programTag)
                .append("'");

        String sqlStr = sb.toString();

        List<Object[]> objects = oraStatDal.execBySql(sqlStr);

        if (!CollectionUtils.isEmpty(objects)) {
            for (Object[] object : objects) {
                if (!Objects.isNull(object) && object.length > 0)
                    return object[0].toString();
            }
        }

        return createNewMachineTag();
    }

    private String createNewMachineTag() {
        String newGuid = UUID.randomUUID().toString().replace("-", "");
        String programTag = prop.getProperty("progam.tag");
        String localIp = getIp();
        String hostName = getHost();

        StringBuilder sql = new StringBuilder();
        sql.append("insert into ")
                .append(STD_EXCEPTION_SYSINFO_TABLE)
                .append("(ESI_GUID,ESI_PROTAG,ESI_IP,ESI_COMPUTERNAME,ESI_FREQUENCY,ESI_STATUS)")
                .append(" values(")
                .append("'").append(newGuid).append("'").append(",")
                .append("'" + programTag + "'").append(",")
                .append("'" + localIp + "'").append(",")
                .append("'" + hostName + "'").append(",")
                .append(300).append(",")
                .append(1)
                .append(")");

        if (oraStatDal.execSql(sql.toString())) {
            log.info("machine tag create success.ip:{},host:{},program:{},guid:{}", localIp, hostName, programTag, newGuid);
            return newGuid;
        } else {
            log.info("machine tag create failed.ip:{},host:{},program:{},guid:{}", localIp, hostName, programTag, newGuid);
            return "";
        }
    }

    /**
     * 获取本机ip
     */
    private static String getIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("get ip failed");
            return "";
        }
    }

    /**
     * 获取主机名称
     */
    private static String getHost() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            log.error("get host failed");
            return "";
        }
    }
}
