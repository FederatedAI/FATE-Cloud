package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.system.pojo.qo.JobInformationQo;
import com.webank.ai.fatecloud.system.pojo.qo.PartyIdQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedJobStatisticsServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @ApiOperation(value = "push job information of Site")
    public CommonResponse pushJobInformation(@RequestBody List<JobInformationQo> jobInformationQos) {
        log.info("RequestBody:{}", jobInformationQos);
        return federatedJobStatisticsServiceFacade.pushJobInformation(jobInformationQos);
    }
}
