package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.pojo.qo.ModelAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ModelHistoryQo;
import com.webank.ai.fatecloud.system.pojo.qo.OneSiteQo;
import com.webank.ai.fatecloud.system.pojo.qo.SiteListWithModelsQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedModelServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/system")
@CrossOrigin
@Api(tags = "FederatedSystemController",description = "Federation System Interface")
@Data
@Slf4j
public class FederatedSystemController {
    @Autowired
    FederatedModelServiceFacade federatedModelServiceFacade;


    @PostMapping(value = "/add")
    @ApiOperation(value = "add model information for site")
    public CommonResponse addModelNew(@RequestBody ArrayList<ModelAddQo> modelAddQos, HttpServletRequest httpServletRequest){
        log.info("RequestBody:{} Http:{}", modelAddQos,httpServletRequest);
        return federatedModelServiceFacade.addModelNew(modelAddQos,httpServletRequest);
    }

    @PostMapping(value = "/page")
    @ApiOperation(value = "find paged model information of site")
    public CommonResponse<PageBean<FederatedSiteManagerDo>> findPagedSitesWithModel(@RequestBody SiteListWithModelsQo siteListWithModelsQo){
        log.info("RequestBody:{}", siteListWithModelsQo);
        return federatedModelServiceFacade.findPagedSitesWithModel(siteListWithModelsQo);
    }

    @PostMapping(value = "/history")
    @ApiOperation(value = "find update history of model")
    public CommonResponse<List<FederatedSiteModelDo>> findModelHistory(@RequestBody ModelHistoryQo modelHistoryQo){
        log.info("RequestBody:{}", modelHistoryQo);
        return federatedModelServiceFacade.findModelHistory(modelHistoryQo);
    }

    @PostMapping(value = "/find")
    @ApiOperation(value = "find model information of site")
    public CommonResponse<List<FederatedSiteModelDo>> findSitesWithModel(@RequestBody OneSiteQo oneSiteQo){
        log.info("RequestBody:{}", oneSiteQo);
        return federatedModelServiceFacade.findSitesWithModel(oneSiteQo);
    }

}
