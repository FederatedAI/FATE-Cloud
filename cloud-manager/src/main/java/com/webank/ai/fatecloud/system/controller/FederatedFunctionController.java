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
package com.webank.ai.fatecloud.system.controller;


import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.system.pojo.dto.FunctionStatusDto;
import com.webank.ai.fatecloud.system.pojo.qo.FunctionUpdateQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedFunctionServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/function")
@Slf4j
@Api(tags = "FederatedFunctionController", description = "function api")
public class FederatedFunctionController implements InitializingBean {

    @Autowired
    FederatedFunctionServiceFacade federatedFunctionServiceFacade;


    @PostMapping(value = "/find/all")
    @ApiOperation(value = "find all function information")
    public CommonResponse<List<FunctionStatusDto>> findAllFunctionStatus() {
        return federatedFunctionServiceFacade.findAllFunctionStatus();
    }


    @PostMapping(value = "/update/status")
    @ApiOperation(value = "find all function information")
    public CommonResponse updateFunctionStatus(@RequestBody FunctionUpdateQo functionUpdateQo) {
        return federatedFunctionServiceFacade.updateFunctionStatus(functionUpdateQo);

    }

    //for fate manager

    @PostMapping(value = "/find/all/fateManager")
    @ApiOperation(value = "find all function information for fate manager")
    public CommonResponse<List<FunctionStatusDto>> findAllFunctionStatusForFateManager(HttpServletRequest httpServletRequest) {
        return federatedFunctionServiceFacade.findAllFunctionStatusForFateManager(httpServletRequest);
    }


    @Override
    public void afterPropertiesSet() {
        federatedFunctionServiceFacade.initialingFunction();
    }

}
