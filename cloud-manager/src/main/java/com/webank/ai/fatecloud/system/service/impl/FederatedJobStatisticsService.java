package com.webank.ai.fatecloud.system.service.impl;

import com.webank.ai.fatecloud.system.dao.entity.FederatedJobStatisticsDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedJobStatisticsMapper;
import com.webank.ai.fatecloud.system.pojo.qo.JobInformationQo;
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
    public void pushJobInformation(List<JobInformationQo> jobInformationQos) {
        for (JobInformationQo jobInformationQo : jobInformationQos) {
            new FederatedJobStatisticsDo(jobInformationQo)
            federatedJobStatisticsMapper.insert()
        }
    }
}
