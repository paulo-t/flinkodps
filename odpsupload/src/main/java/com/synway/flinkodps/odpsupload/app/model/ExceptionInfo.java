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
public class ExceptionInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 统计数量
     */
    private long count;
}
