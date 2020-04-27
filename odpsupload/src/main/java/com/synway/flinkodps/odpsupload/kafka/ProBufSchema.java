package com.synway.flinkodps.odpsupload.kafka;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.kafka
 * @date:2020/4/22
 */
@Slf4j
public class ProBufSchema implements KafkaDeserializationSchema<ConsumerRecord<String, Message>> {
    @Override
    public boolean isEndOfStream(ConsumerRecord<String, Message> stringMessageConsumerRecord) {
        return false;
    }

    private final SimpleStringSchema simpleStringSchema = new SimpleStringSchema();

    @Override
    public ConsumerRecord<String, Message> deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) {
        try {
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
