package com.webank.ai.fatecloud.system.service.facade;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionAddDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionPageQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionUpdateQo;
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

    public CommonResponse<ProductVersionAddDto> add(ProductVersionAddQo productVersionAddQo) {

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
}
