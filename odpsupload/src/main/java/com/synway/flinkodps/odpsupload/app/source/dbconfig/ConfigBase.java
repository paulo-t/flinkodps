package com.synway.flinkodps.odpsupload.app.source.dbconfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synway.flinkodps.odpsupload.app.model.FieldVO;
import com.synway.flinkodps.odpsupload.app.model.OdpsTableConfig;
import com.synway.flinkodps.odpsupload.dal.DbBase;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.impl.OraConfigDal;
import com.synway.flinkodps.odpsupload.dal.model.FieldDO;
import com.synway.flinkodps.odpsupload.dal.model.RelationDO;
import com.synway.flinkodps.odpsupload.enums.FieldType;
import com.synway.flinkodps.odpsupload.enums.OdpsFieldType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source
 * @date:2020/4/24
 */
public abstract class ConfigBase {
    /**
     * 默认项目-tab
     */
    protected String odpsTableProject;
    /**
     * 默认项目-ct
     */
    protected String odpsCtProject;
    /**
     * 表类型
     */
    private String dataTypeList;
    /**
     * 数据库查询
     */
    protected DbBase dbBase;

    protected void build(String odpsTableProject, String odpsCtProject,String dataTypeList){
        dbBase = OraConfigDal.build();
        this.odpsTableProject = odpsTableProject;
        this.odpsCtProject = odpsCtProject;
        this.dataTypeList = dataTypeList;
    }
    /**
     * 时间戳字段
     */
    private static final List<String> CAPTURE_TIME_FIELDS = new ArrayList<String>() {
        {
            this.add("FIRST_TIME");
            this.add("LAST_TIME");
            this.add("COUNTER");
            this.add("CAPTURE_TIME");
        }
    };
    /**
     * 粗提库标识
     */
    protected static final String CT = "_CT";
    /**
     * 粗提库中文标识
     */
    protected static final String CT_CHI = "_粗提";
    /**
     * 获取表字段
     */
    protected abstract List<FieldDO> getObjFields();
    /**
     * 向一个表中添加字段
     */
    protected abstract void addField(List<FieldVO> objFields, FieldDO field);
    /**
     * 添加字段
     */
    protected void addBaseField(List<FieldVO> objFields, FieldDO field) {
        FieldType fieldType = FieldType.getByCode(field.getFieldType());
        objFields.add(new FieldVO().build(field.getColumnName(), fieldType, getOdpsFieldType(fieldType), field.getFieldChiName()));
    }
    /**
     * 获取父表信息
     */
    protected Map<String, String> getRelationData() {
        Map<String, String> relationData = Maps.newHashMap();

        StringBuilder sb = new StringBuilder();

        sb.append("select ")
                .append("obj1.tablename as table_name, obj2.tablename as parent_table_name ")
                .append("from ")
                .append("(select objectid, tableid, tablename, parentid from object where objectstate = 1 and datatype in (" + dataTypeList + ") and parentid > 0) obj1, object obj2 ")
                .append("where obj1.parentid = obj2.objectid");

        String sqlStr = sb.toString();

        List<RelationDO> relations = dbBase.execQuery(sqlStr, RelationDO.class);
        for (RelationDO relation : relations) {
            if (Objects.isNull(relation) || StringUtils.isEmpty(relation.getTableName()) || StringUtils.isEmpty(relation.getParentTableName())) {
                continue;
            }
            String subName = relation.getTableName();
            String psubName = relation.getParentTableName();

            if(!StringUtils.isEmpty(subName) && !StringUtils.isEmpty(psubName)){
                relationData.put(subName.toLowerCase(), psubName.toLowerCase());
            }
        }

        return relationData;
    }
    /**
     * 创建odps表信息
     */
    protected abstract OdpsTableConfig createOdpsTableConfig(String tableId, String tableName, int etlRule, int transState, int sys, String objMemo, String objName, List<FieldVO> objFields);
    /**
     * 添加协议
     */
    protected abstract void addObject2Map(Map<String, OdpsTableConfig> map, String tableName, String tableId, OdpsTableConfig odpsTableConfig);
    /**
     * 获取所有的表信息
     */
    protected Map<String, OdpsTableConfig> getData() {
        List<FieldDO> fields = getObjFields();
        Map<String, OdpsTableConfig> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(fields)) {
            return result;
        }
        //是否是新的协议的字段
        String tableId = "";
        String tableName = "";
        String objName = "";
        String objMemo = "";
        int sys = 0;
        //是否是下一个协议的字段
        boolean isNext = false;
        //每个协议的字段
        List<FieldVO> objFields = Lists.newArrayList();

        for (FieldDO field : fields) {
            if (!StringUtils.isEmpty(field.getTableName())) {
                //tableName不同并且字段的数量大于0说明是下一张表的字段
                if (!field.getTableId().equalsIgnoreCase(tableId)) {
                    if (objFields.size() > 0) {
                        isNext = true;
                        OdpsTableConfig odpsTableConfig = createOdpsTableConfig(tableId, tableName, 0, 0, sys, objMemo, objName, objFields);
                        addObject2Map(result, tableName, tableId, odpsTableConfig);

                        //清理上一个协议的字段
                        objFields.clear();
                        //添加下一个协议的第一个字段
                        addField(objFields, field);
                    } else {
                        //没有字段表明是第一个字段直接添加
                        addField(objFields, field);
                    }
                } else {
                    //相同表的字段直接添加
                    addField(objFields, field);
                }
                tableName = field.getTableName();
                if (isNext) {
                    objName = field.getObjectName();
                    objMemo = field.getObjectMemo();
                    sys = field.getDataSource();
                    tableId = field.getTableId();
                } else {
                    if (!StringUtils.isEmpty(field.getObjectName())) {
                        objName = field.getObjectName();
                    }
                    if (!StringUtils.isEmpty(field.getObjectMemo())) {
                        objMemo = field.getObjectMemo();
                    }
                    sys = field.getDataSource();
                    if (!StringUtils.isEmpty(field.getTableId())) {
                        tableId = field.getTableId();
                    }
                }
            }
        }

        //最后一个表的字段处理
        OdpsTableConfig odpsTableConfig = createOdpsTableConfig(tableId, tableName, 0, 0, sys, objMemo, objName, objFields);
        addObject2Map(result, tableName, tableId, odpsTableConfig);

        /**
         * 单独处理mem表
         */
        handleMemo(result);

        return result;
    }

    /**
     * 单独处理mem
     */
    protected abstract void handleMemo(Map<String, OdpsTableConfig> map);

    /**
     * 是否是时间字段
     */
    protected boolean isCaptureTimeField(String fieldName) {
        return CAPTURE_TIME_FIELDS.contains(fieldName);
    }

    /**
     * 将字段类型转换成odps字段类型
     */
    protected OdpsFieldType getOdpsFieldType(FieldType fieldType) {
        switch (fieldType) {
            case INT:
            case DATE:
            case DATETIME:
                return OdpsFieldType.BIGINT;
            case DOUBLE:
                return OdpsFieldType.DOUBLE;
            default:
                return OdpsFieldType.STRING;
        }
    }
    /**
     * 获取表评论
     */
    protected String getTableComment(String objName,String objMemo){
        if(StringUtils.isEmpty(objMemo)){
            return objName;
        }
        return String.format("%s(%s)",objName,objMemo);
    }
}
