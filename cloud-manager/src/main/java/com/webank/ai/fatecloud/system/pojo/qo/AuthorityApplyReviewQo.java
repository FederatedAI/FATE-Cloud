package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "authority review qo")
public class AuthorityApplyReviewQo implements Serializable {

    @ApiModelProperty("institutions name")
    private String institutions;

//    @ApiModelProperty("institutions name")
//    private Integer status;

    @ApiModelProperty("approved institutions name list")
    private Set<String> approvedInstitutionsList;

}
