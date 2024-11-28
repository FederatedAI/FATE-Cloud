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
package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.system.Interface.FederatedCloudManagerOriginUserServiceInterface;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerOriginUserDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudManagerOriginUserMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service(value = "default")
public class FederatedCloudManagerOriginUserService implements FederatedCloudManagerOriginUserServiceInterface {

    @Autowired
    FederatedCloudManagerOriginUserMapper federatedCloudManagerOriginUserMapper;

    @Override
    public boolean checkFederatedCloudManagerOriginUser(String username, String password) {
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerOriginUserDoQueryWrapper.eq("name", username).eq("password", md5Password);
        List<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDos = federatedCloudManagerOriginUserMapper.selectList(federatedCloudManagerOriginUserDoQueryWrapper);

        return federatedCloudManagerOriginUserDos.size() > 0;
    }

    @Override
    public boolean findFederatedCloudManagerOriginUser(String username) {
        QueryWrapper<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerOriginUserDoQueryWrapper.eq("name", username);
        List<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDos = federatedCloudManagerOriginUserMapper.selectList(federatedCloudManagerOriginUserDoQueryWrapper);
        return federatedCloudManagerOriginUserDos.size() > 0;

    }


}
