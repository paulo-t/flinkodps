package com.synway.flinkodps.odpsupload.dal.jdbc.druid.properties;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.jdbc.druid.properties
 * @date:2019/12/26
 */
@Data
@ToString
public class DruidDataSourceProperties implements Serializable {
    private static final long serialVersionUID = -3882541752395259209L;
    /**
     * 驱动
     */
    private String driverClassName;
    /**
     * 数据库连接地址
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 初始连接数量
     */
    private int initialSize;
    /**
     * 最小连接数量
     */
    private int minIdle;
    /**
     * 大连接数
     */
    private int maxActive;
    /**
     * 获取连接最大等待时间
     */
    private int maxWait;
    /**
     * 检测空闲连接间隔时间
     */
    private long timeBetweenEvictionRunsMillis;
    /**
     * 测查询
     */
    private String validationQuery;
    /**
     * 获取连接时检测是否有效 开启会影响性能
     */
    private boolean testOnBorrow;
    /**
     * 归还连接时检测是否有效 开启会影响性能
     */
    private boolean testOnReturn;
    /**
     * 取连接时检测是否有效 当间隔时间大于time-between-eviction-runs-millis检测连接是否有效
     */
    private boolean testOnIdle;
    /**
     * 开启查询缓存
     */
    private boolean poolPreparedStatements;
    /**
     * 缓存数量
     */
    private int maxOpenPreparedStatements;
    /**
     * 一个连接缓存数量
     */
    private int maxPoolPreparedStatementPerConnectionSize;
    private long timeBetweenLogStatsMillis;
    private String proxyFilters;
    private long minEvictableIdleTimeMillis;
}
