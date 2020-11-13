package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class FederatedModelServiceFacade {
    @Autowired
    FederatedModelService federatedModelService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse modelHeart(ArrayList<ModelHeartQo> modelHeartQos, HttpServletRequest httpServletRequest) {
        HashSet<Long> ids = new HashSet<>();
        for (ModelHeartQo modelHeartQo : modelHeartQos) {

            if (modelHeartQo.getInstallItems() == null || 0 == modelHeartQo.getInstallItems().trim().length()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }

            if (modelHeartQo.getVersion() == null || 0 == modelHeartQo.getVersion().trim().length()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }

            if (modelHeartQo.getId() == null) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            } else {
                ids.add(modelHeartQo.getId());
            }
            if (modelHeartQo.getDetectiveStatus() == null) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
        }
        if (ids.size() > 1) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);

        }

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(modelHeartQos), Dict.FATE_SITE_USER, new int[]{2}, 2);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        federatedModelService.modelHeart(modelHeartQos);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }


    public CommonResponse addModelNew(List<ModelAddQo> modelAddQos, HttpServletRequest httpServletRequest) {
        HashSet<Long> ids = new HashSet<>();
        for (ModelAddQo modelAddQo : modelAddQos) {
            if (modelAddQo.getInstallItems() == null || 0 == modelAddQo.getInstallItems().trim().length()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            if (modelAddQo.getVersion() == null || 0 == modelAddQo.getVersion().trim().length()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            if (modelAddQo.getType() == null || 0 == modelAddQo.getType().trim().length()) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            if (modelAddQo.getId() == null) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            } else {
                ids.add(modelAddQo.getId());
            }
            if (modelAddQo.getUpdateStatus() == null) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
            if (modelAddQo.getDetectiveStatus() == null) {
                return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
            }
        }
        if (ids.size() > 1) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);

        }

//        boolean result = checkSignature.checkSignature(httpServletRequest, JSON.toJSONString(modelAddQos), 2);
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(modelAddQos), Dict.FATE_SITE_USER, new int[]{2}, 2);

        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        federatedModelService.addModelNew(modelAddQos);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<FederatedSiteManagerDo>> findPagedSitesWithModel(SiteListWithModelsQo siteListWithModelsQo) {
        PageBean<FederatedSiteManagerDo> pagedSites = federatedModelService.findPagedSitesWithModel(siteListWithModelsQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedSites);

    }

    public CommonResponse findModelHistory(ModelHistoryQo modelHistoryQo) {
        if (modelHistoryQo.getId() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (modelHistoryQo.getInstallItems() == null || 0 == modelHistoryQo.getInstallItems().trim().length()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        List<FederatedSiteModelDo> modelHistory = federatedModelService.findModelHistory(modelHistoryQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, modelHistory);
    }

    public CommonResponse findSitesWithModel(OneSiteQo oneSiteQo) {
        if (oneSiteQo.getId() == null) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        List<FederatedSiteModelDo> sitesWithModel = federatedModelService.findSitesWithModel(oneSiteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, sitesWithModel);
    }

}
