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
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetDto;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetRangeInfoDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedGroupSetServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/group")
@Api(tags = "FederatedGroupSetController", description = "About Group Set")
public class FederatedGroupSetController {

    @Autowired
    FederatedGroupSetServiceFacade federatedGroupSetServiceFacade;

    @PostMapping(value = "/checkGroupName")
    @ApiOperation(value = "check group name")
    public CommonResponse checkGroupName(@RequestBody FederatedGroupSetNameCheckQo federatedGroupSetNameCheckQo) {
        log.info("RequestBody:{}", federatedGroupSetNameCheckQo);
        return federatedGroupSetServiceFacade.checkGroupName(federatedGroupSetNameCheckQo);
    }


    @PostMapping(value = "/checkUpdateRange")
    @ApiOperation(value = "check updated range info ")
    public CommonResponse checkUpdateRange(@RequestBody FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        log.info("RequestBody:{}", federatedGroupSetUpdateQo);
        return federatedGroupSetServiceFacade.checkUpdateRange(federatedGroupSetUpdateQo);
    }


    @PostMapping(value = "/addNewGroup")
    @ApiOperation(value = "add new group")
    public CommonResponse addNewGroup(@RequestBody GroupAddQo groupAddQo) {
        log.info("url:addNewGroup, requestBody:{}", groupAddQo);
        return federatedGroupSetServiceFacade.addNewGroup(groupAddQo);
    }

    @PostMapping(value = "/deleteNewGroup")
    @ApiOperation(value = "delete new group")
    public CommonResponse deleteNewGroup(@RequestBody GroupDeleteQo groupDeleteQo) {
        log.info("url:deleteNewGroup, requestBody:{}", groupDeleteQo);
        return federatedGroupSetServiceFacade.deleteNewGroup(groupDeleteQo);
    }

    @PostMapping(value = "/updateNewGroup")
    @ApiOperation(value = "update new group")
    public CommonResponse updateNewGroup(@RequestBody GroupUpdateQo groupUpdateQo) {
        log.info("url:updateNewGroup, requestBody:{}", groupUpdateQo);
        return federatedGroupSetServiceFacade.updateNewGroup(groupUpdateQo);
    }

    @PostMapping(value = "/findNewPage")
    @ApiOperation(value = "Find new group page")
    public CommonResponse<PageBean<FederatedGroupSetDo>> selectNewGroupSetList(@RequestBody FederatedGroupSetQo federatedGroupSetQo) {
        log.info("RequestBody:{}", federatedGroupSetQo);
        return federatedGroupSetServiceFacade.selectNewGroupSetList(federatedGroupSetQo);
    }

    @PostMapping(value = "/checkUpdateRangeNew")
    @ApiOperation(value = "check updated regions")
    public CommonResponse checkUpdateRangeNew(@RequestBody GroupUpdateQo groupUpdateQo) {
        log.info("RequestBody:{}", groupUpdateQo);
        return federatedGroupSetServiceFacade.checkUpdateRangeNew(groupUpdateQo);
    }

    @PostMapping(value = "/checkNew")
    @ApiOperation(value = "region info check")
    public CommonResponse checkGroupNew(@RequestBody GroupCheckRegionAllocateQo groupCheckRegionAllocateQo) {
        log.info("RequestBody:{}", groupCheckRegionAllocateQo);
        return federatedGroupSetServiceFacade.checkGroupRegion(groupCheckRegionAllocateQo.getGroupId(), groupCheckRegionAllocateQo.getRegions());
    }

    @PostMapping(value = "/findPagedRegionInfoNew")
    @ApiOperation(value = "find paged region information")
    public CommonResponse<PageBean<FederatedGroupSetDo>> findPagedRegionInfoNew(@RequestBody PageInfoQo pageInfoQo) {
        log.info("RequestBody:{}", pageInfoQo);
        return federatedGroupSetServiceFacade.findPagedRegionInfoNew(pageInfoQo);
    }
}
