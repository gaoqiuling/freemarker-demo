package com.itisacat.freemarker.service.util.exception;

import com.itisacat.freemarker.service.util.EmptyUtils;
import com.itisacat.freemarker.service.util.property.BaseProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CcException extends AppException {
    @Getter
    private String moduleName;


    private static String defaultModule = "";
    private static Map<Integer,String> errorList = new HashMap<>();

    static {
        defaultModule = BaseProperties.getProperty("default.application.name",String.class,"");
        //读取Error列表数据
        errorList = new HashMap<>();
    }

    public CcException(String moduleName, Integer errorCode, String msg, Object returnObject){
        super(errorCode,msg,returnObject);
        this.moduleName = moduleName;
    }

    public CcException(Integer errorCode, String msg){
        this(defaultModule,errorCode,msg,null);
    }

    public CcException(Integer errorCode){
        this(defaultModule,errorCode,errorList.get(errorCode),null);
    }


    /**
     * 统一转换异常消息
     * @param ex
     * @return
     */
    public static SysException exportException(Exception ex){
        SysException exception = null;
        Boolean flag = false;

        if(
                ex instanceof NumberFormatException
                        ||
                        ex instanceof com.alibaba.fastjson.JSONException
                        ||
                        ex instanceof org.springframework.validation.BindException
                ){
            exception = new SysException(-40001, "参数错误~~~");
        }else if(ex instanceof org.springframework.web.method.annotation.MethodArgumentTypeMismatchException){
            exception = new SysException(-40402, "请求数据不存在");
        }
        else if(ex instanceof SysException || ex instanceof AppException ||ex instanceof CcException){
            if(ex instanceof SysException){
                SysException sysEx = (SysException)ex;
                exception = new SysException(sysEx.getCode(), sysEx.getMessage());
            }else if(ex instanceof AppException ||ex instanceof CcException){
                if(ex instanceof CcException) {
                    CcException appEx = (CcException) ex;
                    exception = new SysException(
                            appEx.getCode(),
                            EmptyUtils.isEmpty(
                                    appEx.getMessage())? CcExceptionConfig.getError(appEx.getCode()).getMsg():appEx.getMessage(),
                            appEx.getReturnObj()
                    );
                }else{
                    AppException appEx = (AppException) ex;
                    exception = new SysException(appEx.getCode(), appEx.getMessage(), appEx.getReturnObj());
                }
            }
        }else{
            exception = new SysException(-40000, String.format("[系统发生未知错误] - %s",ex.getMessage()));
            log.error("",ex);
            flag = true;
        }
//
        if(!(ex instanceof SysException || ex instanceof AppException)) {
            if(flag) {
                log.error("", ex);
            }
        }

        return exception;
    }
}
