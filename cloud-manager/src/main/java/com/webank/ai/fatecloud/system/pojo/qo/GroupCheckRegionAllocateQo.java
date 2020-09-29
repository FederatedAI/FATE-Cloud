package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("check group range whether allocated or not")
public class GroupCheckRegionAllocateQo implements Serializable {
    @ApiModelProperty(value = "regions")
    private List<Region> regions;

    @ApiModelProperty(value = "group id")
    private Long groupId;
}
