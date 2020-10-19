package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "institution info")
public class InstitutionsDto implements Serializable {

    @ApiModelProperty("institution name")
    private String institutions;

    @ApiModelProperty("institution number")
    private Integer number;
}
