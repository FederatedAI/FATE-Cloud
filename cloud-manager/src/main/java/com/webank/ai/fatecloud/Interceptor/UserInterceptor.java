package com.webank.ai.fatecloud.Interceptor;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerUserDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudManagerUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    FederatedCloudManagerUserMapper federatedCloudManagerUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object cloudUser = request.getSession().getAttribute(Dict.CLOUD_USER);
        if (cloudUser == null) {
            response.getWriter().write(JSON.toJSONString(new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_LOGIN_ERROR)));
            return false;
        }

        FederatedCloudManagerUserDo cloudUserObject = (FederatedCloudManagerUserDo) cloudUser;
        Long cloudManagerId = cloudUserObject.getCloudManagerId();
        FederatedCloudManagerUserDo federatedCloudManagerUserDo = federatedCloudManagerUserMapper.selectById(cloudManagerId);
        if (federatedCloudManagerUserDo == null) {
            response.getWriter().write(JSON.toJSONString(new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_LOGIN_ERROR)));
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
