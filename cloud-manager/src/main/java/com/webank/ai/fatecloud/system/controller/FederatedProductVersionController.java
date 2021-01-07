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
package com.webank.ai.fatecloud.system.controller;


import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionAddDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedProductVersionServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@Api(tags = "FederatedProductVersionController", description = "manager the product version")
@Data
@Slf4j
public class FederatedProductVersionController {

    @Autowired
    FederatedProductVersionServiceFacade federatedProductVersionServiceFacade;

    @PostMapping(value = "/add")
    @ApiOperation(value = "add product and component item")
    public CommonResponse<ProductVersionAddDto> add(@RequestBody ProductVersionAddQo productVersionAddQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionAddQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.add(productVersionAddQo);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete product and component item")
    public CommonResponse delete(@RequestBody ProductVersionAddDto productVersionAddDto, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionAddDto);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.delete(productVersionAddDto);
    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "update product and component item")
    public CommonResponse update(@RequestBody ProductVersionUpdateQo productVersionUpdateQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionUpdateQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.update(productVersionUpdateQo);
    }


    @PostMapping(value = "/page")
    @ApiOperation(value = "find paged items")
    public CommonResponse<PageBean<FederatedProductVersionDo>> page(@RequestBody ProductVersionPageQo productVersionPageQo) {
        log.info("RequestBody:{}", productVersionPageQo);

        return federatedProductVersionServiceFacade.page(productVersionPageQo);
    }


    @PostMapping(value = "/version")
    @ApiOperation(value = "find version items")
    public CommonResponse<ProductVersionDto> findVersion() {

        return federatedProductVersionServiceFacade.findVersion();
    }

    @PostMapping(value = "/name")
    @ApiOperation(value = "find product name")
    public CommonResponse<List<String>> findName() throws Exception {

        return federatedProductVersionServiceFacade.findName();
    }

    //interfaces for fate manager
    @PostMapping(value = "/page/fatemanager")
    @ApiOperation(value = "find paged items for fate manager")
    public CommonResponse<PageBean<FederatedProductVersionDo>> pageForFateManager(@RequestBody ProductVersionPageForFateManagerQo productVersionPageForFateManagerQo, HttpServletRequest httpServletRequest) {
        log.info("RequestBody:{}", productVersionPageForFateManagerQo);
        return federatedProductVersionServiceFacade.pageForFateManager(productVersionPageForFateManagerQo, httpServletRequest);
    }


}
