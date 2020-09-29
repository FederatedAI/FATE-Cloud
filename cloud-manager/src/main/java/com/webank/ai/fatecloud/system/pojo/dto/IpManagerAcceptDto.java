package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("ip manager accept resp")
public class IpManagerAcceptDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "case id")
    private String caseId;

}
