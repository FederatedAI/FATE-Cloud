package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerUserDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedCloudManagerUserMapper;
import com.webank.ai.fatecloud.system.pojo.dto.AuthorityHistoryDto;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserLoginQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserPagedQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@Slf4j
public class FederatedCloudManagerUserService {

    @Autowired
    FederatedCloudManagerUserMapper federatedCloudManagerUserMapper;

    public FederatedCloudManagerUserDo addCloudManagerUser(CloudManagerUserAddQo cloudManagerUserAddQo) {

        FederatedCloudManagerUserDo federatedCloudManagerUserDo = new FederatedCloudManagerUserDo();
        federatedCloudManagerUserDo.setName(cloudManagerUserAddQo.getName());
        federatedCloudManagerUserDo.setAdminLevel(cloudManagerUserAddQo.getAdminLevel());
        federatedCloudManagerUserDo.setCreator(cloudManagerUserAddQo.getCreator());

        federatedCloudManagerUserMapper.insert(federatedCloudManagerUserDo);
        return federatedCloudManagerUserDo;
    }

    public PageBean<FederatedCloudManagerUserDo> findPagedCloudManagerUser(CloudManagerUserPagedQo cloudManagerUserPagedQo) {
        QueryWrapper<FederatedCloudManagerUserDo> federatedCloudManagerUserDoQueryWrapper = new QueryWrapper<>();

        if (cloudManagerUserPagedQo.getName() != null) {
            federatedCloudManagerUserDoQueryWrapper.like("name", cloudManagerUserPagedQo.getName());
            cloudManagerUserPagedQo.setName("%" + cloudManagerUserPagedQo.getName() + "%");

        }

        Integer count = federatedCloudManagerUserMapper.selectCount(federatedCloudManagerUserDoQueryWrapper);
        PageBean<FederatedCloudManagerUserDo> userListBean = new PageBean<>(cloudManagerUserPagedQo.getPageNum(), cloudManagerUserPagedQo.getPageSize(), count);
        long startIndex = userListBean.getStartIndex();

        List<FederatedCloudManagerUserDo> pagedCloudUser = federatedCloudManagerUserMapper.findPagedCloudUser(cloudManagerUserPagedQo, startIndex);

        userListBean.setList(pagedCloudUser);
        return userListBean;

    }

    public boolean findCloudManagerUser(String name) {
        QueryWrapper<FederatedCloudManagerUserDo> federatedCloudManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerUserDoQueryWrapper.eq("name", name);

        Integer count = federatedCloudManagerUserMapper.selectCount(federatedCloudManagerUserDoQueryWrapper);
        if (count > 0) {
            return true;
        }
        return false;

    }

    public void deleteCloudManagerUser(CloudManagerUserDeleteQo cloudManagerUserDeleteQo) {
        QueryWrapper<FederatedCloudManagerUserDo> federatedCloudManagerUserDoQueryWrapper = new QueryWrapper<>();
        federatedCloudManagerUserDoQueryWrapper.eq("cloud_manager_id", cloudManagerUserDeleteQo.getCloudManagerId());
        federatedCloudManagerUserMapper.delete(federatedCloudManagerUserDoQueryWrapper);
    }

    public FederatedCloudManagerUserDo loginCloudManagerUser(CloudManagerUserLoginQo cloudManagerUserLoginQo) {
        Integer count = federatedCloudManagerUserMapper.selectCount(null);
        if (count == 0) {
            FederatedCloudManagerUserDo federatedCloudManagerUserDo = new FederatedCloudManagerUserDo();
            federatedCloudManagerUserDo.setName(cloudManagerUserLoginQo.getName());
            federatedCloudManagerUserDo.setCreator(cloudManagerUserLoginQo.getName());
            federatedCloudManagerUserMapper.insert(federatedCloudManagerUserDo);
            return federatedCloudManagerUserDo;
        } else {

            QueryWrapper<FederatedCloudManagerUserDo> federatedCloudManagerUserDoQueryWrapper = new QueryWrapper<>();
            federatedCloudManagerUserDoQueryWrapper.eq("name", cloudManagerUserLoginQo.getName());

            List<FederatedCloudManagerUserDo> federatedCloudManagerUserDos = federatedCloudManagerUserMapper.selectList(federatedCloudManagerUserDoQueryWrapper);
            if (federatedCloudManagerUserDos.size() > 0) {
                return federatedCloudManagerUserDos.get(0);
            } else {
                return null;
            }
        }

    }

    public int count() {
        return federatedCloudManagerUserMapper.selectCount(null);
    }
}
