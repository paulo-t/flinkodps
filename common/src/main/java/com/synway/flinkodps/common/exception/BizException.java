package com.synway.flinkodps.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizeconfig.common.exception
 * @date:2020/1/21
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class BizException extends Exception implements Serializable {
    private static final long serialVersionUID = 4678677204339785220L;
    private int errCode;

    public BizException(int errCode,String errMsg){
        super(errMsg);
        this.errCode = errCode;
    }
}
