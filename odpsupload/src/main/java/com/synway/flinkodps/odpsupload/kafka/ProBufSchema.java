package com.synway.flinkodps.odpsupload.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.synway.flinkodps.odpsupload.app.model.StdOdpsStatInfo;
import com.synway.flinkodps.odpsupload.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.kafka
 * @date:2020/4/22
 */
@Slf4j
public class ProBufSchema implements KafkaDeserializationSchema<ConsumerRecord<String, Message>> {
    private static volatile boolean isFirst = true;

    /**
     * 每个协议的数据量
     */
    private Map<String, Long> dataCount = Maps.newHashMap();

    /**
     * 统计服务器
     */
    private final String statBrokerList;

    /**
     * 统计主题
     */
    private final String statTopic;

    public ProBufSchema(String statBrokerList, String statTopic) {
        this.statBrokerList = statBrokerList;
        this.statTopic = statTopic;
    }

    @Override
    public boolean isEndOfStream(ConsumerRecord<String, Message> stringMessageConsumerRecord) {
        return false;
    }

    private final SimpleStringSchema simpleStringSchema = new SimpleStringSchema();

    //当前时间
    private long startTime = System.currentTimeMillis();

    @Override
    public ConsumerRecord<String, Message> deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) {
        try {
            //第一次可能还没获取到配置流，没有配置信息，先等待一段时间
            if (isFirst) {
                Thread.sleep(30000);
                isFirst = false;
            }
            //key
            String key = simpleStringSchema.deserialize(consumerRecord.key());
            //value
            RecordProbuf.RecordSet recordSet = RecordProbuf.RecordSet.parseFrom(consumerRecord.value());
            if (Objects.isNull(recordSet) || Objects.isNull(recordSet.getAttri())) {
                log.info("data deserialize failed, partition:{}, offset:{}", consumerRecord.partition(), consumerRecord.offset());
            }

            Message message = new Message();
            message.setDataType(recordSet.getAttri().getDatatype());
            message.setDataTypeEx(recordSet.getAttri().getDatatypeEx());
            message.setDatasource(recordSet.getAttri().getDatasource());
            message.setCount(recordSet.getAttri().getCount());
            message.setCreateDate(recordSet.getAttri().getCreatedate());
            message.setDataDate(recordSet.getAttri().getDatadate());

            List<String> data = Lists.newArrayList();
            if (!CollectionUtils.isEmpty(recordSet.getRecordList())) {
                for (ByteString bytes : recordSet.getRecordList()) {
                    if (!Objects.isNull(bytes)) {
                        data.add(bytes.toStringUtf8());
                    }
                }
            }
            message.setData(data);

            ConsumerRecord<String, Message> record = new ConsumerRecord(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), key, message);

            if (dataCount.containsKey(record.value().getDataType())) {
                dataCount.put(record.value().getDataType(), dataCount.get(record.value().getDataType()) + 1);
            } else {
                dataCount.put(record.value().getDataType(), 1L);
            }

            log.info("kafka count stat:count:{}, value:{}", dataCount.size(), dataCount);

            long now = System.currentTimeMillis();

            //5分钟统计一次这里先写死
            if (now - startTime > 300000) {
                for (String tableId : dataCount.keySet()) {
                    Long count = dataCount.get(tableId);
                    StdOdpsStatInfo stdOdpsStatInfo = new StdOdpsStatInfo();
                    stdOdpsStatInfo.setRowCount(count);
                    stdOdpsStatInfo.setStateType(3);
                    stdOdpsStatInfo.setObjEngName(tableId);
                    stdOdpsStatInfo.setDataSource(0);
                    stdOdpsStatInfo.setTableName("");
                    KafkaUtils.sendData(statBrokerList, statTopic, message.getDataType(), stdOdpsStatInfo);
                }
                dataCount.clear();
                startTime = now;
            }

            return record;
        } catch (Exception e) {
            log.info("data analysis failed, partition:{}, offset:{}", consumerRecord.partition(), consumerRecord.offset());
            return null;
        }
    }

    @Override
    public TypeInformation<ConsumerRecord<String, Message>> getProducedType() {
        return TypeInformation.of(new TypeHint<ConsumerRecord<String, Message>>() {
        });
    }
}
