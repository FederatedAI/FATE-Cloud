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
@ApiModel(value = "used institutions in group")
public class InstitutionsInGroup implements Serializable {


    @ApiModelProperty(value = "party id  group")
    private Long groupId;

    @ApiModelProperty(value = "party id  group")
    private int[] statusList;

}
