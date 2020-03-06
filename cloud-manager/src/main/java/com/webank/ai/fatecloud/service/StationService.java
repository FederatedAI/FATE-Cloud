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
package com.webank.ai.fatecloud.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.webank.ai.fatecloud.dao.StationMapper;
import com.webank.ai.fatecloud.global.Dict;
import com.webank.ai.fatecloud.global.ResponseResult;
import com.webank.ai.fatecloud.global.ReturnCode;
import com.webank.ai.fatecloud.pojo.*;
import com.webank.ai.fatecloud.utils.EncryptUtil;
import com.webank.ai.fatecloud.utils.KeyAndSecretGenerate;
import com.webank.ai.fatecloud.utils.PageBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@EnableScheduling
@Service
public class StationService {

    private final Logger logger = LoggerFactory.getLogger(StationService.class);

    @Autowired
    StationMapper stationMapper;

    public StationVO createStation() {
        Station station = new Station();
        String appKey = KeyAndSecretGenerate.getAppKey();
        String appSecret = KeyAndSecretGenerate.getAppSecret(appKey);
        HashMap<String, String> keyMap = new HashMap<>();
        keyMap.put(Dict.KEY, appKey);
        keyMap.put(Dict.SECRET, appSecret);
        String secret = JSON.toJSONString(keyMap);
        station.setSecretInfo(secret);

        stationMapper.insertSelective(station);

        return new StationVO(stationMapper.selectByPrimaryKey(station.getId()));
    }

    public Station findStation(Long id) {
        return stationMapper.selectByPrimaryKey(id);

    }


    public StationVO findStationVO(StationQO stationQO) {
        return new StationVO(stationMapper.selectByPrimaryKey(stationQO.getPartyId()));

    }

    public Long totalCount() {
        ArrayList<Byte> status = new ArrayList<>();
        status.add((byte) 2);
        status.add((byte) 1);

        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        criteria.andNodeStatusIn(status);
        return stationMapper.countByExample(stationExample);
    }

    public PageBean findPage(Long pageNum, Long pageSize) {
        Long size = totalCount();
        PageBean<StationVO> objectPageBean = new PageBean<>(pageNum, pageSize, size);
        long startIndex = objectPageBean.getStartIndex();

        ArrayList<Byte> status = new ArrayList<>();
        status.add((byte) 2);
        status.add((byte) 1);

        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        criteria.andNodeStatusIn(status);
        String order = Dict.UPDATE_TIME + " " + Dict.ORDER;
        stationExample.setOrderByClause(order);

        String limit = startIndex + "," + pageSize;
        stationExample.setLimitClause(limit);

        List<Station> stations = stationMapper.selectByExample(stationExample);
        List<StationVO> stationsVOs = new ArrayList<>();
        for (Station station : stations) {
            StationVO stationVO = new StationVO(station);
            stationsVOs.add(stationVO);
        }
        objectPageBean.setList(stationsVOs);
        return objectPageBean;
    }


    public ResponseResult updateStation(StationQO stationQO) {

        Station station = new Station(stationQO);
        Long id = station.getId();
        Station stationFromDatasource = findStation(id);

        logger.info("station from database:{}", stationFromDatasource);

        if (stationFromDatasource == null) {
            return new ResponseResult(ReturnCode.DATA_ERROR);
        }
        if ((byte) 1 != stationFromDatasource.getNodeStatus()) {
            return new ResponseResult(ReturnCode.STATUS_ERROR);
        }

        stationMapper.updateByPrimaryKeySelective(station);
        return new ResponseResult<>(ReturnCode.SUCCESS);
    }


    public ResponseResult registerStation(RegisterStationQO registerStationQO) {
        Station station = new Station(registerStationQO);
        Long id = station.getId();
        Station stationFromDatasource = findStation(id);

        logger.info("station from database:{}", stationFromDatasource);

        if (stationFromDatasource == null) {
            return new ResponseResult(ReturnCode.DATA_ERROR);
        }
        if ((byte) 3 == stationFromDatasource.getNodeStatus()) {
            return new ResponseResult(ReturnCode.REGISTER_ERROR);
        }
        station.setNodeStatus((byte) 2);
        station.setDetectiveStatus((byte) 1);
        station.setLastDetectiveTime(new Date());
        stationMapper.updateByPrimaryKeySelective(station);
        return new ResponseResult<>(ReturnCode.SUCCESS);
    }


    public ResponseResult deleteStation(StationQO stationQO) {
        Station station = new Station(stationQO);
        Long id = station.getId();
        Station stationFromDatasource = findStation(id);
        if (stationFromDatasource == null) {
            return new ResponseResult(ReturnCode.DATA_ERROR);
        }
        station.setNodeStatus((byte) 3);
        stationMapper.updateByPrimaryKeySelective(station);
        return new ResponseResult<>(ReturnCode.SUCCESS);
    }


    public ResponseResult heartCheck(StationQO stationQO) {
        Station station = new Station(stationQO);
        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        criteria.andIdEqualTo(station.getId());
        Station stationFromDatasource = stationMapper.selectByPrimaryKey(station.getId());
        Byte nodeStatus = stationFromDatasource.getNodeStatus();
        if (1 == nodeStatus) {
            return new ResponseResult<>(ReturnCode.STATION_REGISTER_ERROR);
        }
        if (3 == nodeStatus) {
            return new ResponseResult<>(ReturnCode.REGISTER_ERROR);
        }

        Station stationForUpdate = new Station();
        Date date = new Date();
        stationForUpdate.setLastDetectiveTime(date);
        stationForUpdate.setDetectiveStatus((byte) 1);

        stationMapper.updateByExampleSelective(stationForUpdate, stationExample);
        return new ResponseResult<>(ReturnCode.SUCCESS);

    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void updateStationStatus() {
        logger.info("start heart check");

        StationExample stationExampleForAll = new StationExample();
        StationExample.Criteria criteria = stationExampleForAll.createCriteria();
        criteria.andNodeStatusEqualTo((byte) 2);
        List<Station> stations = stationMapper.selectByExample(stationExampleForAll);
        for (Station station : stations) {
            Date lastDetectiveTime = station.getLastDetectiveTime();
            long detectiveTime = lastDetectiveTime.getTime();
            Date date = new Date();
            long timeNow = date.getTime();
            if (timeNow - detectiveTime > 60000) {
                station.setLastDetectiveTime(date);
                station.setDetectiveStatus((byte) 1);
                Long id = station.getId();

                StationExample stationExample = new StationExample();
                StationExample.Criteria criteria1 = stationExample.createCriteria();
                criteria1.andIdEqualTo(id);
                stationMapper.updateByExampleSelective(station, stationExample);
            }

        }
    }

    public ResponseResult checkStationAuthorization(String parameters) {
        JSONObject jsonObject = JSON.parseObject(parameters);
        if (null == jsonObject || null == jsonObject.getString(Dict.HTTP_HEADER) || null == jsonObject.getString(Dict.HTTP_URI) || null == jsonObject.getString(Dict.HTTP_BODY)) {
            return new ResponseResult(ReturnCode.PARAMETERS_ERROR);
        }

        JSONObject headers = jsonObject.getJSONObject(Dict.HTTP_HEADER);
        String signature = headers.getString(Dict.SIGNATURE);
        String partyId = headers.getString(Dict.PARTY_ID);
        String role = headers.getString(Dict.ROLE);
        String appKey = headers.getString(Dict.APP_KEY);
        String timestamp = headers.getString(Dict.TIMESTAMP);
        String nonce = headers.getString(Dict.NONCE);

        Station station = findStation(Long.parseLong(partyId));
        if (null == station) {
            return new ResponseResult(ReturnCode.DATA_ERROR);
        }
        String secretInfoString = station.getSecretInfo();
        JSONObject secretInfo = JSON.parseObject(secretInfoString);
        if (null == secretInfo.getString(Dict.KEY) || null == secretInfo.getString(Dict.SECRET)) {
            return new ResponseResult(ReturnCode.DATA_ERROR);
        }
        String key = secretInfo.getString(Dict.KEY);
        String secret = secretInfo.getString(Dict.SECRET);

        String httpURI = jsonObject.getString(Dict.HTTP_URI);
        String httpBody = jsonObject.getString(Dict.HTTP_BODY);

        String trueSignature = EncryptUtil.generateSignature(secret, partyId, Byte.toString(station.getRoleType()), key, timestamp, nonce, httpURI, httpBody);
        String headSignature = EncryptUtil.generateSignature(secret, partyId, role, appKey, timestamp, nonce, httpURI, httpBody);

        if (!(StringUtils.equals(signature, trueSignature) && StringUtils.equals(headSignature, trueSignature))) {
            return new ResponseResult(ReturnCode.AUTHORIZATION_ERROR);
        }

        if ((2 == station.getNodeStatus()) && (1 == station.getDetectiveStatus())) {
            return new ResponseResult(ReturnCode.SUCCESS);
        }
        return new ResponseResult(ReturnCode.STATION_ERROR);
    }


}
