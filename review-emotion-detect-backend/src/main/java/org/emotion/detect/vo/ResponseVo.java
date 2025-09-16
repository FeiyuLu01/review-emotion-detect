package org.emotion.detect.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.emotion.detect.enums.ResponseEnum;

/**
 * Generic response wrapper for API responses
 * Provides a consistent format for all API responses with status, message, and data
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseVo<T> {
    /** Response status code */
    private Integer status;
    /** Response message */
    private String msg;
    /** Response data payload */
    private T data;

    /**
     * Private constructor for error responses with message
     * @param status status code
     * @param msg error message
     */
    private ResponseVo(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    /**
     * Private constructor for success responses with data
     * @param status status code
     * @param data response data
     */
    private ResponseVo(Integer status, T data){
        this.status = status;
        this.data = data;
    }

    /**
     * Create a successful response with data
     * @param data the data to return
     * @return ResponseVo with success status and data
     */
    public static <T> ResponseVo<T> success(T data){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),data);
    }

    /**
     * Create an error response with custom message
     * @param responseEnum the response enum containing error status
     * @param msg custom error message
     * @return ResponseVo with error status and custom message
     */
    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg){
        return new ResponseVo<>(responseEnum.getCode(),msg);
    }
}
