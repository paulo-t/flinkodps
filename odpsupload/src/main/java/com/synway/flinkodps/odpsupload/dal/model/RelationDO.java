package com.synway.flinkodps.odpsupload.dal.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.dal.model
 * @date:2020/4/23
 */
@Data
@ToString
public class RelationDO implements Serializable {
    private static final long serialVersionUID = 3954817019653537417L;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 父表名
     */
    private String parentTableName;
}
