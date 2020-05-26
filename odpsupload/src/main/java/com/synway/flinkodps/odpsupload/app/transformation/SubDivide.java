package com.synway.flinkodps.odpsupload.app.transformation;

import com.synway.flinkodps.odpsupload.kafka.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 细分数据
 *
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.transformation
 * @date:2020/5/12
 */
@Slf4j
public class SubDivide extends BroadcastProcessFunction<ConsumerRecord<String, Message>, Map<String, Long>, ConsumerRecord<String, Message>> {
    private final MapStateDescriptor<String, Long> statStateDescriptor;

    private int devideCount;

    public SubDivide(MapStateDescriptor<String, Long> statStateDescriptor,int devideCount) {
        this.statStateDescriptor = statStateDescriptor;
        this.devideCount = devideCount;
    }

    @Override
    public void processElement(ConsumerRecord<String, Message> record, ReadOnlyContext readOnlyContext, Collector<ConsumerRecord<String, Message>> collector) throws Exception {
        ReadOnlyBroadcastState<String, Long> broadcastState = readOnlyContext.getBroadcastState(statStateDescriptor);
        Long dataCount = broadcastState.get(StringUtils.isEmpty(record.value().getDataType()) ? "" : record.value().getDataType().toLowerCase());
        record.value().setDataType(getRandomFlag(record.value().getDataType(), dataCount));
        collector.collect(record);
    }

    @Override
    public void processBroadcastElement(Map<String, Long> statInfo, Context context, Collector<ConsumerRecord<String, Message>> collector) throws Exception {
        BroadcastState<String, Long> statState = context.getBroadcastState(statStateDescriptor);

        for (String objName : statInfo.keySet()) {
            if(!StringUtils.isEmpty(objName)){
                statState.put(objName.toLowerCase(), statInfo.get(objName));
            }
        }

        log.info("update stat data success:{}", statInfo.size());
    }

    private String getRandomFlag(String objName, Long count) {
        if (StringUtils.isEmpty(objName)) {
            return objName;
        }

        int max = getRuntimeContext().getMaxNumberOfParallelSubtasks();

        int randomNum;

        if (Objects.isNull(count) || count == 0) {
            randomNum = max;
        } else {
            long var1 = count / devideCount + 1;
            randomNum = var1 > max ? max : (int) var1;
        }

        return objName + "@" + ThreadLocalRandom.current().nextInt(randomNum);
    }
}
