package com.webank.ai.fatecloud.system.service.facade;

import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticOfInstitutionsDimensionDto;
import com.webank.ai.fatecloud.system.pojo.dto.JobStatisticsOfSiteDimensionDto;
import com.webank.ai.fatecloud.system.pojo.qo.JobOfSiteDimensionQo;
import com.webank.ai.fatecloud.system.pojo.qo.JobStatisticsQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedJobStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FederatedJobStatisticsServiceFacade {

    @Autowired
    FederatedJobStatisticsService federatedJobStatisticsService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse pushJosStatistics(List<JobStatisticsQo> jobStatisticsQos) {
        //check authority
        //todo
//        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityInstitutionsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
//        if (!result) {
//            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
//        }
        if (!(jobStatisticsQos.size() > 0)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        federatedJobStatisticsService.pushJosStatistics(jobStatisticsQos);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse<JobStatisticsOfSiteDimensionDto> getJobStatisticsOfSiteDimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {
        JobStatisticsOfSiteDimensionDto jobStatisticsOfSiteDimensionDto = federatedJobStatisticsService.getJobStatisticsOfSiteDimension(jobOfSiteDimensionQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsOfSiteDimensionDto);
    }

    public CommonResponse<List<JobStatisticOfInstitutionsDimensionDto>> getJobStatisticsODimension(JobOfSiteDimensionQo jobOfSiteDimensionQo) {
        List<JobStatisticOfInstitutionsDimensionDto> jobStatisticsODimensionList = federatedJobStatisticsService.getJobStatisticsODimension(jobOfSiteDimensionQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, jobStatisticsODimensionList);

    }
}
