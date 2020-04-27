package com.synway.flinkodps.biz.dal.mapper;

import com.synway.flinkodps.biz.dal.dao.PersonDAO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.dal.mapper
 * @date:2020/4/22
 */
public interface PersonMapper {
    @Select("SELECT id,name,age FROM person")
    List<PersonDAO> selectAll();
}
