package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.system.dao.entity.FederatedFunctionDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedFunctionMapper;
import com.webank.ai.fatecloud.system.pojo.dto.FunctionStatusDto;
import com.webank.ai.fatecloud.system.pojo.qo.FunctionUpdateQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        for (String function : Dict.FUNCTIONS) {
            System.out.println(Dict.FUNCTIONS);
            QueryWrapper<FederatedFunctionDo> federatedFunctionDoQueryWrapper = new QueryWrapper<>();
            federatedFunctionDoQueryWrapper.eq("function_name", function);
            Integer count = federatedFunctionMapper.selectCount(federatedFunctionDoQueryWrapper);
            if (count == 0) {
                FederatedFunctionDo federatedFunctionDo = new FederatedFunctionDo();
                federatedFunctionDo.setFunctionName(function);
                federatedFunctionDo.setCreateTime(new Date());
                federatedFunctionDo.setUpdateTime(new Date());
                federatedFunctionMapper.insert(federatedFunctionDo);
            }
        }

    }


    public boolean checkFunctionStatus(String function) {
        QueryWrapper<FederatedFunctionDo> federatedFunctionDoQueryWrapper = new QueryWrapper<>();
        federatedFunctionDoQueryWrapper.eq("function_name", function).eq("status", 1);
        Integer count = federatedFunctionMapper.selectCount(federatedFunctionDoQueryWrapper);
        if (count == null) {
            return false;
        }
        return true;
    }


}
