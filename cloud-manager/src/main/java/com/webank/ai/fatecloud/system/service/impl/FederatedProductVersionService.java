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
package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedComponentVersionDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedProductVersionDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedComponentVersionMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedProductVersionMapper;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionAddDto;
import com.webank.ai.fatecloud.system.pojo.dto.ProductVersionDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class FederatedProductVersionService implements Serializable {

    @Autowired
    FederatedProductVersionMapper federatedProductVersionMapper;

    @Autowired
    FederatedComponentVersionMapper federatedComponentVersionMapper;

    public ProductVersionAddDto add(ProductVersionAddQo productVersionAddQo) {
        //add product version item
        FederatedProductVersionDo federatedProductVersionDo = new FederatedProductVersionDo(productVersionAddQo);
        federatedProductVersionMapper.insert(federatedProductVersionDo);

        //add component version items
        Long productId = federatedProductVersionDo.getProductId();
        List<ComponentVersionAddQo> componentVersionAddQos = productVersionAddQo.getComponentVersionAddQos();
        for (ComponentVersionAddQo componentVersionAddQo : componentVersionAddQos) {
            FederatedComponentVersionDo federatedComponentVersionDo = new FederatedComponentVersionDo(componentVersionAddQo);
            federatedComponentVersionDo.setProductId(productId);
            federatedComponentVersionMapper.insert(federatedComponentVersionDo);
        }
        ProductVersionAddDto productVersionAddDto = new ProductVersionAddDto();
        productVersionAddDto.setProductId(productId);
        return productVersionAddDto;

    }

    public void delete(ProductVersionAddDto productVersionAddDto) {
        //delete product version item
        federatedProductVersionMapper.deleteById(productVersionAddDto.getProductId());

        //delete component version items
        QueryWrapper<FederatedComponentVersionDo> federatedComponentVersionDoQueryWrapper = new QueryWrapper<>();
        federatedComponentVersionDoQueryWrapper.eq("product_id", productVersionAddDto.getProductId());
        federatedComponentVersionMapper.delete(federatedComponentVersionDoQueryWrapper);

    }

    public void update(ProductVersionUpdateQo productVersionUpdateQo) {

        //add product version item
        FederatedProductVersionDo federatedProductVersionDo = new FederatedProductVersionDo(productVersionUpdateQo);
        federatedProductVersionMapper.updateById(federatedProductVersionDo);

        //delete component version items
        Long productId = productVersionUpdateQo.getProductId();

        QueryWrapper<FederatedComponentVersionDo> federatedComponentVersionDoQueryWrapper = new QueryWrapper<>();
        federatedComponentVersionDoQueryWrapper.eq("product_id", productId);
        federatedComponentVersionMapper.delete(federatedComponentVersionDoQueryWrapper);

        //add component version items
        List<ComponentVersionAddQo> componentVersionAddQos = productVersionUpdateQo.getComponentVersionAddQos();
        for (ComponentVersionAddQo componentVersionAddQo : componentVersionAddQos) {
            FederatedComponentVersionDo federatedComponentVersionDo = new FederatedComponentVersionDo(componentVersionAddQo);
            federatedComponentVersionDo.setProductId(productId);
            federatedComponentVersionMapper.insert(federatedComponentVersionDo);
        }

    }

    public PageBean<FederatedProductVersionDo> page(ProductVersionPageQo productVersionPageQo) {
        long count = federatedProductVersionMapper.count(productVersionPageQo);

        PageBean<FederatedProductVersionDo> federatedProductVersionDoPageBean = new PageBean<>(productVersionPageQo.getPageNum(), productVersionPageQo.getPageSize(), count);
        long startIndex = federatedProductVersionDoPageBean.getStartIndex();
        List<FederatedProductVersionDo> productVersionPageDtoList = federatedProductVersionMapper.page(startIndex, productVersionPageQo);

        federatedProductVersionDoPageBean.setList(productVersionPageDtoList);

        return federatedProductVersionDoPageBean;

    }

    public PageBean<FederatedProductVersionDo> pageForFateManager(ProductVersionPageForFateManagerQo productVersionPageForFateManagerQo ) {
        long count = federatedProductVersionMapper.countForFateManager(productVersionPageForFateManagerQo);

        PageBean<FederatedProductVersionDo> federatedProductVersionDoPageBean = new PageBean<>(productVersionPageForFateManagerQo.getPageNum(), productVersionPageForFateManagerQo.getPageSize(), count);
        long startIndex = federatedProductVersionDoPageBean.getStartIndex();
        List<FederatedProductVersionDo> productVersionPageDtoList = federatedProductVersionMapper.pageForFateManager(startIndex, productVersionPageForFateManagerQo);

        federatedProductVersionDoPageBean.setList(productVersionPageDtoList);

        return federatedProductVersionDoPageBean;

    }

    public ProductVersionDto findVersion() {
        List<String> productNames = federatedProductVersionMapper.getProductNames();
        List<String> productVersions = federatedProductVersionMapper.getProductVersions();

        ProductVersionDto productVersionDto = new ProductVersionDto();
        productVersionDto.setProductNameList(productNames);
        productVersionDto.setProductVersionList(productVersions);
        return productVersionDto;
    }

    public List<String> findName() throws Exception {

        Class<Enum> clz = (Class<Enum>) Class.forName("com.webank.ai.fatecloud.common.Enum.ProductVersionEnum");

        Object[] objects = clz.getEnumConstants();
        Method getName = clz.getMethod("getName");
        List<String> productNames = new ArrayList<>();
        for (Object obj : objects) {
            productNames.add((String) getName.invoke(obj));
        }

        return productNames;
    }

}
