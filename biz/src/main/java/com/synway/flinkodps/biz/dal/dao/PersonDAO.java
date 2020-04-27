package com.synway.flinkodps.biz.dal.dao;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.dal.dao
 * @date:2020/4/22
 */
@Data
@ToString
public class PersonDAO implements Serializable {
    private static final long serialVersionUID = 3192322834042364968L;
    /**
     * id
     */
    private long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 年龄
     */
    private int age;
}
