package com.itisacat.freemarker.support.enums;

import com.itisacat.freemarker.service.util.EmptyUtils;
import com.itisacat.freemarker.service.util.exception.CcException;

public enum AnimalEnum {
    cat(1, "catService"),
    dog(2, "dogService");

    private Integer type;
    private String serviceName;

    AnimalEnum(Integer type, String serviceName) {
        this.type = type;
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getType() {
        return type;
    }

    public static AnimalEnum validate(Integer type) {
        if (EmptyUtils.isEmpty(type)) {
            throw new CcException(-40001, "没有该类型");
        }

        for (AnimalEnum businessEnum : AnimalEnum.values()) {
            if (businessEnum.getType().equals(type)) {
                return businessEnum;
            }
        }
        throw new CcException(-40001, "没有该类型");
    }
}
