package com.synway.flinkodps.odpsupload.app.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.model
 * @date:2020/5/11
 */
@Data
@ToString
public class StdOdpsStatInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 系统
     */
    private int dataSource;
    /**
     * 协议英文名
     */
    private String objEngName;
    /**
     * 统计类型 0:失败 1:成功 3:接收的数据
     */
    private int stateType;
    /**
     * 数量
     */
    private long rowCount;
}
