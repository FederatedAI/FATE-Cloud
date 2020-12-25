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
import com.webank.ai.fatecloud.system.dao.entity.FederatedOrganizationDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedOrganizationMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.dto.RegisteredOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FederatedOrganizationService {
    @Autowired
    FederatedOrganizationMapper federatedOrganizationMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    public int registerFederatedOrganization(FederatedOrganizationRegisterQo federatedOrganizationRegisterQo) {
        List<FederatedOrganizationDo> federatedOrganizationDos = federatedOrganizationMapper.selectList(null);
        if (0 != federatedOrganizationDos.size()) {
            return 0;
        }
        FederatedOrganizationDo federatedOrganizationDo = new FederatedOrganizationDo(federatedOrganizationRegisterQo);
        return federatedOrganizationMapper.insert(federatedOrganizationDo);
    }

    public FederatedOrganizationDto findFederatedOrganization() {
        List<FederatedOrganizationDo> federatedOrganizationDos = federatedOrganizationMapper.selectList(null);
        if (0 != federatedOrganizationDos.size()) {
            return new FederatedOrganizationDto(federatedOrganizationDos.get(0));
        }
        return null;
    }

    public RegisteredOrganizationDto findRegisteredOrganization() {
        RegisteredOrganizationDto registeredOrganizationDto = new RegisteredOrganizationDto();

        FederatedOrganizationDto federatedOrganization = findFederatedOrganization();
        registeredOrganizationDto.setFederatedOrganizationDto(federatedOrganization);

        QueryWrapper<FederatedSiteManagerDo> ew = new QueryWrapper<>();
        ew.eq("status",2);
        Integer total = federatedSiteManagerMapper.selectCount(ew);
        registeredOrganizationDto.setTotal(total);

        return registeredOrganizationDto;
    }
}