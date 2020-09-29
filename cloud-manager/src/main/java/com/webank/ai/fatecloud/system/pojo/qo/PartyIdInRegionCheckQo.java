package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Api(value = "check party id in group regions bean")
public class PartyIdInRegionCheckQo implements Serializable {
    @ApiModelProperty(value = "party id for site")
    private Long partyId;
    @ApiModelProperty(value = "regions")
    private List<Region> regions;
}
