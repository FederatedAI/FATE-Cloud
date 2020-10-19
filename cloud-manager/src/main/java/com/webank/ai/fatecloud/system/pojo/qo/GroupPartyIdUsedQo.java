package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("group party id used")
public class GroupPartyIdUsedQo implements Serializable {

    @ApiModelProperty(value = "group id")
    private Integer groupId;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
    private Integer status;

    @ApiModelProperty(value = "partyid,siteanme,institutions")
    private String condition;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("order type,0:asc,1:desc,default:asc")
    private Integer updateTimeOrder = 0;
}
