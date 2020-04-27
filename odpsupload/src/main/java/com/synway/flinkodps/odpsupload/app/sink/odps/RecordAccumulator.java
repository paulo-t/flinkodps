package com.synway.flinkodps.odpsupload.app.sink.odps;

import com.google.common.collect.Lists;
import com.synway.flinkodps.odpsupload.app.model.FieldVO;
import lombok.Data;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.sink.odps
 * @date:2020/4/26
 */
@Data
public class RecordAccumulator {
    /**
     * 项目名称
     */
    private String project;
    /**
     * 表id
     */
    private String tableId;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表备注
     */
    private String tableComment;
    /**
     * 字段数量
     */
    private int colCount;
    /**
     * 清洗规则
     */
    private int etlRule;
    /**
     * 字段英文名集合
     */
    private List<FieldVO> fields;
    /**
     * 0:增量 1:全量 2:建立二级分区
     */
    private int transState;
    /**
     * 系统
     */
    private int sys;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 数据
     */
    private List<AccumulatorData> data;
    /**
     * batch的大小
     */
    private int batchSize;
    /**
     * 当前batch的数据量
     */
    private AtomicInteger appendCount;

    public RecordAccumulator(){
        appendCount = new AtomicInteger(0);
        data = Lists.newArrayList();
    }

    /**
     * 添加数据
     */
    public int append(AccumulatorData data) {
        this.data.add(data);
        updateTime= System.currentTimeMillis();
        return appendCount.addAndGet(data.getData().size());
    }

    /**
     * batch是否满了
     */
    public boolean isFull(){
        return appendCount.get() >= batchSize;
    }

    /**
     * 是否过期
     */
    public boolean isExpire(long expireTime){
        return (System.currentTimeMillis() - updateTime) >= expireTime;
    }
}
