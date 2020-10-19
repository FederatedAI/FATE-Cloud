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
@ApiModel(value = "site activate info")
public class SiteActivateQo implements Serializable {

    @ApiModelProperty(value = "federated registration link")
    private String registrationLink;
}
