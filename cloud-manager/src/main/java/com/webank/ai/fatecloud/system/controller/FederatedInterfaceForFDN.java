package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.qo.SiteListQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedIpManagerServiceFacade;
import com.webank.ai.fatecloud.system.service.facade.FederatedSiteManagerServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api")
@Slf4j
@Api(tags = "FederatedInterfaceForFDN", description = "interface for FDN")
public class FederatedInterfaceForFDN {

    @Autowired
    FederatedSiteManagerServiceFacade federatedSiteManagerServiceFacade;


    @PostMapping(value = "/site/findOneSite")
    @ApiOperation(value = "find site info for fate manager for fate manager")
    public CommonResponse findOneSite(HttpServletRequest httpServletRequest) {
        log.info("RequestBody:{}", httpServletRequest);
        return federatedSiteManagerServiceFacade.findOneSiteForFDN(httpServletRequest);
    }

    @PostMapping(value = "/site/checkAuthority")
    @ApiOperation(value = "check site authority in http body for fate manager")
    public CommonResponse checkSiteAuthority(@RequestBody String siteInfo, HttpServletRequest httpServletRequest) {
        log.info("RequestBody:{}", siteInfo);
        return federatedSiteManagerServiceFacade.checkSiteAuthorityForFDN(siteInfo, httpServletRequest);
    }

    @PostMapping(value = "/site/heart")
    @ApiOperation(value = "heart check for fate manager")
    public CommonResponse heart(HttpServletRequest httpServletRequest) {
        log.info("RequestBody:{}", httpServletRequest);
        return federatedSiteManagerServiceFacade.heartForFDN(httpServletRequest);
    }

    @PostMapping(value = "/site/page")
    @ApiOperation(value = "find all sites by page")
    public CommonResponse<PageBean<SiteDetailDto>> findPagedSites(@RequestBody SiteListQo siteListQo) {
        log.info("RequestBody:{}", siteListQo);
        return federatedSiteManagerServiceFacade.findPagedSitesForFDN(siteListQo);
    }

}
