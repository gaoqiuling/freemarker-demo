/*
 * Copyright 1999-2017 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.itisacat.freemarker.service.util.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;
import com.itisacat.freemarker.service.util.JsonUtil;
import com.itisacat.freemarker.service.util.property.BaseProperties;
import com.itisacat.freemarker.support.enums.NumberEnum;
import org.joda.time.DateTime;
import  org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateCodec extends AbstractDateDeserializer implements ObjectSerializer, ObjectDeserializer {

    public static final DateCodec instance = new DateCodec();

    private Map<String, String> dateFormatPattern = new LinkedHashMap<>();


    public DateCodec() {
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{4}",
                "yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}",
                "yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}", "yyyy-MM-dd'T'HH:mm:ss");
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{0,7}\\+[0-9]{4}",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{0,7}\\+[0-9]{2}:[0-9]{2}",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
        dateFormatPattern.put("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{0,7}",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
       
        String requestDateformat = BaseProperties.getString("rest.request.dateformat");
        if (requestDateformat != null && requestDateformat.length() > 0) {
            JsonUtil.json2Map(requestDateformat).forEach((k, v) -> dateFormatPattern.put(k, (String) v));
        }
    }

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        Date date;
        if (object instanceof Date) {
            date = (Date) object;
        } else {
            date = TypeUtils.castToDate(object);
        }

        if (out.isEnabled(SerializerFeature.WriteDateUseDateFormat)) {
            DateFormat format = serializer.getDateFormat();
            if (format == null) {
                format = new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT, JSON.defaultLocale);
                format.setTimeZone(JSON.defaultTimeZone);
            }
            String text = format.format(date);
            out.writeString(text);
            return;
        }

        if (out.isEnabled(SerializerFeature.WriteClassName)) {
            if (object.getClass() != fieldType) {
                if (object.getClass() == Date.class) {
                    out.write("new Date(");
                    out.writeLong(((Date) object).getTime());
                    out.write(')');
                } else {
                    out.write('{');
                    out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
                    serializer.write(object.getClass().getName());
                    out.writeFieldValue(',', "val", ((Date) object).getTime());
                    out.write('}');
                }
                return;
            }
        }

        long time = date.getTime();
        if (out.isEnabled(SerializerFeature.UseISO8601DateFormat)) {
            char quote = out.isEnabled(SerializerFeature.UseSingleQuotes) ? '\'' : '\"';
            out.write(quote);

            Calendar calendar = Calendar.getInstance(JSON.defaultTimeZone, JSON.defaultLocale);
            calendar.setTimeInMillis(time);
            out.write(dateFormat(calendar));
            int timeZone = calendar.getTimeZone().getRawOffset() / (3600 * 1000);
            if (timeZone == 0) {
                out.write('Z');
            } else {
                if (timeZone > NumberEnum.ZERO.getCode()) {
                    out.append('+').append(String.format("%02d", timeZone));
                } else {
                    out.append('-').append(String.format("%02d", -timeZone));
                }
                out.append(":00");
            }

            out.write(quote);
        } else {
            out.writeLong(time);
        }
    }

    private char[] dateFormat(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + NumberEnum.ONE.getCode();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);

        char[] buf;
        if (millis != NumberEnum.ZERO.getCode()) {
            buf = "0000-00-00T00:00:00.000".toCharArray();
            IOUtils.getChars(millis, NumberEnum.TWENTYTHREE.getCode(), buf);
            commonChars(year, month, day, hour, minute, second, buf);

        } else {
            if (second == NumberEnum.ZERO.getCode() && minute == NumberEnum.ZERO.getCode()
                    && hour == NumberEnum.ZERO.getCode()) {
                buf = "0000-00-00".toCharArray();
                IOUtils.getChars(day, NumberEnum.TEN.getCode(), buf);
                IOUtils.getChars(month, NumberEnum.SEVEN.getCode(), buf);
                IOUtils.getChars(year, NumberEnum.FOUR.getCode(), buf);
            } else {
                buf = "0000-00-00T00:00:00".toCharArray();
                commonChars(year, month, day, hour, minute, second, buf);
            }
        }
        return buf;
    }

    private void commonChars(int year, int month, int day, int hour, int minute, int second, char[] buf) {
        IOUtils.getChars(second, NumberEnum.NINETEEN.getCode(), buf);
        IOUtils.getChars(minute, NumberEnum.SIXTEEN.getCode(), buf);
        IOUtils.getChars(hour, NumberEnum.THIRTEEN.getCode(), buf);
        IOUtils.getChars(day, NumberEnum.TEN.getCode(), buf);
        IOUtils.getChars(month, NumberEnum.SEVEN.getCode(), buf);
        IOUtils.getChars(year, NumberEnum.FOUR.getCode(), buf);
    }

    @SuppressWarnings("unchecked")
    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {

        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            return (T) val;
        } else if (val instanceof Number) {
            return (T) new Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }

            int strValLength = strVal.length();
            if (strValLength == parser.getDateFomartPattern().length()) {
                DateFormat dateFormat = parser.getDateFormat();
                try {
                    return (T) dateFormat.parse(strVal);
                } catch (ParseException e) {
                    // skip
                }
            }
            for (Map.Entry<String, String> entry : dateFormatPattern.entrySet()) {
                if (checkDataFormart(entry.getKey(), strVal)) {
                    return (T) DateTime.parse(strVal, DateTimeFormat.forPattern(entry.getValue())).toDate();
                }
            }

            JSONScanner dateLexer = new JSONScanner(strVal);
            try {
                if (dateLexer.scanISO8601DateIfMatch(false)) {
                    Calendar calendar = dateLexer.getCalendar();

                    if (clazz == Calendar.class) {
                        return (T) calendar;
                    }

                    return (T) calendar.getTime();
                }
            } finally {
                dateLexer.close();
            }

            if (strVal.startsWith("/Date(") && strVal.endsWith(")/")) {
                String dotnetDateStr =
                        strVal.substring(NumberEnum.SIX.getCode(), strVal.length() - NumberEnum.TWO.getCode());
                strVal = dotnetDateStr;
            }

            long longVal = Long.parseLong(strVal);
            return (T) new Date(longVal);
        }

        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }


    private boolean checkDataFormart(String regEx, String checkData) {
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(checkData);
        // 字符串是否与正则表达式相匹配
        return matcher.matches();
    }
    
}
