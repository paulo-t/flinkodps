package com.synway.flinkodps.biz.aop;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: 日志切面类
 * @date:2020/1/21
 */
@Component
@Aspect
@Slf4j
public class WebLogAspect {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    /**
     * 切点
     */
    @Pointcut("execution(public * com.synway.flinkodps.biz.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * 环绕通知
     */
    @Around("webLog()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getName(); // 获取类名
        String methodName = pjp.getSignature().getName(); // 获取执行的方法名称
        String[] parameterNamesArgs = ((MethodSignature) pjp.getSignature()).getParameterNames(); // 获取参数名称
        Object result; // 定义返回参数
        Object[] args = pjp.getArgs(); // 获取方法参数
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //请求参数
        RequestInfo requestInfo = new RequestInfo();
        //类名
        requestInfo.setClassName(className);
        //方法名
        requestInfo.setMethodName(methodName);
        //请求url
        requestInfo.setUrl(request.getRequestURL().toString());
        //请求ip
        requestInfo.setIp(getIpAddr(request));
        //请求参数
        Map<String, Object> param = Maps.newHashMap();
        // 获取请求参数集合并进行遍历拼接
        for (int i = 0; i < args.length; i++) {
            param.put(parameterNamesArgs[i], (args[i]));
        }
        requestInfo.setParam(param);
        //头信息
        Map<String, Object> header = Maps.newConcurrentMap();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            header.put(key, value);
        }
        requestInfo.setHeader(header);
        long start = System.currentTimeMillis();// 记录开始时间
        requestInfo.setRequestTime(sdf.format(new Date(start)));

        log.info("request:{}", JSON.toJSON(requestInfo));
        result = pjp.proceed();// 执行目标方法

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setUrl(request.getRequestURL().toString());
        responseInfo.setData(result);
        long end = System.currentTimeMillis();
        responseInfo.setResponseTime(sdf.format(new Date(end)));
        responseInfo.setCostTime(end - start);

        log.info("response:{}", JSON.toJSON(responseInfo));
        return result;
    }

    /**
     * @Description: 获取ip
     */
    public String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        // 或者这样也行,对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        return ipAddress;
    }
}
