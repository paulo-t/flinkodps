package com.synway.flinkodps.odpsupload.app;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.transformation
 * @date:2020/5/26
 */
public class OdpsUpload2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.execute(OdpsUpload2.class.getSimpleName());
    }
}
