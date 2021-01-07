/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.CheckVersion;
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
        if (!CheckVersion.checkVersion(productVersion)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        List<ComponentVersionAddQo> componentVersionAddQos = productVersionAddQo.getComponentVersionAddQos();
        for (ComponentVersionAddQo componentVersionAddQo : componentVersionAddQos) {
            String componentVersion = componentVersionAddQo.getComponentVersion();
            if (!CheckVersion.checkVersion(componentVersion)) {
                return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
            }
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

    public CommonResponse<ProductVersionDto> findVersion()   {
        ProductVersionDto productVersionDto = federatedProductVersionService.findVersion();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, productVersionDto);
    }

    public CommonResponse<List<String>> findName() throws Exception {
        List productNames = federatedProductVersionService.findName();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, productNames);
    }

    public CommonResponse<PageBean<FederatedProductVersionDo>> pageForFateManager(ProductVersionPageForFateManagerQo productVersionPageForFateManagerQo, HttpServletRequest httpServletRequest) {
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(productVersionPageForFateManagerQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }
        PageBean<FederatedProductVersionDo> page = federatedProductVersionService.pageForFateManager(productVersionPageForFateManagerQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, page);
    }

}
