package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedJobStatisticsMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.dto.InstitutionsWithSites;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticOfInstitutionsDimensionDto;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticsOfSiteDimension;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticsOfSiteDimensionDto;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

            QueryWrapper<FederatedJobStatisticsDo> federatedJobStatisticsDoQueryWrapper = new QueryWrapper<>();
            federatedJobStatisticsDoQueryWrapper.eq("site_guest_id", jobStatisticsQo.getSiteGuestId()).eq("site_host_id", jobStatisticsQo.getSiteHostId()).eq("job_finish_date", jobStatisticsQo.getJobFinishDate());
            Integer count = federatedJobStatisticsMapper.selectCount(federatedJobStatisticsDoQueryWrapper);
            if (count > 0) {
                federatedJobStatisticsMapper.update(federatedJobStatisticsDo, federatedJobStatisticsDoQueryWrapper);
            } else {
                federatedJobStatisticsMapper.insert(federatedJobStatisticsDo);
            }
        }
    }

    public JobStatisticsOfSiteDimensionDto getJobStatisticsOfSiteDimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {

        //get job statistics
        List<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionList = federatedJobStatisticsMapper.getJobStatisticsOfSiteDimension(jobOfSiteDimensionQo);

        //get table site columns
        QueryWrapper<FederatedSiteManagerDo> federatedSiteManagerDoQueryWrapper = new QueryWrapper<FederatedSiteManagerDo>();
        federatedSiteManagerDoQueryWrapper.eq("institutions", jobOfSiteDimensionQo.getInstitutions()).eq("status", 2);
        List<FederatedSiteManagerDo> federatedSiteManagerDos = federatedSiteManagerMapper.selectList(federatedSiteManagerDoQueryWrapper);
        ArrayList<String> sites = new ArrayList<>();
        for (FederatedSiteManagerDo federatedSiteManagerDo : federatedSiteManagerDos) {
            sites.add(federatedSiteManagerDo.getSiteName());
        }

        //get table site rows
        List<InstitutionsWithSites> institutionsWithSites = federatedSiteManagerMapper.findInstitutionsWithSites(jobOfSiteDimensionQo.getInstitutions());

        JobStatisticsOfSiteDimensionDto jobStatisticsOfSiteDimensionDto = new JobStatisticsOfSiteDimensionDto();
        jobStatisticsOfSiteDimensionDto.setJobStatisticsOfSiteDimensions(jobStatisticsOfSiteDimensionList);
        jobStatisticsOfSiteDimensionDto.setSites(sites);
        jobStatisticsOfSiteDimensionDto.setInstitutionsWithSites(institutionsWithSites);
        return jobStatisticsOfSiteDimensionDto;
    }

    public List<JobStatisticOfInstitutionsDimensionDto> getJobStatisticsODimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {
        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticOfInstitutionsDimensionDtos = federatedJobStatisticsMapper.getJobStatisticsODimension(jobOfSiteDimensionQo);
        return jobStatisticOfInstitutionsDimensionDtos;
    }
}
