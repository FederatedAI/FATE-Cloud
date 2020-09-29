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
@ApiModel("add site")
public class SiteAddQo implements Serializable {

    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "site belongs to institutions")
    private String institutions;

    @ApiModelProperty(value = "site partyid")
    private Long partyId;

    @ApiModelProperty(value = "party id  group")
    private Long groupId;

    @ApiModelProperty(value = "network access entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    private String networkAccessExits;

}
