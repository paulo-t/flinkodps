package com.synway.flinkodps.odpsupload.app.source.stat;

import com.google.common.collect.Maps;
import com.synway.flinkodps.odpsupload.app.model.ExceptionInfo;
import com.synway.flinkodps.odpsupload.dal.DbBase;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.impl.OraStatDal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source.stat
 * @date:2020/5/11
 */
@Slf4j
public class StatSource extends RichSourceFunction<Map<String, Long>> {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 数据源运行标识
     */
    private volatile boolean isRunning = true;

    /**
     * 定时线程池
     */
    private static volatile ScheduledExecutorService scheduledExecutorService;
    /**
     * 统计线程池锁
     */
    private static String STAT_POOL_LOCK = "StatPoolLock";

    /**
     * 统计层数据访问
     */
    private static DbBase statDal;

    /**
     * 成功统计id
     */
    private int statSuccessId;
    /**
     * 失败统计id
     */
    private int statFailId;

    public StatSource(int statSuccessId,int statFailId){
        this.statSuccessId = statSuccessId;
        this.statFailId = statFailId;
    }

    @Override
    public void run(SourceContext<Map<String, Long>> sourceContext) throws Exception {
        Map<String, Long> statData = getStatData();
        sourceContext.collect(statData);

        while (isRunning){
            ScheduledFuture<Map<String, Long>> schedule = scheduledExecutorService.schedule(new StatThread(), 1, TimeUnit.MINUTES);
            Map<String, Long> statInfo = schedule.get();
            log.info("statInfo:{}",statInfo);
            sourceContext.collect(statInfo);
        }
    }

    @Override
    public void cancel() {
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
        isRunning = false;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        if(Objects.isNull(scheduledExecutorService)){
            synchronized (STAT_POOL_LOCK){
                if(Objects.isNull(scheduledExecutorService)){
                    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                }
            }
        }

        statDal = OraStatDal.build();
    }

    private class StatThread implements Callable<Map<String, Long>> {
        @Override
        public Map<String, Long> call() throws Exception {
            return getStatData();
        }
    }

    private Map<String, Long> getStatData() {
        Map<String, Long> statMap = Maps.newHashMap();

        StringBuilder sb = new StringBuilder();
        String todayStr = dateFormat.format(DateUtils.addDays(new Date(),-1));
        sb.append("select  ")
                .append("EI_DST_OBJ_ENGNAME as table_name, EI_COUNT as count ")
                .append("from EXCEPTIONINFO ")
                .append("where TO_CHAR(EI_UPDATETIME,'yyyy-mm-dd') = '")
                .append(todayStr)
                .append("'")
                .append(" and ED_ID IN(")
                .append(statSuccessId)
                .append(",")
                .append(statFailId)
                .append(")");

        String strSql = sb.toString();

        List<ExceptionInfo> exceptionInfos = statDal.execQuery(strSql, ExceptionInfo.class);

        if (!CollectionUtils.isEmpty(exceptionInfos)) {
            List<ExceptionInfo> filteredData = exceptionInfos.stream().filter(e -> !StringUtils.isEmpty(e.getTableName())).collect(Collectors.toList());
            Map<String, List<ExceptionInfo>> groupedData = filteredData.stream().collect(Collectors.groupingBy(e -> e.getTableName()));
            for (String objName : groupedData.keySet()) {
                if (!StringUtils.isEmpty(objName)) {
                    List<ExceptionInfo> singleExceptionInfos = groupedData.get(objName);
                    if (!CollectionUtils.isEmpty(singleExceptionInfos)) {
                        statMap.put(objName, singleExceptionInfos.stream().mapToLong(e -> e.getCount()).sum());
                    }
                }
            }
        }

        return statMap;
    }
}
