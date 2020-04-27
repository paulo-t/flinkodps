package com.synway.flinkodps.biz.bus.mapper;

import com.synway.flinkodps.biz.bus.model.PersonVO;
import com.synway.flinkodps.biz.dal.dao.PersonDAO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.bus.mapper
 * @date:2020/4/22
 */
@Mapper(componentModel = "spring",nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface PersonCMapper {
    List<PersonVO>  daos2vos(List<PersonDAO> personList);
}
