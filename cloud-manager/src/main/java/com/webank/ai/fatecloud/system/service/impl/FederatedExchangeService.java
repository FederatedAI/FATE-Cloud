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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.grpc.ExchangeGrpcUtil;
import com.webank.ai.fatecloud.grpc.NetworkBean;
import com.webank.ai.fatecloud.system.dao.entity.ExchangeDetailsDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.mapper.ExchangeDetailsMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedExchangeMapper;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FederatedExchangeService implements Serializable {
    @Autowired
    FederatedExchangeMapper federatedExchangeMapper;

    @Autowired
    ExchangeDetailsMapper exchangeDetailsMapper;

    private void updateRouteTableJsonString(ExchangeAddQo exchangeAddQo) throws UnsupportedEncodingException {

        //build route table string
        HashMap<String, Object> routeTableMap = new HashMap<>();
        List<ExchangeDetailsAddBean> exchangeDetailsAddBeanList = exchangeAddQo.getExchangeDetailsAddBeanList();
        for (ExchangeDetailsAddBean exchangeDetailsAddBean : exchangeDetailsAddBeanList) {
            String[] partIdNetwork = exchangeDetailsAddBean.getNetworkAccess().split(":");
            NetworkBean networkBean = new NetworkBean();
            networkBean.setIp(partIdNetwork[0]);
            networkBean.setPort(Integer.valueOf(partIdNetwork[1]));

            HashMap<String, List<NetworkBean>> defaultMap = new HashMap<>();
            ArrayList<NetworkBean> networkBeans = new ArrayList<>();
            networkBeans.add(networkBean);
            defaultMap.put("default", networkBeans);

            String partyId = exchangeDetailsAddBean.getPartyId();
            routeTableMap.put(partyId, defaultMap);
        }

        HashMap<String, Boolean> permission = new HashMap<>();
        permission.put("default_allow", true);

        HashMap<String, Object> routeTableFinalObject = new HashMap<>();
        routeTableFinalObject.put("route_table", routeTableMap);
        routeTableFinalObject.put("permission", permission);

        String routeTableJsonString = JSON.toJSONString(routeTableFinalObject);
        log.info("exchange string to add:{}", routeTableJsonString);

        //send grpc request
        String[] network = exchangeAddQo.getNetworkAccess().split(":");
        Proxy.Packet packet = ExchangeGrpcUtil.setExchange(network[0], Integer.parseInt(network[1]), "eggroll", routeTableJsonString, "10002", "set_route_table");
        //todo

    }


    @Transactional
    public FederatedExchangeDo addExchange(ExchangeAddQo exchangeAddQo) {

        //update rollsite
        try {
            this.updateRouteTableJsonString(exchangeAddQo);
        } catch (UnsupportedEncodingException e) {
            log.error("update route table error by grpc ", e);
            return null;
        }

        //add exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeName(exchangeAddQo.getExchangeName());
        federatedExchangeDo.setNetworkAccess(exchangeAddQo.getNetworkAccess());
        federatedExchangeDo.setStatus(1);
        federatedExchangeMapper.insert(federatedExchangeDo);

        //add exchange details table
        List<ExchangeDetailsAddBean> exchangeDetailsAddBeanList = exchangeAddQo.getExchangeDetailsAddBeanList();
        for (ExchangeDetailsAddBean exchangeDetailsAddBean : exchangeDetailsAddBeanList) {
            ExchangeDetailsDo exchangeDetailsDo = new ExchangeDetailsDo();
            exchangeDetailsDo.setExchangeDetailsId(federatedExchangeDo.getExchangeId());
            exchangeDetailsDo.setBatch(1L);
            exchangeDetailsDo.setPartyId(exchangeDetailsAddBean.getPartyId());
            exchangeDetailsDo.setNetworkAccess(exchangeDetailsAddBean.getNetworkAccess());
            exchangeDetailsMapper.insert(exchangeDetailsDo);
        }

        return federatedExchangeDo;
    }

    @Transactional
    public void deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {
        //update exchange table
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchangeId", exchangeDeleteQo.getExchangeId());
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setStatus(2);
        federatedExchangeMapper.update(federatedExchangeDo, federatedExchangeDoQueryWrapper);

    }

    @Transactional
    public FederatedExchangeDo updateExchange(ExchangeUpdateQo exchangeUpdateQo) {

        //update rollsite
        try {
            this.updateRouteTableJsonString(new ExchangeAddQo(exchangeUpdateQo));
        } catch (UnsupportedEncodingException e) {
            log.error("update route table error by grpc ", e);
            return null;
        }

        //update exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(exchangeUpdateQo.getExchangeId());
        federatedExchangeDo.setExchangeName(exchangeUpdateQo.getExchangeName());
        federatedExchangeDo.setNetworkAccess(exchangeUpdateQo.getNetworkAccess());
        federatedExchangeDo.setStatus(1);
        federatedExchangeDo.setUpdateTime(new Date());
        federatedExchangeMapper.updateById(federatedExchangeDo);

        //add exchange details table
        Long maxBatch = exchangeDetailsMapper.findMaxBatch(exchangeUpdateQo);

        List<ExchangeDetailsAddBean> exchangeDetailsAddBeanList = exchangeUpdateQo.getExchangeDetailsAddBeanList();
        for (ExchangeDetailsAddBean exchangeDetailsAddBean : exchangeDetailsAddBeanList) {
            ExchangeDetailsDo exchangeDetailsDo = new ExchangeDetailsDo();
            exchangeDetailsDo.setExchangeDetailsId(federatedExchangeDo.getExchangeId());
            exchangeDetailsDo.setBatch(maxBatch + 1);
            exchangeDetailsDo.setPartyId(exchangeDetailsAddBean.getPartyId());
            exchangeDetailsDo.setNetworkAccess(exchangeDetailsAddBean.getNetworkAccess());
            exchangeDetailsMapper.insert(exchangeDetailsDo);
        }

        return federatedExchangeDo;

    }

    public PageBean<FederatedExchangeDo> findExchangePage(ExchangePageQo exchangePageQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("status", 1);
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        PageBean<FederatedExchangeDo> siteDetailDtoPageBean = new PageBean<>(exchangePageQo.getPageNum(), exchangePageQo.getPageSize(), count);
        long startIndex = siteDetailDtoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDoList = federatedExchangeMapper.findExchangePage(startIndex, exchangePageQo);
        siteDetailDtoPageBean.setList(federatedExchangeDoList);
        return siteDetailDtoPageBean;

    }

    public boolean checkExchangeName(ExchangeAddQo exchangeAddQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_name", exchangeAddQo.getExchangeName()).eq("status", 1);
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        return count > 0;
    }

    public boolean checkExchangeName(ExchangeUpdateQo exchangeUpdateQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_name", exchangeUpdateQo.getExchangeName()).eq("status", 1).ne("exchange_id", exchangeUpdateQo.getExchangeId());
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        return count > 0;
    }

    public List<ExchangeDetailsDo> queryExchange(ExchangeQueryQo exchangeQueryQo) {
        ArrayList<ExchangeDetailsDo> exchangeDetailsDos = new ArrayList<>();

        //send grpc request
        String[] network = exchangeQueryQo.getNetworkAccess().split(":");
        try {
            Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), "eggroll", "eggroll", "set_route_table");

            Proxy.Data body = exchange.getBody();
            ByteString value = body.getValue();

        } catch (UnsupportedEncodingException e) {
            log.error("update route table error by grpc ", e);
            return null;
        }


        return exchangeDetailsDos;
    }
}
