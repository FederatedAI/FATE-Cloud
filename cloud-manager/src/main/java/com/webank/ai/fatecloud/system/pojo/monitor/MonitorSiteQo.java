package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("MonitorSiteQo")
public class MonitorSiteQo implements Serializable {
    @ApiModelProperty("monitorReq")
    private MonitorReq  monitorReq;

    @ApiModelProperty("institution")
    private String  institution;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;
}