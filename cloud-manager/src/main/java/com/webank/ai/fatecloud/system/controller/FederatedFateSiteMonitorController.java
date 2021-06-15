package com.webank.ai.fatecloud.system.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FateSiteJobDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorInsitutionQo;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorInstituionDto;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedFateSiteMonitorServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/job/v3")
@Slf4j
@Api(tags = "FederatedFateSiteMonitorController", description = "Federation job statistics interface for fate site")
public class FederatedFateSiteMonitorController {

    @Autowired
    FederatedFateSiteMonitorServiceFacade federatedFateSiteMonitorServiceFacade;


    @PostMapping(value = "/push")
    @ApiOperation(value = "push job statistics of site")
    public CommonResponse pushJosStatistics(@RequestBody ArrayList<FateSiteJobQo> fateSiteJobQoArrayList, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        log.info("jobs to push is jobStatisticsQos:{}", fateSiteJobQoArrayList);
        CommonResponse commonResponse = federatedFateSiteMonitorServiceFacade.pushJosStatistics(fateSiteJobQoArrayList, httpServletRequest);
        log.info("response of jobStatisticsQos:{} is commonResponse:{}",fateSiteJobQoArrayList,commonResponse);
        return commonResponse;

    }

    @PostMapping(value = "/find/summary/finished")
    @ApiOperation(value = "find summary statistics for finished jobs ")
    public CommonResponse<FinishedJosSummaryDto> findFinishedJobsSummary(@RequestBody FinishedJobsSummaryQo finishedJobsSummaryQo) throws ParseException {

        return federatedFateSiteMonitorServiceFacade.findFinishedJobsSummary(finishedJobsSummaryQo);
    }

    @PostMapping(value = "/find/typed/table")
    @ApiOperation(value = "find table statistics for each job type ")
    public CommonResponse<List<JobTypedTableDto>> findTypedTable(@RequestBody JobTypedTableQo jobTypedTableQo) throws ParseException {

        return federatedFateSiteMonitorServiceFacade.findTypedTable(jobTypedTableQo);
    }

    @PostMapping(value = "/find/typed/duration")
    @ApiOperation(value = "find duration distribution statistics for each job type ")
    public CommonResponse<List<JobTypedDurationDto>> findTypedDuration(@RequestBody JobTypedTableQo jobTypedTableQo) throws ParseException {

        return federatedFateSiteMonitorServiceFacade.findTypedDuration(jobTypedTableQo);
    }


    @PostMapping(value = "/summary/institutions/all/period")
    @ApiOperation(value = "find job summary statistics for all institutions for a period ")
    public CommonResponse<JobStatisticsSummaryTodayInstitutionsAllDto> getJobStatisticsSummaryInstitutionsAllForPeriod(@Valid @RequestBody JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);
    }

    @PostMapping(value = "/summary/institutions/each/period")
    @ApiOperation(value = "find job summary statistics for each institutions for a period ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>> getJobStatisticsSummaryInstitutionsEachForPeriod(@Valid @RequestBody JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsSummaryInstitutionsEachForPeriod(jobStatisticsSummaryForPeriodQo);
    }

    @PostMapping(value = "/summary/site/all/period")
    @ApiOperation(value = "find job summary statistics for all sites for a period ")
    public CommonResponse<JobStatisticsSummaryTodaySiteAllDto> getJobStatisticsSummarySiteAllForPeriod(@Valid @RequestBody JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
    }

    @PostMapping(value = "/summary/site/each/period")
    @ApiOperation(value = "find job summary statistics for each site for a period ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodaySiteEachDto>> getJobStatisticsSummarySiteEachForPeriod(@Valid @RequestBody JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsSummarySiteEachForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
    }

    @PostMapping(value = "/institutions/period")
    @ApiOperation(value = "find job statistics of institutions dimension for a period")
    public CommonResponse<PageBean<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimensionForPeriod(@Valid @RequestBody JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsODimensionForPeriod(jobOfSiteDimensionPeriodQo);
    }

    @PostMapping(value = "/site/period")
    @ApiOperation(value = "find sites based statistics")
    public CommonResponse<MonitorSiteDto> getJobStatisticsOfSiteDimensionForPeriod(@Valid @RequestBody JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo, BindingResult bindingResult) throws ParseException {
        log.info("RequestBody:{}", jobOfSiteDimensionPeriodQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getJobStatisticsOfSiteDimensionForPeriod(jobOfSiteDimensionPeriodQo);
    }


    @PostMapping(value = "/site/all")
    @ApiOperation(value = "find all sites ")
    public CommonResponse<List<String>> getSiteAll( @RequestBody AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        if (StringUtils.isBlank(authorityApplyDetailsQo.getInstitutions())) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorServiceFacade.getSiteAll(authorityApplyDetailsQo);
    }
}
