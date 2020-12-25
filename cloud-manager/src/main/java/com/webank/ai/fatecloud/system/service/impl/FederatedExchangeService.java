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
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedExchangeMapper;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeUpdateQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
public class FederatedExchangeService implements Serializable {
    @Autowired
    FederatedExchangeMapper federatedExchangeMapper;


    public FederatedExchangeDo addExchange(ExchangeAddQo exchangeAddQo) {
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeName(exchangeAddQo.getExchangeName());
        federatedExchangeDo.setNetworkAccessEntrances(exchangeAddQo.getNetworkAccessEntrances());
        federatedExchangeDo.setNetworkAccessExits(exchangeAddQo.getNetworkAccessExits());
        federatedExchangeMapper.insert(federatedExchangeDo);
        return federatedExchangeDo;
    }

    public void deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {

        federatedExchangeMapper.deleteById(exchangeDeleteQo.getExchangeId());
    }

    public void updateExchange(ExchangeUpdateQo exchangeUpdateQo) {
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(exchangeUpdateQo.getExchangeId());
        federatedExchangeDo.setExchangeName(exchangeUpdateQo.getExchangeName());
        federatedExchangeDo.setNetworkAccessEntrances(exchangeUpdateQo.getNetworkAccessEntrances());
        federatedExchangeDo.setNetworkAccessExits(exchangeUpdateQo.getNetworkAccessExits());
        federatedExchangeMapper.updateById(federatedExchangeDo);

    }

    public PageBean<FederatedExchangeDo> findExchangePage(ExchangePageQo exchangePageQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        PageBean<FederatedExchangeDo> siteDetailDtoPageBean = new PageBean<>(exchangePageQo.getPageNum(), exchangePageQo.getPageSize(), count);
        long startIndex = siteDetailDtoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDoList=federatedExchangeMapper.findExchangePage(startIndex,exchangePageQo);
        siteDetailDtoPageBean.setList(federatedExchangeDoList);
        return siteDetailDtoPageBean;

    }

    public boolean checkExchangeName(ExchangeAddQo exchangeAddQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_name",exchangeAddQo.getExchangeName());
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        return count > 0;
    }
}
