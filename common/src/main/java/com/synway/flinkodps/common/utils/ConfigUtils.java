package com.synway.flinkodps.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 配置文件读取
 *
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.common.utils
 * @date:2019/12/25
 */
public class ConfigUtils implements Serializable {

    private static final long serialVersionUID = 4966784236958849229L;

    protected static final Map<String, String> config;

    static {
        InputStream resourceAsStream = ConfigUtils.class.getResourceAsStream("/application.properties");
        if(Objects.isNull(resourceAsStream)){
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Properties props = new Properties();
        try {
            props.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = (Map)props;
    }


    public static String get(String key, String def) {
        return StringUtils.isEmpty(config.get(key)) ? def : config.get(key);
    }
}
