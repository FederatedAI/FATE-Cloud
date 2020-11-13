package com.webank.ai.fatecloud.system.service.facade;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionPageQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedProductVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;



@Slf4j
@Service
public class FederatedProductVersionServiceFacade implements Serializable {

    @Autowired
    FederatedProductVersionService federatedProductVersionService;

    public CommonResponse add(ProductVersionAddQo productVersionAddQo) {

        federatedProductVersionService.add(productVersionAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse delete(ProductVersionDeleteQo productVersionDeleteQo) {
        federatedProductVersionService.delete(productVersionDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse update(ProductVersionAddQo productVersionAddQo) {
        federatedProductVersionService.update(productVersionAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<ProductVersionPageDto>> page(ProductVersionPageQo productVersionPageQo) {

        PageBean<ProductVersionPageDto> page = federatedProductVersionService.page(productVersionPageQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS,page);

    }
}
