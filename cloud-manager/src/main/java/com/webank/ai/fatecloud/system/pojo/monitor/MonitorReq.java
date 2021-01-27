package com.webank.ai.fatecloud.system.pojo.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Data
@ApiModel("MonitorReq")
@Service
public class MonitorReq implements Serializable {

    @ApiModelProperty("startDate")
    private String  startDate;

    @ApiModelProperty("endDate")
    private String  endDate;
}
