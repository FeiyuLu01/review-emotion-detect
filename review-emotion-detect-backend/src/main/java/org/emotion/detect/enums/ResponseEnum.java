package org.emotion.detect.enums;

import lombok.Getter;

/**
 * Enum for different response status codes and messages
 * Used to standardize API responses across the application
 */
public enum ResponseEnum {
    /** Success response */
    SUCCESS(200,"Successful"),
    /** General server error */
    ERROR(-1,"Server Error"),
    ;

    /** Response status code */
    final Integer code;
    /** Response description message */
    final String desc;

    /**
     * Constructor for response enum
     * @param code status code
     * @param desc description message
     */
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
