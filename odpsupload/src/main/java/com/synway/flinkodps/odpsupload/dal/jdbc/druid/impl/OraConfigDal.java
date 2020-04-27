package com.synway.flinkodps.odpsupload.dal.jdbc.druid.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.synway.flinkodps.common.utils.ConfigUtils;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.DruidBase;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.properties.DruidDataSourceProperties;


import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.jdbc.druid.impl
 * @date:2019/12/26
 */

public class OraConfigDal extends DruidBase implements Serializable {
    private static final long serialVersionUID = 3678488525634404630L;

    private static volatile DruidDataSource oraConfigDataSource = null;

    private static Object obj = new Object();

    private OraConfigDal(){ }

    private static OraConfigDal oraConfigDal = null;

    public static OraConfigDal build(){
        if(Objects.isNull(oraConfigDal)){
            synchronized (OraConfigDal.class){
                if(Objects.isNull(oraConfigDal)){
                    oraConfigDal = new OraConfigDal();
                }
            }
        }

        return oraConfigDal;
    }

    @Override
    protected DruidDataSourceProperties buildProperties() {
        DruidDataSourceProperties properties = buildBasicProperties();

        String oraConfigUrl = ConfigUtils.get("ora-config-url", "jdbc:oracle:thin:@192.168.39.52:1521:synlte");
        String oraConfigUsr = ConfigUtils.get("ora-config-usr", "SYNLTE");
        String oraConfigPwd = ConfigUtils.get("ora-config-pwd", "ORACLE");
        String oraConfigDriver = ConfigUtils.get("ora-config-driver-class-name", "oracle.jdbc.driver.OracleDriver");

        properties.setDriverClassName(oraConfigDriver);
        properties.setUrl(oraConfigUrl);
        properties.setUsername(oraConfigUsr);
        properties.setPassword(oraConfigPwd);

        return properties;
    }

    @Override
    public DataSource initDataSource(){

        if (Objects.isNull(oraConfigDataSource)) {
            synchronized (obj) {
                if (Objects.isNull(oraConfigDataSource)) {
                    DruidDataSourceProperties properties = buildProperties();
                    oraConfigDataSource = buildDruidDataSource(properties);
                }
            }
        }
        return oraConfigDataSource;
    }
}
