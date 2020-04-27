package com.synway.flinkodps.odpsupload.app.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * odps入库信息
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.model
 * @date:2020/4/24
 */
@Data
@ToString
public class  OdpsInfo {
    private OdpsTableConfig odpsTableConfig;
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
     * 数据数量
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
    /**
     * 分区
     */
    private int partition;
    /**
     * 偏移量
     */
    private long offset;
    /**
     * 重试次数
     */
    private int redoTime;

    public OdpsInfo(){}
}
