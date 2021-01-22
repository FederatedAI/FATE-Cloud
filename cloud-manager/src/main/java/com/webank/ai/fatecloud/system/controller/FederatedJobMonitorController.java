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
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedJobStatisticsServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Data
@RequestMapping("/api/job")
@Api(tags = "FederatedJobMonitorController", description = "Federation job statistics interface")
@CrossOrigin
public class FederatedJobMonitorController {


    @Autowired
    FederatedJobStatisticsServiceFacade federatedJobStatisticsServiceFacade;

    @PostMapping(value = "/summary/total")
    @ApiOperation(value = "statistitcs institution total by region")
    public CommonResponse<MonitorTotalDto> getMonitorTotal(@Valid @RequestBody TotalReqQo totalReqQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", totalReqQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getMonitorTotal(totalReqQo);
    }

    @PostMapping(value = "/summary/detail")
    @ApiOperation(value = "statistitcs institution site detail by region")
    public CommonResponse<InstitutionDetailDto> getInsitutionDetail(@Valid @RequestBody InsitutionDetailQo insitutionDetailQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", insitutionDetailQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getInsitutionDetail(insitutionDetailQo);
    }


    @PostMapping(value = "/summary/institutions")
    @ApiOperation(value = "statistitcs institution by region")
    public CommonResponse<MonitorInstituionDto> getInstitutionMonitor(@Valid @RequestBody MonitorInsitutionQo monitorInsitutionQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", monitorInsitutionQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getInstitutionMonitor(monitorInsitutionQo);
    }

    @PostMapping(value = "/summary/site")
    @ApiOperation(value = "statistitcs institution site by region")
    public CommonResponse<List<MonitorSiteDto>> getSiteMonitor(@Valid @RequestBody MonitorSiteQo monitorSiteQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", monitorSiteQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getSiteMonitor(monitorSiteQo);
    }
}
