package com.synway.flinkodps.common.exception;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.common.exception
 * @date:2020/4/22
 */
@Data
@ToString
public class DalException extends Exception implements Serializable {
    private static final long serialVersionUID = -3064166380252265938L;
    private int errCode;
    public DalException(int errCode,String errMsg){
        super(errMsg);
        this.errCode = errCode;
    }
}
