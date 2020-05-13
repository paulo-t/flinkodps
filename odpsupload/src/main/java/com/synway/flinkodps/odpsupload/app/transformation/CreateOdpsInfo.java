package com.synway.flinkodps.odpsupload.app.transformation;

import com.synway.flinkodps.odpsupload.app.model.DBConfigInfo;
import com.synway.flinkodps.odpsupload.app.model.OdpsInfo;
import com.synway.flinkodps.odpsupload.app.model.OdpsTableConfig;
import com.synway.flinkodps.odpsupload.kafka.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.transformation
 * @date:2020/4/26
 */
@Slf4j
public class CreateOdpsInfo extends KeyedBroadcastProcessFunction<String, ConsumerRecord<String, Message>, DBConfigInfo, OdpsInfo> {
    private static final String TABLE_DATA_KEY = "tableData";
    private static final String CT_DATA_KEY = "ctData";
    private static final String MAPPING_DATA_KEY = "mappingData";

    //表信息状态
    private MapStateDescriptor<String, Map<String, OdpsTableConfig>> configStateDescriptor;
    //映射关系状态
    private MapStateDescriptor<String, Map<String,String>> mappingStateDescriptor;
    //找不到数据库表配置信息的数据输出流tag
    private OutputTag<ConsumerRecord<String, Message>> noTableOutputTag;

    public CreateOdpsInfo(MapStateDescriptor<String, Map<String, OdpsTableConfig>> configStateDescriptor,MapStateDescriptor<String, Map<String,String>> mappingStateDescriptor,OutputTag<ConsumerRecord<String, Message>> noTableOutputTag){
        this.configStateDescriptor = configStateDescriptor;
        this.mappingStateDescriptor = mappingStateDescriptor;
        this.noTableOutputTag = noTableOutputTag;
    }

    @Override
    public void processElement(ConsumerRecord<String, Message> record, ReadOnlyContext readOnlyContext, Collector<OdpsInfo> collector) throws Exception {
        ReadOnlyBroadcastState<String, Map<String, OdpsTableConfig>> tableConfigState = readOnlyContext.getBroadcastState(configStateDescriptor);
        ReadOnlyBroadcastState<String, Map<String, String>> mappingConfigState = readOnlyContext.getBroadcastState(mappingStateDescriptor);

        Map<String, OdpsTableConfig> tableDbConfig = tableConfigState.get(TABLE_DATA_KEY);
        Map<String, OdpsTableConfig> ctDbConfig = tableConfigState.get(CT_DATA_KEY);
        Map<String, String> mappingDbConfig = mappingConfigState.get(MAPPING_DATA_KEY);
        String judgeStr = "";

        if(!StringUtils.isEmpty(record.value().getDataType())){
            String[] splits = record.value().getDataType().split("@");
            judgeStr = splits[0].toLowerCase();
        }

        //表名为空
        if(StringUtils.isEmpty(judgeStr)){
            log.error("data table name is null.partition:{},offset:{}",record.partition(),record.offset());
            readOnlyContext.output(noTableOutputTag,record);
        }

        OdpsTableConfig dbConfig = getJudgeMap(judgeStr, tableDbConfig, ctDbConfig, mappingDbConfig);
        if(Objects.isNull(dbConfig)){
            log.error("table {} get judge failed.",judgeStr);
            readOnlyContext.output(noTableOutputTag,record);
            return;
        }

        //表信息和数据的组合
        OdpsInfo odpsInfo = new OdpsInfo();
        odpsInfo.setProject(dbConfig.getProject());
        odpsInfo.setTableId(readOnlyContext.getCurrentKey());
        odpsInfo.setTableName(dbConfig.getTableName());
        odpsInfo.setTableComment(dbConfig.getTableComment());
        odpsInfo.setColCount(dbConfig.getColCount());
        odpsInfo.setEtlRule(dbConfig.getEtlRule());
        odpsInfo.setFields(dbConfig.getFields());
        odpsInfo.setTransState(dbConfig.getTransState());
        odpsInfo.setSys(dbConfig.getSys());
        odpsInfo.setCount(record.value().getCount());
        odpsInfo.setCreateDate(record.value().getCreateDate());
        odpsInfo.setDataDate(record.value().getDataDate());
        odpsInfo.setData(record.value().getData());
        odpsInfo.setPartition(record.partition());
        odpsInfo.setOffset(record.offset());
        odpsInfo.setRedoTime(0);
        collector.collect(odpsInfo);
    }

    @Override
    public void processBroadcastElement(DBConfigInfo dbConfigInfo, Context context, Collector<OdpsInfo> collector) throws Exception {
        //资源库和粗提库的配置
        BroadcastState<String, Map<String, OdpsTableConfig>> tableConfig = context.getBroadcastState(configStateDescriptor);
        //映射关系的配置
        BroadcastState<String, Map<String, String>> mappingConfig = context.getBroadcastState(mappingStateDescriptor);

        //资源库数据
        if(Objects.isNull(dbConfigInfo)){
            log.info("query db config error!");
            return ;
        }

        //替换资源库数据
        Map<String, OdpsTableConfig> tableData = dbConfigInfo.getTableData();
        if(MapUtils.isNotEmpty(tableData)){
            tableConfig.put(TABLE_DATA_KEY,tableData);
            log.info("update table data success.count:{}",tableData.size());
        }else {
            log.info("table data is empty!");
        }

        //替换粗提库数据
        Map<String, OdpsTableConfig> ctData = dbConfigInfo.getCtData();
        if(MapUtils.isNotEmpty(ctData)){
            tableConfig.put(CT_DATA_KEY,ctData);
            log.info("update ct data success.count:{}",ctData.size());
        }else {
            log.info("ct data is empty!");
        }

        //替换映射数据
        Map<String, String> mappingData = dbConfigInfo.getMappingData();
        if(MapUtils.isNotEmpty(mappingData)){
            mappingConfig.put(MAPPING_DATA_KEY,mappingData);
            log.info("update mapping data success.count:{}",mappingData.size());
        }else {
            log.info("mapping data is empty!");
        }
    }

    /**
     * 获取表信息
     */
    public OdpsTableConfig getJudgeMap(String strDataType, Map<String, OdpsTableConfig>tableData, Map<String, OdpsTableConfig> ctData, Map<String, String> mappingData) {
        if (Objects.isNull(strDataType)) {
            return null;
        }

        String parentTable = getParentTable(strDataType, mappingData);

        if(MapUtils.isNotEmpty(tableData) && tableData.containsKey(parentTable)){
            return tableData.get(parentTable);
        }

        if(MapUtils.isNotEmpty(ctData) && ctData.containsKey(parentTable)){
            return ctData.get(parentTable);
        }

        return null;
    }

    /**
     * 获取父表信息
     */
    private String getParentTable(String tableName, Map<String, String> mappingData) {
        if (MapUtils.isEmpty(mappingData)) {
            return tableName;
        }
        String parentTable = mappingData.get(tableName);
        parentTable = StringUtils.isEmpty(parentTable) ? tableName : parentTable;
        return parentTable;
    }
}
