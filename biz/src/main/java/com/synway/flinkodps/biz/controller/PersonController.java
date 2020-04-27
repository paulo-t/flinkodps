package com.synway.flinkodps.biz.controller;

import com.synway.flinkodps.biz.bus.model.PersonVO;
import com.synway.flinkodps.biz.bus.service.PersonService;
import com.synway.flinkodps.common.exception.BizException;
import com.synway.flinkodps.common.model.BaseResponse;
import com.synway.flinkodps.common.utils.BaseResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.biz.controller
 * @date:2020/4/22
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    @Autowired
    PersonService personService;

    @GetMapping("/getAll")
    public BaseResponse<List<PersonVO>> getAll(){
        try {
            return BaseResponseUtils.success(personService.getAll());
        } catch (BizException e) {
           return  BaseResponseUtils.error(e.getErrCode(),e.getMessage());
        }
    }
}
