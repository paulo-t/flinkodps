package com.synway.flinkodps.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.common.utils
 * @date:2020/4/26
 */
public class ParseUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    public static int parseInt(Object obj, int defaultVal) {
        try {
            if (Objects.isNull(obj)) {
                return defaultVal;
            }

            if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
                return Integer.parseInt((String) obj);
            }

            return defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static long parseLong(Object obj, long defaultVal) {
        try {
            if (Objects.isNull(obj)) {
                return defaultVal;
            }

            if (obj instanceof String && StringUtils.isNumeric((String) obj)) {
                return Long.parseLong((String) obj);
            }

            return defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static boolean parseBoolen(Object obj, boolean defaultVal) {
        try {
            if (Objects.isNull(obj)) {
                return defaultVal;
            }

            if (obj instanceof String) {
                return Boolean.parseBoolean((String) obj);
            }

            return defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static double parseDouble(Object obj, double defaultVal) {
        try {
            if (Objects.isNull(obj)) {
                return defaultVal;
            }

            if (obj instanceof String) {
                return Double.parseDouble((String) obj);
            }

            return defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }


    public static Date parseDate(String dateStr, Date defaultVal){
        try {
            if (dateStr.length() == 8) {
                return sdf1.parse(dateStr);
            } else if (dateStr.length() == 14) {
                return sdf2.parse(dateStr);
            } else {
                return sdf.parse(dateStr);
            }
        } catch (ParseException e) {
            return defaultVal;
        }
    }
}
