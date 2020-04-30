package com.synway.flinkodps.odpsupload.app.source.dbconfig;

import com.synway.flinkodps.odpsupload.app.model.DBConfigInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.concurrent.*;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source
 * @date:2020/4/22
 */
@Slf4j
public class OraConfigSource extends RichSourceFunction<DBConfigInfo> {
    /**
     * 数据源运行标识
     */
    private volatile boolean isRunning = true;
    /**
     * 定时线程池
     */
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * 表类型
     */
    private String dataTypeList;
    /**
     * 默认项目-tab
     */
    private String odpsTableProject;
    /**
     * 默认项目-ct
     */
    private String odpsCtProject;
    /**
     * 资源库数据读取
     */
    private static ConfigBase tableConfig;
    /**
     * 粗提库数据读取
     */
    private static ConfigBase ctConfig;

    public OraConfigSource(String odpsTableProject, String odpsCtProject, String dataTypeList) {
        this.odpsTableProject = odpsTableProject;
        this.dataTypeList = dataTypeList;
        this.odpsCtProject = odpsCtProject;
    }

    @Override
    public void run(SourceContext<DBConfigInfo> context) throws Exception {
        while (isRunning) {
            ScheduledFuture<DBConfigInfo> schedule = scheduledExecutorService.schedule(new ConfigThread(), 5, TimeUnit.SECONDS);
            DBConfigInfo dbConfigInfo = schedule.get();
            context.collect(dbConfigInfo);
        }
    }

    @Override
    public void cancel() {
        scheduledExecutorService.shutdown();
        isRunning = false;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        tableConfig = new TableConfig(odpsTableProject, odpsCtProject, dataTypeList);
        ctConfig = new CtConfig(odpsTableProject, odpsCtProject, dataTypeList);
    }

    private static class ConfigThread implements Callable<DBConfigInfo> {
        @Override
        public DBConfigInfo call() throws Exception {
            DBConfigInfo dbConfigInfo = new DBConfigInfo();
            dbConfigInfo.setTableData(tableConfig.getData());
            log.info("资源库数据获取成功,数量:{},数据:{}", dbConfigInfo.getTableData().size(), dbConfigInfo.getTableData());

            dbConfigInfo.setCtData(ctConfig.getData());
            log.info("粗提库数据获取成功,数量:{},数据:{}", dbConfigInfo.getCtData().size(), dbConfigInfo.getCtData());

            dbConfigInfo.setMappingData(tableConfig.getRelationData());
            log.info("表映射数据获取成功,数量:{}，数据:{}", dbConfigInfo.getMappingData().size(), dbConfigInfo.getMappingData());

            return dbConfigInfo;
        }
    }
}
