package com.synway.flinkodps.biz.bus.service.impl;

import com.synway.flinkodps.biz.bus.mapper.PersonCMapper;
import com.synway.flinkodps.biz.bus.model.PersonVO;
import com.synway.flinkodps.biz.bus.service.PersonService;
import com.synway.flinkodps.biz.dal.dao.PersonDAO;
import com.synway.flinkodps.biz.dal.mapper.PersonMapper;
import com.synway.flinkodps.common.exception.BizException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.bus.service.impl
 * @date:2020/4/22
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Resource
    PersonMapper personMapper;
    @Resource
    PersonCMapper personCMapper;
    @Override
    public List<PersonVO> getAll() throws BizException {
        List<PersonDAO> personList = personMapper.selectAll();
        return personCMapper.daos2vos(personList);
    }
}
