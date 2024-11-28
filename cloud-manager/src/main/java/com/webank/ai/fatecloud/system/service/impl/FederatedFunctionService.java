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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFunctionDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedFunctionMapper;
import com.webank.ai.fatecloud.system.pojo.dto.FunctionStatusDto;
import com.webank.ai.fatecloud.system.pojo.qo.FunctionUpdateQo;
import com.webank.ai.fatecloud.system.pojo.qo.ScenarioQo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class FederatedFunctionService {

    @Autowired
    FederatedFunctionMapper federatedFunctionMapper;


    public List<FunctionStatusDto> findAllFunctionStatus() {
        List<FederatedFunctionDo> federatedFunctionDos = federatedFunctionMapper.selectList(null);
        LinkedList<FunctionStatusDto> functionStatusDtos = new LinkedList<>();

        for (FederatedFunctionDo federatedFunctionDo : federatedFunctionDos) {
            FunctionStatusDto functionStatusDto = new FunctionStatusDto(federatedFunctionDo);
            functionStatusDtos.add(functionStatusDto);
        }
        return functionStatusDtos;
    }

    public void updateFunctionStatus(FunctionUpdateQo functionUpdateQo) {
        FederatedFunctionDo federatedFunctionDo = new FederatedFunctionDo();
        federatedFunctionDo.setFunctionId(functionUpdateQo.getFunctionId());
        federatedFunctionDo.setStatus(functionUpdateQo.getStatus());
        federatedFunctionMapper.updateById(federatedFunctionDo);
    }


    public void initialingFunction() {
        Date date = new Date();
        for (String function : Dict.FUNCTIONS) {
            QueryWrapper<FederatedFunctionDo> federatedFunctionDoQueryWrapper = new QueryWrapper<>();
            federatedFunctionDoQueryWrapper.eq("function_name", function);
            Integer count = federatedFunctionMapper.selectCount(federatedFunctionDoQueryWrapper);
            if (count == 0) {
                FederatedFunctionDo federatedFunctionDo = new FederatedFunctionDo();
                federatedFunctionDo.setFunctionName(function);
                federatedFunctionDo.setCreateTime(date);
                federatedFunctionDo.setUpdateTime(date);
                federatedFunctionDo.setStatus(2);
                federatedFunctionMapper.insert(federatedFunctionDo);
            }
        }

    }


    public boolean checkFunctionStatus(String function) {
        QueryWrapper<FederatedFunctionDo> federatedFunctionDoQueryWrapper = new QueryWrapper<>();
        federatedFunctionDoQueryWrapper.eq("function_name", function).eq("status", 1);
        List<FederatedFunctionDo> federatedFunctionDos = federatedFunctionMapper.selectList(federatedFunctionDoQueryWrapper);
        if (federatedFunctionDos.size() <= 0) {
            return false;
        }
        if (Dict.FUNCTIONS[1].equals(function)) {
            String descriptions = federatedFunctionDos.get(0).getDescriptions();
            return "1".equals(descriptions) || "2".equals(descriptions) || "3".equals(descriptions);
        }
        return true;
    }

    public boolean checkParameters(FunctionUpdateQo functionUpdateQo) {
        Long functionId = functionUpdateQo.getFunctionId();
        Integer status = functionUpdateQo.getStatus();

        //check function id
        if (functionId == null) {
            return false;
        }
        //check status
        if (!statusList.contains(status)) {
            return false;
        }

        return true;
    }

    private final List<Integer> statusList = Arrays.asList(1, 2);
    private final List<String> scenarioList = Arrays.asList("1", "2", "3");

    public boolean checkScenarioParameters(ScenarioQo scenarioQo) {
        if (scenarioQo.getFunctionId() == null) {
            return false;
        }

        FederatedFunctionDo federatedFunctionDo = federatedFunctionMapper.selectById(scenarioQo.getFunctionId());
        //check function
        if (!Dict.FUNCTIONS[1].equals(federatedFunctionDo.getFunctionName())) {
            return false;
        }
        //check first update
        if (!StringUtils.isBlank(federatedFunctionDo.getDescriptions())) {
            return false;
        }

        //check input scenario type
        return scenarioList.contains(scenarioQo.getScenarioType());
    }

    public void updateScenario(ScenarioQo scenarioQo) {
        FederatedFunctionDo federatedFunctionDo = new FederatedFunctionDo();
        federatedFunctionDo.setFunctionId(scenarioQo.getFunctionId());
        federatedFunctionDo.setDescriptions(scenarioQo.getScenarioType());
        federatedFunctionDo.setStatus(1);
        federatedFunctionMapper.updateById(federatedFunctionDo);
    }
}
