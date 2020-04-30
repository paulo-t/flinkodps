package com.synway.flinkodps.biz.controller.model;

import com.synway.flinkodps.biz.enums.FieldType;
import com.synway.flinkodps.biz.enums.OdpsFieldType;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.model
 * @date:2020/4/22
 */
@Data
@ToString
public class FieldVO implements Serializable {
    private static final long serialVersionUID  = -4364609635177401573L;
    /**
     * 字段英文名
     */
    private String engName;
    /**
     * 字段类型
     */
    private FieldType fieldType;
    /**
     * odps字段类型
     */
    private OdpsFieldType odpsType;
    /**
     * 字段中文名
     */
    private String chiName;
}
