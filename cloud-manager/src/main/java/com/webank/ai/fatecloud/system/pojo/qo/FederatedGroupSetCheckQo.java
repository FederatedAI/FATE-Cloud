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
@ApiModel(value = "Check Group Set")
public class FederatedGroupSetCheckQo implements Serializable {

    public FederatedGroupSetCheckQo(FederatedGroupSetAddQo federatedGroupSetAddQo) {
        this.rangeInfo = federatedGroupSetAddQo.getRangeInfo();
        this.groupId = federatedGroupSetAddQo.getGroupId();
    }

    public FederatedGroupSetCheckQo(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        this.rangeInfo = federatedGroupSetUpdateQo.getRangeInfo();
        this.groupId = federatedGroupSetUpdateQo.getGroupId();
    }

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;

    @ApiModelProperty(value = "group id")
    private Long groupId;

}
