package com.webank.ai.fatecloud.system.pojo.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.webank.ai.fatecloud.common.RangeInfo;
import com.webank.ai.fatecloud.system.dao.entity.FederatedGroupSetDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "range info of group set ")
public class FederatedGroupSetRangeInfoDto implements Serializable {

    public FederatedGroupSetRangeInfoDto(FederatedGroupSetDo federatedGroupSetDo) {
        this.groupId = federatedGroupSetDo.getGroupId();
        this.groupName = federatedGroupSetDo.getGroupName();

        if (federatedGroupSetDo.getRangeInfo() != null) {
            String rangeInfoString = federatedGroupSetDo.getRangeInfo();
            this.rangeInfo = JSON.parseObject(rangeInfoString, new TypeReference<RangeInfo>() {
            });
        }
    }

    @ApiModelProperty(value = "group id")
    private Long groupId;

    @ApiModelProperty(value = "group name")
    private String groupName;

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;
}
