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
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.PartyDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.entity.RollSiteDo;
import com.webank.ai.fatecloud.system.pojo.dto.RollSitePageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FederatedExchangeServiceFacade implements Serializable {

    @Autowired
    FederatedExchangeService federatedExchangeService;

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

        if (StringUtils.isBlank(exchangeAddQo.getVip())) {
            return 1;
        }

        List<RollSiteAddBean> rollSiteAddBeanList = exchangeAddQo.getRollSiteAddBeanList();
        if (rollSiteAddBeanList == null || rollSiteAddBeanList.size() == 0) {
            return 1;
        }

        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
        HashSet<String> rollSiteNetworkList = new HashSet<>();
        for (RollSiteAddBean rollSiteAddBean : rollSiteAddBeanList) {

            //check roll site ip
            String networkAccess = rollSiteAddBean.getNetworkAccess();
            if (!networkAccess.matches(ipAndPortRegex)) {
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

        if (!exchangeAddQo.getVip().matches(ipAndPortRegex)) {
            return 1;
        }

        return 0;

    }

    public boolean checkExchangeName(ExchangeAddQo exchangeAddQo) {
        return federatedExchangeService.checkExchangeName(exchangeAddQo);
    }

    public boolean checkExchangeName(ExchangeUpdateQo exchangeUpdateQo) {
        return federatedExchangeService.checkExchangeName(exchangeUpdateQo);
    }

    public CommonResponse deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {
        federatedExchangeService.deleteExchange(exchangeDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }


    public boolean checkPartyNetwork(List<PartyAddBean> partyAddBeanList, Boolean checkStatus) {
        if (partyAddBeanList == null || partyAddBeanList.size() <= 0) {
            return false;
        }

        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";
        HashSet<String> partyIds = new HashSet<>();

        for (PartyAddBean partyAddBean : partyAddBeanList) {
            String networkAccess = partyAddBean.getNetworkAccess();
            if (!networkAccess.matches(ipAndPortRegex)) {
                return false;
            }
            //check secure status
            Integer secureStatus = partyAddBean.getSecureStatus();
            if (secureStatus == null || (secureStatus != 1 && secureStatus != 2)) {
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
                integers.add(1);
                integers.add(2);
                integers.add(3);
                if (!integers.contains(status)) {
                    return false;
                }

            }
        }

        if (partyIds.size() != partyAddBeanList.size()) {
            return false;
        }

        return true;
    }


    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePageForFateManager(ExchangePageForFateManagerQo exchangePageForFateManagerQo, HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(exchangePageForFateManagerQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePageForFateManager(exchangePageForFateManagerQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangePage);
    }

    public CommonResponse<List<PartyDo>> queryExchange(ExchangeQueryQo exchangeQueryQo) {

        String networkAccess = exchangeQueryQo.getNetworkAccess();
        if (StringUtils.isBlank(networkAccess)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$";

        if (!networkAccess.matches(ipAndPortRegex)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

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
        //check party
        if (!this.checkPartyNetwork(rollSieAddQo.getPartyAddBeanList(), true)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
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

        federatedExchangeService.updateRollSite(rollSiteUpdateQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse deleteRollSite(RollSiteDeleteQo rollSiteDeleteQo) {
        Long rollSiteId = rollSiteDeleteQo.getRollSiteId();
        if (rollSiteId == null || rollSiteId <= 0) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

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
        if (StringUtils.isBlank(String.valueOf(rollSitePageQo.getExchangeId()))) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        PageBean<RollSitePageDto> rollSitePageDtoPageBean = federatedExchangeService.findRollSitePage(rollSitePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, rollSitePageDtoPageBean);

    }
}
