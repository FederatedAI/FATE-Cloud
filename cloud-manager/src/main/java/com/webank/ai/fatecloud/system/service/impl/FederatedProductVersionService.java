package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedProductVersionMapper;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionPageDto;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionPageQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;


@Slf4j
@Service
public class FederatedProductVersionService implements Serializable {

    @Autowired
    FederatedProductVersionMapper federatedProductVersionMapper;


    public void add(ProductVersionAddQo productVersionAddQo) {
        FederatedProductVersionDo federatedProductVersionDo = new FederatedProductVersionDo(productVersionAddQo);
        federatedProductVersionMapper.insert(federatedProductVersionDo);
    }

    public void delete(ProductVersionDeleteQo productVersionDeleteQo) {
        QueryWrapper<FederatedProductVersionDo> federatedProductVersionDoQueryWrapper = new QueryWrapper<>();
        federatedProductVersionDoQueryWrapper.eq("product_name", productVersionDeleteQo.getProductName()).eq("product_version", productVersionDeleteQo.getProductVersion()).eq("component_name", productVersionDeleteQo.getComponentName());

        federatedProductVersionMapper.delete(federatedProductVersionDoQueryWrapper);
    }

    public void update(ProductVersionAddQo productVersionAddQo) {
        FederatedProductVersionDo federatedProductVersionDo = new FederatedProductVersionDo(productVersionAddQo);
        federatedProductVersionMapper.updateById(federatedProductVersionDo);
    }

    public PageBean<ProductVersionPageDto> page(ProductVersionPageQo productVersionPageQo) {
        long count = federatedProductVersionMapper.count(productVersionPageQo);

        PageBean<ProductVersionPageDto> federatedProductVersionDoPageBean = new PageBean<ProductVersionPageDto>(productVersionPageQo.getPageNum(), productVersionPageQo.getPageSize(), count);

        long startIndex = federatedProductVersionDoPageBean.getStartIndex();
        List<ProductVersionPageDto> productVersionPageDtoList = federatedProductVersionMapper.page(startIndex, productVersionPageQo);

        federatedProductVersionDoPageBean.setList(productVersionPageDtoList);

        return federatedProductVersionDoPageBean;

    }
}
