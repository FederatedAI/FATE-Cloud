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
@ApiModel("check group name")
public class FederatedGroupSetNameCheckQo implements Serializable {
    public FederatedGroupSetNameCheckQo(FederatedGroupSetAddQo federatedGroupSetAddQo) {
        if (federatedGroupSetAddQo.getGroupName() != null) {
            this.groupName = federatedGroupSetAddQo.getGroupName();
        }

    }

    public FederatedGroupSetNameCheckQo(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        if (federatedGroupSetUpdateQo.getGroupName() != null) {
            this.groupName = federatedGroupSetUpdateQo.getGroupName();
        }
        if (federatedGroupSetUpdateQo.getGroupId() != null) {
            this.groupId = federatedGroupSetUpdateQo.getGroupId();
        }
    }

    public FederatedGroupSetNameCheckQo(GroupAddQo groupAddQo) {
        if (groupAddQo.getGroupName() != null) {
            this.groupName = groupAddQo.getGroupName();
        }
    }

    public FederatedGroupSetNameCheckQo(GroupUpdateQo groupUpdateQo) {
        if (groupUpdateQo.getGroupId() != null) {
            this.groupId = groupUpdateQo.getGroupId();
        }
        if (groupUpdateQo.getGroupName() != null) {
            this.groupName = groupUpdateQo.getGroupName();
        }
    }

    @ApiModelProperty(value = "party id group name")
    private String groupName;

    @ApiModelProperty(value = "group id")
    private Long groupId;
}
