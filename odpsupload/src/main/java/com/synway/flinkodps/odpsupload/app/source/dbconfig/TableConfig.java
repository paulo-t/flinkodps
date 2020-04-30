package com.synway.flinkodps.odpsupload.app.source.dbconfig;

import com.google.common.collect.Lists;
import com.synway.flinkodps.odpsupload.app.model.FieldVO;
import com.synway.flinkodps.odpsupload.app.model.OdpsTableConfig;
import com.synway.flinkodps.odpsupload.dal.model.FieldDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source
 * @date:2020/4/24
 */
public class TableConfig extends ConfigBase {
    public TableConfig(String odpsTableProject, String odpsCtProject,String dataTypeList) {
        super.build(odpsTableProject,odpsCtProject,dataTypeList);
    }

    @Override
    protected List<FieldDO> getObjFields() {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("select ")
                .append("tableid as table_id, tablename as table_name, tablename_b as table_name_b, objectname as object_name, objectmemo as object_memo, columnname as column_name, fieldtype as field_type, fieldchineename as field_chi_name, data_source, datatype as data_type ")
                .append("from object o, objectfield oo ")
                .append("where o.objectid = oo.objectid and datatype != 2 ")
                .append("and objectstate in(0,1) and fieldid != 'SYN0104' ")
                .append("order by o.objectid,recno");

        String sqlstr = sqlSb.toString();

        List<FieldDO> fields = dbBase.execQuery(sqlstr, FieldDO.class);
        if (CollectionUtils.isEmpty(fields)) {
            return fields;
        }

        List<FieldDO> unionFields = Lists.newArrayList();

        //将tableName和tableNameB拆分出来，后面同一处理
        for (FieldDO field : fields) {
            unionFields.add(field);
            if (!StringUtils.isEmpty(field.getTableName())) {
                FieldDO fieldDOB = new FieldDO();
                BeanUtils.copyProperties(field, fieldDOB);
                fieldDOB.setTableName(fieldDOB.getTableNameB());
                unionFields.add(fieldDOB);
            }
        }

        return unionFields;
    }


    @Override
    protected void addField(List<FieldVO> objFields, FieldDO field) {
        addBaseField(objFields, field);
    }

    @Override
    protected OdpsTableConfig createOdpsTableConfig(String tableId, String tableName, int etlRule, int transState, int sys, String objMemo, String objName, List<FieldVO> objFields) {
        return new OdpsTableConfig().build(odpsTableProject, tableId, tableName,getTableComment(objName,objMemo), objFields.size(), etlRule, objFields, transState, sys);
    }

    @Override
    protected void addObject2Map(Map<String, OdpsTableConfig> map, String tableName, String tableId, OdpsTableConfig odpsTableConfig) {
        if(!StringUtils.isEmpty(tableId)){
            map.put(tableId.toLowerCase(), odpsTableConfig);
        }
    }

    @Override
    protected void handleMemo(Map<String, OdpsTableConfig> map) {
        //mem单独处理
        OdpsTableConfig memInfo = map.get("nb_tab_mem");
        if (!Objects.isNull(memInfo)) {
            OdpsTableConfig replace = new OdpsTableConfig();
            BeanUtils.copyProperties(memInfo, replace);
            replace.setProject("nb_tab_synmem");
            map.put("nb_tab_synmem", replace);
        }
    }
}
