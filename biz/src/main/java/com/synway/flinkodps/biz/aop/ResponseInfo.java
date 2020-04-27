package com.synway.flinkodps.biz.aop;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Create by tfy on 2020/3/25 17:17
 **/
@Data
@ToString
public class ResponseInfo implements Serializable {
    private static final long serialVersionUID = -4642316233103841858L;
    /**
     *请求url
     */
    private String url;
    /**
     * 返回时间
     */
    private String responseTime;
    /**
     * 返回参数
     */
    private Object data;
    /**
     * 耗时
     */
    private long costTime;
}
