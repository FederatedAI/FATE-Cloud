package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.dto.RegisteredOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedOrganizationService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class FederatedOrganizationServiceFacade {
    @Autowired
    FederatedOrganizationService federatedOrganizationService;

    @Autowired
    CheckSignature checkSignature;

    @Autowired
    FederatedSiteManagerService federatedSiteManagerService;

    public CommonResponse registerFederatedOrganization(FederatedOrganizationRegisterQo federatedOrganizationRegisterQo) {
        if (0 == federatedOrganizationService.registerFederatedOrganization(federatedOrganizationRegisterQo)) {
            return new CommonResponse(ReturnCodeEnum.REGISTER_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<FederatedOrganizationDto> findFederatedOrganization() {
        FederatedOrganizationDto federatedOrganization = federatedOrganizationService.findFederatedOrganization();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedOrganization);
    }

    public CommonResponse<RegisteredOrganizationDto> findRegisteredOrganization(HttpServletRequest httpServletRequest) {
//        boolean result = checkSignature.checkSignature(httpServletRequest, "",2,3);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_SITE_USER, new int[]{2}, 2,3);
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        RegisteredOrganizationDto registeredOrganization = federatedOrganizationService.findRegisteredOrganization();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, registeredOrganization);
    }
}
