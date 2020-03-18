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
package com.webank.ai.fatecloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.global.Dict;
import com.webank.ai.fatecloud.global.ResponseResult;
import com.webank.ai.fatecloud.global.ReturnCode;
import com.webank.ai.fatecloud.pojo.*;
import com.webank.ai.fatecloud.service.StationService;
import com.webank.ai.fatecloud.utils.EncryptUtil;
import com.webank.ai.fatecloud.utils.MyRequestWrapper;
import com.webank.ai.fatecloud.utils.PageBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/station")
public class StationController {

    private final Logger logger = LoggerFactory.getLogger(StationController.class);

    @Autowired
    StationService stationService;
    @Autowired
    HttpServletRequest httpServletRequest;


    @RequestMapping(value = "/management/add", method = RequestMethod.POST)
    public ResponseResult addStation() {

        StationVO stationVO = stationService.createStation();
        if (stationVO == null) {
            return new ResponseResult(ReturnCode.CREATE_ERROR);
        }

        return new ResponseResult<>(ReturnCode.SUCCESS, stationVO);
    }


    @RequestMapping(value = "/management/update", method = RequestMethod.POST)
    public ResponseResult updateStation(@RequestBody StationQO stationQO) {

        logger.info("RequestBody:{}", stationQO.toString());

        if (null == stationQO.getPartyId()) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        return stationService.updateStation(stationQO);
    }


    @RequestMapping(value = "/management/remove", method = RequestMethod.POST)
    public ResponseResult deleteStation(@RequestBody StationQO stationQO) {

        logger.info("RequestBody:{}", stationQO.toString());

        if (null == stationQO.getPartyId()) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        return stationService.deleteStation(stationQO);

    }


    @RequestMapping(value = "/management/register", method = RequestMethod.POST)
    public ResponseResult registerStation(@RequestBody RegisterStationQO registerStationQO) throws IOException {

        logger.info("RequestBody:{}", registerStationQO.toString());

        if (null == registerStationQO.getPartyId()) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        return checkSignature() == 0 ? stationService.registerStation(registerStationQO) : new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
    }


    @RequestMapping(value = "/management/query", method = RequestMethod.POST)
    public ResponseResult findStation(@RequestBody StationQO stationQO) throws IOException {

        logger.info("RequestBody:{}", stationQO.toString());

        if (null == stationQO.getPartyId()) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        return checkSignature() == 0 ? new ResponseResult<>(ReturnCode.SUCCESS, stationService.findStationVO(stationQO)) : new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);

    }

    @RequestMapping(value = "/management/queryPage", method = RequestMethod.POST)
    public ResponseResult findPage(@RequestBody PageQO pageParameters) {

        logger.info("RequestBody:{}", pageParameters.toString());

        Long pageNumber = pageParameters.getPageNumber();
        Long pageSize = pageParameters.getPageSize();
        if (null == pageNumber || null == pageSize) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        PageBean page = stationService.findPage(pageNumber, pageSize);

        return new ResponseResult<>(ReturnCode.SUCCESS, page);
    }


    @RequestMapping(value = "/management/heart", method = RequestMethod.POST)
    public ResponseResult checkHeart(@RequestBody StationQO stationQO) throws IOException {

        logger.info("RequestBody:{}", stationQO.toString());

        if (null == stationQO.getPartyId()) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        return checkSignature() == 0 ? stationService.heartCheck(stationQO) : new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
    }

    @RequestMapping(value = "/management/check", method = RequestMethod.POST)
    public ResponseResult checkStationAuthorization(@RequestBody String parameters) throws IOException {

        logger.info("RequestBody:{}", parameters);
        int checkcode =checkSignature();
        if(checkcode == 0){
            String partyId = httpServletRequest.getHeader(Dict.PARTY_ID);
            Station station = stationService.findStation(Long.parseLong(partyId));
            if ((2 == station.getNodeStatus()) && (1 == station.getDetectiveStatus())) {
                return stationService.checkStationAuthorization(parameters);
            }else
            {
                return  new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
            }

        }else {
            return  new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
        }
//        return checkSignature() == 0 ? stationService.checkStationAuthorization(parameters) : new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
    }

    private int checkSignature() throws IOException {
        String signature = httpServletRequest.getHeader(Dict.SIGNATURE);
        String partyId = httpServletRequest.getHeader(Dict.PARTY_ID);
        String role = httpServletRequest.getHeader(Dict.ROLE);
        String appKey = httpServletRequest.getHeader(Dict.APP_KEY);
        String timestamp = httpServletRequest.getHeader(Dict.TIMESTAMP);
        String nonce = httpServletRequest.getHeader(Dict.NONCE);
        String httpURI = httpServletRequest.getRequestURI();
        MyRequestWrapper myRequestWrapper = new MyRequestWrapper(httpServletRequest);
        String httpBody = myRequestWrapper.getBody();
        logger.info("Head Information | partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}", partyId, role, appKey, timestamp, nonce, httpURI, httpBody, signature);
        Station station = stationService.findStation(Long.parseLong(partyId));
        if (null == station) {
            return 1;
        }

        String secretInfoString = station.getSecretInfo();
        JSONObject secretInfo = JSON.parseObject(secretInfoString);
        if (null == secretInfo.getString(Dict.KEY) || null == secretInfo.getString(Dict.SECRET)) {
            return 1;
        }
        String key = secretInfo.getString(Dict.KEY);
        String secret = secretInfo.getString(Dict.SECRET);

        String trueSignature = EncryptUtil.generateSignature(secret, partyId, Byte.toString(station.getRoleType()), key, timestamp, nonce, httpURI, httpBody);
        String headSignature = EncryptUtil.generateSignature(secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);

        logger.info("Request Signature:{}", signature);
        logger.info("Head Signature Information | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}", secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody, headSignature);
        logger.info("True Signature Information | secret:{},partyId:{},role:{},key:{},timestamp:{},nonce:{},httpURI:{},httpBody:{},signature:{}", secret, partyId, Byte.toString(station.getRoleType()), key, timestamp, nonce, httpURI, httpBody, trueSignature);

        if (StringUtils.equals(signature, trueSignature) && StringUtils.equals(headSignature, trueSignature)) {
            return 0;
        } else {
            return 1;
        }

    }

    private JSONObject checkParameter(String parameters, String... parametersNeedCheck) {
        JSONObject jsonObject = JSON.parseObject(parameters);
        ArrayList<String> results = new ArrayList<>();
        for (String parameter : parametersNeedCheck) {
            String result = jsonObject.getString(parameter);
            results.add(result);
        }
        String[] results_Array = new String[results.size()];
        results.toArray(results_Array);
        try {
            Preconditions.checkArgument(StringUtils.isNoneEmpty(results_Array));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }
}
