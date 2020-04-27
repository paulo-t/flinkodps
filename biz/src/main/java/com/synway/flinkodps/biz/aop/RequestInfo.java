package com.synway.flinkodps.biz.aop;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * Create by tfy on 2020/3/25 17:00
 **/
@Data
@ToString
public class RequestInfo implements Serializable {
    private static final long serialVersionUID = 2313141468554471750L;
    /**
     * 请求ip
     */
    private String ip;
    /**
     * 请求地址
     */
    private String url;
    /**
     *请求参数
     */
    private Map<String,Object> param;
    /**
     * 头信息
     */
    private Map<String,Object> header;
    /**
     * 请求时间
     */
    private String requestTime;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
}
