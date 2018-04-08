package com.itisacat.freemarker.service.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    private static final String DATE_STRING_NOT_NULL = "The 'dateString' must not be null!";
    private static final String DATE_NOT_NULL = "The 'date' must not be null!";

    private DateUtil() {

    }

    /**
     * 默认日期格式：yyyy-MM-dd
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 默认时间格式：yyyyMM
     */
    public static final String YYYYMM_DATE_PATTERN = "yyyyMM";

    /**
     * 默认时间格式：yyyyMMdd
     */
    public static final String YYYYMMDD_DATE_PATTERN = "yyyyMMdd";

    /**
     * 默认时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认时间戳格式，到毫秒 yyyy-MM-dd HH:mm:ss SSS
     */
    public static final String DEFAULT_DATEDETAIL_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";

    /**
     * itisacat时间戳格式
     */
    public static final String ITISACAT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * ITISACAT-pass时间戳格式-旧版（这种格式在SimpleDateFormat下解析2017-02-09T17:57:49. 0000000是OK的，在joad下就会比较严格，会出现异常）
     */
    public static final String ITISACAT_DATE_FORMAT_PASS = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * ITISACAT-pass时间戳格式-新版（严格的格式）
     */
    public static final String ITISACAT_DATE_FORMAT_PASS_SSSSSSS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";

    /**
     * 1天折算成毫秒数
     */
    public static final long MILLIS_A_DAY = 24 * 3600 * 1000l;


    public static Long toTimeticks(Date time) {
        return time.getTime();
    }

    /**
     * 获取UTC时间戳（以秒为单位）
     */
    public static int getTimestampInSeconds() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取UTC时间戳（以毫秒为单位）
     */
    public static long getTimestampInMillis() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return cal.getTimeInMillis();
    }
}
