package com.synway.flinkodps.biz.enums;

import lombok.Getter;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.enums
 * @date:2020/4/23
 */
@Getter
public enum ObjectType {
    TABLE(1,"行为库"),
    CT(2,"粗提库"),
    ;
    /**
     * 类型
     */
    private int type;
    /**
     * 名字
     */
    private String name;

    ObjectType(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
