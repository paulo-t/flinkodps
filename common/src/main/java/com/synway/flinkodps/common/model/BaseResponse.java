package com.synway.flinkodps.common.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizeconfig.common.model
 * @date:2020/1/21
 */
@Data
@ToString
public class BaseResponse<T> implements Serializable {
    /**
     * 状态码
     */
    private int code;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;
}
