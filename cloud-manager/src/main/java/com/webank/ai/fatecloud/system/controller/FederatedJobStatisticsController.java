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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteDto;
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
@Api(tags = "FederatedJobStatisticsController", description = "Federation job statistics interface")
@CrossOrigin
public class FederatedJobStatisticsController {

    @Autowired
    FederatedJobStatisticsServiceFacade federatedJobStatisticsServiceFacade;

    @PostMapping(value = "/push")
    @ApiOperation(value = "push job statistics of site")
    public CommonResponse pushJosStatistics(@Valid @RequestBody ArrayList<JobStatisticsQo> jobStatisticsQos, BindingResult bindingResult, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        log.info("job push RequestBody:{}", jobStatisticsQos);
        if (bindingResult.hasErrors()) {
            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR, errors.getDefaultMessage());
        }
        return federatedJobStatisticsServiceFacade.pushJosStatistics(jobStatisticsQos,httpServletRequest);
    }

    @PostMapping(value = "/summary/institutions/all/period")
    @ApiOperation(value = "find job summary statistics for all institutions for a period ")
    public CommonResponse<JobStatisticsSummaryTodayInstitutionsAllDto> getJobStatisticsSummaryInstitutionsAllForPeriod(@Valid @RequestBody JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryForPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);
    }

    @PostMapping(value = "/summary/institutions/each/period")
    @ApiOperation(value = "find job summary statistics for each institutions for a period ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>> getJobStatisticsSummaryInstitutionsEachForPeriod(@Valid @RequestBody JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryForPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryInstitutionsEachForPeriod(jobStatisticsSummaryForPeriodQo);
    }

    @PostMapping(value = "/summary/site/all/period")
    @ApiOperation(value = "find job summary statistics for all sites for a period ")
    public CommonResponse<JobStatisticsSummaryTodaySiteAllDto> getJobStatisticsSummarySiteAllForPeriod(@Valid @RequestBody JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummarySiteAllForPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
    }

    @PostMapping(value = "/summary/site/each/period")
    @ApiOperation(value = "find job summary statistics for each site for a period ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodaySiteEachDto>> getJobStatisticsSummarySiteEachForPeriod(@Valid @RequestBody JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummarySiteAllForPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummarySiteEachForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
    }


    @PostMapping(value = "/institutions/period")
    @ApiOperation(value = "find job statistics of institutions dimension for a period")
    public CommonResponse<PageBean<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimensionForPeriod(@Valid @RequestBody JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsODimensionForPeriod(jobOfSiteDimensionPeriodQo);
    }

    @PostMapping(value = "/site/period")
    @ApiOperation(value = "find sites based statistics")
    public CommonResponse<MonitorSiteDto> getJobStatisticsOfSiteDimensionForPeriod(@Valid @RequestBody JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionPeriodQo);
        if (bindingResult.hasErrors()) {
//            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsOfSiteDimensionForPeriod(jobOfSiteDimensionPeriodQo);
    }
}
