package com.synway.flinkodps.odpsupload.kafka;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.kafka
 * @date:2020/4/22
 */
@Data
@ToString
public class Message {
    /**
     * 表名
     */
    private String dataType;
    /**
     * 扩展信息
     */
    private String dataTypeEx;
    /**
     * 系统
     */
    private String datasource;
    /**
     * 数量
     */
    private int count;
    /**
     * 创建时间
     */
    private long createDate;
    /**
     * 数据产生日期
     */
    private long dataDate;
    /**
     * 数据
     */
    private List<String> data;

    public Message() {
    }
}
