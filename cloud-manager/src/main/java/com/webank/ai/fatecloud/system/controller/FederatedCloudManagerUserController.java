package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedCloudManagerUserServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/api/cloud/user")
@Slf4j
@Api(tags = "FederatedCloudManagerUserController", description = "cloud manager user api")

public class FederatedCloudManagerUserController {

    @Autowired
    FederatedCloudManagerUserServiceFacade federatedCloudManagerUserServiceFacade;

    @PostMapping(value = "/add")
    @ApiOperation(value = "add cloud manager user")
    public CommonResponse<FederatedCloudManagerUserDo> addCloudManagerUser(@RequestBody CloudManagerUserAddQo cloudManagerUserAddQo) {
        log.info("RequestBody:{}", cloudManagerUserAddQo);
        return federatedCloudManagerUserServiceFacade.addCloudManagerUser(cloudManagerUserAddQo);

    }


    @PostMapping(value = "/find/page")
    @ApiOperation(value = "find paged cloud manager user")
    public CommonResponse<PageBean<FederatedCloudManagerUserDo>> findPagedCloudManagerUser(@RequestBody CloudManagerUserPagedQo cloudManagerUserPagedQo) {
        log.info("RequestBody:{}", cloudManagerUserPagedQo);
        return federatedCloudManagerUserServiceFacade.findPagedCloudManagerUser(cloudManagerUserPagedQo);

    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete cloud manager user")
    public CommonResponse deleteCloudManagerUser(@RequestBody CloudManagerUserDeleteQo cloudManagerUserDeleteQo) {
        log.info("RequestBody:{}", cloudManagerUserDeleteQo);
        return federatedCloudManagerUserServiceFacade.deleteCloudManagerUser(cloudManagerUserDeleteQo);

    }

    @PostMapping(value = "/login")
    @ApiOperation(value = "cloud manager user log in")
    public CommonResponse loginCloudManagerUser(@RequestBody CloudManagerUserLoginQo cloudManagerUserLoginQo, HttpSession httpSession) {
        log.info("RequestBody:{}", cloudManagerUserLoginQo);
        return federatedCloudManagerUserServiceFacade.loginCloudManagerUser(cloudManagerUserLoginQo, httpSession);

    }

    @PostMapping(value = "/logout")
    @ApiOperation(value = "cloud manager user log out")
    public CommonResponse logoutCloudManagerUser(HttpSession httpSession) {
        log.info("RequestBody:{}", httpSession);
        return federatedCloudManagerUserServiceFacade.logoutCloudManagerUser(httpSession);

    }
}
