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
@ApiModel(value = "information of institution to apply")
public class AuthorityApplyReceiver implements Serializable {

    @ApiModelProperty(value = "primary key")
    private Long authorityId;

    @ApiModelProperty(value = "accessible institutions")
    private String authorityInstitutions;

    @ApiModelProperty(value = "authority status, 1:pending, 2:yes, 3:no, 4:cancel")
    private Integer status;

}
