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
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.FateManagerUserPagedQo;
import com.webank.ai.fatecloud.system.pojo.qo.InstitutionCheckQo;
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

    public CommonResponse<String> addFateManagerUser(FateManagerUserAddQo fateManagerUserAddQo) throws UnsupportedEncodingException {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(fateManagerUserAddQo.getInstitutions(), fateManagerUserAddQo.getCreator()));
        Preconditions.checkArgument(CheckInstitutionsUtil.checkPath(fateManagerUserAddQo.getInstitutions(),fateManagerUserAddQo.getCreator()));

        CommonResponse<Boolean> booleanCommonResponse = checkInstitution(new InstitutionCheckQo(fateManagerUserAddQo.getInstitutions()));
        if (booleanCommonResponse.getData()) {
            return new CommonResponse<>(ReturnCodeEnum.FATE_MANAGER_USER_ERROR);
        }
        String registrationUrl = federatedFateManagerUserService.addFateManagerUser(fateManagerUserAddQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, registrationUrl);

    }

    public CommonResponse<Boolean> checkInstitution(InstitutionCheckQo institutionCheckQo) {

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedFateManagerUserService.checkInstitution(institutionCheckQo));

    }

    public CommonResponse activateFateManagerUser(HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_MANAGER_USER, new int[]{1}, null);

        if (result) {

            federatedFateManagerUserService.activateFateManagerUser(httpServletRequest);
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        }
        return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
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
