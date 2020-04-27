package com.synway.flinkodps.odpsupload.app.model;

import com.synway.flinkodps.odpsupload.enums.FieldType;
import com.synway.flinkodps.odpsupload.enums.OdpsFieldType;
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

    public FieldVO(){}

    public FieldVO build(String engName, FieldType fieldType, OdpsFieldType odpsType, String chiName) {
        FieldVO fieldVO = new FieldVO();
        fieldVO.setEngName(engName);
        fieldVO.setChiName(chiName);
        fieldVO.setFieldType(fieldType);
        fieldVO.setOdpsType(odpsType);
        return fieldVO;
    }
}
