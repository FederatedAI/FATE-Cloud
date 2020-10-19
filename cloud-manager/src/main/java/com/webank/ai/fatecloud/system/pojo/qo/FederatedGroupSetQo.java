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
@ApiModel("group set list")
public class FederatedGroupSetQo implements Serializable {

    @ApiModelProperty(value = "group name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role = 0;

    @ApiModelProperty("order rule, 1:asc, 2:desc, default:desc")
    private Integer orderRule = 2;

    @ApiModelProperty("order field, default:update_time")
    private String orderField = "update_time";

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;
}
