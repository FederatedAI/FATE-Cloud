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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedJobStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
public class FederatedJobStatisticsServiceFacade {

    @Autowired
    FederatedJobStatisticsService federatedJobStatisticsService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse pushJosStatistics(List<JobStatisticsQo> jobStatisticsQos, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        //check authority
        String httpBody;
        if ((httpServletRequest.getHeader(Dict.VERSION) != null)) {
            ObjectMapper mapper = new ObjectMapper();
            httpBody = mapper.writeValueAsString(jobStatisticsQos);
        } else {
            httpBody = JSON.toJSONString(jobStatisticsQos);
        }
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, httpBody, Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        if (!(jobStatisticsQos.size() > 0)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        federatedJobStatisticsService.pushJosStatistics(jobStatisticsQos);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse<JobStatisticsSummaryTodayInstitutionsAllDto> getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {

        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummary = federatedJobStatisticsService.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummary);
    }

    public CommonResponse<MonitorSiteDto> getJobStatisticsOfSiteDimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {
        MonitorSiteDto monitorSiteDto = federatedJobStatisticsService.getJobStatisticsOfSiteDimensionForPeriod(jobOfSiteDimensionPeriodQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, monitorSiteDto);
    }

    public CommonResponse<PageBean<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {
        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticsODimensionList = federatedJobStatisticsService.getJobStatisticsODimensionForPeriod(jobOfSiteDimensionPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsODimensionList);
    }


    public CommonResponse<PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>> getJobStatisticsSummaryInstitutionsEachForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtos = federatedJobStatisticsService.getJobStatisticsSummaryInstitutionsEachForPeriod(jobStatisticsSummaryForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodayInstitutionsEachDtos);

    }

    public CommonResponse<JobStatisticsSummaryTodaySiteAllDto> getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedJobStatisticsService.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodaySiteAllDto);

    }

    public CommonResponse<PageBean<JobStatisticsSummaryTodaySiteEachDto>> getJobStatisticsSummarySiteEachForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedJobStatisticsService.getJobStatisticsSummarySiteEachForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodaySiteEachDtos);
    }


    //monitor write by nobslin
    public CommonResponse<MonitorTotalDto> getMonitorTotal(TotalReqQo totalReqQo) {
        MonitorTotalDto monitorTotalDto = federatedJobStatisticsService.getMonitorTotal(totalReqQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, monitorTotalDto);
    }

    public CommonResponse<InstitutionDetailDto> getInsitutionDetail(InsitutionDetailQo insitutionDetailQo) {
        InstitutionDetailDto institutionDetailDto = federatedJobStatisticsService.getInsitutionDetail(insitutionDetailQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, institutionDetailDto);
    }

    public CommonResponse<MonitorInstituionDto> getInstitutionMonitor(MonitorInsitutionQo monitorInsitutionQo) {
        MonitorInstituionDto pageBean = federatedJobStatisticsService.getInstitutionMonitor(monitorInsitutionQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pageBean);
    }

    public CommonResponse<MonitorSiteDto> getSiteMonitor(MonitorSiteQo monitorSiteQo) {
        MonitorSiteDto pageBean = federatedJobStatisticsService.getSiteMonitor(monitorSiteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pageBean);
    }
}
