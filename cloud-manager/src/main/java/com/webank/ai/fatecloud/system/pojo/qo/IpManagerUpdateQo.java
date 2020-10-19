package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("ip manager update")
public class IpManagerUpdateQo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "site partyid")
    private Integer partyId;

    @ApiModelProperty(value = "log deal status,0:no deal,1:agreed,2:rejected")
    private Integer status;

    @ApiModelProperty(value = "case_id")
    private String caseId;
}
