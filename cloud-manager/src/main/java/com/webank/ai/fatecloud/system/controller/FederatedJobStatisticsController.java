package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
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
    @ApiOperation(value = "push job statistics of Site")
    public CommonResponse pushJosStatistics(@Valid @RequestBody List<JobStatisticsQo> jobStatisticsQos, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobStatisticsQos);
        if (bindingResult.hasErrors()) {
            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR, errors.getDefaultMessage());
        }
        return federatedJobStatisticsServiceFacade.pushJosStatistics(jobStatisticsQos);
    }

    @PostMapping(value = "/site")
    @ApiOperation(value = "find job statistics of Site dimension")
    public CommonResponse getJobStatisticsOfSiteDimension(@Valid @RequestBody JobOfSiteDimensionQo jobOfSiteDimensionQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", jobOfSiteDimensionQo);
        if (bindingResult.hasErrors()) {
            FieldError errors = bindingResult.getFieldError();
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR, errors.getDefaultMessage());
        }
        return federatedJobStatisticsServiceFacade.getJobStatisticsOfSiteDimension(jobOfSiteDimensionQo);
    }
}
