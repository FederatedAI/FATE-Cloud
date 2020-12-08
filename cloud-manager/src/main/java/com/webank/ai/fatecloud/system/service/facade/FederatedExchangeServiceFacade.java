package com.webank.ai.fatecloud.system.service.facade;

import com.alibaba.fastjson.JSON;
import com.webank.ai.fatecloud.common.CheckSignature;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedExchangeDo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangePageQo;
import com.webank.ai.fatecloud.system.pojo.qo.ExchangeUpdateQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedExchangeService;
import lombok.extern.slf4j.Slf4j;
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
        FederatedExchangeDo federatedExchangeDo = federatedExchangeService.addExchange(exchangeAddQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS,federatedExchangeDo);

    }

    public CommonResponse deleteExchange(ExchangeDeleteQo exchangeDeleteQo) {
        federatedExchangeService.deleteExchange(exchangeDeleteQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse updateExchange(ExchangeUpdateQo exchangeUpdateQo) {
        federatedExchangeService.updateExchange(exchangeUpdateQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }

    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePage(ExchangePageQo exchangePageQo) {

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePage(exchangePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS,exchangePage);
    }

    public CommonResponse<PageBean<FederatedExchangeDo>> findExchangePageForFateManager(ExchangePageQo exchangePageQo, HttpServletRequest httpServletRequest) {

        boolean result = checkSignature.checkSignatureNew(httpServletRequest, JSON.toJSONString(exchangePageQo), Dict.FATE_MANAGER_USER, new int[]{2}, null);
        if (!result) {
            return new CommonResponse(ReturnCodeEnum.AUTHORITY_ERROR);
        }

        PageBean<FederatedExchangeDo> exchangePage = federatedExchangeService.findExchangePage(exchangePageQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS,exchangePage);
    }
}
