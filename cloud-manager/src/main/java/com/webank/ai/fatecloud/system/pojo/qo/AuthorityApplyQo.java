package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "apply for institutions authority")
public class AuthorityApplyQo implements Serializable {

    @ApiModelProperty("institutions name")
    private String institutions;

    @ApiModelProperty("institutions names")
    private ArrayList<String> authorityInstitutions;

}
