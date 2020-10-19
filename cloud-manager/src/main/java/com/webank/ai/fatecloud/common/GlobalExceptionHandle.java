package com.webank.ai.fatecloud.common;

import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(Exception.class)
    public CommonResponse globalExceptionHandle(Exception e) {
        log.error("global error:",e);
        return new CommonResponse<>(ReturnCodeEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResponse parametersHandle(HttpServletRequest req, IllegalArgumentException e) {
        log.error("parameter error", e);
        return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public CommonResponse nullPointerHandle(HttpServletRequest req, NullPointerException e) {
        log.error("null pointer error", e);
        return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
    }

    @ExceptionHandler(OutOfMemoryError.class)
    public CommonResponse memoryError(HttpServletRequest req, OutOfMemoryError e) {
        log.error("memory error", e);
        return new CommonResponse<>(ReturnCodeEnum.MEMORY_ERROR);
    }

}
