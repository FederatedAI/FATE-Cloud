package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.common.IdNamePair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("ip manager update rep")
public class IpManagerUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ip manager deal result")
    private IdNamePair ipManagerStatusPair;
}
