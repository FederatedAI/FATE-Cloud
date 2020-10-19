package com.webank.ai.fatecloud.system.pojo.qo;

import com.webank.ai.fatecloud.common.RangeInfo;
import io.swagger.annotations.Api;
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
@ApiModel(value = "update group bean")
public class GroupUpdateQo implements Serializable {
    @ApiModelProperty(value = "group id")
    private Long groupId;

    @ApiModelProperty(value = "party id group name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role;

    @ApiModelProperty(value = "regions")
    private List<Region> regions;

}
