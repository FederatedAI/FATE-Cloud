package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("ip manager list")
public class IpManagerListQo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "partyid,siteanme")
    private String condition;

    @ApiModelProperty(value = "role,1:guest,2:host")
    private Integer role;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("order type,1:asc,2:desc,default:desc")
    private Integer updateTimeOrder = 2;
}
