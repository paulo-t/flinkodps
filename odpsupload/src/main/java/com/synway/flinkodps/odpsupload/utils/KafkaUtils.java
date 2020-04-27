package com.synway.flinkodps.odpsupload.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.utils
 * @date:2020/4/26
 */
@Slf4j
public class KafkaUtils {
    /**
     * 向指定的topic发送数据
     */
    public static <T> void sendData(String brokerList, String topic,String key, T obj) {
        try {
            Properties props = new Properties();

            props.setProperty("bootstrap.servers", brokerList);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //key 序列化
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //value 序列化
            props.setProperty("fetch.max.wait.ms", "1800000");
            KafkaProducer<String, String> kafkaProducer = new KafkaProducer(props);

            String jsonData = JSON.toJSONString(obj);
            ProducerRecord record = new ProducerRecord<String, String>(topic, key,jsonData);
            kafkaProducer.send(record);
            System.out.println("kafka send data success:" + jsonData);

            kafkaProducer.flush();
        } catch (Exception e) {
            log.error("kafka send data failed:{}", e.getMessage());
        }
    }
}
