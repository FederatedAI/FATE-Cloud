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
package com.webank.ai.fatecloud.system.service.facade;

import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.ObjectUtil;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.dto.RegisteredOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedOrganizationService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class FederatedOrganizationServiceFacade {
    @Autowired
    FederatedOrganizationService federatedOrganizationService;

    @Autowired
    CheckSignature checkSignature;

    @Autowired
    FederatedSiteManagerService federatedSiteManagerService;

    public CommonResponse registerFederatedOrganization(FederatedOrganizationRegisterQo federatedOrganizationRegisterQo) {
        if (0 == federatedOrganizationService.registerFederatedOrganization(federatedOrganizationRegisterQo)) {
            return new CommonResponse(ReturnCodeEnum.REGISTER_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<FederatedOrganizationDto> findFederatedOrganization() {
        FederatedOrganizationDto federatedOrganization = federatedOrganizationService.findFederatedOrganization();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedOrganization);
    }

    public CommonResponse<RegisteredOrganizationDto> findRegisteredOrganization(HttpServletRequest httpServletRequest) {
//        boolean result = checkSignature.checkSignature(httpServletRequest, "",2,3);

//        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_SITE_USER, new int[]{2}, 2,3);
//        if (!result) {
//            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
//        }

        String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
        boolean result;
        if (StringUtils.isNotBlank(fateManagerUserId)) {
            if (ObjectUtil.isEmpty(httpServletRequest.getHeader(Dict.PARTY_ID))) {
                result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_MANAGER_USER, new int[]{2}, 1, 2, 3);
            }else{
                result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_SITE_USER, new int[]{2}, 1, 2, 3);
            }
        } else {
            result = checkSignature.checkSignature(httpServletRequest, "", 2, 3);
        }
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        RegisteredOrganizationDto registeredOrganization = federatedOrganizationService.findRegisteredOrganization();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, registeredOrganization);
    }
}
