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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.EncryptUtil;
import com.webank.ai.fatecloud.common.util.KeyAndSecretGenerate;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedOrganizationDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedFateManagerUserMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedOrganizationMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserPagedQo;
import com.webank.ai.fatecloud.system.pojo.qo.InstitutionCheckQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FederatedFateManagerUserService {
    @Autowired
    FederatedFateManagerUserMapper federatedFateManagerUserMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedOrganizationMapper federatedOrganizationMapper;


    @Value(value = "${cloud-manager.ip}")
    String ip;

    @Value(value = "${server.servlet.context-path}")
    String prefix;

    public String addFateManagerUser(FateManagerUserAddQo fateManagerUserAddQo) throws UnsupportedEncodingException {
        FederatedFateManagerUserDo federatedFateManagerUserDo = new FederatedFateManagerUserDo();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        federatedFateManagerUserDo.setFateManagerId(uuid);

        federatedFateManagerUserDo.setInstitutions(fateManagerUserAddQo.getInstitutions());

        String appKey = KeyAndSecretGenerate.getAppKey();
        String appSecret = KeyAndSecretGenerate.getAppSecret(appKey);
        HashMap<String, String> secretMap = new HashMap<>();
        secretMap.put("key", appKey);
        secretMap.put("secret", appSecret);
        String secretInfo = JSON.toJSONString(secretMap);
        federatedFateManagerUserDo.setSecretInfo(secretInfo);


        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("fateManagerUser", federatedFateManagerUserDo);

        QueryWrapper<FederatedOrganizationDo> query = new QueryWrapper<>();
        query.select("id", "name","institution").eq("status", 1);
        FederatedOrganizationDo federatedOrganizationDo = federatedOrganizationMapper.selectOne(query);
        stringObjectHashMap.put("federatedOrganization", federatedOrganizationDo);

        String userInfo = JSON.toJSONString(stringObjectHashMap);
        String fateUserRegistrationUrl = ip + prefix + "/api/user/activate" + "?st=" + userInfo.replace("\"{", "{").replace("}\"", "}").replace("\\", "").replace("\"", "\\\"");
        String encodedFateUserRegistrationUrl = EncryptUtil.encode(fateUserRegistrationUrl);

        federatedFateManagerUserDo.setRegistrationLink(encodedFateUserRegistrationUrl);

        federatedFateManagerUserDo.setCreator(fateManagerUserAddQo.getCreator());

        federatedFateManagerUserMapper.insert(federatedFateManagerUserDo);

        return encodedFateUserRegistrationUrl;

    }

    public boolean checkInstitution(InstitutionCheckQo institutionCheckQo) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("institutions", institutionCheckQo.getInstitution());

        Integer count = federatedFateManagerUserMapper.selectCount(federatedFateManagerUserDoQueryWrapper);

        return count > 0;

    }

    public void activateFateManagerUser(HttpServletRequest httpServletRequest) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("fate_manager_id", httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID));

        //activate fate manager user
        FederatedFateManagerUserDo federatedFateManagerUserDo = new FederatedFateManagerUserDo();
        federatedFateManagerUserDo.setStatus(2);
        federatedFateManagerUserMapper.update(federatedFateManagerUserDo, federatedFateManagerUserDoQueryWrapper);


    }

    public FederatedFateManagerUserDo findFateManagerUser(String fateManagerId) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("fate_manager_id", fateManagerId);
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);
        if (federatedFateManagerUserDos.size() > 0) {
            return federatedFateManagerUserDos.get(0);
        } else {
            return null;
        }
    }


    public void deleteFateManagerUser(FateManagerUserDeleteQo fateManagerUserDeleteQo) {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.eq("fate_manager_id", fateManagerUserDeleteQo.getFateManagerId());
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);

        federatedFateManagerUserMapper.delete(federatedFateManagerUserDoQueryWrapper);

        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<>();
        federatedSiteManagerDoQueryWrapper.eq("institutions", federatedFateManagerUserDos.get(0).getInstitutions());

        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setStatus(3);
        federatedSiteManagerMapper.update(federatedSiteManagerDo, federatedSiteManagerDoQueryWrapper);

    }

    public List<String> findAllFateManagerUserInstitutions() {
        QueryWrapper<FederatedFateManagerUserDo> federatedFateManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedFateManagerUserDoQueryWrapper.select("institutions").eq("status",2);
        List<FederatedFateManagerUserDo> federatedFateManagerUserDos = federatedFateManagerUserMapper.selectList(federatedFateManagerUserDoQueryWrapper);

        LinkedList<String> institutionsList = new LinkedList<>();
        for (FederatedFateManagerUserDo federatedFateManagerUserDo : federatedFateManagerUserDos) {
            institutionsList.add(federatedFateManagerUserDo.getInstitutions());
        }
        return institutionsList;
    }

    public PageBean<FederatedFateManagerUserDo> findPagedFateManagerUser(FateManagerUserPagedQo fateManagerUserPagedQo) {
        QueryWrapper<FederatedFateManagerUserDo> federatedCloudManagerUserDoQueryWrapper = new QueryWrapper<>();

        if (fateManagerUserPagedQo.getInstitutions() != null) {
            federatedCloudManagerUserDoQueryWrapper.like("institutions", fateManagerUserPagedQo.getInstitutions());
            fateManagerUserPagedQo.setInstitutions("%" + fateManagerUserPagedQo.getInstitutions() + "%");

        }

        Integer count = federatedFateManagerUserMapper.selectCount(federatedCloudManagerUserDoQueryWrapper);
        PageBean<FederatedFateManagerUserDo> userListBean = new PageBean<>(fateManagerUserPagedQo.getPageNum(), fateManagerUserPagedQo.getPageSize(), count);
        long startIndex = userListBean.getStartIndex();

        List<FederatedFateManagerUserDo> pagedCloudUser = federatedFateManagerUserMapper.findPagedFateManagerUser(fateManagerUserPagedQo, startIndex);

        userListBean.setList(pagedCloudUser);
        return userListBean;
    }
}
