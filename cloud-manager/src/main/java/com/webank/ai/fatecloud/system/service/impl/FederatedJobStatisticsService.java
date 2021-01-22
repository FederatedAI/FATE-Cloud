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
package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedJobStatisticsMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.*;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FederatedJobStatisticsService {

    @Autowired
    FederatedJobStatisticsMapper federatedJobStatisticsMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Transactional
    public void pushJosStatistics(List<JobStatisticsQo> jobStatisticsQos) {
        for (JobStatisticsQo jobStatisticsQo : jobStatisticsQos) {
            //find the job info whether exist or not. if exist,update! if not,insert!
            FederatedJobStatisticsDo federatedJobStatisticsDo = new FederatedJobStatisticsDo(jobStatisticsQo);

            Date date = new Date(jobStatisticsQo.getJobFinishDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(date);

            QueryWrapper<FederatedJobStatisticsDo> federatedJobStatisticsDoQueryWrapper = new QueryWrapper<>();
            federatedJobStatisticsDoQueryWrapper.eq("site_guest_id", jobStatisticsQo.getSiteGuestId()).eq("site_host_id", jobStatisticsQo.getSiteHostId()).eq("job_finish_date", format);

            Integer count = federatedJobStatisticsMapper.selectCount(federatedJobStatisticsDoQueryWrapper);
            if (count > 0) {
                federatedJobStatisticsMapper.update(federatedJobStatisticsDo, federatedJobStatisticsDoQueryWrapper);
            } else {
                //link the site information by id

                FederatedSiteManagerDo federatedSiteManagerDoGuest = federatedSiteManagerMapper.selectById(jobStatisticsQo.getSiteGuestId());
                federatedJobStatisticsDo.setSiteGuestInstitutions(federatedSiteManagerDoGuest.getInstitutions());
                federatedJobStatisticsDo.setSiteGuestName(federatedSiteManagerDoGuest.getSiteName());

                FederatedSiteManagerDo federatedSiteManagerDoHost = federatedSiteManagerMapper.selectById(jobStatisticsQo.getSiteHostId());
                federatedJobStatisticsDo.setSiteHostInstitutions(federatedSiteManagerDoHost.getInstitutions());
                federatedJobStatisticsDo.setSiteHostName(federatedSiteManagerDoHost.getSiteName());

                federatedJobStatisticsMapper.insert(federatedJobStatisticsDo);
            }
        }
    }


    public JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryTodayInstitutionsAll(JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo) {
        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummaryTodayInstitutionsAllDto = federatedJobStatisticsMapper.getJobStatisticsSummaryTodayInstitutionsAll(jobStatisticsSummaryTodayQo);
        return jobStatisticsSummaryTodayInstitutionsAllDto;
    }

    public JobStatisticsSummaryTodayInstitutionsAllDto getJobStatisticsSummaryInstitutionsAllForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {
        JobStatisticsSummaryTodayInstitutionsAllDto jobStatisticsSummaryTodayInstitutionsAllDto = federatedJobStatisticsMapper.getJobStatisticsSummaryInstitutionsAllForPeriod(jobStatisticsSummaryForPeriodQo);
        return jobStatisticsSummaryTodayInstitutionsAllDto;
    }

    public PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> getJobStatisticsSummaryTodayInstitutionsEach(JobStatisticsSummaryTodayQo jobStatisticsSummaryTodayQo) {

        List<String> activatedInstitutionsToday = federatedJobStatisticsMapper.getActivatedInstitutionsToday(jobStatisticsSummaryTodayQo);
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtoPageBean = new PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>(jobStatisticsSummaryTodayQo.getPageNum(), jobStatisticsSummaryTodayQo.getPageSize(), activatedInstitutionsToday.size());
        long startIndex = jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.getStartIndex();

        List<JobStatisticsSummaryTodayInstitutionsEachDto> institutionToday = federatedJobStatisticsMapper.getInstitutionToday(startIndex, jobStatisticsSummaryTodayQo);
        jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.setList(institutionToday);
        return jobStatisticsSummaryTodayInstitutionsEachDtoPageBean;

    }

    public JobStatisticsOfSiteDimensionDto getJobStatisticsOfSiteDimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {

        //get table site columns
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<FederatedSiteManagerDo>();
        federatedSiteManagerDoQueryWrapper.eq("institutions", jobOfSiteDimensionQo.getInstitutions()).eq("status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        ArrayList<String> sites = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            sites.add(federatedSiteManagerDo.getSiteName());
        }

        //get paged job statistics
        long count = federatedJobStatisticsMapper.findCountOfSite(jobOfSiteDimensionQo);
        PageBean<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionPageBean = new PageBean<>(jobOfSiteDimensionQo.getPageNum(), jobOfSiteDimensionQo.getPageSize(), count);
        long startIndex = jobStatisticsOfSiteDimensionPageBean.getStartIndex();
        List<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionList = federatedJobStatisticsMapper.getPagedJobStatisticsOfSiteDimensionDynamicRow(startIndex, jobOfSiteDimensionQo);
        jobStatisticsOfSiteDimensionPageBean.setList(jobStatisticsOfSiteDimensionList);


        //get table site rows
        List<InstitutionsWithSites> institutionsWithSites = federatedJobStatisticsMapper.findInstitutionsWithSitesPaged(startIndex, jobOfSiteDimensionQo);

        JobStatisticsOfSiteDimensionDto jobStatisticsOfSiteDimensionDto = new JobStatisticsOfSiteDimensionDto();
        jobStatisticsOfSiteDimensionDto.setJobStatisticsOfSiteDimensions(jobStatisticsOfSiteDimensionPageBean);
        jobStatisticsOfSiteDimensionDto.setSites(sites);
        jobStatisticsOfSiteDimensionDto.setInstitutionsWithSites(institutionsWithSites);
        return jobStatisticsOfSiteDimensionDto;
    }


    //get institutions statistics today
    public PageBean<JobStatisticOfInstitutionsDimensionDto> getJobStatisticsODimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {

        int count = federatedJobStatisticsMapper.findInstitutionsCountToday(jobOfSiteDimensionQo);

        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtoPageBean = new PageBean<>(jobOfSiteDimensionQo.getPageNum(), jobOfSiteDimensionQo.getPageSize(), count);
        long startIndex = jobStatisticOfInstitutionsDimensionDtoPageBean.getStartIndex();

        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtos = federatedJobStatisticsMapper.getPagedJobStatisticsODimension(startIndex, jobOfSiteDimensionQo);
        jobStatisticOfInstitutionsDimensionDtoPageBean.setList(jobStatisticOfInstitutionsDimensionDtos);

        return jobStatisticOfInstitutionsDimensionDtoPageBean;
    }

    public PageBean<JobStatisticOfInstitutionsDimensionDto> getJobStatisticsODimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {

        int count = federatedJobStatisticsMapper.findInstitutionsCountPeriod(jobOfSiteDimensionPeriodQo);

        PageBean<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtoPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticOfInstitutionsDimensionDtoPageBean.getStartIndex();

        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtos = federatedJobStatisticsMapper.getPagedJobStatisticsODimensionForPeriod(startIndex, jobOfSiteDimensionPeriodQo);
        jobStatisticOfInstitutionsDimensionDtoPageBean.setList(jobStatisticOfInstitutionsDimensionDtos);

        return jobStatisticOfInstitutionsDimensionDtoPageBean;
    }


    public JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummaryTodaySiteAll(JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo) {

        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedJobStatisticsMapper.getJobStatisticsSummaryTodaySiteAll(jobStatisticsSummaryTodaySiteAllQo);

        return jobStatisticsSummaryTodaySiteAllDto;
    }

    public PageBean<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummaryTodaySiteEach(JobStatisticsSummaryTodaySiteAllQo jobStatisticsSummaryTodaySiteAllQo) {
        long count = federatedJobStatisticsMapper.getJobStatisticsSummaryTodaySiteEachCount(jobStatisticsSummaryTodaySiteAllQo);
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtoPageBean = new PageBean<>(jobStatisticsSummaryTodaySiteAllQo.getPageNum(), jobStatisticsSummaryTodaySiteAllQo.getPageSize(), count);
        long startIndex = jobStatisticsSummaryTodaySiteEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedJobStatisticsMapper.getJobStatisticsSummaryTodaySiteEach(startIndex, jobStatisticsSummaryTodaySiteAllQo);

        jobStatisticsSummaryTodaySiteEachDtoPageBean.setList(jobStatisticsSummaryTodaySiteEachDtos);
        return jobStatisticsSummaryTodaySiteEachDtoPageBean;

    }

    public JobStatisticsOfSiteDimensionDto getJobStatisticsOfSiteDimensionForPeriod(JobOfSiteDimensionPeriodQo jobOfSiteDimensionPeriodQo) {

        //get table site columns
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<FederatedSiteManagerDo>();
        federatedSiteManagerDoQueryWrapper.eq("institutions", jobOfSiteDimensionPeriodQo.getInstitutions()).eq("status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        ArrayList<String> sites = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            sites.add(federatedSiteManagerDo.getSiteName());
        }

        //get paged job statistics
        long count = federatedJobStatisticsMapper.findCountOfSitePeriod(jobOfSiteDimensionPeriodQo);
        PageBean<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionPageBean = new PageBean<>(jobOfSiteDimensionPeriodQo.getPageNum(), jobOfSiteDimensionPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsOfSiteDimensionPageBean.getStartIndex();
        List<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionList = federatedJobStatisticsMapper.getPagedJobStatisticsOfSiteDimensionForPeriodDynamicRow(startIndex, jobOfSiteDimensionPeriodQo);
        jobStatisticsOfSiteDimensionPageBean.setList(jobStatisticsOfSiteDimensionList);

        //get table site rows
        List<InstitutionsWithSites> institutionsWithSites = federatedJobStatisticsMapper.findInstitutionsWithSitesPagedPeriod(startIndex, jobOfSiteDimensionPeriodQo);

        JobStatisticsOfSiteDimensionDto jobStatisticsOfSiteDimensionDto = new JobStatisticsOfSiteDimensionDto();
        jobStatisticsOfSiteDimensionDto.setJobStatisticsOfSiteDimensions(jobStatisticsOfSiteDimensionPageBean);
        jobStatisticsOfSiteDimensionDto.setSites(sites);
        jobStatisticsOfSiteDimensionDto.setInstitutionsWithSites(institutionsWithSites);
        return jobStatisticsOfSiteDimensionDto;
    }


    public PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> getJobStatisticsSummaryInstitutionsEachForPeriod(JobStatisticsSummaryForPeriodQo jobStatisticsSummaryForPeriodQo) {

        List<String> activatedInstitutionsPeriod = federatedJobStatisticsMapper.getActivatedInstitutionsPeriod(jobStatisticsSummaryForPeriodQo);
        PageBean<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtoPageBean = new PageBean<JobStatisticsSummaryTodayInstitutionsEachDto>(jobStatisticsSummaryForPeriodQo.getPageNum(), jobStatisticsSummaryForPeriodQo.getPageSize(), activatedInstitutionsPeriod.size());

        long startIndex = jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodayInstitutionsEachDto> jobStatisticsSummaryTodayInstitutionsEachDtos = federatedJobStatisticsMapper.getInstitutionPeriod(startIndex, jobStatisticsSummaryForPeriodQo);
        jobStatisticsSummaryTodayInstitutionsEachDtoPageBean.setList(jobStatisticsSummaryTodayInstitutionsEachDtos);
        return jobStatisticsSummaryTodayInstitutionsEachDtoPageBean;

    }

    public JobStatisticsSummaryTodaySiteAllDto getJobStatisticsSummarySiteAllForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {
        JobStatisticsSummaryTodaySiteAllDto jobStatisticsSummaryTodaySiteAllDto = federatedJobStatisticsMapper.getJobStatisticsSummarySiteAllForPeriod(jobStatisticsSummarySiteAllForPeriodQo);

        return jobStatisticsSummaryTodaySiteAllDto;
    }

    public PageBean<JobStatisticsSummaryTodaySiteEachDto> getJobStatisticsSummarySiteEachForPeriod(JobStatisticsSummarySiteAllForPeriodQo jobStatisticsSummarySiteAllForPeriodQo) {

        long count = federatedJobStatisticsMapper.getJobStatisticsSummarySiteEachForPeriodCount(jobStatisticsSummarySiteAllForPeriodQo);
        PageBean<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtoPageBean = new PageBean<>(jobStatisticsSummarySiteAllForPeriodQo.getPageNum(), jobStatisticsSummarySiteAllForPeriodQo.getPageSize(), count);
        long startIndex = jobStatisticsSummaryTodaySiteEachDtoPageBean.getStartIndex();
        List<JobStatisticsSummaryTodaySiteEachDto> jobStatisticsSummaryTodaySiteEachDtos = federatedJobStatisticsMapper.getJobStatisticsSummarySiteEachForPeriod(startIndex, jobStatisticsSummarySiteAllForPeriodQo);

        jobStatisticsSummaryTodaySiteEachDtoPageBean.setList(jobStatisticsSummaryTodaySiteEachDtos);
        return jobStatisticsSummaryTodaySiteEachDtoPageBean;
    }
}
