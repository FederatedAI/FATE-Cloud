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
package com.webank.ai.fatecloud.system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.dao.entity.PartyDo;
import com.webank.ai.fatecloud.system.pojo.dto.RollSitePageDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.facade.FederatedExchangeServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange")
@Slf4j
@Api(tags = "FederatedExchangeController", description = "federated exchange controller api")
public class FederatedExchangeController {

    @Autowired
    FederatedExchangeServiceFacade federatedExchangeServiceFacade;

    @PostMapping(value = "/query")
    @ApiOperation(value = "find exchange")
    public CommonResponse<List<PartyDo>> queryExchange(@RequestBody ExchangeQueryQo exchangeQueryQo) {
        log.info("url:find paged exchange, requestBody:{}", exchangeQueryQo);
        return federatedExchangeServiceFacade.queryExchange(exchangeQueryQo);
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "add new exchange")
    public CommonResponse addExchange(@RequestBody ExchangeAddQo exchangeAddQo) {
        log.info("url:add exchange requestBody:{}", exchangeAddQo);
        return federatedExchangeServiceFacade.addExchange(exchangeAddQo);
    }

    @PostMapping(value = "/delete")
    @ApiOperation(value = "delete exchange")
    public CommonResponse deleteExchange(@RequestBody ExchangeDeleteQo exchangeDeleteQo) {
        log.info("url:delete exchange, requestBody:{}", exchangeDeleteQo);
        return federatedExchangeServiceFacade.deleteExchange(exchangeDeleteQo);
    }

    @PostMapping(value = "/rollsite/add")
    @ApiOperation(value = "add roll site for exchange")
    public CommonResponse addRollSite(@RequestBody RollSieAddQo rollSieAddQo) {
        log.info("url:add roll site, requestBody:{}", rollSieAddQo);
        return federatedExchangeServiceFacade.addRollSite(rollSieAddQo);
    }

    @PostMapping(value = "/rollsite/update")
    @ApiOperation(value = "update roll site for exchange")
    public CommonResponse updateRollSite(@RequestBody RollSiteUpdateQo rollSiteUpdateQo) {
        log.info("url:update roll site, requestBody:{}", rollSiteUpdateQo);
        return federatedExchangeServiceFacade.updateRollSite(rollSiteUpdateQo);
    }

    @PostMapping(value = "/rollsite/delete")
    @ApiOperation(value = "delete roll site for exchange")
    public CommonResponse deleteRollSite(@RequestBody RollSiteDeleteQo rollSiteDeleteQo) {
        log.info("url:delete roll site, requestBody:{}", rollSiteDeleteQo);
        return federatedExchangeServiceFacade.deleteRollSite(rollSiteDeleteQo);
    }

    @PostMapping(value = "/rollsite/publish")
    @ApiOperation(value = "publish roll site for exchange")
    public CommonResponse publishRollSite(@RequestBody RollSiteDeleteQo rollSiteDeleteQo) {
        log.info("url:publish roll site, requestBody:{}", rollSiteDeleteQo);
        return federatedExchangeServiceFacade.publishRollSite(rollSiteDeleteQo);
    }


    @PostMapping(value = "/exchange/page")
    @ApiOperation(value = "find exchange page")
    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePage(@RequestBody ExchangePageQo exchangePageQo) {
        log.info("url:find paged exchange, requestBody:{}", exchangePageQo);
        return federatedExchangeServiceFacade.findExchangePage(exchangePageQo);
    }

    @PostMapping(value = "/rollsite/page")
    @ApiOperation(value = "find roll site page")
    public CommonResponse<PageBean<RollSitePageDto>> findRollSitePage(@RequestBody RollSitePageQo rollSitePageQo) {
        log.info("url:find paged roll site, requestBody:{}", rollSitePageQo);
        return federatedExchangeServiceFacade.findRollSitePage(rollSitePageQo);
    }

    @PostMapping(value = "/exchange/page/fatemanager")
    @ApiOperation(value = "find exchange page for fate manager")
    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePageForFateManager(@RequestBody ExchangePageForFateManagerQo exchangePageForFateManagerQo, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        log.info("url:find paged exchange for fate manager, requestBody:{}", exchangePageForFateManagerQo);
        return federatedExchangeServiceFacade.findExchangePageForFateManager(exchangePageForFateManagerQo, httpServletRequest);
    }

    @PostMapping(value = "/party/page")
    @ApiOperation(value = "find party page")
    public CommonResponse<List<PartyDo>> findPartyPage(@RequestBody PartyQueryQo partyQueryQo) {
        log.info("url:find party page, requestBody:{}", partyQueryQo);
        return federatedExchangeServiceFacade.finPartyPage(partyQueryQo);
    }

    @PostMapping(value = "/party/check")
    @ApiOperation(value = "check party is exist")
    public CommonResponse<Boolean> checkPartyExist(@RequestBody PartyQueryQo partyQueryQo) {
        log.info("url:check party is exist, requestBody:{}", partyQueryQo);
        return federatedExchangeServiceFacade.checkPartyExist(partyQueryQo);
    }

    @PostMapping(value = "/party/edit")
    @ApiOperation(value = "edit site party info")
    public CommonResponse<Void> updateParty(@RequestBody PartyUpdateQo partyUpdateQo) {
        log.info("url:edit site party info, requestBody:{}", partyUpdateQo);
        return federatedExchangeServiceFacade.updateIpManagerPartyInfo(partyUpdateQo);
    }

    @PostMapping(value = "/exchange/dropDownBox")
    @ApiOperation(value = "find exchange drop down box")
    public CommonResponse<List<FederatedExchangeDo>> findAllExchange() {
        CommonResponse<List<FederatedExchangeDo>> exchangeDoList = federatedExchangeServiceFacade.findAllExchange();
        log.info("url:find exchange drop down box, resultBody:{}", exchangeDoList);
        return exchangeDoList;
    }

}
