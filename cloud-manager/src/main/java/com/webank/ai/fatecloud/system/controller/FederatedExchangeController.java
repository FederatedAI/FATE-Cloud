package com.webank.ai.fatecloud.system.controller;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeUpdateQo;
import com.webank.ai.fatecloud.system.service.facade.FederatedExchangeServiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange")
@Slf4j
@Api(tags = "FederatedExchangeController", description = "federated exchange controller api")
public class FederatedExchangeController {

    @Autowired
    FederatedExchangeServiceFacade federatedExchangeServiceFacade;

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

    @PostMapping(value = "/update")
    @ApiOperation(value = "update exchange")
    public CommonResponse updateExchange(@RequestBody ExchangeUpdateQo exchangeUpdateQo) {
        log.info("url:update exchange, requestBody:{}", exchangeUpdateQo);
        return federatedExchangeServiceFacade.updateExchange(exchangeUpdateQo);
    }

    @PostMapping(value = "/page")
    @ApiOperation(value = "find exchange page")
    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePage(@RequestBody ExchangePageQo exchangePageQo) {
        log.info("url:find paged exchange, requestBody:{}", exchangePageQo);
        return federatedExchangeServiceFacade.findExchangePage(exchangePageQo);
    }
}
