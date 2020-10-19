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
@ApiModel("update site")
public class SiteUpdateQo extends SiteAddQo implements Serializable {

    @ApiModelProperty(value = "primary key")
    private Long id;
}
