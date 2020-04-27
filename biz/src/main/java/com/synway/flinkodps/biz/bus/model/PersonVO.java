package com.synway.flinkodps.biz.bus.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.bus.model
 * @date:2020/4/22
 */
@Data
@ToString
public class PersonVO implements Serializable {
    private static final long serialVersionUID = 7508404779838794503L;
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
