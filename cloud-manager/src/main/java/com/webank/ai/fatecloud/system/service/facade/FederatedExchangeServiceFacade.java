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
package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.ObjectUtil;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.entity.PartyDo;
import com.webank.ai.fatecloud.system.pojo.dto.RollSitePageDto;
import com.webank.ai.fatecloud.system.pojo.dto.SiteDetailDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedExchangeService;
import com.webank.ai.fatecloud.system.service.impl.FederatedSiteManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FederatedExchangeServiceFacade implements Serializable {

    @Autowired
    FederatedExchangeService federatedExchangeService;

    @Autowired
    FederatedSiteManagerService federatedSiteManagerService;

    @Autowired
    CheckSignature checkSignature;

    public CommonResponse addExchange(ExchangeAddQo exchangeAddQo) {
        //check duplicate name
        if (this.checkExchangeName(exchangeAddQo)) {
            return new CommonResponse<>(ReturnCodeEnum.EXCHANGE_NAME_REPEAT);
        }
        //check exchange add qo
        int i = checkExchangeAddQo(exchangeAddQo);
        if (i == 1) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (i == 2) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_EXIST_ERROR);
        }
        if (i == 3) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_NETWORK_REPEAT);
        }

        for (RollSiteAddBean rollSiteAddBean : exchangeAddQo.getRollSiteAddBeanList()) {
            if (federatedExchangeService.queryExchange(new ExchangeQueryQo(rollSiteAddBean.getNetworkAccess())) == null) {
                return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR, rollSiteAddBean.getNetworkAccess());
            }

            // remove admin exchange party, check party ambiguity
            List<PartyAddBean> partyAddBeanList = rollSiteAddBean.getPartyAddBeanList();
            partyAddBeanList.removeIf(partyAddBean -> federatedExchangeService.checkExchangePartyId(partyAddBean.getPartyId()));
            for (PartyAddBean partyAddBean : partyAddBeanList) {
                if (federatedExchangeService.checkPartyExistInOtherExchange(partyAddBean.getPartyId(), null)) {
                    return new CommonResponse<>(ReturnCodeEnum.SITE_PARTY_EXCHANGE_REPEAT_ERROR, partyAddBean.getPartyId());
                }
            }
        }

        FederatedExchangeDo federatedExchangeDo = federatedExchangeService.addExchange(exchangeAddQo);

        if (federatedExchangeDo == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);
        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedExchangeDo);

    }

    private int checkExchangeAddQo(ExchangeAddQo exchangeAddQo) {

        if (StringUtils.isBlank(exchangeAddQo.getExchangeName())) {
            return 1;
        }

        if (!ObjectUtil.matchNetworkAddress(exchangeAddQo.getVipEntrance())) {
            return 1;
        }

        List<RollSiteAddBean> rollSiteAddBeanList = exchangeAddQo.getRollSiteAddBeanList();
        if (rollSiteAddBeanList == null || rollSiteAddBeanList.size() == 0) {
            return 1;
        }

//        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
        HashSet<String> rollSiteNetworkList = new HashSet<>();
        for (RollSiteAddBean rollSiteAddBean : rollSiteAddBeanList) {

            //check roll site ip
            String networkAccess = rollSiteAddBean.getNetworkAccess();
            if (!ObjectUtil.matchNetworkAddress(networkAccess) || !ObjectUtil.matchNetworkAddress(rollSiteAddBean.getNetworkAccessExit())) {
                return 1;
            }

            //check roll site is managed or not
            if (federatedExchangeService.findRollSite(networkAccess)) {
                return 2;
            }


            //check party list
            List<PartyAddBean> partyAddBeanList = rollSiteAddBean.getPartyAddBeanList();
            boolean result = this.checkPartyNetwork(partyAddBeanList, false);
            if (!result) {
                return 1;
            }

            rollSiteNetworkList.add(rollSiteAddBean.getNetworkAccess());
        }

        if (rollSiteAddBeanList.size() != rollSiteNetworkList.size()) {
            return 3;
        }

        //if (!exchangeAddQo.getVipEntrance().matches("[\\d]{1,3}(\\.[\\d]{1,3}){3}:[0-9]{1,5}")) {
        //    return 1;
        //}

        return 0;

    }

    public boolean checkExchangeName(ExchangeAddQo exchangeAddQo) {
        return federatedExchangeService.checkExchangeName(exchangeAddQo);
    }

    public boolean checkExchangeName(ExchangeUpdateQo exchangeUpdateQo) {
        return federatedExchangeService.checkExchangeName(exchangeUpdateQo);
    }

    public CommonResponse deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {
        // check if there are any using sites under the switch
        boolean result = federatedExchangeService.checkSiteExistByExchange(exchangeDeleteQo.getExchangeId());
        if (result) {
            return new CommonResponse<>(ReturnCodeEnum.SITE_PARTY_EXIST_ERROR);
        }

        federatedExchangeService.deleteExchange(exchangeDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }


    public boolean checkPartyNetwork(List<PartyAddBean> partyAddBeanList, Boolean checkStatus) {
        if (partyAddBeanList == null || partyAddBeanList.size() <= 0) {
            return false;
        }

//        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
        HashSet<String> partyIds = new HashSet<>();

        for (PartyAddBean partyAddBean : partyAddBeanList) {
            if (federatedExchangeService.checkExchangePartyId(partyAddBean.getPartyId())){
                partyIds.add(partyAddBean.getPartyId());
                continue;
            }

            String networkAccess = partyAddBean.getNetworkAccess();
            if (!ObjectUtil.matchNetworkAddressNew(networkAccess) && !ObjectUtil.matchNetworkAddress(networkAccess)) {
                return false;
            }

            //check secure status
            Integer secureStatus = partyAddBean.getSecureStatus();
            if (secureStatus == null || (secureStatus != 1 && secureStatus != 2)) {
                return false;
            }

            //check polling status
            Integer pollingStatus = partyAddBean.getPollingStatus();
            if (pollingStatus == null || (pollingStatus != 1 && pollingStatus != 2)) {
                return false;
            }

            String partyId = partyAddBean.getPartyId();
            if (StringUtils.isBlank(partyId)) {
                return false;
            }
            partyIds.add(partyId);

            if (checkStatus) {
                Integer status = partyAddBean.getStatus();

                HashSet<Integer> integers = new HashSet<>();
                Collections.addAll(integers, 1, 2, 3);
                if (!integers.contains(status)) {
                    return false;
                }

            }
        }

        return partyIds.size() == partyAddBeanList.size();
    }


    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePageForFateManager(ExchangePageForFateManagerQo exchangePageForFateManagerQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        String httpBody;
        if ((httpServletRequest.getHeader(Dict.VERSION) != null)) {
            ObjectMapper mapper = new ObjectMapper();
            httpBody = mapper.writeValueAsString(exchangePageForFateManagerQo);
        } else {
            httpBody = JSON.toJSONString(exchangePageForFateManagerQo);
        }
        boolean result = checkSignature.checkSignatureNew(httpServletRequest, httpBody, Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePageForFateManager(exchangePageForFateManagerQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangePage);
    }

    public CommonResponse<List<PartyDo>> queryExchange(ExchangeQueryQo exchangeQueryQo) {

        String networkAccess = exchangeQueryQo.getNetworkAccess();
        if (!ObjectUtil.matchNetworkAddress(networkAccess)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
//        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
//
//        if (!networkAccess.matches(ipAndPortRegex)) {
//            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
//        }

        List<PartyDo> partyDos = federatedExchangeService.queryExchange(exchangeQueryQo);

        if (partyDos == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);
        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, partyDos);

    }

    public CommonResponse addRollSite(RollSieAddQo rollSieAddQo) {

        //check roll site is managed or not
        if (federatedExchangeService.findRollSite(rollSieAddQo.getNetworkAccess())) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_EXIST_ERROR);
        }
        if (federatedExchangeService.queryExchange(new ExchangeQueryQo(rollSieAddQo.getNetworkAccess())) == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR, rollSieAddQo.getNetworkAccess());
        }

        //check party
        List<PartyAddBean> partyAddBeanList = rollSieAddQo.getPartyAddBeanList();
        if (!this.checkPartyNetwork(partyAddBeanList, true) || !ObjectUtil.matchNetworkAddress(rollSieAddQo.getNetworkAccessExit())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        // remove admin exchange party, check party ambiguity
        partyAddBeanList.removeIf(partyAddBean -> federatedExchangeService.checkExchangePartyId(partyAddBean.getPartyId()));
        for (PartyAddBean partyAddBean : partyAddBeanList) {
            if (federatedExchangeService.checkPartyExistInOtherExchange(partyAddBean.getPartyId(), rollSieAddQo.getExchangeId())) {
                return new CommonResponse<>(ReturnCodeEnum.SITE_PARTY_EXCHANGE_REPEAT_ERROR);
            }
        }

        federatedExchangeService.addRollSite(rollSieAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse updateRollSite(RollSiteUpdateQo rollSiteUpdateQo) {
        Long rollSiteId = rollSiteUpdateQo.getRollSiteId();
        if (rollSiteId == null || rollSiteId <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }

        //check party
        if (!this.checkPartyNetwork(rollSiteUpdateQo.getPartyAddBeanList(), true)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        // exclude exchange admin party
        rollSiteUpdateQo.getPartyAddBeanList().removeIf(partyAddBean -> federatedExchangeService.checkExchangePartyId(partyAddBean.getPartyId()));
        federatedExchangeService.updateRollSite(rollSiteUpdateQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse deleteRollSite(RollSiteDeleteQo rollSiteDeleteQo) {
        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        if (rollSiteId == null || rollSiteId <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }

        if (federatedExchangeService.checkSiteExistByRollSite(rollSiteId)) {
            return new CommonResponse<>(ReturnCodeEnum.SITE_PARTY_EXIST_ERROR);
        }

        federatedExchangeService.deleteRollSite(rollSiteDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse publishRollSite(RollSiteDeleteQo rollSiteDeleteQo) {
        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        if (rollSiteId == null || rollSiteId <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        int i = federatedExchangeService.publishRollSite(rollSiteDeleteQo);
        if (i == 1) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);

        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePage(ExchangePageQo exchangePageQo) {

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePage(exchangePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangePage);
    }

    public CommonResponse<PageBean<RollSitePageDto>> findRollSitePage(RollSitePageQo rollSitePageQo) {
        if (rollSitePageQo.getExchangeId() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        PageBean<RollSitePageDto> rollSitePageDtoPageBean = federatedExchangeService.findRollSitePage(rollSitePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, rollSitePageDtoPageBean);

    }

    // v1.4
    public CommonResponse<List<PartyDo>> finPartyPage(PartyQueryQo partyQueryQo) {
        if (ObjectUtil.isEmpty(partyQueryQo.getRollSiteId())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        PageBean<PartyDo> partyDoPageBean = federatedExchangeService.findPartyPage(partyQueryQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, partyDoPageBean.getList());
    }

    // v1.4
    public CommonResponse<Void> updateIpManagerPartyInfo(PartyUpdateQo partyUpdateQo) {
        if (ObjectUtil.isEmpty(partyUpdateQo.getId(), partyUpdateQo.getPartyId(), partyUpdateQo.getExchangeId(),
                partyUpdateQo.getPollingStatus(), partyUpdateQo.getSecureStatus())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        try {
            federatedExchangeService.updateIpManagerPartyInfo(partyUpdateQo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new CommonResponse<>(ReturnCodeEnum.SITE_PARTY_UPDATE_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    // v1.4
    public CommonResponse<List<FederatedExchangeDo>> findAllExchange() {
        List<FederatedExchangeDo> exchangeDoList = federatedExchangeService.findAllExchange();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangeDoList);
    }

    public CommonResponse<Boolean> checkPartyExist(PartyQueryQo partyQueryQo) {
        if (ObjectUtil.isEmpty(partyQueryQo.getPartyId())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        long parseInt;
        try {
            parseInt = Long.parseLong(partyQueryQo.getPartyId());
        } catch (Exception e) {
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS, true);
        }

        if (partyQueryQo.getRollSiteId() != null) {
            List<PartyDo> partyByRollSiteId = federatedExchangeService.findPartyByRollSiteId(partyQueryQo.getRollSiteId());
            if (partyByRollSiteId.stream().anyMatch(partyDo -> partyQueryQo.getPartyId().equals(partyDo.getPartyId()))) {
                return new CommonResponse<>(ReturnCodeEnum.SUCCESS, Boolean.TRUE);
            }
        }

        SiteDetailDto siteByPartyId = federatedSiteManagerService.findSiteByPartyId(parseInt, "", 2);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, siteByPartyId != null);
    }
}
