package com.synway.flinkodps.odpsupload.dal;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: 数据访问接口定义
 * @date:2019/12/25
 */
public interface DbBase {
    /**
     * 执行SQL语句不带返回值
     * @return
     */
    boolean execSql(String sql);
    /**
     *查询语句
     */
    <T>List<T> execQuery(String sql,Class<T> c);
    /**
     * 执行SQL语句带返回值
     * @return
     */
    List<Object[]> execBySql(String sql);
}
