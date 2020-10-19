package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_federated_ip_manager")
@ApiModel(value = "Federated Ip Manager")
public class FederatedIpManagerDo implements Serializable  {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "primary key")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "case id")
    @TableId(value = "case_id")
    private String caseId;

    @ApiModelProperty(value = "site name")
    @TableId(value = "site_name")
    private String siteName;

    @ApiModelProperty(value = "group_id")
    @TableId(value = "group_id")
    private Long groupId;

    @ApiModelProperty(value = "site belongs to institutions")
    @TableId(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "site partyid")
    @TableId(value = "party_id")
    private Long partyId;

    @ApiModelProperty(value = "role,1:guest,2:host")
    @TableId(value = "role")
    private Integer role;

    @ApiModelProperty(value = "network access entrances")
    @TableId(value = "network_access_entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    @TableId(value = "network_access_exits")
    private String networkAccessExits;

    @ApiModelProperty(value = "network access entrances_old")
    @TableId(value = "network_access_entrances_old")
    private String networkAccessEntrancesOld;

    @ApiModelProperty(value = "network access exits_old")
    @TableId(value = "network_access_exits_old")
    private String networkAccessExitsOld;

    @ApiModelProperty(value = "log deal status,0:no deal,1:agreed,2:rejected")
    @TableId(value = "status")
    private Integer status;

    @ApiModelProperty(value = "create time")
    @TableId(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableId(value = "update_time")
    private Date updateTime;

}
