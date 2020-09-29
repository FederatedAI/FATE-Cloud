package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedGroupSetMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedModelMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.qo.ModelAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.ModelHistoryQo;
import com.webank.ai.fatecloud.system.pojo.qo.OneSiteQo;
import com.webank.ai.fatecloud.system.pojo.qo.SiteListWithModelsQo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FederatedModelService {

    @Autowired
    FederatedModelMapper federatedModelMapper;

    @Transactional
    public void addModel(ModelAddQo modelAddQo) {
        FederatedSiteModelDo modelDo = new FederatedSiteModelDo(modelAddQo);

        //update model status of site existed in databases
        QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapper = new QueryWrapper<>();
        federatedSiteModelDoQueryWrapper.eq("id", modelAddQo.getId()).eq("install_items", modelAddQo.getInstallItems()).eq("status", 1);
        List<FederatedSiteModelDo> federatedSiteModelDos = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapper);
        if (federatedSiteModelDos.size() > 0) {
            for (FederatedSiteModelDo federatedSiteModelDo : federatedSiteModelDos) {
                federatedSiteModelDo.setStatus(2);
                federatedModelMapper.updateById(federatedSiteModelDo);
            }
            modelDo.setInstallTime(federatedSiteModelDos.get(0).getInstallTime());
        }
        federatedModelMapper.insert(modelDo);
    }

    public void addModelNew(List<ModelAddQo> modelAddQos) {
        //update status of old models
        QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapperForOldModel = new QueryWrapper<>();
        federatedSiteModelDoQueryWrapperForOldModel.eq("id", modelAddQos.get(0).getId()).eq("status", 1);
        List<FederatedSiteModelDo> federatedSiteModelDosForOldModel = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapperForOldModel);
        ArrayList<String> oldModels = new ArrayList<>();
        for (FederatedSiteModelDo federatedSiteModelDo : federatedSiteModelDosForOldModel) {
            oldModels.add(federatedSiteModelDo.getInstallItems());
        }

        ArrayList<String> newModels = new ArrayList<>();
        for (ModelAddQo modelAddQo : modelAddQos) {
            newModels.add(modelAddQo.getInstallItems());
        }

        for (String oldModel : oldModels) {
            if (!newModels.contains(oldModel)) {
                QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion = new QueryWrapper<>();
                federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion.eq("id", modelAddQos.get(0).getId()).eq("status", 1).eq("install_items", oldModel);
                FederatedSiteModelDo modelDo = new FederatedSiteModelDo();
                modelDo.setStatus(2);
                federatedModelMapper.update(modelDo, federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion);
            }
        }


        for (ModelAddQo modelAddQo : modelAddQos) {
            FederatedSiteModelDo modelDo = new FederatedSiteModelDo(modelAddQo);

            //update model status of site existed in databases
            QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapper = new QueryWrapper<>();
            federatedSiteModelDoQueryWrapper.eq("id", modelAddQo.getId()).eq("install_items", modelAddQo.getInstallItems()).eq("status", 1);
            List<FederatedSiteModelDo> federatedSiteModelDos = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapper);
            if (federatedSiteModelDos.size() > 0) {
                for (FederatedSiteModelDo federatedSiteModelDo : federatedSiteModelDos) {
                    federatedSiteModelDo.setStatus(2);
                    federatedModelMapper.updateById(federatedSiteModelDo);
                }
                modelDo.setInstallTime(federatedSiteModelDos.get(0).getInstallTime());
            } else {
                modelDo.setInstallTime(modelAddQo.getUpdateTime());
            }
            modelDo.setUpdateTime(modelAddQo.getUpdateTime());
            federatedModelMapper.insert(modelDo);
        }

    }

    public PageBean<FederatedSiteManagerDo> findPagedSitesWithModel(SiteListWithModelsQo siteListWithModelsQo) {
        int sum = federatedModelMapper.selectCount(siteListWithModelsQo);
        PageBean<FederatedSiteManagerDo> federatedSiteManagerDoPageBean = new PageBean<>(siteListWithModelsQo.getPageNum(), siteListWithModelsQo.getPageSize(), sum);
        List<FederatedSiteManagerDo> pagedRegionInfo = federatedModelMapper.findPage(siteListWithModelsQo, federatedSiteManagerDoPageBean.getStartIndex());
        federatedSiteManagerDoPageBean.setList(pagedRegionInfo);
        return federatedSiteManagerDoPageBean;
    }

    public List<FederatedSiteModelDo> findModelHistory(ModelHistoryQo modelHistoryQo) {
        QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapper = new QueryWrapper<>();
        federatedSiteModelDoQueryWrapper.eq(modelHistoryQo.getId() != null, "id", modelHistoryQo.getId()).eq(modelHistoryQo.getInstallItems() != null, "install_items", modelHistoryQo.getInstallItems());
        List<FederatedSiteModelDo> federatedSiteModelDos = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapper);
        return federatedSiteModelDos;
    }

    public List<FederatedSiteModelDo> findSitesWithModel(OneSiteQo oneSiteQo) {
        QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapper = new QueryWrapper<>();
        federatedSiteModelDoQueryWrapper.eq(oneSiteQo.getId() != null, "id", oneSiteQo.getId()).eq("status", 1);
        List<FederatedSiteModelDo> federatedSiteModelDos = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapper);
        return federatedSiteModelDos;
    }

}
