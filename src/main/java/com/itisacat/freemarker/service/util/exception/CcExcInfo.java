package com.itisacat.freemarker.service.util.exception;

import lombok.Data;

import java.io.Serializable;

@Data
public class CcExcInfo implements Serializable {
    private Integer errorCode;

    private String msg;

    private String domain;

    public CcExcInfo(){}

    public CcExcInfo(Integer code){
        this.errorCode = code;
        this.msg = "[未定义]";
    }
}
