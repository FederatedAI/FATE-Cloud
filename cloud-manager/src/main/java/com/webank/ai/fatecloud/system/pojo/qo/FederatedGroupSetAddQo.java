package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel("party group setting add")
public class FederatedGroupSetAddQo extends FederatedGroupSetCheckQo implements Serializable {

    @ApiModelProperty(value = "party id group name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    private Integer role;

}
