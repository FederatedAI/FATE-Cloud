package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FateSiteJobDo;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorInsitutionQo;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorInstituionDto;
import com.webank.ai.fatecloud.system.pojo.monitor.MonitorSiteDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedFateSiteMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FederatedFateSiteMonitorServiceFacade {

    @Autowired
    FederatedFateSiteMonitorService federatedFateSiteMonitorService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse pushJosStatistics(ArrayList<FateSiteJobQo> fateSiteJobQoArrayList, HttpServletRequest httpServletRequest) throws JsonProcessingException {

        //check authority
        String httpBody;
        if ((httpServletRequest.getHeader(Dict.VERSION) != null)) {
            ObjectMapper mapper = new ObjectMapper();
            httpBody = mapper.writeValueAsString(fateSiteJobQoArrayList);
        } else {
            httpBody = JSON.toJSONString(fateSiteJobQoArrayList);
        }
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, httpBody, Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        if (fateSiteJobQoArrayList == null || fateSiteJobQoArrayList.size() <= 0){
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        federatedFateSiteMonitorService.pushJosStatistics(fateSiteJobQoArrayList);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse<FinishedJosSummaryDto> findFinishedJobsSummary(FinishedJobsSummaryQo finishedJobsSummaryQo) throws ParseException {
        Date beginDate = finishedJobsSummaryQo.getBeginDate();
        Date endDate = finishedJobsSummaryQo.getEndDate();
        if (Objects.isNull(beginDate) || Objects.isNull(endDate)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorService.findFinishedJobsSummary(finishedJobsSummaryQo);
    }

    public CommonResponse<List<JobTypedTableDto>> findTypedTable(JobTypedTableQo jobTypedTableQo) throws ParseException {
        Date beginDate = jobTypedTableQo.getBeginDate();
        Date endDate = jobTypedTableQo.getEndDate();
        String type = jobTypedTableQo.getType();
        if (Objects.isNull(beginDate) || Objects.isNull(endDate)||StringUtils.isBlank(type)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        return federatedFateSiteMonitorService.findTypedTable(jobTypedTableQo);

    }

    public CommonResponse<List<JobTypedDurationDto>> findTypedDuration(JobTypedTableQo jobTypedTableQo) throws ParseException {
        Date beginDate = jobTypedTableQo.getBeginDate();
        Date endDate = jobTypedTableQo.getEndDate();
        String type = jobTypedTableQo.getType();
        if (Objects.isNull(beginDate) || Objects.isNull(endDate)||StringUtils.isBlank(type)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedFateSiteMonitorService.findTypedDuration(jobTypedTableQo);


    }


    public CommonResponse<JobStatisticsSummaryTodayInstitutionsAllDto> getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {

        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummary = federatedFateSiteMonitorService.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummary);
    }

    public CommonResponse<PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>> getJobStatisticsSummaryInstitutionsEachForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtos = federatedFateSiteMonitorService.getJobStatisticsSummaryInstitutionsEachForPeriod(jobStatisticsSummaryForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodayInstitutionsEachDtos);

    }

    public CommonResponse<JobStatisticsSummaryTodaySiteAllDto> getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedFateSiteMonitorService.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodaySiteAllDto);
    }

    public CommonResponse<PageBean<JobStatisticsSummaryTodaySiteEachDto>> getJobStatisticsSummarySiteEachForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedFateSiteMonitorService.getJobStatisticsSummarySiteEachForPeriod(jobStatisticsSummarySiteAllForPeriodQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsSummaryTodaySiteEachDtos);
    }

    public CommonResponse<MonitorSiteDto> getJobStatisticsOfSiteDimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {
        MonitorSiteDto monitorSiteDto = federatedFateSiteMonitorService.getJobStatisticsOfSiteDimensionForPeriod(jobOfSiteDimensionPeriodQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, monitorSiteDto);
    }


    public CommonResponse<PageBean<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {

        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticsODimensionList = federatedFateSiteMonitorService.getJobStatisticsODimensionForPeriod(jobOfSiteDimensionPeriodQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsODimensionList);
    }

    public CommonResponse<List<String>> getSiteAll(AuthorityApplyDetailsQo authorityApplyDetailsQo) {
        List<String> siteList = federatedFateSiteMonitorService.getSiteAll(authorityApplyDetailsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, siteList);

    }
}
