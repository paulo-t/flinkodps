package com.synway.flinkodps.odpsupload.app.source.dbconfig;

import com.synway.flinkodps.odpsupload.app.model.FieldVO;
import com.synway.flinkodps.odpsupload.app.model.OdpsTableConfig;
import com.synway.flinkodps.odpsupload.dal.model.FieldDO;
import com.synway.flinkodps.odpsupload.enums.FieldType;
import com.synway.flinkodps.odpsupload.enums.OdpsFieldType;

import java.util.List;
import java.util.Map;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.source.dbconfig
 * @date:2020/4/24
 */
public class CtConfig extends ConfigBase {
    public CtConfig(String odpsTableProject, String odpsCtProject, String dataTypeList) {
        super.build(odpsTableProject, odpsCtProject, dataTypeList);
    }

    @Override
    protected List<FieldDO> getObjFields() {

        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("select ")
                .append("tableid as table_id, tablename as table_name, objectname as object_name, objectmemo as object_memo, columnname as column_name, fieldtype as field_type, fieldchineename as field_chi_name, data_source ")
                .append("from object o, objectfield oo ")
                .append("where o.objectid = oo.objectid and tablename like 'NB_APP_%' ")
                .append("and objectstate in(0,1) and fieldid != 'SYN0104' ")
                .append("order by o.objectid,recno");

        String sqlstr = sqlSb.toString();

        List<FieldDO> fields = dbBase.execQuery(sqlstr, FieldDO.class);

        return fields;
    }

    @Override
    protected void addField(List<FieldVO> objFields, FieldDO field) {
        if (isCaptureTimeField(field.getColumnName())) {
            if (!objFields.stream().filter(f -> f.getEngName().equalsIgnoreCase("CAPTURE_TIME")).findFirst().isPresent()) {
                objFields.add(new FieldVO().build(field.getColumnName(), FieldType.LONG, OdpsFieldType.BIGINT, field.getFieldChiName()));
                return;
            }
        }
        //SID转换成MD_ID
        if (field.getColumnName().equalsIgnoreCase("SID")) {
            field.setColumnName("MD_ID");
        }
        addBaseField(objFields, field);
    }

    @Override
    protected OdpsTableConfig createOdpsTableConfig(String tableId, String tableName, int etlRule, int transState, int sys, String objMemo, String objName, List<FieldVO> objFields) {
        return new OdpsTableConfig().build(odpsCtProject, tableId, tableName + CT, getTableComment(objName, objMemo) + CT_CHI, objFields.size(), 0, objFields, 0, sys);
    }

    @Override
    protected void addObject2Map(Map<String, OdpsTableConfig> map, String tableName, String tableId, OdpsTableConfig odpsTableConfig) {
        map.put(tableId.toLowerCase(), odpsTableConfig);
    }

    @Override
    protected void handleMemo(Map<String, OdpsTableConfig> map) {

    }
}
