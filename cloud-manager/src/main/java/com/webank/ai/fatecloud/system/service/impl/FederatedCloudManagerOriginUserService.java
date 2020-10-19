package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.system.Interface.FederatedCloudManagerOriginUserServiceInterface;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerOriginUserDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudManagerOriginUserMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service(value = "default")
public class FederatedCloudManagerOriginUserService implements FederatedCloudManagerOriginUserServiceInterface {

    @Autowired
    FederatedCloudManagerOriginUserMapper federatedCloudManagerOriginUserMapper;

    @Override
    public boolean checkFederatedCloudManagerOriginUser(String username, String password) {
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        QueryWrapper<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerOriginUserDoQueryWrapper.eq("name", username).eq("password", md5Password);
        List<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDos = federatedCloudManagerOriginUserMapper.selectList(federatedCloudManagerOriginUserDoQueryWrapper);

        return federatedCloudManagerOriginUserDos.size() > 0;
    }

    @Override
    public boolean findFederatedCloudManagerOriginUser(String username) {
        QueryWrapper<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerOriginUserDoQueryWrapper.eq("name", username);
        List<FederatedCloudManagerOriginUserDo> federatedCloudManagerOriginUserDos = federatedCloudManagerOriginUserMapper.selectList(federatedCloudManagerOriginUserDoQueryWrapper);
        return federatedCloudManagerOriginUserDos.size() > 0;

    }


}
