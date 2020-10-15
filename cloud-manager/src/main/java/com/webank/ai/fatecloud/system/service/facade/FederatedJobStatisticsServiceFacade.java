package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.qo.JobInformationQo;
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

    public CommonResponse pushJobInformation(List<JobInformationQo> jobInformationQos) {
        //check authority
//        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(authorityInstitutionsQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
//        if (!result) {
//            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
//        }
        if(!(jobInformationQos.size()>0)){
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        federatedJobStatisticsService.pushJobInformation(jobInformationQos);

    }
}
