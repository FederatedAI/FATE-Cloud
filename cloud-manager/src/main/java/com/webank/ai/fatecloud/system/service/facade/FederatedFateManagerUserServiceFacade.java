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

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.*;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.CheckInstitutionsUtil;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedFateManagerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@Slf4j
public class FederatedFateManagerUserServiceFacade {
    @Autowired
    FederatedFateManagerUserService federatedFateManagerUserService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse<FederatedFateManagerUserDo> addFateManagerUser(FateManagerUserAddQo fateManagerUserAddQo) throws UnsupportedEncodingException {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(fateManagerUserAddQo.getInstitutions(), fateManagerUserAddQo.getCreator(), fateManagerUserAddQo.getProtocol()));
        Preconditions.checkArgument(CheckInstitutionsUtil.checkPath(fateManagerUserAddQo.getInstitutions(), fateManagerUserAddQo.getCreator()));

        CommonResponse<Boolean> booleanCommonResponse = checkInstitution(new InstitutionCheckQo(fateManagerUserAddQo.getInstitutions()));
        if (booleanCommonResponse.getData()) {
            return new CommonResponse<>(ReturnCodeEnum.FATE_MANAGER_USER_ERROR);
        }
        FederatedFateManagerUserDo federatedFateManagerUserDo = federatedFateManagerUserService.addFateManagerUser(fateManagerUserAddQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedFateManagerUserDo);

    }

    public CommonResponse<FederatedFateManagerUserDo> updateFateManagerUser(FateManagerUserUpdateQo fateManagerUserUpdateQo) throws UnsupportedEncodingException {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(fateManagerUserUpdateQo.getInstitution(), fateManagerUserUpdateQo.getCreator(), fateManagerUserUpdateQo.getProtocol(), fateManagerUserUpdateQo.getFateManagerId()));
        Preconditions.checkArgument(CheckInstitutionsUtil.checkPath(fateManagerUserUpdateQo.getInstitution(), fateManagerUserUpdateQo.getCreator(), fateManagerUserUpdateQo.getFateManagerId()));

        boolean institutionResult = federatedFateManagerUserService.checkUpdateInstitution(fateManagerUserUpdateQo);
        if (institutionResult) {
            return new CommonResponse<>(ReturnCodeEnum.FATE_MANAGER_USER_ERROR);
        }
        boolean statusResult = federatedFateManagerUserService.checkStatus(fateManagerUserUpdateQo);

        if (!statusResult) {
            return new CommonResponse<>(ReturnCodeEnum.FATE_MANAGER_STATUS_ERROR);

        }
        FederatedFateManagerUserDo federatedFateManagerUserDo = federatedFateManagerUserService.updateFateManagerUser(fateManagerUserUpdateQo);

        return new CommonResponse<FederatedFateManagerUserDo>(ReturnCodeEnum.SUCCESS, federatedFateManagerUserDo);

    }

    public CommonResponse<Boolean> checkInstitution(InstitutionCheckQo institutionCheckQo) {

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedFateManagerUserService.checkInstitution(institutionCheckQo));

    }

    public CommonResponse activateFateManagerUser(SiteActivateQo siteActivateQo,HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_MANAGER_USER, new int[]{1}, null);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        boolean urlResult = federatedFateManagerUserService.checkUrl(siteActivateQo,httpServletRequest);
        if(!urlResult){
            return new CommonResponse<>(ReturnCodeEnum.FATE_MANAGER_URL_ERROR);

        }

        federatedFateManagerUserService.activateFateManagerUser(httpServletRequest);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }


    public CommonResponse deleteFateManagerUser(FateManagerUserDeleteQo fateManagerUserDeleteQo) {
        federatedFateManagerUserService.deleteFateManagerUser(fateManagerUserDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<List<String>> findAllFateManagerUserInstitutions() {

        List<String> allFateManagerUser = federatedFateManagerUserService.findAllFateManagerUserInstitutions();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, allFateManagerUser);
    }

    public CommonResponse<PageBean<FederatedFateManagerUserDo>> findPagedFateManagerUser(FateManagerUserPagedQo fateManagerUserPagedQo) {

        PageBean<FederatedFateManagerUserDo> pagedFateManagerUser = federatedFateManagerUserService.findPagedFateManagerUser(fateManagerUserPagedQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedFateManagerUser);
    }
}
