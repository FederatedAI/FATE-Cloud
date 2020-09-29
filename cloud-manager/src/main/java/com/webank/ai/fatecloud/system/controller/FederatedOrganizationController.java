package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.dto.RegisteredOrganizationDto;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedOrganizationRegisterQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedOrganizationServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/federation")
@Api(tags = "WelcomePageController", description = "Welcome Page")
@CrossOrigin
public class FederatedOrganizationController {
    @Autowired
    FederatedOrganizationServiceFacade federatedOrganizationServiceFacade;

    @GetMapping(value = "/find")
    @ApiOperation(value = "Find Federation If Register")
    public CommonResponse<FederatedOrganizationDto> findFederatedOrganization() {
        return federatedOrganizationServiceFacade.findFederatedOrganization();
    }

    @PostMapping(value = "/register")
    @ApiOperation(value = "Register Federation")
    public CommonResponse registerFederation(@RequestBody FederatedOrganizationRegisterQo federationRegisterQo) {
        return federatedOrganizationServiceFacade.registerFederatedOrganization(federationRegisterQo);
    }

    //interface for fate manager

    @GetMapping(value = "/findOrganization")
    @ApiOperation(value = "Find Federation If Register")
    public CommonResponse<RegisteredOrganizationDto> findRegisteredOrganization(HttpServletRequest httpServletRequest) {
        return federatedOrganizationServiceFacade.findRegisteredOrganization(httpServletRequest);
    }

}
