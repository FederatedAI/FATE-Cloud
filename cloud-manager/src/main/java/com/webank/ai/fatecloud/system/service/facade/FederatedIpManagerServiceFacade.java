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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedIpManagerDo;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerAcceptDto;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerListDto;
import com.webank.ai.fatecloud.system.pojo.dto.IpManagerQueryDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedFateManagerUserService;
import com.webank.ai.fatecloud.system.service.impl.FederatedIpManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@Service
public class FederatedIpManagerServiceFacade {

    @Autowired
    FederatedIpManagerService federatedIpManagerService;

    @Autowired
    FederatedFateManagerUserService federatedFateManagerUserService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse<PageBean<IpManagerListDto>> getIpList(IpManagerListQo ipManagerListQo) {
        PageBean<IpManagerListDto> ipManagerListDtoPageBean = federatedIpManagerService.getIpList(ipManagerListQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, ipManagerListDtoPageBean);
    }

    public CommonResponse dealIpModify(IpManagerUpdateQo ipManagerUpdateQo) {
        if (ipManagerUpdateQo.getCaseId() == null || ipManagerUpdateQo.getCaseId().isEmpty()) {
            return new CommonResponse<>(ReturnCodeEnum.CASEID_ERROR, null);
        }
        Boolean aBoolean = federatedIpManagerService.dealIpModify(ipManagerUpdateQo);
        if (!aBoolean) {
            return new CommonResponse<>(ReturnCodeEnum.CASEID_IP_DEAL_ERROR, aBoolean);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, aBoolean);
    }

    public CommonResponse<IpManagerAcceptDto> acceptIpModify(IpManagerAcceptQo ipManagerAcceptQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String httpBody;
        if ((httpServletRequest.getHeader(Dict.VERSION) != null)) {
            ObjectMapper mapper = new ObjectMapper();
            httpBody = mapper.writeValueAsString(ipManagerAcceptQo);
        } else {
            httpBody = JSON.toJSONString(ipManagerAcceptQo);
        }
        String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
        boolean result;
        if (StringUtils.isNotBlank(fateManagerUserId)) {
            result = checkSignature.checkSignatureNew(httpServletRequest, httpBody, Dict.FATE_SITE_USER, new int[]{2}, 2);
        } else {
            result = checkSignature.checkSignature(httpServletRequest, httpBody, 2);
        }
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        IpManagerAcceptDto ipManagerAcceptDto = federatedIpManagerService.acceptIpModify(ipManagerAcceptQo, httpServletRequest.getHeader(Dict.APP_KEY));
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, ipManagerAcceptDto);
    }

    public CommonResponse<IpManagerQueryDto> queryIpModify(IpManagerQueryQo ipManagerQueryQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String httpBody;
        if ((httpServletRequest.getHeader(Dict.VERSION) != null)) {
            ObjectMapper mapper = new ObjectMapper();
            httpBody = mapper.writeValueAsString(ipManagerQueryQo);
        } else {
            httpBody = JSON.toJSONString(ipManagerQueryQo);
        }
        String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
        boolean result;
        if (StringUtils.isNotBlank(fateManagerUserId)) {
            result = checkSignature.checkSignatureNew(httpServletRequest, httpBody, Dict.FATE_SITE_USER, new int[]{2}, 2);
        } else {
            result = checkSignature.checkSignature(httpServletRequest, httpBody, 2);
        }
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        IpManagerQueryDto ipManagerQueryDto = federatedIpManagerService.queryIpModify(ipManagerQueryQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, ipManagerQueryDto);
    }

    public CommonResponse<List<FederatedIpManagerDo>> queryIpModifyHistory(HistoryQo historyQo) {
        if (historyQo == null || historyQo.getPartyId() <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        List<FederatedIpManagerDo> federatedIpManagerDos = federatedIpManagerService.queryIpModifyHistory(historyQo.getPartyId());
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedIpManagerDos);

    }

    public CommonResponse<List<FederatedIpManagerDo>> queryUpdateIpModify(HttpServletRequest httpServletRequest) {
        String fateManagerUserId = httpServletRequest.getHeader(Dict.FATE_MANAGER_USER_ID);
        if (StringUtils.isBlank(fateManagerUserId)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        FederatedFateManagerUserDo fateManagerUser = federatedFateManagerUserService.findFateManagerUser(fateManagerUserId);
        if (fateManagerUser != null && fateManagerUser.getStatus() != 2){
            return new CommonResponse<>(ReturnCodeEnum.INSTITUTIONS_DELETED_ERROR);
        }

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_MANAGER_USER, new int[]{2}, 2);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        List<FederatedIpManagerDo> updateList =  federatedIpManagerService.queryUpdateIpModify(fateManagerUserId);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, updateList);
    }
}
