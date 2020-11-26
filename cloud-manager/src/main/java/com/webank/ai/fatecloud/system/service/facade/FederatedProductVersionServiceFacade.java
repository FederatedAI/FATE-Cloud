package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionAddDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedProductVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


@Slf4j
@Service
public class FederatedProductVersionServiceFacade implements Serializable {

    @Autowired
    FederatedProductVersionService federatedProductVersionService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse<ProductVersionAddDto> add(ProductVersionAddQo productVersionAddQo) {
         String productVersion = productVersionAddQo.getProductVersion();
         List<ComponentVersionAddQo> componentVersionAddQos = productVersionAddQo.getComponentVersionAddQos();
        for (ComponentVersionAddQo componentVersionAddQo : componentVersionAddQos) {

        }


        ProductVersionAddDto productVersionAddDto = federatedProductVersionService.add(productVersionAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, productVersionAddDto);

    }

    public CommonResponse delete(ProductVersionAddDto productVersionAddDto) {
        federatedProductVersionService.delete(productVersionAddDto);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse update(ProductVersionUpdateQo productVersionUpdateQo) {
        federatedProductVersionService.update(productVersionUpdateQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<FederatedProductVersionDo>> page(ProductVersionPageQo productVersionPageQo) {

        PageBean<FederatedProductVersionDo> page = federatedProductVersionService.page(productVersionPageQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, page);

    }

    public CommonResponse<ProductVersionDto> findVersion() {
        ProductVersionDto productVersionDto = federatedProductVersionService.findVersion();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, productVersionDto);
    }

    public CommonResponse<PageBean<FederatedProductVersionDo>> pageForFateManager(ProductVersionPageQo productVersionPageQo, HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, "", Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        PageBean<FederatedProductVersionDo> page = federatedProductVersionService.page(productVersionPageQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, page);
    }
}
