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
@ApiModel("group bean for add")
public class GroupAddQo implements Serializable {
    @ApiModelProperty(value = "party id group name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role;

    @ApiModelProperty(value = "regions")
    private List<Region> regions;

}
