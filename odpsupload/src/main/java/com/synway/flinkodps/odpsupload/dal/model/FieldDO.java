package com.synway.flinkodps.odpsupload.dal.model;

import com.synway.flinkodps.odpsupload.app.model.FieldVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.dal.model
 * @date:2020/4/22
 */
@Data
@ToString
public class FieldDO implements Serializable {
    private static final long serialVersionUID = 1551472433558219201L;
    /**
     * 表id JZ_RESOURCE_0001_01
     */
    private String tableId;
    /**
     * 表名 NB_APP_INENTITY01
     */
    private String tableName;
    /**
     * tableNameB
     */
    private String tableNameB;
    /**
     * 协议名称
     */
    private String objectName;
    /**
     *协议备注
     */
    private String objectMemo;
    /**
     *
     */
    private String columnName;
    /**
     * 字段类型
     */
    private int fieldType;
    /**
     * 字段中文名
     */
    private String fieldChiName;
    /**
     * 系统 144
     */
    private int dataSource;
}
