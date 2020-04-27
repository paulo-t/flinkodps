package com.synway.flinkodps.odpsupload.app.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source
 * @date:2020/4/22
 */
@Data
@ToString
public class DBConfigInfo implements Serializable {
    private static final long serialVersionUID = 7250896240828064189L;

    /**
     * 行为库数据
     */
    private Map<String, OdpsTableConfig> tableData;
    /**
     * 知识库数据
     */
    private Map<String, OdpsTableConfig> ctData;
    /**
     * 表映射数据
     */
    private Map<String, String> mappingData;

    public DBConfigInfo(){};
}
