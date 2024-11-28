package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorSiteItem")
public class MonitorSiteItem implements Serializable {

    @ApiModelProperty(value = "institution ")
    private String institution;

    @ApiModelProperty(value = "institutionSite ")
    private List<MonitorTwoSite> institutionSiteList;
}
