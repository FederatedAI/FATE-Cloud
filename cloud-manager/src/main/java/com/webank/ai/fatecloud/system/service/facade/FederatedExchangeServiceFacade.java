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
import com.webank.ai.fatecloud.system.dao.entity.ExchangeDetailsDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

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
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        //check exchange add qo
        if (!checkExchangeAddQo(exchangeAddQo)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        FederatedExchangeDo federatedExchangeDo = federatedExchangeService.addExchange(exchangeAddQo);

        if (federatedExchangeDo == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);
        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedExchangeDo);

    }

    private boolean checkExchangeAddQo(ExchangeAddQo exchangeAddQo) {

        if (StringUtils.isBlank(exchangeAddQo.getExchangeName())) {
            return false;
        }
        List<ExchangeDetailsAddBean> exchangeDetailsAddBeanList = exchangeAddQo.getExchangeDetailsAddBeanList();
        if (exchangeDetailsAddBeanList == null || exchangeDetailsAddBeanList.size() == 0) {
            return false;
        }
        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$\n";
        for (ExchangeDetailsAddBean exchangeDetailsAddBean : exchangeDetailsAddBeanList) {
            String partyId = exchangeDetailsAddBean.getPartyId();
            if (StringUtils.isBlank(partyId)) {
                return false;
            }
            String networkAccess = exchangeDetailsAddBean.getNetworkAccess();
            if (!networkAccess.matches(ipAndPortRegex)) {
                return false;
            }
        }

        return exchangeAddQo.getNetworkAccess().matches(ipAndPortRegex);

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

    public CommonResponse updateExchange(ExchangeUpdateQo exchangeUpdateQo) {

        //check duplicate name
        if (this.checkExchangeName(exchangeUpdateQo)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        ExchangeAddQo exchangeAddQo = new ExchangeAddQo(exchangeUpdateQo);
        //check duplicate name
        if (!checkExchangeAddQo(exchangeAddQo)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        FederatedExchangeDo federatedExchangeDo = federatedExchangeService.addExchange(exchangeAddQo);

        if (federatedExchangeDo == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);
        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePage(ExchangePageQo exchangePageQo) {

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePage(exchangePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangePage);
    }

    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePageForFateManager(ExchangePageQo exchangePageQo, HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(exchangePageQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePage(exchangePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangePage);
    }

    public CommonResponse<List<ExchangeDetailsDo>> queryExchange(ExchangeQueryQo exchangeQueryQo) {
        if (exchangeQueryQo == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        String networkAccess = exchangeQueryQo.getNetworkAccess();
        if (StringUtils.isBlank(networkAccess)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        String ipAndPortRegex = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[0-5]\\d{4}|[1-9]\\d{0,3})$\n";

        if (!networkAccess.matches(ipAndPortRegex)) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        List<ExchangeDetailsDo> exchangeDetailsDos = federatedExchangeService.queryExchange(exchangeQueryQo);

        if (exchangeDetailsDos == null) {
            return new CommonResponse<>(ReturnCodeEnum.ROLLSITE_GRPC_ERROR);
        }

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, exchangeDetailsDos);

    }
}
