package com.synway.flinkodps.odpsupload.enums;

import lombok.Getter;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.enums
 * @date:2020/4/23
 */
@Getter
public enum  OdpsFieldType {
    BIGINT(0, "bigint"),
    DOUBLE(1, "double"),
    STRING(2, "string"),
    ;
    private int code;
    private String type;

    OdpsFieldType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    /**
     * 获取字段类型
     */
    public static OdpsFieldType getByCode(int code) {
        for (OdpsFieldType value : OdpsFieldType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
