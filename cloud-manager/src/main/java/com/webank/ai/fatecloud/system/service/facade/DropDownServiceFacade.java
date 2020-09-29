package com.webank.ai.fatecloud.system.service.facade;

import com.google.common.base.Preconditions;
import com.webank.ai.fatecloud.common.CommonResponse;
import com.webank.ai.fatecloud.common.Enum.ReturnCodeEnum;
import com.webank.ai.fatecloud.system.pojo.qo.DropDownVersionQo;
import com.webank.ai.fatecloud.system.service.impl.DropDownService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DropDownServiceFacade {

    @Autowired
    DropDownService dropDownService;


    public CommonResponse<List<String>> getDropDownVersionList(DropDownVersionQo dropDownVersionQo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(dropDownVersionQo.getVersionName()));
        boolean result = checkParameter(dropDownVersionQo.getVersionName());
        if (!result) {
            return new CommonResponse<>(ReturnCodeEnum.PARAMETERS_ERROR);
        }

        List<String> dropDownVersionList = dropDownService.getDropDownVersionList(dropDownVersionQo);
        return new CommonResponse<>(ReturnCodeEnum.SUCCESS, dropDownVersionList);
    }

    public boolean checkParameter(String parameter) {
        boolean result;
        switch (parameter) {
            case "fate_version":
            case "fate_serving_version":
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }
}
