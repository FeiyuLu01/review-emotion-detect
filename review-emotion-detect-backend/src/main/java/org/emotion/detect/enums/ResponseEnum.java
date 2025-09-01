package org.emotion.detect.enums;

import lombok.Getter;

public enum ResponseEnum {
    SUCCESS(200,"Successful"),
    SUCCESS_OUT(5,"Logout Successful"),
    ERROR(-1,"Server Error"),
    PARAM_ERROR(3,"Parameter Error"),
    ;


    final Integer code;
    final String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
