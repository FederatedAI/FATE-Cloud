package com.webank.ai.fatecloud.system.pojo.qo;

import com.webank.ai.fatecloud.common.RangeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("group setting update")
public class FederatedGroupSetUpdateQo implements Serializable {

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;

    @ApiModelProperty(value = "group id")
    private Long groupId;

    @ApiModelProperty(value = "party id group name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role;


}
