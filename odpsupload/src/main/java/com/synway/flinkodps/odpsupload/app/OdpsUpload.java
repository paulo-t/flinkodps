package com.synway.flinkodps.odpsupload.app;

import com.synway.flinkodps.common.utils.ConfigUtils;
import com.synway.flinkodps.common.utils.ParseUtil;
import com.synway.flinkodps.odpsupload.app.model.DBConfigInfo;
import com.synway.flinkodps.odpsupload.app.model.OdpsInfo;
import com.synway.flinkodps.odpsupload.app.model.OdpsTableConfig;
import com.synway.flinkodps.odpsupload.app.sink.odps.OdpsSink;
import com.synway.flinkodps.odpsupload.app.source.dbconfig.OraConfigSource;
import com.synway.flinkodps.odpsupload.app.transformation.CreateOdpsInfo;
import com.synway.flinkodps.odpsupload.constants.OdpsUploadConstant;
import com.synway.flinkodps.odpsupload.kafka.Message;
import com.synway.flinkodps.odpsupload.kafka.ProBufSchema;
import com.synway.flinkodps.odpsupload.kafka.RedoSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app
 * @date:2020/4/22
 */
@Slf4j
public class OdpsUpload {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String checkPointIntervalStr = ConfigUtils.get("check-point-interval", "900000");
        env.enableCheckpointing(ParseUtil.parseLong(checkPointIntervalStr,900000));
        //checkpoint模式
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //两个checkpoint之间最小间隔500ms
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);

        //kafka消费者配置
        Properties consumerConfig = new Properties();
        consumerConfig.setProperty("bootstrap.servers", ConfigUtils.get("bootstrap-servers","1.1.1.5:9092,1.1.1.10:9092"));
        consumerConfig.setProperty("group.id",ConfigUtils.get("odps-group-id","flink-odps"));
        FlinkKafkaConsumer<ConsumerRecord<String, Message>> flinkKafkaConsumer = new FlinkKafkaConsumer<ConsumerRecord<String, Message>>(ConfigUtils.get("odps-topic","HBASE"),new ProBufSchema(),consumerConfig);
        flinkKafkaConsumer.setStartFromEarliest();
        DataStream<ConsumerRecord<String, Message>> messageSource = env.addSource(flinkKafkaConsumer);

        //过滤空的消息
        DataStream<ConsumerRecord<String, Message>> filteredMessage = messageSource.filter((FilterFunction<ConsumerRecord<String, Message>>) stringMessageConsumerRecord -> !Objects.isNull(stringMessageConsumerRecord));
        //按表明划分数据
        KeyedStream<ConsumerRecord<String, Message>, String> messageKeyedStream = filteredMessage.keyBy((KeySelector<ConsumerRecord<String, Message>, String>) record -> record.value().getDataTypeEx());

        /**
         * 配置信息
         */
        //数据库资源配置
        MapStateDescriptor<String, Map<String, OdpsTableConfig>> configStateDescriptor = new MapStateDescriptor<>(OdpsUploadConstant.CONFIG_STATE_DESCRIPTOR_NAME, Types.STRING, TypeInformation.of(new TypeHint<Map<String, OdpsTableConfig>>() {
        }));
        //表映射关系配置
        MapStateDescriptor<String, Map<String,String>> mappingStateDescriptor = new MapStateDescriptor<>(OdpsUploadConstant.MAPPING_STATE_DESCRIPTOR_NAME, Types.STRING, TypeInformation.of(new TypeHint<Map<String, String>>() {
        }));
        //找不到数据库表配置信息的数据输出流tag
        OutputTag<ConsumerRecord<String, Message>> noTableOutputTag = new OutputTag<ConsumerRecord<String, Message>>("no-table-side-output"){};

        //配置流并行度设置成1，通过广播转态发送配置
        BroadcastStream<DBConfigInfo> broadcastConfig = env.addSource(new OraConfigSource(ConfigUtils.get("odps-table-project","adpstabproject"),ConfigUtils.get("odps-ct-project","adpsctproject"),ConfigUtils.get("data-type-list","3"))).setParallelism(1).broadcast(configStateDescriptor, mappingStateDescriptor);

        //获取odps库中表的信息和数据
        SingleOutputStreamOperator<OdpsInfo> dataWithDbInfoStream = messageKeyedStream.connect(broadcastConfig).process(new CreateOdpsInfo(configStateDescriptor, mappingStateDescriptor, noTableOutputTag));
        //TODO 没有查询到表信息的数据
        DataStream<ConsumerRecord<String, Message>> noTableData = dataWithDbInfoStream.getSideOutput(noTableOutputTag);
        noTableData.writeAsText("/data1/odpsupload/noTableData", FileSystem.WriteMode.NO_OVERWRITE);
        //入odps
        Properties props = new Properties();
        //每个数据块的数据数量
        props.setProperty("batch.size",ConfigUtils.get("batch-size","100000"));
        //每个数据块的过期时间(默认6小时)
        props.setProperty("batch.timeout",ConfigUtils.get("batch-timeout","9600000"));
        //每个odps session的过期时间(默认6小时)
        props.setProperty("session.timeout",ConfigUtils.get("session-timeout","9600000"));
        //每个数据块可以上传的block数量
        props.setProperty("block.count",ConfigUtils.get("block-count","10000"));
        //tunnel url
        props.setProperty("tunnel.url",ConfigUtils.get("tunnel-url","http://dt.cn-hangzhou-g20-d01.odps.cloud.st.zj"));
        //分区标识
        props.setProperty("partition.flag",ConfigUtils.get("partition-flag","dt"));
        //数据分隔符
        props.setProperty("split.str",ConfigUtils.get("split-str","\t"));
        //线程池数量
        props.setProperty("pool.size",ConfigUtils.get("pool-size","5"));
        //重试的服务器
        props.setProperty("redo.broker.list",ConfigUtils.get("redo-broker-list","1.1.1.5:9092,1.1.1.10:9092"));
        //重试的topic
        props.setProperty("redo.topic",ConfigUtils.get("redo-topic","odpsRedo"));

        //重试kafka消费者配置
        Properties redoConfig = new Properties();
        redoConfig.setProperty("bootstrap.servers", ConfigUtils.get("redo-broker-list","1.1.1.5:9092,1.1.1.10:9092"));
        redoConfig.setProperty("group.id",ConfigUtils.get("odps-group-id","flink-odps"));
        FlinkKafkaConsumer<OdpsInfo> redoConsumer = new FlinkKafkaConsumer<>(ConfigUtils.get("redo-topic", "odpsRedo"), new RedoSchema(), redoConfig);
        DataStreamSource<OdpsInfo> initRedoStream = env.addSource(redoConsumer);
        SingleOutputStreamOperator<OdpsInfo> filterRedoStream = initRedoStream.filter((FilterFunction<OdpsInfo>) odpsInfo -> !Objects.isNull(odpsInfo));

        //找不到数据库表配置信息的数据输出流tag
        OutputTag<OdpsInfo> maxRedoTag = new OutputTag<OdpsInfo>("max-redo-side-output"){};
        int maxRedoTime = ParseUtil.parseInt(ConfigUtils.get("max-redo-time","3"),3);

        SingleOutputStreamOperator<OdpsInfo> redoStream = filterRedoStream.process(new ProcessFunction<OdpsInfo, OdpsInfo>() {
            @Override
            public void processElement(OdpsInfo odpsInfo, Context context, Collector<OdpsInfo> collector) throws Exception {
                if (odpsInfo.getRedoTime() > maxRedoTime) {
                    context.output(maxRedoTag, odpsInfo);
                } else {
                    collector.collect(odpsInfo);
                }
            }
        });

        //达到最大次数不在重试
        DataStream<OdpsInfo> maxRedoOutput = redoStream.getSideOutput(maxRedoTag);
        maxRedoOutput.writeAsText("/data1/odpsupload/redoMax");

        //重试流和正常流合并
        DataStream<OdpsInfo> unionStream = dataWithDbInfoStream.union(dataWithDbInfoStream);
        //union之后再做一次keyBy保证相同的表用同一个实例去处理
        KeyedStream<OdpsInfo, String> odpsInfoStringKeyedStream = unionStream.keyBy((KeySelector<OdpsInfo, String>) odpsInfo -> odpsInfo.getProject() + "_" + odpsInfo.getTableName());

        //数据写入odps
        odpsInfoStringKeyedStream.addSink(new OdpsSink(props));

        env.execute(OdpsUpload.class.getSimpleName());
    }
}
