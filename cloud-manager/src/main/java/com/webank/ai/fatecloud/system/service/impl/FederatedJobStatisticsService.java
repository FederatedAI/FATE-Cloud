package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedJobStatisticsMapper;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticsOfSiteDimension;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class FederatedJobStatisticsService {

    @Autowired
    FederatedJobStatisticsMapper federatedJobStatisticsMapper;

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

    public List<JobStatisticsOfSiteDimension> getJobStatisticsOfSiteDimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {

        //todo
        List<JobStatisticsOfSiteDimension> jobStatisticsOfSiteDimensionList = federatedJobStatisticsMapper.getJobStatisticsOfSiteDimension(jobOfSiteDimensionQo);


        return jobStatisticsOfSiteDimensionList;
    }
}
