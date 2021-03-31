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
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.qo.DropDownVersionQo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DropDownService {
    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    public List<String> getDropDownVersionList(DropDownVersionQo dropDownVersionQo) {
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.select(dropDownVersionQo.getVersionName()).eq("institutions", dropDownVersionQo.getInstitutions()).groupBy(dropDownVersionQo.getVersionName());

        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        ArrayList<String> versionList = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            if (federatedSiteManagerDo == null) {
                continue;
            }
            if ("fate_version".equals(dropDownVersionQo.getVersionName())) {
                if (StringUtils.isBlank(federatedSiteManagerDo.getFateVersion())) {
                    continue;
                }
                versionList.add(federatedSiteManagerDo.getFateVersion());
            }
            if ("fate_serving_version".equals(dropDownVersionQo.getVersionName())) {
                if (StringUtils.isBlank(federatedSiteManagerDo.getFateServingVersion())) {
                    continue;
                }
                versionList.add(federatedSiteManagerDo.getFateServingVersion());
            }
        }
        return versionList;

    }
}
