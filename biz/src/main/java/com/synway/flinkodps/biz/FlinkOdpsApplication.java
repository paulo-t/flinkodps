package com.synway.flinkodps.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.bus
 * @date:2020/4/21
 */
@SpringBootApplication
@MapperScan("com.synway.flinkodps.biz.dal.mapper")
public class FlinkOdpsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlinkOdpsApplication.class,args);
    }
}
