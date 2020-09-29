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
@ApiModel(value = "drop down version list qo")
public class DropDownVersionQo implements Serializable {

    @ApiModelProperty("version name")
    private String versionName;
}
