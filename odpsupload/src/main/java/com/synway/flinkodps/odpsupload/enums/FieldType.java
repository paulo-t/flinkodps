package com.synway.flinkodps.odpsupload.enums;

import lombok.Getter;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.enums
 * @date:2020/4/23
 */
@Getter
public enum FieldType {
    INT(0, "int"),
    DOUBLE(1, "double"),
    STRING(2, "string"),
    DATE(3, "date"),
    DATETIME(4, "datetime"),
    LONG(5,"long");

    private int code;
    private String type;

    FieldType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    /**
     * 获取字段类型
     */
    public static FieldType getByCode(int code) {
        for (FieldType value : FieldType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return FieldType.STRING;
    }
}
