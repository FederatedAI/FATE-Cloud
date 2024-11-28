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
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedFateManagerUserServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "/api/fate/user")
@Slf4j
@Api(tags = "FederatedFateManagerUserController", description = "federated fate manager user controller api")
public class FederatedFateManagerUserController {

    @Autowired
    FederatedFateManagerUserServiceFacade federatedFateManagerUserServiceFacade;

    @PostMapping(value = "/add")
    @ApiOperation(value = "add fate manager to cloud manager")
    public CommonResponse<FederatedFateManagerUserDo> addFateManagerUser(@RequestBody FateManagerUserAddQo fateManagerUserAddQo) throws UnsupportedEncodingException {
        log.info("RequestBody:{}", fateManagerUserAddQo);
        return federatedFateManagerUserServiceFacade.addFateManagerUser(fateManagerUserAddQo);

    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "update fate manager to cloud manager")
    public CommonResponse<FederatedFateManagerUserDo> updateFateManagerUser(@RequestBody FateManagerUserUpdateQo fateManagerUserUpdateQo) throws UnsupportedEncodingException {
        log.info("RequestBody:{}", fateManagerUserUpdateQo);
        return federatedFateManagerUserServiceFacade.updateFateManagerUser(fateManagerUserUpdateQo);

    }

    @PostMapping(value = "/check")
    @ApiOperation(value = "check institution if used or not ")
    public CommonResponse checkInstitution(@RequestBody InstitutionCheckQo institutionCheckQo) {
        log.info("RequestBody:{}", institutionCheckQo);
        return federatedFateManagerUserServiceFacade.checkInstitution(institutionCheckQo);

    }


    @PostMapping(value = "/activate")
    @ApiOperation(value = "activate fate manager user")
    public CommonResponse activateFateManagerUser(@RequestBody SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        log.info("request for activate fate manager, header:{} | body:{}", httpServletRequest, siteActivateQo);
        CommonResponse commonResponse = federatedFateManagerUserServiceFacade.activateFateManagerUser(siteActivateQo, httpServletRequest);
        log.info("response for activate fate manager :{}", commonResponse);
        return commonResponse;

    }

    @PostMapping(value = "/activate/v2")
    @ApiOperation(value = "activate fate manager user")
    public CommonResponse<HashMap<String, Object>> shortLinkActivateFateManagerUser(@RequestBody SiteActivateQo siteActivateQo, HttpServletRequest httpServletRequest) {
        log.info("request for short activate fate manager, header:{} | body:{}", httpServletRequest, siteActivateQo);
        CommonResponse<HashMap<String, Object>> commonResponse = federatedFateManagerUserServiceFacade.shortLinkActivateFateManagerUser(siteActivateQo, httpServletRequest);
        log.info("response for short activate fate manager :{}", commonResponse);
        return commonResponse;

    }

    @PostMapping(value = "/reactivate")
    @ApiOperation(value = "activate fate manager user")
    public CommonResponse<Boolean> reactivateFateManagerUser(@RequestBody FateManagerUserUpdateQo fateManagerUserUpdateQo) {
        log.info("request for reactivate fate manager, body:{}", fateManagerUserUpdateQo);
        CommonResponse<Boolean> commonResponse = federatedFateManagerUserServiceFacade.reactivateFateManagerUser(fateManagerUserUpdateQo);
        log.info("response for activate fate manager :{}", commonResponse);
        return commonResponse;

    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete institution if used or not ")
    public CommonResponse deleteFateManagerUser(@RequestBody FateManagerUserDeleteQo fateManagerUserDeleteQo) {
        log.info("RequestBody:{}", fateManagerUserDeleteQo);
        return federatedFateManagerUserServiceFacade.deleteFateManagerUser(fateManagerUserDeleteQo);

    }

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "all institutions")
    public CommonResponse<List<String>> findAllFateManagerUserInstitutions() {
        return federatedFateManagerUserServiceFacade.findAllFateManagerUserInstitutions();

    }

    @PostMapping(value = "/find/page")
    @ApiOperation(value = "find paged fate manager user")
    public CommonResponse<PageBean<FederatedFateManagerUserDo>> findPagedCloudManagerUser(@RequestBody FateManagerUserPagedQo fateManagerUserPagedQo) {
        log.info("RequestBody:{}", fateManagerUserPagedQo);
        return federatedFateManagerUserServiceFacade.findPagedFateManagerUser(fateManagerUserPagedQo);

    }

    @PostMapping(value = "/institutions/all")
    @ApiOperation(value = "all institutions including not activated")
    public CommonResponse<List<String>> findAllInstitutions() {
        return federatedFateManagerUserServiceFacade.findAllInstitutions();

    }

}
