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
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.dto.FunctionStatusDto;
import com.webank.ai.fatecloud.system.pojo.qo.FunctionUpdateQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class FederatedFunctionServiceFacade {

    @Autowired
    FederatedFunctionService federatedFunctionService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse<List<FunctionStatusDto>> findAllFunctionStatus() {

        List<FunctionStatusDto> allFunctionStatus = federatedFunctionService.findAllFunctionStatus();

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, allFunctionStatus);

    }

    public CommonResponse updateFunctionStatus(FunctionUpdateQo functionUpdateQo) {

        if (functionUpdateQo.getStatus() != 1 && functionUpdateQo.getStatus() != 2) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        federatedFunctionService.updateFunctionStatus(functionUpdateQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<List<FunctionStatusDto>> findAllFunctionStatusForFateManager(HttpServletRequest httpServletRequest) {

        //check authority
        boolean result = checkSignature.checkSignatureNew(httpServletRequest,"", Dict.FATE_MANAGER_USER, new int[]{2},null);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        List<FunctionStatusDto> allFunctionStatus = federatedFunctionService.findAllFunctionStatus();

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, allFunctionStatus);

    }

    public void initialingFunction() {
        federatedFunctionService.initialingFunction();
    }



}
