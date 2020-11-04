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
@ApiModel("check party id info bean")
public class PartyIdCheckQo implements Serializable {

    @ApiModelProperty(value = "site party id")
    private Long partyId;

    @ApiModelProperty(value = "institutions launching the authority-check")
    private String institutions;

}
