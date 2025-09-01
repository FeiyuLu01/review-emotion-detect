package org.emotion.detect.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.emotion.detect.enums.ResponseEnum;

@JsonInclude(value = JsonInclude.Include.ALWAYS)
public class ResponseVo<T> {
    private Integer status;
    private String msg;
    private T data;

    private ResponseVo(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    private ResponseVo(Integer status, T data){
        this.status = status;
        this.data = data;
        this.msg = "success"; // 添加默认消息
    }

    public static <T> ResponseVo<T> success(T data){
        return new ResponseVo<>(ResponseEnum.SUCCESS.getCode(),data);
    }

    public static <T> ResponseVo<T> error(ResponseEnum responseEnum, String msg){
        return new ResponseVo<>(responseEnum.getCode(), msg);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
