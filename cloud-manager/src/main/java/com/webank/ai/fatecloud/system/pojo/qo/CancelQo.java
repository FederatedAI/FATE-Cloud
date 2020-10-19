package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "cancel authority qo")
public class CancelQo implements Serializable {

    @ApiModelProperty("institutions name")
    private String institutions;

    @ApiModelProperty("canceled institutions name list")
    private Set<String> canceledInstitutionsList;
}
