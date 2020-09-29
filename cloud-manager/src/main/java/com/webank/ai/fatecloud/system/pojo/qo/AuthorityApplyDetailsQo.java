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
@ApiModel(value = "institutions for finding details of authority")
public class AuthorityApplyDetailsQo implements Serializable {

    @ApiModelProperty("institutions name")
    private String institutions;
}
