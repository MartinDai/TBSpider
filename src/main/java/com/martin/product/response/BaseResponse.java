package com.martin.product.response;


/**
 * 基础响应结果类
 */
public class BaseResponse<T> {

    /**
     * 错误信息
     */
    private String message;
    /**
     * 是否响应成功
     */
    private boolean success;
    /**
     * 响应数据
     */
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(T data) {
        success = true;
        this.data = data;
    }

    public static BaseResponse<Void> success() {
        BaseResponse<Void> response = new BaseResponse<>();
        response.success = true;
        return response;
    }

    public static <T> BaseResponse<T> success(T t) {
        return new BaseResponse<>(t);
    }

    public static BaseResponse<Void> fail(String message) {
        BaseResponse<Void> response = new BaseResponse<>();
        response.message = message;
        return response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
