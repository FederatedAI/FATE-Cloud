package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("InsitutionDetailQo")
public class InsitutionDetailQo implements Serializable {
    @ApiModelProperty("monitorReq")
    private MonitorReq  monitorReq;

    @ApiModelProperty("institution")
    private String  institution;
}
