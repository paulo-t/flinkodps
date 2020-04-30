package com.synway.flinkodps.biz.controller;

import com.synway.flinkodps.biz.controller.model.OdpsTableConfig;
import com.synway.flinkodps.common.enums.ResponseStatus;
import com.synway.flinkodps.common.model.BaseResponse;
import com.synway.flinkodps.common.utils.BaseResponseUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.queryablestate.client.QueryableStateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.controller
 * @date:2020/4/29
 */
@RestController
@RequestMapping("/state")
public class StateController {
    private static final String TABLE_DATA_KEY = "tableData";
    private static final String CT_DATA_KEY = "ctData";
    private static final String MAPPING_DATA_KEY = "mappingData";

    private static final String TABLE_STATE_NAME = "table-config";
    private static final String MAPPING_STATE_NAME = "mapping-config";

    private static final String CONFIG_STATE_DESCRIPTOR_NAME = "configDataBroadcastState";
    private static final String MAPPING_STATE_DESCRIPTOR_NAME = "mappingDataBroadcastState";

    @Value("${remote.host.name}")
    private String remoteHostName;
    @Value("${remote.host.port}")
    private int remoteHostPort;

    @GetMapping("/getTableConfig")
    BaseResponse<Map<String, OdpsTableConfig>> getTableConfig(String jobId, int type) {
        try {
            //从后台可以看到任务的id
            JobID jobID = JobID.fromHexString(jobId);

            //flink-core-1.7.0-sources.jar!/org/apache/flink/configuration/QueryableStateOptions.java
            QueryableStateClient client = new QueryableStateClient(remoteHostName, remoteHostPort);

            MapStateDescriptor<String, Map<String, OdpsTableConfig>> configStateDescriptor = new MapStateDescriptor<>(CONFIG_STATE_DESCRIPTOR_NAME, Types.STRING, TypeInformation.of(new TypeHint<Map<String, OdpsTableConfig>>() {
            }));

            CompletableFuture<MapState<String, Map<String, OdpsTableConfig>>> result = client.getKvState(jobID, TABLE_STATE_NAME, TABLE_DATA_KEY, BasicTypeInfo.STRING_TYPE_INFO, configStateDescriptor);

            MapState<String, Map<String, OdpsTableConfig>> mapState = result.join();

            Map<String, OdpsTableConfig> ret;
            if (type == 1) {
                ret = mapState.get(TABLE_DATA_KEY);
            } else {
                ret = mapState.get(CT_DATA_KEY);
            }

            client.shutdownAndWait();

            return BaseResponseUtils.success(ret);
        } catch (Exception e) {
            return BaseResponseUtils.error(ResponseStatus.ERROR.getCode(), e.getMessage());
        }
    }


    @GetMapping("/getMappingConfig")
    BaseResponse<Map<String, String>> getMappingConfig(String jobId) {
        try {
            //从后台可以看到任务的id
            JobID jobID = JobID.fromHexString(jobId);

            //flink-core-1.7.0-sources.jar!/org/apache/flink/configuration/QueryableStateOptions.java
            QueryableStateClient client = new QueryableStateClient(remoteHostName, remoteHostPort);

            //表映射关系配置
            MapStateDescriptor<String, Map<String, String>> mappingStateDescriptor = new MapStateDescriptor<>(MAPPING_STATE_DESCRIPTOR_NAME, Types.STRING, TypeInformation.of(new TypeHint<Map<String, String>>() {
            }));

            CompletableFuture<MapState<String, Map<String, String>>> result = client.getKvState(jobID, MAPPING_STATE_NAME, MAPPING_DATA_KEY, BasicTypeInfo.STRING_TYPE_INFO, mappingStateDescriptor);

            MapState<String, Map<String, String>> mapState = result.join();

            Map<String, String> ret = mapState.get(MAPPING_DATA_KEY);

            client.shutdownAndWait();

            return BaseResponseUtils.success(ret);
        } catch (Exception e) {
            return BaseResponseUtils.error(ResponseStatus.ERROR.getCode(), e.getMessage());
        }
    }


}
