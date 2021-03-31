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
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedModelServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/system")
@Api(tags = "FederatedSystemController",description = "Federation System Interface")
@Data
@Slf4j
public class FederatedSystemController {
    @Autowired
    FederatedModelServiceFacade federatedModelServiceFacade;


    @PostMapping(value = "/add")
    @ApiOperation(value = "add model information for site")
    public CommonResponse addModelNew(@RequestBody ArrayList<ModelAddQo> modelAddQos, HttpServletRequest httpServletRequest){
        log.info("RequestBody:{} Http:{}", modelAddQos,httpServletRequest);
        return federatedModelServiceFacade.addModelNew(modelAddQos,httpServletRequest);
    }

    @PostMapping(value = "/heart")
    @ApiOperation(value = "update model status")
    public CommonResponse modelHeart(@RequestBody ArrayList<ModelHeartQo> modelHeartQos, HttpServletRequest httpServletRequest){
        log.info("RequestBody:{} Http:{}", modelHeartQos,httpServletRequest);
        return federatedModelServiceFacade.modelHeart(modelHeartQos,httpServletRequest);
    }

    @PostMapping(value = "/page")
    @ApiOperation(value = "find paged model information of site")
    public CommonResponse<PageBean<FederatedSiteManagerDo>> findPagedSitesWithModel(@RequestBody SiteListWithModelsQo siteListWithModelsQo){
        log.info("RequestBody:{}", siteListWithModelsQo);
        return federatedModelServiceFacade.findPagedSitesWithModel(siteListWithModelsQo);
    }

    @PostMapping(value = "/history")
    @ApiOperation(value = "find update history of model")
    public CommonResponse<List<FederatedSiteModelDo>> findModelHistory(@RequestBody ModelHistoryQo modelHistoryQo){
        log.info("RequestBody:{}", modelHistoryQo);
        return federatedModelServiceFacade.findModelHistory(modelHistoryQo);
    }

    @PostMapping(value = "/find")
    @ApiOperation(value = "find model information of site")
    public CommonResponse<List<FederatedSiteModelDo>> findSitesWithModel(@RequestBody OneSiteQo oneSiteQo){
        log.info("RequestBody:{}", oneSiteQo);
        return federatedModelServiceFacade.findSitesWithModel(oneSiteQo);
    }

}
