package com.webank.ai.fatecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteManagerDo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedSiteModelDo;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedGroupSetMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedModelMapper;
import com.webank.ai.fatecloud.system.dao.mapper.FederatedSiteManagerMapper;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FederatedModelService {

    @Autowired
    FederatedModelMapper federatedModelMapper;

    @Autowired
    FederatedSiteManagerMapper federatedSiteManagerMapper;

    @Transactional
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

        int detectiveStatus = 2;
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
            modelDo.setLastDetectiveTime(new Date());
            federatedModelMapper.insert(modelDo);
            if (modelAddQo.getDetectiveStatus() == 1) {
                detectiveStatus = 1;
            }
        }

        //update site detective status
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setId(modelAddQos.get(0).getId());
        federatedSiteManagerDo.setLastDetectiveTime(new Date());
        federatedSiteManagerDo.setDetectiveStatus(detectiveStatus);
        federatedSiteManagerMapper.updateById(federatedSiteManagerDo);

    }

    @Transactional
    public void modelHeart(ArrayList<ModelHeartQo> modelHeartQos) {
        Date date = new Date();
        int detectiveStatus = 2;

        for (ModelHeartQo modelHeartQo : modelHeartQos) {
            QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion = new QueryWrapper<>();
            federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion.eq("id", modelHeartQo.getId()).eq("status", 1).eq("install_items", modelHeartQo.getInstallItems()).eq("version", modelHeartQo.getVersion());
            FederatedSiteModelDo modelDo = new FederatedSiteModelDo();
            modelDo.setDetectiveStatus(modelHeartQo.getDetectiveStatus());
            modelDo.setLastDetectiveTime(date);
            federatedModelMapper.update(modelDo, federatedSiteModelDoQueryWrapperForModelNotExistInNewVersion);
            if (modelHeartQo.getDetectiveStatus() == 1) {
                detectiveStatus = 1;
            }
        }

        //update site detective status
        FederatedSiteManagerDo federatedSiteManagerDo = new FederatedSiteManagerDo();
        federatedSiteManagerDo.setId(modelHeartQos.get(0).getId());
        federatedSiteManagerDo.setLastDetectiveTime(date);
        federatedSiteManagerDo.setDetectiveStatus(detectiveStatus);
        federatedSiteManagerMapper.updateById(federatedSiteManagerDo);
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

    //update model status and site status
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void updateModelAndSiteStatus() {
        log.info("start detective");
        long time = new Date().getTime();
        QueryWrapper<FederatedSiteModelDo> federatedSiteModelDoQueryWrapper = new QueryWrapper<>();
        federatedSiteModelDoQueryWrapper.eq("status", 1).eq("detective_status", 1);
        List<FederatedSiteModelDo> federatedSiteModelDos = federatedModelMapper.selectList(federatedSiteModelDoQueryWrapper);
        for (FederatedSiteModelDo federatedSiteModelDo : federatedSiteModelDos) {
            if (time - federatedSiteModelDo.getLastDetectiveTime().getTime() > 1800000) {
                //update model status
                federatedSiteModelDo.setDetectiveStatus(2);
                federatedModelMapper.updateById(federatedSiteModelDo);

                //update site status
                FederatedSiteManagerDo federatedSiteManagerDo = federatedSiteManagerMapper.selectById(federatedSiteModelDo.getId());
                if (federatedSiteManagerDo.getDetectiveStatus() == 1) {
                    federatedSiteManagerDo.setDetectiveStatus(2);
                    federatedSiteManagerMapper.updateById(federatedSiteManagerDo);
                }

            }
        }
    }


}
