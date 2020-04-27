package com.synway.flinkodps.odpsupload.dal.jdbc.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.synway.flinkodps.common.utils.ConfigUtils;
import com.synway.flinkodps.odpsupload.dal.jdbc.JdbcAbstractBase;
import com.synway.flinkodps.odpsupload.dal.jdbc.druid.properties.DruidDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import scala.Serializable;

import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.jdbc.druid
 * @date:2019/12/26
 */
@Slf4j
public abstract class DruidBase extends JdbcAbstractBase implements Serializable {
    private static final long serialVersionUID = -6167571275886424047L;

    /**
     * 获取连接全部属性
     */
    protected abstract DruidDataSourceProperties buildProperties();

    /**
     * 获取数据库连接基本属性
     */
    protected DruidDataSourceProperties buildBasicProperties() {
        DruidDataSourceProperties basicProperties = new DruidDataSourceProperties();
        basicProperties.setInitialSize(Integer.parseInt(ConfigUtils.get("initial-size", "4")));
        basicProperties.setMinIdle(Integer.parseInt(ConfigUtils.get("min-idle", "4")));
        basicProperties.setMaxActive(Integer.parseInt(ConfigUtils.get("max-active", "20")));
        basicProperties.setMaxWait(Integer.parseInt(ConfigUtils.get("max-wait", "60000")));
        basicProperties.setTimeBetweenEvictionRunsMillis(Long.parseLong(ConfigUtils.get("time-between-eviction-runs-millis", "60000")));
        basicProperties.setValidationQuery(ConfigUtils.get("validation-query", "select 1 from dual"));
        basicProperties.setTestOnBorrow(Boolean.getBoolean(ConfigUtils.get("test-on-borrow", "false")));
        basicProperties.setTestOnReturn(Boolean.parseBoolean(ConfigUtils.get("test-on-return", "false")));
        basicProperties.setTestOnIdle(Boolean.parseBoolean(ConfigUtils.get("test-while-idle", "true")));
        basicProperties.setPoolPreparedStatements(Boolean.parseBoolean(ConfigUtils.get("pool-prepared-statements", "true")));
        basicProperties.setMaxOpenPreparedStatements(Integer.parseInt(ConfigUtils.get("max-open-prepared-statements", "50")));
        basicProperties.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(ConfigUtils.get("max-pool-prepared-statement-per-connection-size", "20")));
        basicProperties.setMinEvictableIdleTimeMillis(100000);
        return basicProperties;
    }

    /**
     * 创建druid数据库连接池
     */
    protected DruidDataSource buildDruidDataSource(DruidDataSourceProperties properties){
        if(Objects.isNull(properties)){
            return null;
        }
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setDriverClassName(properties.getDriverClassName());
        druidDataSource.setUrl(properties.getUrl());
        druidDataSource.setUsername(properties.getUsername());
        druidDataSource.setPassword(properties.getPassword());
        druidDataSource.setInitialSize(properties.getInitialSize());
        druidDataSource.setMinIdle(properties.getMinIdle());
        druidDataSource.setMaxActive(properties.getMaxActive());
        druidDataSource.setMaxWait(properties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(properties.getValidationQuery());
        druidDataSource.setTestOnBorrow(properties.isTestOnBorrow());
        druidDataSource.setTestOnReturn(properties.isTestOnReturn());
        druidDataSource.setTestWhileIdle(properties.isTestOnIdle());
        druidDataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
        druidDataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        druidDataSource.setTimeBetweenLogStatsMillis(properties.getTimeBetweenLogStatsMillis());

        return druidDataSource;
    }
}
