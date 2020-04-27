package com.synway.flinkodps.common.utils;


import com.synway.flinkodps.common.enums.ResponseStatus;
import com.synway.flinkodps.common.model.BaseResponse;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizeconfig.common.utils
 * @date:2020/1/21
 */
public class BaseResponseUtils {
    /**
     * 成功
     */
    public static<T> BaseResponse success(T data){
        BaseResponse<T> ret = new BaseResponse();
        ret.setCode(ResponseStatus.SUCCESS.getCode());
        ret.setMessage("请求成功");
        ret.setData(data);

        return ret;
    }

    /**
     * 错误
     */
    public static BaseResponse error(int code,String msg){
        BaseResponse ret = new BaseResponse();
        ret.setCode(code);
        ret.setMessage(msg);
        return ret;
    }

    /**
     * 错误
     */
    public static BaseResponse fail(){
        BaseResponse ret = new BaseResponse();
        ret.setCode(555);
        ret.setMessage("系统内部错误");
        return ret;
    }
}
