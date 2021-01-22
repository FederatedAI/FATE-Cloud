package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("TwoSiteBase")
public class TwoInstitutionBase extends Base {

    @ApiModelProperty(value = "guestInstitution")
    private String guestInstitution;

    @ApiModelProperty(value = "institutionSiteName")
    private String hostInstitution;
}