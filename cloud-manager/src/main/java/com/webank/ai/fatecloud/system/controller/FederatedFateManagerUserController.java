package com.webank.ai.fatecloud.system.controller;


import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFateManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedFateManagerUserServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/fate/user")
@Slf4j
@Api(tags = "FederatedFateManagerUserController", description = "federated fate manager user controller api")
public class FederatedFateManagerUserController {

    @Autowired
    FederatedFateManagerUserServiceFacade federatedFateManagerUserServiceFacade;

    @PostMapping(value = "/add")
    @ApiOperation(value = "add fate manager to cloud manager")
    public CommonResponse<String> addFateManagerUser(@RequestBody FateManagerUserAddQo fateManagerUserAddQo) throws UnsupportedEncodingException {
        log.info("RequestBody:{}", fateManagerUserAddQo);
        return federatedFateManagerUserServiceFacade.addFateManagerUser(fateManagerUserAddQo);

    }

    @PostMapping(value = "/check")
    @ApiOperation(value = "check institution if used or not ")
    public CommonResponse checkInstitution(@RequestBody InstitutionCheckQo institutionCheckQo) {
        log.info("RequestBody:{}", institutionCheckQo);
        return federatedFateManagerUserServiceFacade.checkInstitution(institutionCheckQo);

    }


    @PostMapping(value = "/activate")
    @ApiOperation(value = "activate fate manager user")
    public CommonResponse activateFateManagerUser(HttpServletRequest httpServletRequest) {
        log.info("RequestBody:{}", httpServletRequest);
        return federatedFateManagerUserServiceFacade.activateFateManagerUser(httpServletRequest);

    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete institution if used or not ")
    public CommonResponse deleteFateManagerUser(@RequestBody FateManagerUserDeleteQo fateManagerUserDeleteQo) {
        log.info("RequestBody:{}", fateManagerUserDeleteQo);
        return federatedFateManagerUserServiceFacade.deleteFateManagerUser(fateManagerUserDeleteQo);

    }

    @PostMapping(value = "/institutions")
    @ApiOperation(value = "all institutions")
    public CommonResponse<List<String>> findAllFateManagerUserInstitutions() {
        return federatedFateManagerUserServiceFacade.findAllFateManagerUserInstitutions();

    }

    @PostMapping(value = "/find/page")
    @ApiOperation(value = "find paged fate manager user")
    public CommonResponse<PageBean<FederatedFateManagerUserDo>> findPagedCloudManagerUser(@RequestBody FateManagerUserPagedQo fateManagerUserPagedQo) {
        log.info("RequestBody:{}", fateManagerUserPagedQo);
        return federatedFateManagerUserServiceFacade.findPagedFateManagerUser(fateManagerUserPagedQo);

    }


}
