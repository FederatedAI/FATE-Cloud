package com.webank.ai.fatecloud.system.service.facade;

import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Dict;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.Interface.FederatedCloudManagerOriginUserServiceInterface;
import com.webank.ai.fatecloud.system.dao.entity.FederatedCloudManagerUserDo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserDeleteQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserLoginQo;
import com.webank.ai.fatecloud.system.pojo.qo.CloudManagerUserPagedQo;
import com.webank.ai.fatecloud.system.service.impl.FederatedCloudManagerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class FederatedCloudManagerUserServiceFacade {

    @Resource(name = "default")
    FederatedCloudManagerOriginUserServiceInterface federatedCloudManagerOriginUserServiceInterface;

    @Autowired
    FederatedCloudManagerUserService federatedCloudManagerUserService;

    public CommonResponse<FederatedCloudManagerUserDo> addCloudManagerUser(CloudManagerUserAddQo cloudManagerUserAddQo) {

        boolean federatedCloudManagerOriginUserResult = federatedCloudManagerOriginUserServiceInterface.findFederatedCloudManagerOriginUser(cloudManagerUserAddQo.getName());
        if (!federatedCloudManagerOriginUserResult) {
            return new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_ORIGIN_USER_LOGIN_ERROR);
        }

        boolean cloudManagerUser = federatedCloudManagerUserService.findCloudManagerUser(cloudManagerUserAddQo.getName());
        if (cloudManagerUser) {
            return new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_USER_ERROR);

        }

        FederatedCloudManagerUserDo federatedCloudManagerUserDo = federatedCloudManagerUserService.addCloudManagerUser(cloudManagerUserAddQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedCloudManagerUserDo);

    }

    public CommonResponse<PageBean<FederatedCloudManagerUserDo>> findPagedCloudManagerUser(CloudManagerUserPagedQo cloudManagerUserPagedQo) {

        PageBean<FederatedCloudManagerUserDo> pagedCloudManagerUser = federatedCloudManagerUserService.findPagedCloudManagerUser(cloudManagerUserPagedQo);

        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedCloudManagerUser);
    }

    public CommonResponse deleteCloudManagerUser(CloudManagerUserDeleteQo cloudManagerUserDeleteQo) {
        int count = federatedCloudManagerUserService.count();
        if (count > 1) {
            federatedCloudManagerUserService.deleteCloudManagerUser(cloudManagerUserDeleteQo);
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
        } else {
            return new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_DELETE_ERROR);

        }

    }

    public CommonResponse<FederatedCloudManagerUserDo> loginCloudManagerUser(CloudManagerUserLoginQo cloudManagerUserLoginQo, HttpSession httpSession) {

        boolean originUserResult = federatedCloudManagerOriginUserServiceInterface.checkFederatedCloudManagerOriginUser(cloudManagerUserLoginQo.getName(), cloudManagerUserLoginQo.getPassword());
        if (!originUserResult) {
            return new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_ORIGIN_USER_LOGIN_ERROR);

        }

        FederatedCloudManagerUserDo federatedCloudManagerUserDo = federatedCloudManagerUserService.loginCloudManagerUser(cloudManagerUserLoginQo);

        if (federatedCloudManagerUserDo != null) {
            httpSession.setAttribute(Dict.CLOUD_USER, federatedCloudManagerUserDo);
            return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedCloudManagerUserDo);
        }
        return new CommonResponse<>(ReturnCodeEnum.CLOUD_MANAGER_LOGIN_AUTHORITY_ERROR);

    }

    public CommonResponse logoutCloudManagerUser(HttpSession httpSession) {
        httpSession.invalidate();
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);

    }
}
