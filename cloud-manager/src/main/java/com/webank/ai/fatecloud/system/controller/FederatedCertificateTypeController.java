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
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudCertificateTypeDo;
import com.webank.ai.fatecloud.system.service.facade.FederatedCloudCertificateTypeServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/certificate/type")
@Api(tags = "FederatedCertificateTypeController", description = "certificate type")
public class FederatedCertificateTypeController {
    @Autowired
    private FederatedCloudCertificateTypeServiceFacade federatedCloudCertificateTypeServiceFacade;

    @PostMapping(value = "/query")
    @ApiOperation(value = "query certificate type")
    public CommonResponse<List<FederatedCloudCertificateTypeDo>> query() {
        return federatedCloudCertificateTypeServiceFacade.queryAll();
    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "update certificate type")
    public CommonResponse<String> update(@RequestBody List<FederatedCloudCertificateTypeDo> certificateTypeDoList) {
        return federatedCloudCertificateTypeServiceFacade.update(certificateTypeDoList);
    }

}
