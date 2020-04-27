package com.synway.flinkodps.odpsupload.dal.jdbc;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.jdbc
 * @date:2019/12/25
 */
@Slf4j
public abstract class JdbcAbstractBase implements JdbcBase, Serializable {
    private static final long serialVersionUID = -8164447376492369827L;

    @Override
    public Connection getConnection() {
        try {
            DataSource dataSource = initDataSource();
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("get connection error:{}", e.getMessage());
            return null;
        }
    }

    @Override
    public void close(Connection con) {
        if (Objects.nonNull(con)) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("close connection error:{}", e.getMessage());
            }
        }
    }

    @Override
    public void close(PreparedStatement ps) {
        if (Objects.nonNull(ps)) {
            try {
                ps.close();
            } catch (SQLException e) {
                log.error("close ps error:{}", e.getMessage());
            }
        }
    }

    @Override
    public void close(ResultSet rs) {
        if (Objects.nonNull(rs)) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("close rs error:{}", e.getMessage());
            }
        }
    }

    @Override
    public boolean execSql(String sql) {
        Connection con = getConnection();
        if (Objects.isNull(con)) {
            return false;
        }

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("sql execute error, sql:{}.{}", sql, e.getMessage());
            return false;
        } finally {
            if (Objects.nonNull(ps)) {
                close(ps);
            }
            if (Objects.nonNull(con)) {
                close(con);
            }
        }

        return true;
    }

    @Override
    public List<Object[]> execBySql(String sql) {
        Connection con = getConnection();
        if (Objects.isNull(con)) {
            return null;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs2List(rs);
        } catch (SQLException e) {
            log.error("sql execute error, sql:{}.{}", sql, e.getMessage());
            return null;
        } finally {
            if (Objects.nonNull(rs)) {
                close(rs);
            }
            if (Objects.nonNull(ps)) {
                close(ps);
            }
            if (Objects.nonNull(con)) {
                close(con);
            }
        }
    }

    @Override
    public <T> List<T> execQuery(String sql, Class<T> clazz) {
        Connection con = getConnection();
        if (Objects.isNull(con)) {
            return null;
        }

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            return rs2Obj(rs, clazz);
        } catch (SQLException e) {
            log.error("sql execute error, sql:{}.{}", sql, e.getMessage());
            return null;
        }
    }

    /**
     * 将sql查询结果转换成list
     */
    protected List<Object[]> rs2List(ResultSet rs) {
        if (Objects.isNull(rs)) {
            return null;
        }

        List<Object[]> list = Lists.newArrayList();
        try {
            int colCount = rs.getMetaData().getColumnCount();
            while (!rs.isClosed() && rs.next()) {
                Object[] obj = new Object[colCount];
                for (int i = 0; i < obj.length; i++) {
                    if (null == rs.getObject(i + 1)) {
                        obj[i] = "";
                    } else {
                        obj[i] = rs.getObject(i + 1);
                    }
                }
                list.add(obj);
            }
        } catch (SQLException e) {
            log.info("convert result to list failed.{}", e.getMessage());
            return null;
        }
        return list;
    }

    private <T> List<T> rs2Obj(ResultSet rs, Class<T> clazz) {
        List<T> result = Lists.newArrayList();

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            //获取总列数
            int count = metaData.getColumnCount();

            while (rs.next()) {
                T newInstance = clazz.newInstance();

                for (int i = 1; i <= count; i++) {
                    //给对象的某个属性赋值
                    String name = metaData.getColumnName(i).toLowerCase();
                    //名格式成java命名格式
                    name = toJavaField(name);// 改变列名格式成java命名格式
                    Class<?> type = null;
                    try {
                        type = clazz.getDeclaredField(name).getType();
                    } catch (NoSuchFieldException e) {
                        log.error("declared field name get error:{}", e.getMessage());
                    }

                    // 首字母大写
                    String substring = name.substring(0, 1);
                    String replace = name.replaceFirst(substring, substring.toUpperCase());
                    Method method = clazz.getMethod("set" + replace, type);

                    /**
                     * 判断读取数据的类型
                     */
                    if (type.isAssignableFrom(String.class)) {
                        method.invoke(newInstance, rs.getString(i));
                    } else if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(Byte.class)) {
                        method.invoke(newInstance, rs.getByte(i));// byte 数据类型是8位、有符号的，以二进制补码表示的整数
                    } else if (type.isAssignableFrom(short.class) || type.isAssignableFrom(Short.class)) {
                        method.invoke(newInstance, rs.getShort(i));// short 数据类型是 16 位、有符号的以二进制补码表示的整数
                    } else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
                        method.invoke(newInstance, rs.getInt(i));// int 数据类型是32位、有符号的以二进制补码表示的整数
                    } else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
                        method.invoke(newInstance, rs.getLong(i));// long 数据类型是 64 位、有符号的以二进制补码表示的整数
                    } else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
                        method.invoke(newInstance, rs.getFloat(i));// float 数据类型是单精度、32位、符合IEEE 754标准的浮点数
                    } else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
                        method.invoke(newInstance, rs.getDouble(i));// double 数据类型是双精度、64 位、符合IEEE 754标准的浮点数
                    } else if (type.isAssignableFrom(BigDecimal.class)) {
                        method.invoke(newInstance, rs.getBigDecimal(i));
                    } else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
                        method.invoke(newInstance, rs.getBoolean(i));// boolean数据类型表示一位的信息
                    } else if (type.isAssignableFrom(Date.class)) {
                        method.invoke(newInstance, rs.getDate(i));
                    }
                }
                result.add(newInstance);
            }
        } catch (SQLException e) {
            log.error("convert rs to obj failed.{}", e.getMessage());
            return null;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            log.error("create new instance error.{}", e.getMessage());
            return null;
        }

        return result;
    }

    /**
     * 数据库命名格式转java命名格式
     *
     * @param str 数据库字段名
     * @return java字段名
     */
    public static String toJavaField(String str) {
        String[] split = str.split("_");
        StringBuilder builder = new StringBuilder();
        builder.append(split[0]);// 拼接第一个字符

        // 如果数组不止一个单词
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                // 去掉下划线，首字母变为大写
                String string = split[i];
                String substring = string.substring(0, 1);
                split[i] = string.replaceFirst(substring, substring.toUpperCase());
                builder.append(split[i]);
            }
        }

        return builder.toString();
    }
}
