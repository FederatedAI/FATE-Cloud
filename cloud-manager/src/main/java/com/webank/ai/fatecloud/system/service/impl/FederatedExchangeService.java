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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.exception.LogicException;
import com.webank.ai.fatecloud.common.util.ObjectUtil;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.grpc.ExchangeGrpcUtil;
import com.webank.ai.fatecloud.grpc.NetworkBean;
import com.webank.ai.fatecloud.system.dao.entity.*;
import com.webank.ai.fatecloud.system.dao.mapper.*;
import com.webank.ai.fatecloud.system.pojo.dto.PartyDetailsDto;
import com.webank.ai.fatecloud.system.pojo.dto.RollSitePageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FederatedExchangeService implements Serializable {
    @Autowired
    FederatedExchangeMapper federatedExchangeMapper;

    @Autowired
    RollSiteMapper rollSiteMapper;

    @Autowired
    PartyMapper partyMapper;

    @Autowired
    RollSitePartyMapper rollSitePartyMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Autowired
    FederatedIpManagerMapper federatedIpManagerMapper;

    @Value(value = "${exchange.key}")
    String exchangeKey;

    @Value(value = "${exchange.partyId}")
    String exchangePartyId;

    /**********    Exchange    *************/
    @Transactional
    public FederatedExchangeDo addExchange(ExchangeAddQo exchangeAddQo) {
        //Date dateNow = new Date();
        //add exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeName(exchangeAddQo.getExchangeName());
        federatedExchangeDo.setVipEntrance(exchangeAddQo.getVipEntrance());
        federatedExchangeMapper.insert(federatedExchangeDo);

        //add roll site table
        List<RollSiteAddBean> rollSiteAddBeanList = exchangeAddQo.getRollSiteAddBeanList();
        for (RollSiteAddBean rollSiteAddBean : rollSiteAddBeanList) {
            RollSiteDo rollSiteDo = new RollSiteDo();
            rollSiteDo.setNetworkAccess(rollSiteAddBean.getNetworkAccess());
            rollSiteDo.setNetworkAccessExit(rollSiteAddBean.getNetworkAccessExit());
            rollSiteDo.setExchangeId(federatedExchangeDo.getExchangeId());
            rollSiteMapper.insert(rollSiteDo);

            //add party table
            List<PartyAddBean> partyAddBeanList = rollSiteAddBean.getPartyAddBeanList();
            for (PartyAddBean partyAddBean : partyAddBeanList) {

                this.updateParty(partyAddBean, rollSiteDo.getRollSiteId());
            }
        }

        return federatedExchangeDo;
    }

    @Transactional
    public void deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {
        Long exchangeId = exchangeDeleteQo.getExchangeId();
        List<RollSiteDo> rollSiteDos = this.findRollSiteByExchangeId(exchangeId);
        for (RollSiteDo rollSiteDo : rollSiteDos) {
            // delete roll site and clean route table
            this.deleteRollSite(rollSiteDo);
        }

        // delete exchange in table
        federatedExchangeMapper.deleteById(exchangeId);
    }

    public boolean checkExchangeName(ExchangeAddQo exchangeAddQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_name", exchangeAddQo.getExchangeName());
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        return count > 0;
    }

    public boolean checkExchangeName(ExchangeUpdateQo exchangeUpdateQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_name", exchangeUpdateQo.getExchangeName()).eq("status", 1).ne("exchange_id", exchangeUpdateQo.getExchangeId());
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        return count > 0;
    }

    public boolean checkSiteExistByExchange(Long exchangeId) {
        Integer count = federatedExchangeMapper.exchangeUsingCount(exchangeId);
        if (count != null && count > 0) return true;

        List<RollSiteDo> rollSiteDoList = rollSiteMapper.selectList(new QueryWrapper<RollSiteDo>().eq("exchange_id", exchangeId));
        for (RollSiteDo rollSiteDo : rollSiteDoList) {
            boolean exist = checkSiteExistByRollSite(rollSiteDo.getRollSiteId());
            if (exist) return true;
        }
        return false;
    }

    public FederatedExchangeDo findExchangeById(Long exchangeId) {
        return federatedExchangeMapper.selectById(exchangeId);
    }

    public List<FederatedExchangeDo> findAllExchange() {
        return federatedExchangeMapper.selectList(new QueryWrapper<>());
    }

    public PageBean<FederatedExchangeDo> findExchangePageForFateManager(ExchangePageForFateManagerQo exchangePageForFateManagerQo) {

        int count = federatedExchangeMapper.findExchangeCountForFateManager(exchangePageForFateManagerQo);

        PageBean<FederatedExchangeDo> federatedExchangeDoPageBean = new PageBean<>(exchangePageForFateManagerQo.getPageNum(), exchangePageForFateManagerQo.getPageSize(), count);
        long startIndex = federatedExchangeDoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDos = federatedExchangeMapper.findExchangePageForFateManager(startIndex, exchangePageForFateManagerQo);

        federatedExchangeDoPageBean.setList(federatedExchangeDos);
        return federatedExchangeDoPageBean;
    }

    public PageBean<FederatedExchangeDo> findExchangePage(ExchangePageQo exchangePageQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        PageBean<FederatedExchangeDo> siteDetailDtoPageBean = new PageBean<>(exchangePageQo.getPageNum(), exchangePageQo.getPageSize(), count);
        long startIndex = siteDetailDtoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDoList = federatedExchangeMapper.findExchangePage(startIndex, exchangePageQo);
        siteDetailDtoPageBean.setList(federatedExchangeDoList);
        return siteDetailDtoPageBean;

    }

    public boolean checkExchangePartyId(String partyId) {
        return partyId.equals(exchangePartyId);
    }

    /**********    RollSite    *************/
    public boolean findRollSite(String network) {
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", network);
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        return rollSiteDos.size() > 0;
    }

    public boolean checkSiteExistByRollSite(Long rollSiteId) {
        Integer count = partyMapper.countSiteByRollSiteId(rollSiteId);
        return count > 0;
    }

    public static ArrayList<PartyDo> buildPartyList(String routerString) {
        ArrayList<PartyDo> partyDos = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(routerString);
        JSONObject route_table = jsonObject.getJSONObject("route_table");
        Set<Map.Entry<String, Object>> entries = route_table.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            PartyDo partyDo = new PartyDo();
            partyDo.setPartyId(entry.getKey());
            partyDo.setNetworkAccess("");

            Object value = entry.getValue();
            String valueString = JSONObject.toJSONString(value);
            JSONObject partyIdValue = JSON.parseObject(valueString);
            JSONArray netWorkList = partyIdValue.getJSONArray("default");

            for (int index = 0; index < netWorkList.size(); index++) {
                JSONObject netWorkJSONObject = netWorkList.getJSONObject(index);
                partyDo.setNetworkAccess(partyDo.getNetworkAccess() + netWorkJSONObject.get("ip") + ":" + netWorkJSONObject.get("port") + ";");
                Object is_secure = netWorkJSONObject.get("is_secure");
                Object is_polling = netWorkJSONObject.get("is_polling");
                if (is_secure != null && "true".equals(String.valueOf(is_secure))) {
                    partyDo.setSecureStatus(1);
                } else {
                    partyDo.setSecureStatus(2);
                }

                if (is_polling != null && "true".equals(String.valueOf(is_polling))) {
                    partyDo.setPollingStatus(1);
                } else {
                    partyDo.setPollingStatus(2);
                }
            }

            partyDo.setStatus(1);
            partyDo.setUpdateTime(new Date());

            partyDos.add(partyDo);

        }

        // order by party id
        partyDos.sort(Comparator.comparing(PartyDo::getPartyId));

        return partyDos;

    }

    @Transactional
    public List<PartyDo> queryExchange(ExchangeQueryQo exchangeQueryQo) {

        //send grpc request and get the data of route_table.json
        String rollSiteAddress = exchangeQueryQo.getNetworkAccess();
        String[] network = rollSiteAddress.split(":");
        ArrayList<PartyDo> partyDos;

        try {
            log.info("query request to exchange, ip:{},port:{}, key:{}, partyId:{}, operator:{}", network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");
            Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");

            Proxy.Data body = exchange.getBody();
            ByteString value = body.getValue();
            String routerTableString = value.toStringUtf8();

            log.info("query response from exchange : {}", routerTableString);
            partyDos = buildPartyList(routerTableString);
        } catch (Exception e) {
            log.error("update route table error by grpc ", e);
            return null;
        }

        //find roll site info in roll site table
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", rollSiteAddress);
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        if (rollSiteDos.size() > 0) { // update database
            // exclude routing table admin
            supplementaryExclusionAdmin(partyDos, rollSiteAddress, exchangePartyId, Boolean.FALSE);

            Date date = new Date();
            //get party info in database
            RollSiteDo rollSiteDo = rollSiteDos.get(0);
            Long rollSiteId = rollSiteDo.getRollSiteId();

            // associate table get party
            List<PartyDo> partyDosExisted = this.findPartyByRollSiteId(rollSiteId);
            Map<String, PartyDo> filterMap = ObjectUtil.toMap(PartyDo::getPartyId, partyDosExisted);

            for (PartyDo partyDo : partyDos) { // update party table
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();
                Integer pollingStatus = partyDo.getPollingStatus();

                boolean exist = false;
                PartyDo existPartyDo = filterMap.get(partyId);
                if (existPartyDo != null) {
                    String existNetworkAccess = existPartyDo.getNetworkAccess();
                    Integer existSecureStatus = existPartyDo.getSecureStatus();
                    Integer existPollingStatus = existPartyDo.getPollingStatus();
                    exist = true;
                    //whether there are changes in route_table.json or not
                    if ((!existNetworkAccess.equals(networkAccess)) || (!existSecureStatus.equals(secureStatus)) || (!existPollingStatus.equals(pollingStatus))) {

                        if (existPartyDo.getStatus() == 1) {//status=1, is able to update basic info
                            existPartyDo.setNetworkAccess(networkAccess);
                            existPartyDo.setSecureStatus(secureStatus);
                            existPartyDo.setPollingStatus(pollingStatus);
                            existPartyDo.setUpdateTime(date);
                        }
                        existPartyDo.setValidTime(date);
                        partyMapper.updateById(existPartyDo);
                    }

                }

                //if party doesn't exist in database, insert it into database
                if (!exist) {
                    // v14. if it does not exist, add it to the database, otherwise add the associated table
                    PartyDo selectOne = this.findPartyByPartyId(partyId);
                    if (selectOne == null) {
                        partyDo.setCreateTime(date);
                        partyDo.setUpdateTime(date);
                        partyDo.setValidTime(date);
                        partyMapper.insert(partyDo);
                    }

                    this.addRollSiteRelation(rollSiteId, partyId);
                }
            }

            //if party has been deleted in route_table.json,delete it in database
            HashSet<String> partyIdsFromGRPC = new HashSet<>();
            for (PartyDo aDo : partyDos) {
                partyIdsFromGRPC.add(aDo.getPartyId());
            }

            // v14. delete party changed to delete associate
            for (PartyDo partyDo : partyDosExisted) {
                if (partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))) {
                    LambdaQueryWrapper<RollSitePartyDo> lambdaQueryWrapper = new QueryWrapper<RollSitePartyDo>()
                            .lambda()
                            .eq(RollSitePartyDo::getRollSiteId, rollSiteId)
                            .eq(RollSitePartyDo::getPartyId, partyDo.getPartyId());
                    rollSitePartyMapper.delete(lambdaQueryWrapper);
                }
            }

            //find newest party information
            List<PartyDo> partyDosFinal = partyMapper.selectExistByRollSiteId(rollSiteId);

            // v14. is using site, the cannot edit manually if true
            for (PartyDo partyDo : partyDosFinal) {
                partyDo.setUsing(partyDo.getExist() > 0);
            }

            // add admin party
            supplementaryExclusionAdmin(partyDosFinal, rollSiteAddress, exchangePartyId, Boolean.TRUE);
            return partyDosFinal;
        } else {
            supplementaryExclusionAdmin(partyDos, rollSiteAddress, exchangePartyId, Boolean.TRUE);
            return partyDos;
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void syncRouteTableInfo() {
        List<RollSiteDo> rollSiteDoList = rollSiteMapper.selectList(new QueryWrapper<>());

        for (RollSiteDo rollSiteDo : rollSiteDoList) {
            // send grpc request
            String[] network = rollSiteDo.getNetworkAccess().split(":");
            ArrayList<PartyDo> partyDos;
            try {
                Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");

                Proxy.Data body = exchange.getBody();
                ByteString value = body.getValue();
                String grpcBody = value.toStringUtf8();
                partyDos = buildPartyList(grpcBody);
            } catch (Exception e) {
                log.error("update route table error by grpc ", e);
                continue;
            }
            // exclude routing table admin
            supplementaryExclusionAdmin(partyDos, rollSiteDo.getNetworkAccess(), exchangePartyId, Boolean.FALSE);

            //update party table
            Long rollSiteId = rollSiteDo.getRollSiteId();
            List<PartyDo> partyDosExisted = this.findPartyByRollSiteId(rollSiteId);
            Map<String, PartyDo> filterMap = ObjectUtil.toMap(PartyDo::getPartyId, partyDosExisted);

            Date date = new Date();
            for (PartyDo partyDo : partyDos) {
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();
                Integer pollingStatus = partyDo.getPollingStatus();

                boolean exist = false;
                PartyDo existPartyDo = filterMap.get(partyId);
                if (existPartyDo != null) {
                    exist = true;
                    //whether there are changes in route_table.json or not
                    if ((!existPartyDo.getNetworkAccess().equals(networkAccess)) ||
                            (!existPartyDo.getSecureStatus().equals(secureStatus)) ||
                            (!existPartyDo.getPollingStatus().equals(pollingStatus))) {

                        if (existPartyDo.getStatus() == 1) {//status=1, is able to update basic info
                            existPartyDo.setNetworkAccess(networkAccess);
                            existPartyDo.setSecureStatus(secureStatus);
                            existPartyDo.setPollingStatus(pollingStatus);
                            existPartyDo.setUpdateTime(date);
                        }
                        existPartyDo.setValidTime(date);
                        partyMapper.updateById(existPartyDo);
                    }
                }

                // party not exist  insert
                if (!exist) {
                    // if it does not exist, add it to the database, otherwise add the associated table
                    PartyDo selectOne = this.findPartyByPartyId(partyId);
                    if (selectOne == null) {
                        partyDo.setCreateTime(date);
                        partyDo.setUpdateTime(date);
                        partyDo.setValidTime(date);
                        partyMapper.insert(partyDo);
                    }
                    this.addRollSiteRelation(rollSiteId, partyId);
                }
            }

            // delete if the party has been posted and is not in the routing table
            Set<String> partyIdsFromGRPC = partyDos.stream().map(PartyDo::getPartyId).collect(Collectors.toSet());
            // delete party changed to delete associate
            for (PartyDo partyDo : partyDosExisted) {
                if (partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))) {
                    LambdaQueryWrapper<RollSitePartyDo> lambdaQueryWrapper = new QueryWrapper<RollSitePartyDo>()
                            .lambda()
                            .eq(RollSitePartyDo::getRollSiteId, rollSiteId)
                            .eq(RollSitePartyDo::getPartyId, partyDo.getPartyId());
                    rollSitePartyMapper.delete(lambdaQueryWrapper);
                }
            }
        }
    }

    private void syncActivationParty(List<PartyDo> syncPartyList, RollSiteDo rollSiteDo) {
        // update party table
        Long rollSiteId = rollSiteDo.getRollSiteId();
        List<PartyDo> partyDosExisted = this.findPartyByRollSiteId(rollSiteId);
        Map<String, PartyDo> filterMap = ObjectUtil.toMap(PartyDo::getPartyId, partyDosExisted);

        List<PartyDo> needSyncPartyList = new ArrayList<>();
        for (PartyDo partyDo : partyDosExisted) {
            if (partyDo.getStatus() == 1) {
                needSyncPartyList.add(partyDo);
            }
        }

        // sync party
        boolean update = false;
        for (PartyDo partyDo : syncPartyList) {
            PartyDo existPartyDo = filterMap.get(partyDo.getPartyId());
            if (existPartyDo == null) {
                update = true;
                needSyncPartyList.add(partyDo);
                this.addRollSiteRelation(rollSiteId, partyDo.getPartyId());
            }
        }

        if (update) {
            try {
                updateRouteTableJsonString(needSyncPartyList, rollSiteDo.getNetworkAccess());
            } catch (Exception e) {
                LogicException.throwInstance(ReturnCodeEnum.SITE_PARTY_UPDATE_ERROR, rollSiteDo.getNetworkAccess());
            }
        }
    }

    @Transactional
    public void addRollSite(RollSieAddQo rollSieAddQo) {
        //update
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        federatedExchangeDoQueryWrapper.eq("exchange_id", rollSieAddQo.getExchangeId());
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setUpdateTime(new Date());
        federatedExchangeMapper.update(federatedExchangeDo, federatedExchangeDoQueryWrapper);

        //insert roll site
        RollSiteDo rollSiteDo = new RollSiteDo();
        rollSiteDo.setExchangeId(rollSieAddQo.getExchangeId());
        rollSiteDo.setNetworkAccess(rollSieAddQo.getNetworkAccess());
        rollSiteDo.setNetworkAccessExit(rollSieAddQo.getNetworkAccessExit());
        rollSiteMapper.insert(rollSiteDo);

        //insert party
        List<PartyAddBean> partyAddBeanList = rollSieAddQo.getPartyAddBeanList();
        for (PartyAddBean partyAddBean : partyAddBeanList) {

            this.updateParty(partyAddBean, rollSiteDo.getRollSiteId());
        }

        List<PartyDo> syncPartyList = partyMapper.findAuthSyncParty(rollSieAddQo.getExchangeId());
        if (!syncPartyList.isEmpty()) {
            this.syncActivationParty(syncPartyList, rollSiteDo);
        }
    }

    @Transactional
    public void updateRollSite(RollSiteUpdateQo rollSiteUpdateQo) {
        Long rollSiteId = rollSiteUpdateQo.getRollSiteId();
        RollSiteDo rollSiteDo = rollSiteMapper.selectById(rollSiteId);
        if (rollSiteDo == null) return;

        Date date = new Date();
        List<PartyAddBean> partyAddBeanList = rollSiteUpdateQo.getPartyAddBeanList();
        boolean updateResult = false;

        // find roll site associate all party
        List<PartyDo> partyDoList = this.findPartyByRollSiteId(rollSiteId);
        Map<String, PartyDo> partyDoMap = ObjectUtil.toMap(PartyDo::getPartyId, partyDoList);
        for (PartyAddBean partyAddBean : partyAddBeanList) {

            // add if not associated, else update if there are changes
            PartyDo partyDo = partyDoMap.get(partyAddBean.getPartyId());
            if (partyDo == null) {
                this.updateParty(partyAddBean, rollSiteId);
                updateResult = true;
            } else {

                if ((!partyAddBean.getNetworkAccess().equals(partyDo.getNetworkAccess())) ||
                        (!partyAddBean.getSecureStatus().equals(partyDo.getSecureStatus())) ||
                        (!partyAddBean.getPollingStatus().equals(partyDo.getPollingStatus())) ||
                        (!partyAddBean.getStatus().equals(partyDo.getStatus()))) {

                    partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
                    partyDo.setSecureStatus(partyAddBean.getSecureStatus());
                    partyDo.setPollingStatus(partyAddBean.getPollingStatus());
                    partyDo.setStatus(partyAddBean.getStatus());
                    partyDo.setUpdateTime(date);
                    partyMapper.updateById(partyDo);
                    updateResult = true;
                }

            }
        }

        if (updateResult) {
            //update roll site time
            rollSiteDo.setUpdateTime(date);
            rollSiteMapper.updateById(rollSiteDo);

            // update exchange
            FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
            federatedExchangeDo.setExchangeId(rollSiteDo.getExchangeId());
            federatedExchangeDo.setUpdateTime(date);
            federatedExchangeMapper.updateById(federatedExchangeDo);
        }

    }

    @Transactional
    public void deleteRollSite(RollSiteDeleteQo rollSiteDeleteQo) {
        RollSiteDo rollSiteDo = rollSiteMapper.selectById(rollSiteDeleteQo.getRollSiteId());
        if (rollSiteDo == null) return;
        this.deleteRollSite(rollSiteDo);

        // update exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(rollSiteDo.getExchangeId());
        federatedExchangeDo.setUpdateTime(new Date());
        federatedExchangeMapper.updateById(federatedExchangeDo);
    }

    private void deleteRollSite(RollSiteDo rollSiteDo) {
        // delete party associate
        QueryWrapper<RollSitePartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.eq("roll_site_id", rollSiteDo.getRollSiteId());
        rollSitePartyMapper.delete(partyDoQueryWrapper);

        // write party
        //List<PartyDo> partyDoList = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());
        List<PartyDo> partyDoList = new ArrayList<>(); // clear

        try {
            // write route table
            this.updateRouteTableJsonString(partyDoList, rollSiteDo.getNetworkAccess());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            LogicException.throwInstance(ReturnCodeEnum.SITE_PARTY_UPDATE_ERROR);
        }

        // delete roll site
        rollSiteMapper.deleteById(rollSiteDo);
    }

    @Transactional
    public int publishRollSite(RollSiteDeleteQo rollSiteDeleteQo) {

        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        RollSiteDo rollSiteDo = rollSiteMapper.selectById(rollSiteId);
        if (rollSiteDo == null) return 1;

        List<PartyDo> partyDos = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());

        //update roll site by grpc
        ArrayList<PartyDo> partyDosToPublish = new ArrayList<>();
        for (PartyDo partyDo : partyDos) {
            if (partyDo.getStatus() == 1 || partyDo.getStatus() == 2) {
                partyDosToPublish.add(partyDo);
            }
        }
        try {
            this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
        } catch (Exception e) {
            log.error("update route table error by grpc ", e);
            return 1;
        }

        //update party table
        Date date = new Date();
        for (PartyDo partyDo : partyDos) {
            if (partyDo.getStatus() == 2) {
                partyDo.setStatus(1);
                partyDo.setUpdateTime(date);
                partyDo.setValidTime(date);
                partyMapper.updateById(partyDo);
            }
            if (partyDo.getStatus() == 3) {
                rollSitePartyMapper.delete(new QueryWrapper<RollSitePartyDo>()
                        .lambda()
                        .eq(RollSitePartyDo::getRollSiteId, rollSiteId)
                        .eq(RollSitePartyDo::getPartyId, partyDo.getPartyId()));
            }

        }

        //update roll site table
        rollSiteDo.setUpdateTime(date);
        rollSiteMapper.updateById(rollSiteDo);

        //update exchange table
        Long exchangeId = rollSiteDo.getExchangeId();
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(exchangeId);
        federatedExchangeDo.setUpdateTime(date);
        federatedExchangeMapper.updateById(federatedExchangeDo);

        return 2;

    }

    private void updateRouteTableJsonString(List<PartyDo> partyDos, String network) throws Exception {

        //build route table string
        TreeMap<String, Object> routeTableMap = new TreeMap<>();
        // if not exist, supplementary routing table administrator
        supplementaryExclusionAdmin(partyDos, network, exchangePartyId, Boolean.TRUE);

        for (PartyDo partyDo : partyDos) {
            String networkAccess = partyDo.getNetworkAccess();
            if (ObjectUtil.isEmpty(networkAccess)) continue;

            ArrayList<NetworkBean> networkBeans = new ArrayList<>();
            Set<String> networkSet = Arrays.stream(networkAccess.split(";")).filter(s -> !s.trim().isEmpty()).collect(Collectors.toSet());
            for (String networkAddress : networkSet) {
                String[] partIdNetwork = networkAddress.split(":");
                NetworkBean networkBean = new NetworkBean();
                networkBean.setIp(partIdNetwork[0]);
                networkBean.setPort(Integer.valueOf(partIdNetwork[1]));

                // only the first save status
                if (partyDo.getSecureStatus() != null) {
                    networkBean.setIs_secure(partyDo.getSecureStatus() == 1);
                }
                if (partyDo.getPollingStatus() != null) {
                    networkBean.setIs_polling(partyDo.getPollingStatus() == 1);
                }

                networkBeans.add(networkBean);
            }

            HashMap<String, List<NetworkBean>> defaultMap = new HashMap<>();
            defaultMap.put("default", networkBeans);

            String partyId = partyDo.getPartyId();
            routeTableMap.put(partyId, defaultMap);
        }

        HashMap<String, Boolean> permission = new HashMap<>();
        permission.put("default_allow", true);

        HashMap<String, Object> routeTableFinalObject = new HashMap<>();
        routeTableFinalObject.put("route_table", routeTableMap);
        routeTableFinalObject.put("permission", permission);

        String routeTableJsonString = JSON.toJSONString(routeTableFinalObject);
        routeTableJsonString = ObjectUtil.formatJson(routeTableJsonString);
        //log.info("exchange string to add:{}", routeTableJsonString);

        //send grpc request
        String[] ipAndPort = network.split(":");
        log.info("publish request to exchange, ip:{}, port:{}, key:{}, content:{}, exchangePartyId:{}, operator:{}", ipAndPort[0], Integer.parseInt(ipAndPort[1]), exchangeKey, routeTableJsonString, exchangePartyId, "set_route_table");
        Proxy.Packet packet = ExchangeGrpcUtil.setExchange(ipAndPort[0], Integer.parseInt(ipAndPort[1]), exchangeKey, routeTableJsonString, exchangePartyId, "set_route_table");

        Proxy.Data body = packet.getBody();
        ByteString value = body.getValue();
        String information = value.toStringUtf8();
        log.info("publish response from exchange:{}", information);
        if (!"setRouteTable finished".equals(information)) {
            throw new Exception();
        }

    }

    public PageBean<RollSitePageDto> findRollSitePage(RollSitePageQo rollSitePageQo) {
        //get roll site info in database of one exchange
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("exchange_id", rollSitePageQo.getExchangeId());
        Integer count = rollSiteMapper.selectCount(rollSiteDoQueryWrapper);
        PageBean<RollSitePageDto> rollSitePageDtoPageBean = new PageBean<>(rollSitePageQo.getPageNum(), rollSitePageQo.getPageSize(), count);
        long startIndex = rollSitePageDtoPageBean.getStartIndex();
        startIndex = startIndex < 0 ? 0 : startIndex;
        List<RollSiteDo> rollSiteMapperRollSitePage = rollSiteMapper.findRollSitePage(startIndex, rollSitePageQo);

        //build RollSitePageDto according to RollSiteDo
        LinkedList<RollSitePageDto> rollSitePageDtoLinkedList = new LinkedList<>();
        for (RollSiteDo rollSiteDo : rollSiteMapperRollSitePage) {
            String status = "published";

            List<PartyDo> partyDos = rollSiteDo.getPartyDos();
            for (PartyDo next : partyDos) {
                if (next.getStatus() != 1 && next.getStatus() != 0) {
                    status = "unpublished";
                    break;
                }
            }
            RollSitePageDto rollSitePageDto = new RollSitePageDto(rollSiteDo);
            rollSitePageDto.setStatus(status);
            rollSitePageDtoLinkedList.add(rollSitePageDto);
        }

        rollSitePageDtoPageBean.setList(rollSitePageDtoLinkedList);

        for (RollSitePageDto rollSitePageDto : rollSitePageDtoLinkedList) {
            //send grpc request
            String[] network = rollSitePageDto.getNetworkAccess().split(":");
            String routerTableString;
            ArrayList<PartyDo> partyDos;
            try {
                Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");

                Proxy.Data body = exchange.getBody();
                ByteString value = body.getValue();
                routerTableString = value.toStringUtf8();
                partyDos = buildPartyList(routerTableString);
            } catch (Exception e) {
                log.error("update route table error by grpc ", e);
                rollSitePageDto.setPartyDos(null);
                rollSitePageDto.setCount(0);
                continue;
            }


            //get valid time in database
            List<PartyDo> partyDosFromDatabase = this.findPartyByRollSiteId(rollSitePageDto.getRollSiteId());

            //update valid time for party info from grpc
            Map<String, PartyDo> stringPartyDoHashMap = ObjectUtil.toMap(PartyDo::getPartyId, partyDosFromDatabase);
            Date date = new Date();
            for (PartyDo partyDo : partyDos) {
                PartyDo partyFromDatabase = stringPartyDoHashMap.get(partyDo.getPartyId());
                if (partyFromDatabase == null || partyFromDatabase.getValidTime() == null) {
                    partyDo.setValidTime(date);
                } else {
                    partyDo.setValidTime(partyFromDatabase.getValidTime());
                }

            }

            partyDos.sort((o1, o2) -> {
                String e = "[0-9]+";
                return o1.getPartyId().matches(e) && o2.getPartyId().matches(e)
                        ? Integer.parseInt(o1.getPartyId()) - Integer.parseInt(o2.getPartyId())
                        : 0;
            });
            rollSitePageDto.setPartyDos(partyDos);
            rollSitePageDto.setCount(partyDos.size());
        }

        return rollSitePageDtoPageBean;

    }

    public List<RollSiteDo> findRollSiteByExchangeId(Long exchangeId) {
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("exchange_id", exchangeId);
        return rollSiteMapper.selectList(rollSiteDoQueryWrapper);
    }

    private int addRollSiteRelation(Long rollSiteId, String partyId) {
        try {
            return rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteId, partyId));
        } catch (Exception e) {
            log.error("insert error: " + e.getMessage());
            return 0;
        }
    }

    /***********   party   ***********/
    public PageBean<PartyDo> findPartyPage(PartyQueryQo partyQueryQo) {
        List<PartyDo> partyDoList = new ArrayList<>();

        if (ObjectUtil.isEmpty(partyQueryQo.getPartyId())) {
            List<PartyDo> partyDosFinal = partyMapper.selectExistByRollSiteId(partyQueryQo.getRollSiteId());
            supplementaryExclusionAdmin(partyDosFinal, "-", exchangePartyId, Boolean.TRUE);

            // is using site, the cannot edit manually if true
            for (PartyDo partyDo : partyDosFinal) {
                partyDo.setUsing(partyDo.getExist() > 0);
            }
            partyDoList.addAll(partyDosFinal);
        } else {
            PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(Long.parseLong(partyQueryQo.getPartyId()));
            if (partyDetailsDto != null) {
                if (partyDetailsDto.getRollSiteDoList().stream().anyMatch(rollSiteDo -> partyQueryQo.getRollSiteId().equals(rollSiteDo.getRollSiteId()))) {
                    PartyDo partyDo = new PartyDo(partyDetailsDto);
                    Integer count = federatedSiteManagerMapper.selectCount(new QueryWrapper<FederatedSiteManagerDo>().eq("party_id", partyDo.getPartyId()));
                    partyDo.setUsing(count > 0);
                    partyDoList.add(partyDo);
                }
            }
        }

        PageBean<PartyDo> partyDoPageBean = new PageBean<>(0, partyDoList.size() + 1, partyDoList.size());
        partyDoPageBean.setList(partyDoList);
        return partyDoPageBean;
    }

    public PartyDo findPartyByPartyId(String partyId) {
        if (ObjectUtil.isEmpty(partyId)) return null;

        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.lambda().eq(PartyDo::getPartyId, partyId);
        return partyMapper.selectOne(partyDoQueryWrapper);
    }

    public List<PartyDo> findPartyByRollSiteId(Long rollSiteId) {
        return partyMapper.selectByRollSiteId(rollSiteId);
    }

    public boolean checkPartyExistInOtherExchange(String partyId, Long exchangeId) {
        PartyDetailsDto detailsDto = partyMapper.selectPartyDetails(Long.parseLong(partyId));
        if (detailsDto != null) {
            return detailsDto.getExchangeId() != null && !detailsDto.getExchangeId().equals(exchangeId);
        }
        return false;
    }

    @Transactional
    public int deleteParty(PartyDo partyDo) {
        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(Long.parseLong(partyDo.getPartyId()));
        List<RollSiteDo> rollSiteDoList;

        // whether to associate roll site
        if (partyDetailsDto != null && !(rollSiteDoList = partyDetailsDto.getRollSiteDoList()).isEmpty()) {
            // update routeTable
            try {
                this.updateRouteTableExcludeParty(rollSiteDoList, partyDo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

        }

        return partyMapper.delete(new QueryWrapper<PartyDo>().eq("party_id", partyDo.getPartyId()));
    }

    @Transactional
    public boolean resetParty(Long partyId) {
        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(partyId);
        if (partyDetailsDto != null) {

            PartyDo partyDo = new PartyDo(partyDetailsDto);
            List<RollSiteDo> rollSiteDoList = partyDetailsDto.getRollSiteDoList();
            if (rollSiteDoList == null) return true;

            try {
                this.updateRouteTableExcludeParty(rollSiteDoList, partyDo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }

            partyDo.setStatus(0);
            partyDo.setUpdateTime(new Date());
            partyMapper.updateById(partyDo);
            return true;
        }
        return false;
    }

    private void updateParty(PartyAddBean partyAddBean, Long rollSiteId) {
        // update if it exists
        Date dateNow = new Date();
        PartyDo findOne = findPartyByPartyId(partyAddBean.getPartyId());
        if (findOne == null) {
            PartyDo partyDo = new PartyDo();
            partyDo.setPartyId(partyAddBean.getPartyId());
            partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
            partyDo.setPollingStatus(partyAddBean.getPollingStatus());
            partyDo.setSecureStatus(partyAddBean.getSecureStatus());
            partyDo.setStatus(partyAddBean.getStatus());
            partyDo.setValidTime(dateNow);

            partyMapper.insert(partyDo);
        } else {
            findOne.setNetworkAccess(partyAddBean.getNetworkAccess());
            findOne.setSecureStatus(partyAddBean.getSecureStatus());
            findOne.setStatus(partyAddBean.getStatus());
            findOne.setPollingStatus(partyAddBean.getPollingStatus());
            findOne.setUpdateTime(dateNow);
            partyMapper.updateById(findOne);
        }

        this.addRollSiteRelation(rollSiteId, partyAddBean.getPartyId());
    }

    @Transactional
    public void updateRouteTableExcludeParty(List<RollSiteDo> rollSiteDoList, PartyDo excludeParty) throws Exception {
        for (RollSiteDo rollSiteDo : rollSiteDoList) {

            List<PartyDo> partyDos = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());

            // update roll site by grpc
            ArrayList<PartyDo> partyDosToPublish = new ArrayList<>();
            for (PartyDo partyDo : partyDos) {
                if (partyDo.getPartyId().equals(excludeParty.getPartyId())) {
                    partyDo.setStatus(3);
                    continue;
                }

                if (partyDo.getStatus() == 1 || partyDo.getStatus() == 2) {
                    partyDosToPublish.add(partyDo);
                }
            }

            this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
            this.updateInfo(partyDos, rollSiteDo);
        }
    }

    @Transactional
    private void updateRouteTableAttachParty(List<RollSiteDo> rollSiteDoList, PartyDo attachParty) throws Exception {
        for (RollSiteDo rollSiteDo : rollSiteDoList) {

            List<PartyDo> partyDos = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());
            partyDos.add(attachParty);

            // update roll site by grpc
            ArrayList<PartyDo> partyDosToPublish = new ArrayList<>();
            for (PartyDo partyDo : partyDos) {
                if (partyDo.getStatus() == 1 || partyDo.getStatus() == 2) {
                    partyDosToPublish.add(partyDo);
                }
            }

            this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
            this.updateInfo(partyDos, rollSiteDo);
        }

    }

    @Transactional
    private void updateInfo(List<PartyDo> partyDos, RollSiteDo rollSiteDo) {
        // update party table
        Date date = new Date();
        for (PartyDo partyDo : partyDos) {
            if (partyDo.getStatus() == 2) {
                partyDo.setStatus(1);
                partyDo.setUpdateTime(date);
                partyDo.setValidTime(date);
                partyMapper.updateById(partyDo);
            }
            if (partyDo.getStatus() == 3) {
                rollSitePartyMapper.delete(new QueryWrapper<RollSitePartyDo>()
                        .lambda()
                        .eq(RollSitePartyDo::getRollSiteId, rollSiteDo.getRollSiteId())
                        .eq(RollSitePartyDo::getPartyId, partyDo.getPartyId()));
            }

        }

        // update roll site table
        rollSiteDo.setUpdateTime(date);
        rollSiteMapper.updateById(rollSiteDo);

        // update exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(rollSiteDo.getExchangeId());
        federatedExchangeDo.setUpdateTime(date);
        federatedExchangeMapper.updateById(federatedExchangeDo);
    }

    public void syncPartyToExchangeRollSite(PartyDo partyDo, Long exchangeId) {
        try {
            PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(Long.parseLong(partyDo.getPartyId()));
            Date updateTime = new Date();
            partyDo.setStatus(1);
            partyDo.setUpdateTime(updateTime);
            partyDo.setValidTime(updateTime);

            if (partyDetailsDto != null) {
                // if there is a binding roll site, clear it
                List<RollSiteDo> bindRollSiteDoList = partyDetailsDto.getRollSiteDoList();
                this.updateRouteTableExcludeParty(bindRollSiteDoList, partyDo);

                partyDo.setId(partyDetailsDto.getId());
                partyMapper.updateById(partyDo);
            } else {
                partyMapper.insert(partyDo);
            }

            // add the previous party routing information
            List<RollSiteDo> rollSiteDoList = findRollSiteByExchangeId(exchangeId);
            // update routeTable
            this.updateRouteTableAttachParty(rollSiteDoList, partyDo);
            // add associate table
            for (RollSiteDo rollSiteDo : rollSiteDoList) {
                this.addRollSiteRelation(rollSiteDo.getRollSiteId(), partyDo.getPartyId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // update site routing information
    public void updatePartyInfo(PartyDo updatePartyDo) {
        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(Long.parseLong(updatePartyDo.getPartyId()));
        if (partyDetailsDto == null) return;

        try {
            Date date = new Date();
            updatePartyDo.setId(partyDetailsDto.getId());
            updatePartyDo.setStatus(1);
            updatePartyDo.setValidTime(date);
            updatePartyDo.setUpdateTime(date);

            // update all party roll site RouteTable
            List<RollSiteDo> rollSiteDoList = partyDetailsDto.getRollSiteDoList();
            for (RollSiteDo rollSiteDo : rollSiteDoList) {

                List<PartyDo> partyDos = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());

                // update roll site by grpc
                ArrayList<PartyDo> partyDosToPublish = new ArrayList<>();
                for (PartyDo partyDo : partyDos) {
                    if (partyDo.getPartyId().equals(updatePartyDo.getPartyId())) {
                        partyDo.setStatus(1);
                        partyDo.setSecureStatus(updatePartyDo.getSecureStatus());
                        partyDo.setPollingStatus(updatePartyDo.getPollingStatus());
                        partyDo.setNetworkAccess(updatePartyDo.getNetworkAccess());
                    }

                    if (partyDo.getStatus() == 1 || partyDo.getStatus() == 2) {
                        partyDosToPublish.add(partyDo);
                    }
                }

                this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
            }

            partyMapper.updateById(updatePartyDo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw LogicException.getInstance(ReturnCodeEnum.SITE_PARTY_UPDATE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIpManagerPartyInfo(PartyUpdateQo partyUpdateQo) throws Exception {

        // if they are not the same, add and update to history
        FederatedSiteManagerDo selectById = federatedSiteManagerMapper.selectById(partyUpdateQo.getId());
        FederatedExchangeDo federatedExchangeDo = federatedExchangeMapper.selectById(partyUpdateQo.getExchangeId());
        if (selectById == null || federatedExchangeDo == null) {
            LogicException.throwInstance(ReturnCodeEnum.UPDATE_ERROR);
        }

        int rows = 0;
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        FederatedIpManagerDo federatedIpManagerDo = new FederatedIpManagerDo();
        federatedSiteManagerDo.setUpdateTime(new Date());
        federatedSiteManagerDo.setId(partyUpdateQo.getId());
        /*String networkAccessExitsOld = selectById.getNetworkAccessExits();
        String networkAccessExits = partyUpdateQo.getNetworkAccessExits();
        // update site manager info
        if (!ObjectUtil.equals(networkAccessExitsOld, networkAccessExits)) {

            federatedSiteManagerDo.setNetworkAccessExits(networkAccessExits);
            // update federatedIpManagerDo site network
            federatedIpManagerDo.setStatus(3);
            federatedIpManagerDo.setNetworkAccessExits(networkAccessExits);
            federatedIpManagerDo.setNetworkAccessExitsOld(networkAccessExitsOld);
        }*/

        // update party exchange info
        int count = 0;
        PartyDetailsDto details = partyMapper.selectPartyDetails(partyUpdateQo.getPartyId());
        if (details != null) {

            PartyDo partyDo = new PartyDo(details);
            String networkAccessEntrancesOld = selectById.getNetworkAccessEntrances();
            String networkAccessEntrances = partyUpdateQo.getNetworkAccessEntrances();
            Integer pollingStatus = partyUpdateQo.getPollingStatus();
            Integer secureStatus = partyUpdateQo.getSecureStatus();
            Integer pollingStatusOld = details.getPollingStatus();
            Integer secureStatusOld = details.getSecureStatus();
            if (!ObjectUtil.equals(secureStatus, secureStatusOld) || !ObjectUtil.equals(pollingStatus, pollingStatusOld)
                    || !ObjectUtil.equals(networkAccessEntrancesOld, networkAccessEntrances)) {

                partyDo.setSecureStatus(secureStatus);
                partyDo.setPollingStatus(pollingStatus);
                partyDo.setNetworkAccess(networkAccessEntrances);
                count += partyMapper.updateById(partyDo);

                // update federatedIpManagerDo party status
                federatedIpManagerDo.setStatus(3);
                federatedIpManagerDo.setSecureStatus(secureStatus);
                federatedIpManagerDo.setSecureStatusOld(secureStatusOld);
                federatedIpManagerDo.setPollingStatus(pollingStatus);
                federatedIpManagerDo.setPollingStatusOld(pollingStatusOld);
                federatedIpManagerDo.setNetworkAccessEntrances(networkAccessEntrances);
                federatedIpManagerDo.setNetworkAccessEntrancesOld(networkAccessEntrancesOld);

                federatedSiteManagerDo.setNetworkAccessEntrances(networkAccessEntrances);

                // update the routing table in advance if the switch is not changed
                if (ObjectUtil.equals(details.getExchangeId(), partyUpdateQo.getExchangeId())) {
                    QueryWrapper<RollSiteDo> queryWrapper = new QueryWrapper<RollSiteDo>()
                            .eq("exchange_id", partyUpdateQo.getExchangeId());
                    List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(queryWrapper);

                    for (RollSiteDo rollSiteDo : rollSiteDos) {
                        // get all parties in the routing table and filter out the parties that meet the conditions
                        List<PartyDo> partyDosToPublish = this.findPartyByRollSiteId(rollSiteDo.getRollSiteId());
                        partyDosToPublish = partyDosToPublish.stream()
                                .filter(party -> (party.getStatus() == 1 || party.getStatus() == 2) && !partyDo.getPartyId().equals(party.getPartyId()))
                                .collect(Collectors.toList());

                        // add the current party
                        partyDo.setStatus(1);
                        partyDosToPublish.add(partyDo);

                        // update roll site by grpc
                        this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
                    }
                }
            }

            // If the exchange is changed, move the party to all routes under the new exchange
            if (!ObjectUtil.equals(details.getExchangeId(), partyUpdateQo.getExchangeId())) {

                partyDo.setStatus(1);
                // 1. delete the previous party routing information
                QueryWrapper<RollSiteDo> queryWrapperOld = new QueryWrapper<RollSiteDo>()
                        .eq("exchange_id", details.getExchangeId());
                List<RollSiteDo> rollSiteDosOld = rollSiteMapper.selectList(queryWrapperOld);
                // update routeTable
                this.updateRouteTableExcludeParty(rollSiteDosOld, partyDo);
                // delete associate table
                count += rollSitePartyMapper.delete(new QueryWrapper<RollSitePartyDo>().lambda().eq(RollSitePartyDo::getPartyId, partyUpdateQo.getPartyId()));

                // 2. add the previous party routing information
                QueryWrapper<RollSiteDo> queryWrapper = new QueryWrapper<RollSiteDo>()
                        .eq("exchange_id", partyUpdateQo.getExchangeId());
                List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(queryWrapper);
                // update routeTable
                this.updateRouteTableAttachParty(rollSiteDos, partyDo);
                // add associate table
                for (RollSiteDo rollSiteDo : rollSiteDos) {
                    count += this.addRollSiteRelation(rollSiteDo.getRollSiteId(), partyUpdateQo.getPartyId() + "");
                }

                // update federatedIpManagerDo exchange
                federatedIpManagerDo.setStatus(3);
                federatedIpManagerDo.setVipEntranceOld(details.getVipEntrance());
                federatedIpManagerDo.setExchangeNameOld(details.getExchangeName());
                federatedIpManagerDo.setVipEntrance(federatedExchangeDo.getVipEntrance());
                federatedIpManagerDo.setExchangeName(federatedExchangeDo.getExchangeName());
                federatedSiteManagerDo.setExchangeId(partyUpdateQo.getExchangeId());
            }
        }

        if (federatedIpManagerDo.getStatus() != null) {
            rows += federatedSiteManagerMapper.updateById(federatedSiteManagerDo);

            federatedIpManagerDo.setPartyId(partyUpdateQo.getPartyId());
            federatedIpManagerDo.setInstitutions(selectById.getInstitutions());
            rows += federatedIpManagerMapper.insert(federatedIpManagerDo);
        }

        log.info("updateParty update site rows = {}, exchange change count = {}", rows, count);
    }

    /**
     * add routing table management party when writing, and exclude management party when reading
     *
     * @param rollSiteAddress current roll site address 127.0.0.1:9370
     * @param partyId         admin partyId
     * @param write           is write
     */
    private void supplementaryExclusionAdmin(List<PartyDo> partyDos, String rollSiteAddress, String partyId, boolean write) {
        if (partyId == null || ObjectUtil.isEmpty(rollSiteAddress) || partyDos == null) return;

        if (write) {
            List<PartyDo> partyDoList = partyDos.stream()
                    .filter(partyDo -> partyId.equals(partyDo.getPartyId()))
                    .collect(Collectors.toList());
            if (partyDoList.isEmpty()) {
                PartyDo partyDo = new PartyDo();
                partyDo.setPartyId(partyId);
                partyDo.setUpdateTime(new Date());
                partyDo.setNetworkAccess(rollSiteAddress);
                partyDo.setUsing(true);
                partyDos.add(partyDo);
            } else {
                partyDoList.get(0).setUsing(true);
                partyDoList.get(0).setPartyId(partyId);
                partyDoList.get(0).setNetworkAccess(rollSiteAddress);
            }
        } else {
            Iterator<PartyDo> iterator = partyDos.iterator();
            while (iterator.hasNext()) {
                if (partyId.equals(iterator.next().getPartyId())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
