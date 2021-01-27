package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorTwoSite")
public class MonitorTwoSite implements Serializable {
    @ApiModelProperty(value = "institution siteName ")
    private String institutionSiteName;

    @ApiModelProperty(value = "site data")
    private List<SiteBase> mixSiteModeling;
}
