package com.synway.flinkodps.odpsupload.dal.jdbc;


import com.synway.flinkodps.common.exception.DalException;
import com.synway.flinkodps.odpsupload.dal.DbBase;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.jdbc
 * @date:2019/12/25
 */
public interface JdbcBase extends DbBase {
    /**
     * 初始化数据库连接池
     */
    DataSource initDataSource();
    /**
     * 获取数据库连接
     */
    Connection getConnection();
    /**
     * 将连接归还到数据库连接池
     */
    void close(Connection con);
    /**
     * 查询关闭
     */
    void close(PreparedStatement ps);
    /**
     * 查询结果关闭
     */
    void close(ResultSet rs);
}
