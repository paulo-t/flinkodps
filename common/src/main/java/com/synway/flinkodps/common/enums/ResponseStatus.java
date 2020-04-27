package com.synway.flinkodps.common.enums;

import lombok.Getter;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: 返回状态枚举信息
 * @date:2020/1/21
 */
@Getter
public enum ResponseStatus {
    SUCCESS(200,"SUCCESS"),
    INVALID_PARAM(300,"INVALID_PARAM"),
    ERROR(400,"ERROR"),
    FAIL(500,"FAIL")
    ;
    private int code;
    private String desc;

    ResponseStatus(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
