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

    public boolean findRollSite(String network) {
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", network);
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        return rollSiteDos.size() > 0;
    }


    @Transactional
    public FederatedExchangeDo addExchange(ExchangeAddQo exchangeAddQo) {
        Date dateNow = new Date();
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
        //update exchange table
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        Long exchangeId = exchangeDeleteQo.getExchangeId();
        federatedExchangeDoQueryWrapper.eq("exchange_id", exchangeId);
        federatedExchangeMapper.delete(federatedExchangeDoQueryWrapper);

        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("exchange_id", exchangeId);
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        for (RollSiteDo rollSiteDo : rollSiteDos) {
            // update rollSite party associate table
            QueryWrapper<RollSitePartyDo> partyDoQueryWrapper = new QueryWrapper<>();
            partyDoQueryWrapper.eq("roll_site_id", rollSiteDo.getRollSiteId());
            rollSitePartyMapper.delete(partyDoQueryWrapper);
        }
        //update roll site table
        rollSiteMapper.delete(rollSiteDoQueryWrapper);

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


    public static ArrayList<PartyDo> buildPartyList(String routerString) {
        ArrayList<PartyDo> partyDos = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(routerString);
        JSONObject route_table = jsonObject.getJSONObject("route_table");
        Set<Map.Entry<String, Object>> entries = route_table.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            PartyDo partyDo = new PartyDo();
            partyDo.setPartyId(entry.getKey());

            Object value = entry.getValue();
            String valueString = JSONObject.toJSONString(value);
            JSONObject partyIdValue = JSON.parseObject(valueString);
            JSONArray netWorkList = partyIdValue.getJSONArray("default");
            JSONObject netWorkJSONObject = netWorkList.getJSONObject(0);
            partyDo.setNetworkAccess(netWorkJSONObject.get("ip") + ":" + netWorkJSONObject.get("port"));
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
            partyDo.setStatus(1);
            partyDo.setUpdateTime(new Date());

            partyDos.add(partyDo);

        }

        // order by party id
        partyDos.sort((o1, o2) -> o1.getPartyId().compareTo(o2.getPartyId()));

        return partyDos;

    }

    public List<PartyDo> queryExchange(ExchangeQueryQo exchangeQueryQo) {

        //send grpc request and get the data of route_table.json
        String[] network = exchangeQueryQo.getNetworkAccess().split(":");
        String routerTableString;

        try {
            log.info("query request to exchange, ip:{},port:{}, key:{}, partyId:{}, operator:{}", network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");
            Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");

            Proxy.Data body = exchange.getBody();
            ByteString value = body.getValue();
            routerTableString = value.toStringUtf8();

        } catch (Exception e) {
            log.error("update route table error by grpc ", e);
            return null;
        }

        log.info("query response from exchange : {}",routerTableString);
        ArrayList<PartyDo> partyDos = buildPartyList(routerTableString);


        //find roll site info in roll site table
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", exchangeQueryQo.getNetworkAccess());
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        if (rollSiteDos.size() > 0) { // update database
            Date date = new Date();

            //get party info in database
            RollSiteDo rollSiteDo = rollSiteDos.get(0);
            Long rollSiteId = rollSiteDo.getRollSiteId();

            // associate table get party
            List<PartyDo> partyDosExisted = this.findPartyByRollSiteId(rollSiteId);

            for (PartyDo partyDo : partyDos) { // update party table
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();
                Integer pollingStatus = partyDo.getPollingStatus();

                boolean exist = false;
                for (PartyDo existPartyDo : partyDosExisted) {
                    String existPartyId = existPartyDo.getPartyId();
                    String existNetworkAccess = existPartyDo.getNetworkAccess();
                    Integer existSecureStatus = existPartyDo.getSecureStatus();
                    Integer existPollingStatus = existPartyDo.getPollingStatus();
                    if (partyId.equals(existPartyId)) {
                        exist = true;
                        //whether there are changes in route_table.json or not
                        if ( (!existNetworkAccess.equals(networkAccess)) || (!existSecureStatus.equals(secureStatus)) || (!existPollingStatus.equals(pollingStatus)) ) {

                            if (existPartyDo.getStatus() == 1) {//status=1, is able to update basic info
                                existPartyDo.setNetworkAccess(networkAccess);
                                existPartyDo.setSecureStatus(secureStatus);
                                existPartyDo.setSecureStatus(pollingStatus);
                                existPartyDo.setUpdateTime(date);
                            }
                            existPartyDo.setValidTime(date);
                            partyMapper.updateById(existPartyDo);
                        }

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

                    rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteId, partyId));
                }
            }

            //if party has been deleted in route_table.json,delete it in database
            HashSet<String> partyIdsFromGRPC = new HashSet<>();
            for (PartyDo aDo : partyDos) {
                partyIdsFromGRPC.add(aDo.getPartyId());
            }

            // v14. delete party changed to delete associate
            Set<String> deletePartySet = partyDosExisted.stream().filter(
                    partyDo -> partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))
            ).map(PartyDo::getPartyId).collect(Collectors.toSet());

            LambdaQueryWrapper<RollSitePartyDo> lambdaQueryWrapper = new QueryWrapper<RollSitePartyDo>()
                    .lambda()
                    .eq(RollSitePartyDo::getRollSiteId, rollSiteId)
                    .in(RollSitePartyDo::getPartyId, deletePartySet);
            rollSitePartyMapper.delete(lambdaQueryWrapper);

            //find newest party information
            List<PartyDo> partyDosFinal = this.findPartyByRollSiteId(rollSiteId);

            // v14. is using site, the cannot edit manually if true
            for (PartyDo partyDo : partyDosFinal) {
                Integer count = federatedSiteManagerMapper.selectCount(new QueryWrapper<FederatedSiteManagerDo>().eq("party_id", partyDo.getPartyId()));
                partyDo.setUsing(count > 0);
            }

            return partyDosFinal;

        } else {
            return partyDos;
        }

    }


    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void queryAndUpdateRollSiteInformation() {
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);

        for (RollSiteDo rollSiteDo : rollSiteDos) {

            //send grpc request
            String[] network = rollSiteDo.getNetworkAccess().split(":");
            String grpcBody;
            try {
                Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), exchangeKey, exchangePartyId, "get_route_table");

                Proxy.Data body = exchange.getBody();
                ByteString value = body.getValue();
                grpcBody = value.toStringUtf8();

            } catch (Exception e) {
                log.error("update route table error by grpc ", e);
                continue;
            }

            ArrayList<PartyDo> partyDos = buildPartyList(grpcBody);

            //update party table
            Long rollSiteId = rollSiteDo.getRollSiteId();
            QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
            partyDoQueryWrapper.eq("roll_site_id", rollSiteId);
            List<PartyDo> partyDosExisted = partyMapper.selectList(partyDoQueryWrapper);

            Date date = new Date();
            for (PartyDo partyDo : partyDos) {
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();
                Integer pollingStatus = partyDo.getPollingStatus();

                boolean exist = false;
                for (PartyDo existPartyDo : partyDosExisted) {
                    String existPartyId = existPartyDo.getPartyId();
                    if (partyId.equals(existPartyId)) {
                        exist = true;
                        //whether there are changes in route_table.json or not
                        if ( (!existPartyDo.getNetworkAccess().equals(networkAccess)) || (!existPartyDo.getSecureStatus().equals(secureStatus)) || (!existPartyDo.getPollingStatus().equals(pollingStatus)) ) {
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
                }

                //party not exist  insert
                if (!exist) {
                    // v14. if it does not exist, add it to the database, otherwise add the associated table
                    PartyDo selectOne = this.findPartyByPartyId(partyId);
                    if (selectOne == null) {
                        partyDo.setCreateTime(date);
                        partyDo.setUpdateTime(date);
                        partyDo.setValidTime(date);
                        partyMapper.insert(partyDo);
                    }

                    rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteId, partyId));
                }
            }

            //delete party
            HashSet<String> partyIdsFromGRPC = new HashSet<>();
            for (PartyDo aDo : partyDos) {
                partyIdsFromGRPC.add(aDo.getPartyId());
            }

            // v14. delete party changed to delete associate
            Set<String> deletePartySet = partyDosExisted.stream().filter(
                    partyDo -> partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))
            ).map(PartyDo::getPartyId).collect(Collectors.toSet());

            LambdaQueryWrapper<RollSitePartyDo> lambdaQueryWrapper = new QueryWrapper<RollSitePartyDo>()
                    .lambda()
                    .eq(RollSitePartyDo::getRollSiteId, rollSiteId)
                    .in(RollSitePartyDo::getPartyId, deletePartySet);
            rollSitePartyMapper.delete(lambdaQueryWrapper);

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

    }

    @Transactional
    public void updateRollSite(RollSiteUpdateQo rollSiteUpdateQo) {
        Date date = new Date();
        List<PartyAddBean> partyAddBeanList = rollSiteUpdateQo.getPartyAddBeanList();
        boolean updateResult = false;

        // find roll site associate all party
        Long rollSiteId = rollSiteUpdateQo.getRollSiteId();
        List<PartyDo> partyDoList = this.findPartyByRollSiteId(rollSiteId);
        Set<String> partyIdSet = partyDoList.stream().map(PartyDo::getPartyId).collect(Collectors.toSet());

        for (PartyAddBean partyAddBean : partyAddBeanList) {

            // add if not associated, else update if there are changes
            if (!partyIdSet.contains(partyAddBean.getPartyId())) {
                this.updateParty(partyAddBean, rollSiteId);
                updateResult = true;
            } else {
                List<PartyDo> partyDos = partyDoList.stream().filter(partyDo -> partyDo.getPartyId().equals(partyAddBean.getPartyId())).collect(Collectors.toList());
                for (PartyDo partyDo : partyDos) {

                    if ((!partyDo.getNetworkAccess().equals(partyAddBean.getNetworkAccess())) || (!partyDo.getSecureStatus().equals(partyAddBean.getSecureStatus())) || (!partyDo.getPollingStatus().equals(partyAddBean.getPollingStatus())) || (!partyDo.getStatus().equals(partyAddBean.getStatus()))) {
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


        }

        if (updateResult) {
            //update roll site time
            QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
            rollSiteDoQueryWrapper.eq("roll_site_id", rollSiteUpdateQo.getRollSiteId());
            RollSiteDo rollSiteDo = new RollSiteDo();
            rollSiteDo.setUpdateTime(date);
            rollSiteMapper.update(rollSiteDo, rollSiteDoQueryWrapper);

            //update exchange
            List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
            RollSiteDo rollSiteDo1 = rollSiteDos.get(0);

            FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
            federatedExchangeDo.setExchangeId(rollSiteDo1.getExchangeId());
            federatedExchangeDo.setUpdateTime(date);
            federatedExchangeMapper.updateById(federatedExchangeDo);
        }

    }

    @Transactional
    public void deleteRollSite(RollSiteDeleteQo rollSiteDeleteQo) {
        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("roll_site_id", rollSiteId);

        //update exchange table
        Date date = new Date();
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        RollSiteDo rollSiteDo1 = rollSiteDos.get(0);
        Long exchangeId = rollSiteDo1.getExchangeId();
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(exchangeId);
        federatedExchangeDo.setUpdateTime(date);
        federatedExchangeMapper.updateById(federatedExchangeDo);

        //delete roll site
        rollSiteMapper.delete(rollSiteDoQueryWrapper);

        //delete party associate
        QueryWrapper<RollSitePartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.eq("roll_site_id", rollSiteId);
        rollSitePartyMapper.delete(partyDoQueryWrapper);

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
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("roll_site_id", rollSiteDeleteQo.getRollSiteId());
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        RollSiteDo rollSiteDo1 = rollSiteDos.get(0);
        Long exchangeId = rollSiteDo1.getExchangeId();

        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeId(exchangeId);
        federatedExchangeDo.setUpdateTime(date);
        federatedExchangeMapper.updateById(federatedExchangeDo);

        return 2;

    }

    private void updateRouteTableJsonString(List<PartyDo> partyDos, String network) throws Exception {

        //build route table string
        TreeMap<String, Object> routeTableMap = new TreeMap<>();

        for (PartyDo partyDo : partyDos) {
            String[] partIdNetwork = partyDo.getNetworkAccess().split(":");
            NetworkBean networkBean = new NetworkBean();
            networkBean.setIp(partIdNetwork[0]);
            networkBean.setPort(Integer.valueOf(partIdNetwork[1]));
            if (partyDo.getSecureStatus() == 1) {
                networkBean.setIs_secure(true);
            } else {
                networkBean.setIs_secure(false);
            }
            if (partyDo.getPollingStatus() == 1) {
                networkBean.setIs_polling(true);
            } else {
                networkBean.setIs_polling(false);
            }

            ArrayList<NetworkBean> networkBeans = new ArrayList<>();
            networkBeans.add(networkBean);
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
        log.info("exchange string to add:{}", routeTableJsonString);

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

    public PageBean<FederatedExchangeDo> findExchangePage(ExchangePageQo exchangePageQo) {
        QueryWrapper<FederatedExchangeDo> federatedExchangeDoQueryWrapper = new QueryWrapper<>();
        Integer count = federatedExchangeMapper.selectCount(federatedExchangeDoQueryWrapper);
        PageBean<FederatedExchangeDo> siteDetailDtoPageBean = new PageBean<>(exchangePageQo.getPageNum(), exchangePageQo.getPageSize(), count);
        long startIndex = siteDetailDtoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDoList = federatedExchangeMapper.findExchangePage(startIndex, exchangePageQo);
        siteDetailDtoPageBean.setList(federatedExchangeDoList);
        return siteDetailDtoPageBean;

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
        LinkedList<RollSitePageDto> rollSitePageDtos = new LinkedList<>();
        for (RollSiteDo rollSiteDo : rollSiteMapperRollSitePage) {
            String status = "published";

            List<PartyDo> partyDos = rollSiteDo.getPartyDos();
            Iterator<PartyDo> iterator = partyDos.iterator();
            while (iterator.hasNext()) {
                PartyDo next = iterator.next();
                if (next.getStatus() != 1) {
//                    iterator.remove();
                    status = "unpublished";
                }
            }
            RollSitePageDto rollSitePageDto = new RollSitePageDto(rollSiteDo);
            rollSitePageDto.setStatus(status);
            rollSitePageDtos.add(rollSitePageDto);
        }

        rollSitePageDtoPageBean.setList(rollSitePageDtos);

        for (RollSitePageDto rollSitePageDto : rollSitePageDtos) {
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
            HashMap<String, PartyDo> stringPartyDoHashMap = new HashMap<>();
            for (PartyDo partyDo : partyDosFromDatabase) {
                stringPartyDoHashMap.put(partyDo.getPartyId(), partyDo);
            }
            Date date = new Date();
            for (PartyDo partyDo : partyDos) {
                PartyDo partyFromDatabase = stringPartyDoHashMap.get(partyDo.getPartyId());
                if (partyFromDatabase == null || partyFromDatabase.getValidTime() == null) {
                    partyDo.setValidTime(date);
                } else {
                    partyDo.setValidTime(partyFromDatabase.getValidTime());
                }

            }
            rollSitePageDto.setPartyDos(partyDos);
            rollSitePageDto.setCount(partyDos.size());
        }

        return rollSitePageDtoPageBean;

    }

    public PageBean<FederatedExchangeDo> findExchangePageForFateManager(ExchangePageForFateManagerQo exchangePageForFateManagerQo) {

        int count = federatedExchangeMapper.findExchangeCountForFateManager(exchangePageForFateManagerQo);

        PageBean<FederatedExchangeDo> federatedExchangeDoPageBean = new PageBean<>(exchangePageForFateManagerQo.getPageNum(), exchangePageForFateManagerQo.getPageSize(), count);
        long startIndex = federatedExchangeDoPageBean.getStartIndex();

        List<FederatedExchangeDo> federatedExchangeDos = federatedExchangeMapper.findExchangePageForFateManager(startIndex, exchangePageForFateManagerQo);

        federatedExchangeDoPageBean.setList(federatedExchangeDos);
        return federatedExchangeDoPageBean;
    }

    public PageBean<PartyDo> findPartyPage(PartyQueryQo partyQueryQo) {
        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper
                .lambda()
                .eq(PartyDo::getPartyId, partyQueryQo.getPartyId())
                .eq(ObjectUtil.noEmpty(partyQueryQo.getStatus()), PartyDo::getStatus, partyQueryQo.getStatus());

        List<PartyDo> partyDoList = partyMapper.selectList(partyDoQueryWrapper);
        for (PartyDo partyDo : partyDoList) {
            Integer count = federatedSiteManagerMapper.selectCount(new QueryWrapper<FederatedSiteManagerDo>().eq("party_id", partyDo.getPartyId()));
            partyDo.setUsing(count > 0);
        }
        PageBean<PartyDo> partyDoPageBean = new PageBean<>(0, 20, partyDoList.size());

        partyDoPageBean.setList(partyDoList);
        return partyDoPageBean;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateIpManagerPartyInfo(PartyUpdateQo partyUpdateQo) throws Exception {

        // if they are not the same, add and update to history
        FederatedSiteManagerDo selectById = federatedSiteManagerMapper.selectById(partyUpdateQo.getId());
        if (selectById == null) return;

        int rows = 0;
        Date date = new Date();
        String networkAccessExitsOld = selectById.getNetworkAccessExits();
        String networkAccessEntrancesOld = selectById.getNetworkAccessEntrances();
        String networkAccessExits = partyUpdateQo.getNetworkAccessExits();
        String networkAccessEntrances = partyUpdateQo.getNetworkAccessEntrances();

        FederatedIpManagerDo federatedIpManagerDo = new FederatedIpManagerDo();
        if (!ObjectUtil.equals(networkAccessEntrancesOld, networkAccessEntrances)
                || !ObjectUtil.equals(networkAccessExitsOld, networkAccessExits)) {

            // update site manager info
            FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
            federatedSiteManagerDo.setUpdateTime(date);
            federatedSiteManagerDo.setId(partyUpdateQo.getId());
            federatedSiteManagerDo.setNetworkAccessEntrances(networkAccessEntrances);
            federatedSiteManagerDo.setNetworkAccessExits(networkAccessExits);
            rows += federatedSiteManagerMapper.updateById(federatedSiteManagerDo);

            // update federatedIpManagerDo site network
            federatedIpManagerDo.setStatus(3);
            federatedIpManagerDo.setNetworkAccessEntrances(networkAccessEntrances);
            federatedIpManagerDo.setNetworkAccessEntrancesOld(networkAccessEntrancesOld);
            federatedIpManagerDo.setNetworkAccessExits(networkAccessExits);
            federatedIpManagerDo.setNetworkAccessExitsOld(networkAccessExitsOld);
        }

        // update party exchange info
        int count = 0;
        PartyDetailsDto details = partyMapper.selectPartyDetails(partyUpdateQo.getPartyId());
        if (details != null) {

            PartyDo partyDo = new PartyDo(details);
            Integer pollingStatus = partyUpdateQo.getPollingStatus();
            Integer secureStatus = partyUpdateQo.getSecureStatus();
            Integer pollingStatusOld = details.getPollingStatus();
            Integer secureStatusOld = details.getSecureStatus();
            if (!ObjectUtil.equals(secureStatus, secureStatusOld)
                    || !ObjectUtil.equals(pollingStatus, pollingStatusOld)) {

                partyDo.setSecureStatus(secureStatus);
                partyDo.setPollingStatus(pollingStatus);
                count += partyMapper.updateById(partyDo);

                // update federatedIpManagerDo party status
                federatedIpManagerDo.setStatus(3);
                federatedIpManagerDo.setSecureStatus(secureStatus);
                federatedIpManagerDo.setSecureStatusOld(secureStatusOld);
                federatedIpManagerDo.setPollingStatus(pollingStatus);
                federatedIpManagerDo.setPollingStatusOld(pollingStatusOld);
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
                    count += rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteDo.getRollSiteId(), partyUpdateQo.getPartyId() + ""));
                }

                // update federatedIpManagerDo exchange
                FederatedExchangeDo federatedExchangeDo = federatedExchangeMapper.selectById(partyUpdateQo.getExchangeId());
                federatedIpManagerDo.setStatus(3);
                federatedIpManagerDo.setVipEntranceOld(details.getVipEntrance());
                federatedIpManagerDo.setExchangeNameOld(details.getExchangeName());
                federatedIpManagerDo.setVipEntrance(federatedExchangeDo.getVipEntrance());
                federatedIpManagerDo.setExchangeName(federatedExchangeDo.getExchangeName());
            }
        }

        if (federatedIpManagerDo.getStatus() != null) {
            rows += federatedIpManagerMapper.insert(federatedIpManagerDo);
        }

        log.info("updateParty update site rows = {}, exchange change count = {}", rows, count);
    }

    public List<FederatedExchangeDo> findAllExchange() {
        return federatedExchangeMapper.selectList(new QueryWrapper<>());
    }

    public PartyDo findPartyByPartyId(String partyId) {
        if (ObjectUtil.isEmpty(partyId)) return null;

        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.lambda().eq(PartyDo::getPartyId, partyId);
        return partyMapper.selectOne(partyDoQueryWrapper);
    }

    public List<PartyDo> findPartyByRollSiteId(Long rollSiteId) {
        LambdaQueryWrapper<RollSitePartyDo> rollSitePartyDoLambdaQueryWrapper = new QueryWrapper<RollSitePartyDo>()
                .lambda().eq(RollSitePartyDo::getRollSiteId, rollSiteId);
        List<RollSitePartyDo> rollSitePartyDoList = rollSitePartyMapper.selectList(rollSitePartyDoLambdaQueryWrapper);
        Set<String> partyIdSet = rollSitePartyDoList.stream().map(RollSitePartyDo::getPartyId).collect(Collectors.toSet());

        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.in("party_id", partyIdSet).orderByAsc("party_id");
        return partyMapper.selectList(partyDoQueryWrapper);
    }

    private void updateParty(PartyAddBean partyAddBean, Long rollSiteId) {
        // update if it exists
        Date dateNow = new Date();
        PartyDo findOne = findPartyByPartyId(partyAddBean.getPartyId());
        if (findOne == null) {
            PartyDo partyDo = new PartyDo();
            partyDo.setPartyId(partyAddBean.getPartyId());
            partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
            partyDo.setSecureStatus(partyAddBean.getSecureStatus());
            partyDo.setStatus(partyAddBean.getStatus());
            partyDo.setPollingStatus(partyAddBean.getPollingStatus());
            partyDo.setValidTime(dateNow);

            partyMapper.insert(partyDo);
        } else {
            findOne.setNetworkAccess(partyAddBean.getNetworkAccess());
            findOne.setSecureStatus(partyAddBean.getSecureStatus());
            findOne.setStatus(partyAddBean.getStatus());
            findOne.setPollingStatus(partyAddBean.getPollingStatus());
            findOne.setValidTime(dateNow);
            partyMapper.updateById(findOne);
        }

        rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteId, partyAddBean.getPartyId()));
    }

    // add site routing information when the site is activated
    public boolean updatePartyInfoAndRollSite(String networkAccess, long partyId) {
        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(partyId);
        if (partyDetailsDto == null) return false;

        try {
            Date date = new Date();
            PartyDo updatePartyDo = new PartyDo(partyDetailsDto);
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
                    if (partyDo.getPartyId().equals(partyId + "")) {
                        partyDo.setStatus(1);
                        partyDo.setNetworkAccess(networkAccess);
                    }

                    if (partyDo.getStatus() == 1 || partyDo.getStatus() == 2) {
                        partyDosToPublish.add(partyDo);
                    }
                }

                this.updateRouteTableJsonString(partyDosToPublish, rollSiteDo.getNetworkAccess());
            }

            partyMapper.updateById(updatePartyDo);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void addToExchangeRollSite(PartyDo partyDo, String exchangeName) {

        PartyDetailsDto partyDetailsDto = partyMapper.selectPartyDetails(Long.parseLong(partyDo.getPartyId()));
        if (partyDetailsDto != null && !exchangeName.equals(partyDetailsDto.getExchangeName())) {
            rollSitePartyMapper.delete(new QueryWrapper<RollSitePartyDo>()
                    .lambda()
                    .eq(RollSitePartyDo::getPartyId, partyDo.getPartyId()));
        }

        if (partyDetailsDto == null || !exchangeName.equals(partyDetailsDto.getExchangeName())) {
            FederatedExchangeDo federatedExchangeDo = federatedExchangeMapper.selectOne(new QueryWrapper<FederatedExchangeDo>()
                    .lambda()
                    .eq(FederatedExchangeDo::getExchangeName, exchangeName));

            if (federatedExchangeDo != null) {
                List<RollSiteDo> rollSiteDoList = rollSiteMapper.selectList(new QueryWrapper<RollSiteDo>()
                        .lambda()
                        .eq(RollSiteDo::getExchangeId, federatedExchangeDo.getExchangeId()));

                for (RollSiteDo rollSiteDo : rollSiteDoList) {
                    rollSitePartyMapper.insert(new RollSitePartyDo(rollSiteDo.getRollSiteId(), partyDo.getPartyId()));
                }
            }
        }
    }

    @Transactional
    private void updateRouteTableExcludeParty(List<RollSiteDo> rollSiteDoList, PartyDo excludeParty) throws Exception {
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
}
