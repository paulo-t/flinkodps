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
public class OraStatDal extends DruidBase implements Serializable {
    private static final long serialVersionUID = -6987468928747088911L;

    private static volatile DruidDataSource oraStatDataSource = null;

    private static Object obj = new Object();

    private OraStatDal(){ }

    private static OraStatDal oraStatDal = null;

    public static OraStatDal build(){
        if(Objects.isNull(oraStatDal)){
            synchronized (OraStatDal.class){
                if(Objects.isNull(oraStatDal)){
                    oraStatDal = new OraStatDal();
                }
            }
        }
        return oraStatDal;
    }

    @Override
    protected DruidDataSourceProperties buildProperties() {
        DruidDataSourceProperties properties = buildBasicProperties();

        String oraStatUrl = ConfigUtils.get("ora-stat-url", "jdbc:oracle:thin:@10.1.13.72:1521:synlte");
        String oraStatUsr = ConfigUtils.get("ora-stat-usr", "STANDARDIZE_LL");
        String oraStatPwd = ConfigUtils.get("ora-stat-pwd", "ORACLE");
        String oraStatDriver = ConfigUtils.get("ora-stat-driver-class-name", "oracle.jdbc.driver.OracleDriver");

        properties.setDriverClassName(oraStatDriver);
        properties.setUrl(oraStatUrl);
        properties.setUsername(oraStatUsr);
        properties.setPassword(oraStatPwd);

        return properties;
    }

    @Override
    public DataSource initDataSource() {
        if (Objects.isNull(oraStatDataSource)) {
            synchronized (obj) {
                if (Objects.isNull(oraStatDataSource)) {
                    DruidDataSourceProperties properties = buildProperties();
                    oraStatDataSource = buildDruidDataSource(properties);
                }
            }
        }
        return oraStatDataSource;
    }
}
