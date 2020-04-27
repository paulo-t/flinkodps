package com.synway.flinkodps.odpsupload.app.sink.odps;

import lombok.Data;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.sink.odps
 * @date:2020/4/27
 */
@Data
public class AccumulatorData {
    /**
     * 分区
     */
    private int partition;
    /**
     * 偏移量
     */
    private long offset;
    /**
     * 数据
     */
    private List<String> data;
    /**
     * 重试次数
     */
    private int redoTime;
}
