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
import javax.servlet.http.HttpSession;

public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    FederatedCloudManagerUserMapper federatedCloudManagerUserMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter().write(JSON.toJSONString(new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_LOGIN_ERROR)));
            return false;
        }
        Object cloudUser = session.getAttribute(Dict.CLOUD_USER);
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
