package com.martin.product.advice;

import com.martin.product.response.BaseResponse;
import com.martin.product.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一处理异常
 */
@ControllerAdvice
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResponse<String> handleException(HttpServletRequest request, Exception e) {
        BaseResponse<String> response = new BaseResponse<>();
        if (e instanceof IllegalArgumentException) {
            BaseResponse.fail(e.getMessage());
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            BaseResponse.fail("不支持的请求方式");
        } else {
            logger.error(LogUtil.buildLog("请求出现异常", request.getRequestURI(), request.getParameterMap()), e);
            BaseResponse.fail("服务器未知异常");
        }

        return response;
    }

}
