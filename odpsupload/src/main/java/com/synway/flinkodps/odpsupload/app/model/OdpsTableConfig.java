package com.synway.flinkodps.odpsupload.app.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source
 * @date:2020/4/22
 */

@Data
@ToString
public class OdpsTableConfig implements Serializable {
    private static final long serialVersionUID = -5926719768101851472L;
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
     * 0:增量 1:全量 2:建立二级分区
     */
    private int transState;
    /**
     * 系统
     */
    private int sys;
    /**
     * 字段英文名集合
     */
    private List<FieldVO> fields;

    public OdpsTableConfig(){}

    public OdpsTableConfig build(String project, String tableId, String tableName, String tableComment, int colCount, int etlRule, List<FieldVO> fields, int transState, int sys) {
        OdpsTableConfig odpsTableConfig = new OdpsTableConfig();
        odpsTableConfig.setProject(project);
        odpsTableConfig.setTableId(tableId);
        odpsTableConfig.setTableName(tableName);
        odpsTableConfig.setTableComment(tableComment);
        odpsTableConfig.setColCount(colCount);
        odpsTableConfig.setEtlRule(etlRule);
        odpsTableConfig.setFields(fields);
        odpsTableConfig.setTransState(transState);
        odpsTableConfig.setSys(sys);
        return odpsTableConfig;
    }
}
