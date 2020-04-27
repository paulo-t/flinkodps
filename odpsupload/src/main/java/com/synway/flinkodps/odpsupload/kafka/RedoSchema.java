package com.synway.flinkodps.odpsupload.kafka;

import com.alibaba.fastjson.JSON;
import com.synway.flinkodps.odpsupload.app.model.OdpsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.kafka
 * @date:2020/4/27
 */
@Slf4j
public class RedoSchema implements KafkaDeserializationSchema<OdpsInfo> {
    private final SimpleStringSchema simpleStringSchema = new SimpleStringSchema();

    @Override
    public boolean isEndOfStream(OdpsInfo odpsInfo) {
        return false;
    }

    @Override
    public OdpsInfo deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        try{
            String value = simpleStringSchema.deserialize(consumerRecord.value());
            OdpsInfo odpsInfo = JSON.parseObject(value, OdpsInfo.class);
            odpsInfo.setPartition(consumerRecord.partition());
            odpsInfo.setOffset(consumerRecord.offset());
            return odpsInfo;
        }catch (Exception e){
            log.error("deserialize redo data error.{}",e.getMessage());
            return null;
        }
    }

    @Override
    public TypeInformation<OdpsInfo> getProducedType() {
        return TypeInformation.of(new TypeHint<OdpsInfo>() {
        });
    }
}
