package com.webank.ai.fatecloud.Interceptor;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.service.impl.FederatedFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SiteAuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    FederatedFunctionService federatedFunctionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = federatedFunctionService.checkFunctionStatus(Dict.FUNCTIONS[1]);
        if (!result) {
            response.getWriter().write(JSON.toJSONString(new CommonResponse<>(ReturnCodeEnum.FUNCTION_ERROR)));
        }
        return result;


    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
