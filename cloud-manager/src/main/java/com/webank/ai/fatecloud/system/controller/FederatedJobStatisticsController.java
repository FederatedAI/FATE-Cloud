package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.*;
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

import javax.validation.Valid;
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
    public CommonResponse pushJosStatistics(@Valid @RequestBody List<JobStatisticsQo> jobStatisticsQos, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsQos);
        if (bindingResult.hasErrors()) {
            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR, errors.getDefaultMessage());
        }
        return federatedJobStatisticsServiceFacade.pushJosStatistics(jobStatisticsQos);
    }

    @PostMapping(value = "/site/today")
    @ApiOperation(value = "find job statistics of site dimension")
    public CommonResponse<JobStatisticsOfSiteDimensionDto> getJobStatisticsOfSiteDimension(@Valid @RequestBody JobOfSiteDimensionQo jobOfSiteDimensionQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionQo);
        if (bindingResult.hasErrors()) {
//            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsOfSiteDimension(jobOfSiteDimensionQo);
    }

    @PostMapping(value = "/institutions/today")
    @ApiOperation(value = "find job statistics of institutions dimension")
    public CommonResponse<PageBean<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimension(@Valid @RequestBody JobOfSiteDimensionQo jobOfSiteDimensionQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsODimension(jobOfSiteDimensionQo);
    }

    @PostMapping(value = "/summary/institutions/all/today")
    @ApiOperation(value = "find job summary statistics for all institutions today ")
    public CommonResponse<JobStatisticsSummaryTodayInstitutionsAllDto> getJobStatisticsSummaryTodayInstitutionsAll(@Valid @RequestBody JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryTodayQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryTodayInstitutionsAll(jobStatisticsSummaryTodayQo);
    }

    @PostMapping(value = "/summary/institutions/each/today")
    @ApiOperation(value = "find job summary statistics for each institutions today ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>> getJobStatisticsSummaryTodayInstitutionsEach(@Valid @RequestBody JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryTodayQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryTodayInstitutionsEach(jobStatisticsSummaryTodayQo);
    }

    @PostMapping(value = "/summary/site/all/today")
    @ApiOperation(value = "find job summary statistics for all sites today ")
    public CommonResponse<JobStatisticsSummaryTodaySiteAllDto> getJobStatisticsSummaryTodaySiteAll(@Valid @RequestBody JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryTodaySiteAllQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryTodaySiteAll(jobStatisticsSummaryTodaySiteAllQo);
    }

    @PostMapping(value = "/summary/site/each/today")
    @ApiOperation(value = "find job summary statistics for each site today ")
    public CommonResponse<PageBean<JobStatisticsSummaryTodaySiteEachDto>> getJobStatisticsSummaryTodaySiteEach(@Valid @RequestBody JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsSummaryTodaySiteAllQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsSummaryTodaySiteEach(jobStatisticsSummaryTodaySiteAllQo);
    }


    @PostMapping(value = "/site/period")
    @ApiOperation(value = "find job statistics of site dimension for a period")
    public CommonResponse<JobStatisticsOfSiteDimensionDto> getJobStatisticsOfSiteDimensionForPeriod(@Valid @RequestBody JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionPeriodQo);
        if (bindingResult.hasErrors()) {
//            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsOfSiteDimensionForPeriod(jobOfSiteDimensionPeriodQo);
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
}
