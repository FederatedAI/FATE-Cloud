package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Api
public class GroupDeleteQo implements Serializable {
    @ApiModelProperty(value = "group id")
    private Long groupId;
}
