package com.synway.flinkodps.biz.bus.service;

import com.synway.flinkodps.biz.bus.model.PersonVO;
import com.synway.flinkodps.common.exception.BizException;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.bus.service
 * @date:2020/4/22
 */
public interface PersonService {
    List<PersonVO> getAll() throws BizException;
}
