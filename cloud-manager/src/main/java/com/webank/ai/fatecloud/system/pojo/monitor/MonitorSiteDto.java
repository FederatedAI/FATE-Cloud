package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "MonitorSiteDto")
public class MonitorSiteDto implements Serializable {

    @ApiModelProperty(value = "total ")
    private Integer total;

    @ApiModelProperty(value = "institutionSite ")
    private List<MonitorSiteItem> data;

    @ApiModelProperty(value = "siteList")
    private List<String> siteList;
}
