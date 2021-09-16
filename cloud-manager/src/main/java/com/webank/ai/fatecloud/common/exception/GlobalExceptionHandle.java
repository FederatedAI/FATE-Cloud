/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.common.exception;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    @ExceptionHandler(Exception.class)
    public CommonResponse globalExceptionHandle(Exception e) {
        log.error("global error:", e);
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

    @ExceptionHandler(LogicException.class)
    public CommonResponse<Object> logicExceptionHandler(LogicException e) {
        log.error("logic error", e);
        return new CommonResponse<>(e.getReturnCode(), e.getData());
    }

    @ExceptionHandler(OutOfMemoryError.class)
    public CommonResponse memoryError(HttpServletRequest req, OutOfMemoryError e) {
        log.error("memory error", e);
        return new CommonResponse<>(ReturnCodeEnum.MEMORY_ERROR);
    }

    @ExceptionHandler(RestClientException.class)
    public CommonResponse memoryError(RestClientException e) {
        log.error("invoke error", e);
        return new CommonResponse<>(ReturnCodeEnum.INVOKE_CONNECT_ERROR);
    }

}
