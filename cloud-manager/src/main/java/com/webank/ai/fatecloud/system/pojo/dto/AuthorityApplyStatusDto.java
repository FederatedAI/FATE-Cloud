package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "authority status for these institutions")
public class AuthorityApplyStatusDto {

    @ApiModelProperty("institutions names")
    private String institutions;

    @ApiModelProperty("apply number for institution")
    private Long number;


}
