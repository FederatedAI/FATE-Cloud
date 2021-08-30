package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "roll site and party associate")
@TableName("t_roll_site_party")
public class RollSitePartyDo {
    @ApiModelProperty(value = "roll site id")
    @TableId(value = "roll_site_id")
    private Long rollSiteId;

    @ApiModelProperty(value = "party id value")
    @TableId(value = "party_id")
    private String partyId;
}
