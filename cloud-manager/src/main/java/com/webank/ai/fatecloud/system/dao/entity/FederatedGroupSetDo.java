package com.webank.ai.fatecloud.system.dao.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedGroupSetAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.FederatedGroupSetUpdateQo;
import com.webank.ai.fatecloud.system.pojo.qo.GroupAddQo;
import com.webank.ai.fatecloud.system.pojo.qo.GroupUpdateQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_federated_group_set")
@ApiModel(value = "Federated Group Set")
public class FederatedGroupSetDo implements Serializable {

    public FederatedGroupSetDo(FederatedGroupSetAddQo federatedGroupSetAddQo) {
        this.groupName = federatedGroupSetAddQo.getGroupName();
        this.role = federatedGroupSetAddQo.getRole();
        this.rangeInfo = JSON.toJSONString(federatedGroupSetAddQo.getRangeInfo());
    }

    public FederatedGroupSetDo(FederatedGroupSetUpdateQo federatedGroupSetUpdateQo) {
        this.groupId = federatedGroupSetUpdateQo.getGroupId();
        this.groupName = federatedGroupSetUpdateQo.getGroupName();
        this.role = federatedGroupSetUpdateQo.getRole();
        this.rangeInfo = JSON.toJSONString(federatedGroupSetUpdateQo.getRangeInfo());
    }

    public FederatedGroupSetDo(GroupAddQo groupAddQo) {
        this.groupName = groupAddQo.getGroupName();
        this.role = groupAddQo.getRole();
    }

    public FederatedGroupSetDo(GroupUpdateQo groupUpdateQo) {
        this.groupId = groupUpdateQo.getGroupId();
        this.groupName = groupUpdateQo.getGroupName();
        this.role = groupUpdateQo.getRole();
    }

    @ApiModelProperty(value = "primary key")
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    @ApiModelProperty(value = "group name")
    @TableField(value = "group_name")
    private String groupName;

    @ApiModelProperty(value = "role, 1:guest, 2:host")
    @TableField(value = "role")
    private Integer role;

    @ApiModelProperty(value = "party id range")
    @TableField(value = "range_info")
    private String rangeInfo;

    @ApiModelProperty(value = "total party id num")
    @TableField(value = "total")
    private Long total;

    @ApiModelProperty(value = "used party id num")
    @TableField(value = "used")
    private Long used;

    @ApiModelProperty(value = "group status, 1:valid, 2:delete")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private List<FederatedGroupDetailDo> federatedGroupDetailDos;
}
