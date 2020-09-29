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
@ApiModel("search for site list by condition")
public class SiteListQo implements Serializable {

    @ApiModelProperty(value = "party id,site name")
    private String condition;

    @ApiModelProperty(value = "role,1:guest,2:host")
    private Integer role;

    @ApiModelProperty(value = "institutions")
    private String institutions;

    @ApiModelProperty(value = "fate version")
    private String fateVersion;

    @ApiModelProperty(value = "fate serving version")
    private String fateServingVersion;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
    private Integer status;

    @ApiModelProperty("order type,1:asc,2:desc,default:desc")
    private Integer orderRule = 2;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

}
