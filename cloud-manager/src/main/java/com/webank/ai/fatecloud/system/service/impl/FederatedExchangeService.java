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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.protobuf.ByteString;
import com.webank.ai.eggroll.api.networking.proxy.Proxy;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.grpc.ExchangeGrpcUtil;
import com.webank.ai.fatecloud.grpc.NetworkBean;
import com.webank.ai.fatecloud.system.dao.entity.PartyDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.entity.RollSiteDo;
import com.webank.ai.fatecloud.system.dao.mapper.PartyMapper;
import com.webank.ai.fatecloud.system.dao.mapper.RollSiteMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedExchangeMapper;
import com.webank.ai.fatecloud.system.pojo.dto.RollSitePageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
@Slf4j
public class FederatedExchangeService implements Serializable {
    @Autowired
    FederatedExchangeMapper federatedExchangeMapper;

    @Autowired
    RollSiteMapper rollSiteMapper;

    @Autowired
    PartyMapper partyMapper;

    public boolean findRollSite(String network) {
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", network);
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        return rollSiteDos.size() > 0;
    }


    @Transactional
    public FederatedExchangeDo addExchange(ExchangeAddQo exchangeAddQo) {

        //add exchange table
        FederatedExchangeDo federatedExchangeDo = new FederatedExchangeDo();
        federatedExchangeDo.setExchangeName(exchangeAddQo.getExchangeName());
        federatedExchangeDo.setVip(exchangeAddQo.getVip());
        federatedExchangeMapper.insert(federatedExchangeDo);

        //add roll site table
        List<RollSiteAddBean> rollSiteAddBeanList = exchangeAddQo.getRollSiteAddBeanList();
        for (RollSiteAddBean rollSiteAddBean : rollSiteAddBeanList) {
            RollSiteDo rollSiteDo = new RollSiteDo();
            rollSiteDo.setNetworkAccess(rollSiteAddBean.getNetworkAccess());
            rollSiteDo.setExchangeId(federatedExchangeDo.getExchangeId());
            rollSiteMapper.insert(rollSiteDo);

            //add party table
            List<PartyAddBean> partyAddBeanList = rollSiteAddBean.getPartyAddBeanList();
            for (PartyAddBean partyAddBean : partyAddBeanList) {
                PartyDo partyDo = new PartyDo();
                partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
                partyDo.setSecureStatus(partyAddBean.getSecureStatus());
                partyDo.setPartyId(partyAddBean.getPartyId());
                partyDo.setStatus(1);
                partyDo.setRollSiteId(rollSiteDo.getRollSiteId());

                partyMapper.insert(partyDo);

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
            //update party table
            QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
            partyDoQueryWrapper.eq("roll_site_id", rollSiteDo.getRollSiteId());
            partyMapper.delete(partyDoQueryWrapper);
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
            if (is_secure != null && "false".equals(String.valueOf(is_secure))) {
                partyDo.setSecureStatus(2);
            } else {
                partyDo.setSecureStatus(1);
            }
            partyDo.setStatus(1);
            partyDo.setUpdateTime(new Date());

            partyDos.add(partyDo);

        }
        return partyDos;

    }

    public List<PartyDo> queryExchange(ExchangeQueryQo exchangeQueryQo) {

        //send grpc request
        String[] network = exchangeQueryQo.getNetworkAccess().split(":");
        String routerTableString;

        try {
            Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), "eggroll", "exchange", "get_route_table");

            Proxy.Data body = exchange.getBody();
            ByteString value = body.getValue();
            routerTableString = value.toStringUtf8();

        } catch (Exception e) {
            log.error("update route table error by grpc ", e);
            return null;
        }


        ArrayList<PartyDo> partyDos = buildPartyList(routerTableString);


        //find roll site in roll site table
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("network_access", exchangeQueryQo.getNetworkAccess());
        List<RollSiteDo> rollSiteDos = rollSiteMapper.selectList(rollSiteDoQueryWrapper);
        if (rollSiteDos.size() > 0) {
            //update party table
            RollSiteDo rollSiteDo = rollSiteDos.get(0);
            Long rollSiteId = rollSiteDo.getRollSiteId();

            QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
            partyDoQueryWrapper.eq("roll_site_id", rollSiteId);
            List<PartyDo> partyDosExisted = partyMapper.selectList(partyDoQueryWrapper);

            for (PartyDo partyDo : partyDos) {
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();

                boolean exist = false;
                for (PartyDo existPartyDo : partyDosExisted) {
                    String existPartyId = existPartyDo.getPartyId();
                    String existNetworkAccess = existPartyDo.getNetworkAccess();
                    Integer existSecureStatus = existPartyDo.getSecureStatus();
                    if (partyId.equals(existPartyId)) {
                        exist = true;
                        if (existPartyDo.getStatus() == 1 && (!existNetworkAccess.equals(networkAccess) || !existSecureStatus.equals(secureStatus))) {
                            existPartyDo.setNetworkAccess(networkAccess);
                            existPartyDo.setSecureStatus(secureStatus);
                            existPartyDo.setUpdateTime(new Date());
                            partyMapper.updateById(existPartyDo);
                        }
//                        break;
                    }
                }

                //party not exist  insert
                if (!exist) {
                    partyDo.setRollSiteId(rollSiteId);
                    Date date = new Date();
                    partyDo.setCreateTime(date);
                    partyDo.setUpdateTime(date);
                    partyMapper.insert(partyDo);
                }
            }

            //delete party
            HashSet<String> partyIdsFromGRPC = new HashSet<>();
            for (PartyDo aDo : partyDos) {
                partyIdsFromGRPC.add(aDo.getPartyId());
            }
            for (PartyDo partyDo : partyDosExisted) {
                if (partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))) {
                    partyMapper.deleteById(partyDo.getId());
                }

            }


            //find newest party information
            List<PartyDo> partyDosFinal = partyMapper.selectList(partyDoQueryWrapper);
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
                Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), "eggroll", "exchange", "get_route_table");

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

            for (PartyDo partyDo : partyDos) {
                String partyId = partyDo.getPartyId();
                String networkAccess = partyDo.getNetworkAccess();
                Integer secureStatus = partyDo.getSecureStatus();

                boolean exist = false;
                for (PartyDo existPartyDo : partyDosExisted) {
                    String existPartyId = existPartyDo.getPartyId();
                    if (partyId.equals(existPartyId)) {
                        exist = true;
                        if (existPartyDo.getStatus() == 1
                                && ((!existPartyDo.getNetworkAccess().equals(networkAccess))||(!existPartyDo.getSecureStatus().equals(secureStatus)))
                        ) {
                            existPartyDo.setNetworkAccess(networkAccess);
                            existPartyDo.setSecureStatus(secureStatus);
                            existPartyDo.setUpdateTime(new Date());
                            partyMapper.updateById(existPartyDo);
                        }
//                        break;
                    }
                }

                //party not exist  insert
                if (!exist) {
                    partyDo.setRollSiteId(rollSiteId);
                    Date date = new Date();
                    partyDo.setCreateTime(date);
                    partyDo.setUpdateTime(date);
                    partyMapper.insert(partyDo);
                }
            }

            //delete party
            HashSet<String> partyIdsFromGRPC = new HashSet<>();
            for (PartyDo aDo : partyDos) {
                partyIdsFromGRPC.add(aDo.getPartyId());
            }
            for (PartyDo partyDo : partyDosExisted) {
                if (partyDo.getStatus() == 1 && (!partyIdsFromGRPC.contains(partyDo.getPartyId()))) {
                    partyMapper.deleteById(partyDo.getId());
                }

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
        rollSiteMapper.insert(rollSiteDo);

        //insert party
        List<PartyAddBean> partyAddBeanList = rollSieAddQo.getPartyAddBeanList();
        for (PartyAddBean partyAddBean : partyAddBeanList) {
            PartyDo partyDo = new PartyDo();
            partyDo.setRollSiteId(rollSiteDo.getRollSiteId());
            partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
            partyDo.setSecureStatus(partyAddBean.getSecureStatus());
            partyDo.setStatus(partyAddBean.getStatus());
            partyDo.setPartyId(partyAddBean.getPartyId());
            partyMapper.insert(partyDo);
        }

    }

    @Transactional
    public void updateRollSite(RollSiteUpdateQo rollSiteUpdateQo) {
        Date date = new Date();
        List<PartyAddBean> partyAddBeanList = rollSiteUpdateQo.getPartyAddBeanList();
        boolean updateResult = false;
        for (PartyAddBean partyAddBean : partyAddBeanList) {

            QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
            Long rollSiteId = rollSiteUpdateQo.getRollSiteId();
            partyDoQueryWrapper.eq("roll_site_id", rollSiteId).eq("party_id", partyAddBean.getPartyId());
            List<PartyDo> partyDos = partyMapper.selectList(partyDoQueryWrapper);

            if (partyDos.size() <= 0) {
                PartyDo partyDo = new PartyDo();
                partyDo.setPartyId(partyAddBean.getPartyId());
                partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
                partyDo.setSecureStatus(partyAddBean.getSecureStatus());
                partyDo.setStatus(partyAddBean.getStatus());
                partyDo.setRollSiteId(rollSiteId);
                partyDo.setCreateTime(date);
                partyDo.setUpdateTime(date);
                partyMapper.insert(partyDo);
                updateResult = true;
            } else {
                for (PartyDo partyDo : partyDos) {

                    if ((!partyDo.getNetworkAccess().equals(partyAddBean.getNetworkAccess())) || (!partyDo.getSecureStatus().equals(partyAddBean.getSecureStatus())) || (!partyDo.getStatus().equals(partyAddBean.getStatus()))) {
                        partyDo.setNetworkAccess(partyAddBean.getNetworkAccess());
                        partyDo.setSecureStatus(partyAddBean.getSecureStatus());
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

        //delete party
        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.eq("roll_site_id", rollSiteId);
        partyMapper.delete(partyDoQueryWrapper);

    }

    @Transactional
    public int publishRollSite(RollSiteDeleteQo rollSiteDeleteQo) {

        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        RollSiteDo rollSiteDo = rollSiteMapper.selectById(rollSiteId);

        QueryWrapper<PartyDo> partyDoQueryWrapper = new QueryWrapper<>();
        partyDoQueryWrapper.eq("roll_site_id", rollSiteId);
        List<PartyDo> partyDos = partyMapper.selectList(partyDoQueryWrapper);

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
                partyMapper.updateById(partyDo);
            }
            if (partyDo.getStatus() == 3) {
                partyMapper.deleteById(partyDo.getId());
            }

        }

        //update roll site table
        rollSiteDo.setUpdateTime(date);
        rollSiteMapper.updateById(rollSiteDo);

        //update exchange table
        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("roll_site_id",rollSiteDeleteQo.getRollSiteId());
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
        HashMap<String, Object> routeTableMap = new HashMap<>();

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
        Proxy.Packet packet = ExchangeGrpcUtil.setExchange(ipAndPort[0], Integer.parseInt(ipAndPort[1]), "eggroll", routeTableJsonString, "exchange", "set_route_table");

        //todo
        Proxy.Data body = packet.getBody();
        ByteString value = body.getValue();
        String information = value.toStringUtf8();
        log.info("returned information when publish router information from roll site :{}", information);
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

        QueryWrapper<RollSiteDo> rollSiteDoQueryWrapper = new QueryWrapper<>();
        rollSiteDoQueryWrapper.eq("exchange_id", rollSitePageQo.getExchangeId());
        Integer count = rollSiteMapper.selectCount(rollSiteDoQueryWrapper);
        PageBean<RollSitePageDto> rollSitePageDtoPageBean = new PageBean<>(rollSitePageQo.getPageNum(), rollSitePageQo.getPageSize(), count);
        long startIndex = rollSitePageDtoPageBean.getStartIndex();

        List<RollSiteDo> rollSiteMapperRollSitePage = rollSiteMapper.findRollSitePage(startIndex, rollSitePageQo);

        LinkedList<RollSitePageDto> rollSitePageDtos = new LinkedList<>();

        for (RollSiteDo rollSiteDo : rollSiteMapperRollSitePage) {
            String status = "published";

            List<PartyDo> partyDos = rollSiteDo.getPartyDos();
            Iterator<PartyDo> iterator = partyDos.iterator();
            while (iterator.hasNext()) {
                PartyDo next = iterator.next();
                if (next.getStatus() != 1) {
                    iterator.remove();
                    status = "unpublished";
                }
            }
            int size = partyDos.size();
            RollSitePageDto rollSitePageDto = new RollSitePageDto(rollSiteDo);
            rollSitePageDto.setStatus(status);
            rollSitePageDto.setCount(size);
            rollSitePageDtos.add(rollSitePageDto);
        }

        rollSitePageDtoPageBean.setList(rollSitePageDtos);

        for (RollSitePageDto rollSitePageDto : rollSitePageDtos) {
            //send grpc request
            String[] network = rollSitePageDto.getNetworkAccess().split(":");
            String routerTableString;

            try {
                Proxy.Packet exchange = ExchangeGrpcUtil.findExchange(network[0], Integer.parseInt(network[1]), "eggroll", "exchange", "get_route_table");

                Proxy.Data body = exchange.getBody();
                ByteString value = body.getValue();
                routerTableString = value.toStringUtf8();

            } catch (Exception e) {
                log.error("update route table error by grpc ", e);
                rollSitePageDto.setPartyDos(null);
                rollSitePageDto.setCount(0);
                continue;
            }

            ArrayList<PartyDo> partyDos = buildPartyList(routerTableString);
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
}
