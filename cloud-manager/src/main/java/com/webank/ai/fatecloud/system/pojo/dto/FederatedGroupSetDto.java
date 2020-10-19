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
@ApiModel(value = "group set dto")
public class FederatedGroupSetDto implements Serializable {

    public FederatedGroupSetDto(FederatedGroupSetDo federatedGroupSetDo) {
        this.groupId = federatedGroupSetDo.getGroupId();
        this.groupName = federatedGroupSetDo.getGroupName();
        this.role = federatedGroupSetDo.getRole();
        this.total = federatedGroupSetDo.getTotal();
        this.used = federatedGroupSetDo.getUsed();
        this.status = federatedGroupSetDo.getStatus();

        if (federatedGroupSetDo.getCreateTime() != null) {
            this.createTime = federatedGroupSetDo.getCreateTime().getTime();
        }

        if (federatedGroupSetDo.getUpdateTime() != null) {
            this.createTime = federatedGroupSetDo.getUpdateTime().getTime();
        }

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

    @ApiModelProperty(value = "role, 0:unknow, 1:guest, 2:host")
    private Integer role;

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;

    @ApiModelProperty(value = "total party id num")
    private Long total;

    @ApiModelProperty(value = "used party id num")
    private Long used;

    @ApiModelProperty(value = "group status, 0:unknow, 1:valid, 2:delete")
    private Integer status;

    @ApiModelProperty(value = "create time")
    private Long createTime;

    @ApiModelProperty(value = "update time")
    private Long updateTime;
}
