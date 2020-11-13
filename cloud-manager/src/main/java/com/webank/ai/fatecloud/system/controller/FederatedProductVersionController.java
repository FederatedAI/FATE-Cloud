package com.webank.ai.fatecloud.system.controller;


import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionPageQo;
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
    public CommonResponse add(@RequestBody ProductVersionAddQo productVersionAddQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionAddQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.add(productVersionAddQo);
    }
    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete product and component item")
    public CommonResponse delete(@RequestBody ProductVersionDeleteQo productVersionDeleteQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionDeleteQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.delete(productVersionDeleteQo);
    }

    @PostMapping(value = "/update")
    @ApiOperation(value = "update product and component item")
    public CommonResponse update(@RequestBody ProductVersionAddQo productVersionAddQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionAddQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.update(productVersionAddQo);
    }


    @PostMapping(value = "/page")
    @ApiOperation(value = "find paged items")
    public CommonResponse<PageBean<ProductVersionPageDto>> page(@RequestBody ProductVersionPageQo productVersionPageQo, BindingResult bindingResult) {
        log.info("RequestBody:{}", productVersionPageQo);
        if (bindingResult.hasErrors()) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedProductVersionServiceFacade.page(productVersionPageQo);
    }

}
