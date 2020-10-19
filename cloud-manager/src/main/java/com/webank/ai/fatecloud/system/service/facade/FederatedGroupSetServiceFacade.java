package com.webank.ai.fatecloud.system.service.facade;

import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.common.util.PageBean;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetRangeInfoDto;
import com.webank.ai.fatecloud.system.pojo.dto.FederatedGroupSetDto;
import com.webank.ai.fatecloud.system.pojo.qo.*;
import com.webank.ai.fatecloud.system.service.impl.FederatedGroupSetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class FederatedGroupSetServiceFacade {

    @Autowired
    FederatedGroupSetService federatedGroupSetService;

    public CommonResponse<PageBean<FederatedGroupSetDto>> selectFederatedGroupSetList(FederatedGroupSetQo federatedGroupSetQo) {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(federatedGroupSetQo.getOrderField()));
        if (1 != federatedGroupSetQo.getOrderRule() && 2 != federatedGroupSetQo.getOrderRule()) {
            throw new IllegalArgumentException();
        }
        if (0 != federatedGroupSetQo.getRole() && 1 != federatedGroupSetQo.getRole() && 2 != federatedGroupSetQo.getRole()) {
            throw new IllegalArgumentException();
        }

        PageBean<FederatedGroupSetDto> federatedGroupSetDoPage = federatedGroupSetService.selectFederatedGroupSetList(federatedGroupSetQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedGroupSetDoPage);
    }


    public CommonResponse addFederatedGroupSet(FederatedGroupSetAddQo federatedGroupSetAddQo) {

        FederatedGroupSetCheckQo federatedGroupSetCheckQo = new FederatedGroupSetCheckQo(federatedGroupSetAddQo);
        CommonResponse commonResponse = checkFederatedGroupSet(federatedGroupSetCheckQo);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        Preconditions.checkArgument(StringUtils.isNoneEmpty(federatedGroupSetAddQo.getGroupName()));

        if (0 != federatedGroupSetAddQo.getRole() && 1 != federatedGroupSetAddQo.getRole() && 2 != federatedGroupSetAddQo.getRole()) {
            throw new IllegalArgumentException();
        }

        //check group name
        boolean existSite = federatedGroupSetService.checkGroupName(new FederatedGroupSetNameCheckQo(federatedGroupSetAddQo));
        if (existSite) {
            return new CommonResponse(ReturnCodeEnum.GROUP_NAME_ERROR);
        }

        federatedGroupSetService.addFederatedGroupSet(federatedGroupSetAddQo);
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse deleteFederatedGroupSet(Long groupId) {
        if (1 == federatedGroupSetService.deleteFederatedGroupSet(groupId)) {
            return new CommonResponse(ReturnCodeEnum.GROUP_DELETE_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkUpdateRange(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        return federatedGroupSetService.checkUsedSite(federatedGroupSetUpdateQo);
    }

    public CommonResponse updateFederatedGroupSet(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        if (federatedGroupSetUpdateQo.getGroupId() == null) {
            throw new IllegalArgumentException();
        }
        Preconditions.checkArgument(StringUtils.isNoneEmpty(federatedGroupSetUpdateQo.getGroupName()));
        if (0 != federatedGroupSetUpdateQo.getRole() && 1 != federatedGroupSetUpdateQo.getRole() && 2 != federatedGroupSetUpdateQo.getRole()) {
            throw new IllegalArgumentException();
        }
        FederatedGroupSetCheckQo federatedGroupSetCheckQo = new FederatedGroupSetCheckQo(federatedGroupSetUpdateQo);
        CommonResponse commonResponse = checkFederatedGroupSet(federatedGroupSetCheckQo);
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        //check group name
        boolean existSite = federatedGroupSetService.checkGroupName(new FederatedGroupSetNameCheckQo(federatedGroupSetUpdateQo));
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_NAME_ERROR);
        }

        CommonResponse commonResponseFromCheckUsedSite = federatedGroupSetService.checkUsedSite(federatedGroupSetUpdateQo);
        if (0 != commonResponseFromCheckUsedSite.getCode()) {
            return commonResponseFromCheckUsedSite;
        }

        federatedGroupSetService.updateFederatedGroupSet(federatedGroupSetUpdateQo);
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse checkFederatedGroupSet(FederatedGroupSetCheckQo federatedGroupSetCheckQo) {
        if (federatedGroupSetCheckQo == null || federatedGroupSetCheckQo.getRangeInfo() == null) {
            throw new IllegalArgumentException();
        }
        int result = federatedGroupSetService.checkFederatedGroupSet(federatedGroupSetCheckQo);
        if (result == 1) {
            return new CommonResponse(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (result == 2) {
            return new CommonResponse(ReturnCodeEnum.ID_RANGE_ERROR);
        }
        return new CommonResponse(ReturnCodeEnum.SUCCESS);

    }


    public CommonResponse<PageBean<FederatedGroupSetRangeInfoDto>> findPagedRangeInfos(PageInfoQo pageInfoQo) {
        PageBean<FederatedGroupSetRangeInfoDto> pagedRangeInfos = federatedGroupSetService.findPagedRangeInfos(pageInfoQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedRangeInfos);
    }

    public CommonResponse checkGroupName(FederatedGroupSetNameCheckQo federatedGroupSetNameCheckQo) {
        if (federatedGroupSetNameCheckQo.getGroupName() == null) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_NAME_PARAMETER_ERROR);
        }
        boolean existSite = federatedGroupSetService.checkGroupName(federatedGroupSetNameCheckQo);
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_NAME_ERROR);
        }
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse addNewGroup(GroupAddQo groupAddQo) {
        CommonResponse commonResponse = this.checkGroupRegion(null, groupAddQo.getRegions());
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

        if (groupAddQo.getRole() == null || (1 != groupAddQo.getRole() && 2 != groupAddQo.getRole())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (groupAddQo.getGroupName() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        //check group name
        boolean existSite = federatedGroupSetService.checkGroupName(new FederatedGroupSetNameCheckQo(groupAddQo));
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_NAME_ERROR);
        }
        federatedGroupSetService.addNewGroup(groupAddQo);
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    //    check the regions whether allocated or not
    public CommonResponse checkGroupRegion(Long groupId, List<Region> regions) {
        if (regions == null || 0 == regions.size()) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedGroupSetService.checkGroupRegion(groupId, regions);
    }

    public CommonResponse deleteNewGroup(GroupDeleteQo groupDeleteQo) {
        if (groupDeleteQo == null || groupDeleteQo.getGroupId() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        Integer usedPartyIdsOfGroup = federatedGroupSetService.findUsedPartyIdsOfGroup(groupDeleteQo.getGroupId());
        if (usedPartyIdsOfGroup != 0) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_DELETE_ERROR);
        }
        federatedGroupSetService.deleteNewGroup(groupDeleteQo.getGroupId());
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse updateNewGroup(GroupUpdateQo groupUpdateQo) {
        if (groupUpdateQo == null || groupUpdateQo.getGroupId() == null || groupUpdateQo.getGroupName() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (groupUpdateQo.getRole() == null || (1 != groupUpdateQo.getRole() && 2 != groupUpdateQo.getRole())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

//        check regions
        CommonResponse commonResponse = this.checkGroupRegion(groupUpdateQo.getGroupId(), groupUpdateQo.getRegions());
        if (0 != commonResponse.getCode()) {
            return commonResponse;
        }

//        check group name
        boolean existSite = federatedGroupSetService.checkGroupName(new FederatedGroupSetNameCheckQo(groupUpdateQo));
        if (existSite) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_NAME_ERROR);
        }
//        check used sites whether in the new regions or not
        CommonResponse commonResponseFromCheckUsedSite = federatedGroupSetService.checkUsedSite(groupUpdateQo);
        if (0 != commonResponseFromCheckUsedSite.getCode()) {
            return commonResponseFromCheckUsedSite;
        }
        //check group status
        boolean status = federatedGroupSetService.checkGroupStatus(groupUpdateQo);
        if (!status) {
            return new CommonResponse<>(ReturnCodeEnum.GROUP_SET_ERROR);
        }
        //update group
        federatedGroupSetService.updateGroup(groupUpdateQo);
        return new CommonResponse(ReturnCodeEnum.SUCCESS);
    }

    public CommonResponse<PageBean<FederatedGroupSetDo>> selectNewGroupSetList(FederatedGroupSetQo federatedGroupSetQo) {
        if (federatedGroupSetQo.getOrderField() == null || 0 == federatedGroupSetQo.getOrderField().trim().length()) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);

        }
        if (federatedGroupSetQo.getOrderRule() == null || (1 != federatedGroupSetQo.getOrderRule() && 2 != federatedGroupSetQo.getOrderRule())) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        if (0 != federatedGroupSetQo.getRole() && 1 != federatedGroupSetQo.getRole() && 2 != federatedGroupSetQo.getRole()) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        PageBean<FederatedGroupSetDo> federatedGroupSetDoPage = federatedGroupSetService.selectNewGroupSetList(federatedGroupSetQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, federatedGroupSetDoPage);

    }

    public CommonResponse checkUpdateRangeNew(GroupUpdateQo groupUpdateQo) {
        if (groupUpdateQo == null || groupUpdateQo.getGroupId() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        return federatedGroupSetService.checkUsedSite(groupUpdateQo);
    }

    public CommonResponse<PageBean<FederatedGroupSetDo>> findPagedRegionInfoNew(PageInfoQo pageInfoQo) {
        if (pageInfoQo.getRole() == null) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }
        PageBean<FederatedGroupSetDo> pagedRegionInfoNew = federatedGroupSetService.findPagedRegionInfoNew(pageInfoQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, pagedRegionInfoNew);
    }
}
