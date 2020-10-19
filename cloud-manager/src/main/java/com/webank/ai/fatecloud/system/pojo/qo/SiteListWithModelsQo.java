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
@ApiModel("bean for find paged sites with models information")
public class SiteListWithModelsQo implements Serializable {

    @ApiModelProperty(value = "party id, site name")
    private String condition;

    @ApiModelProperty(value = "model version")
    private String version;

    @ApiModelProperty(value = "role,0:all, 1:guest,2:host")
    private Integer role = 0;

    @ApiModelProperty(value = "site status,0 : all 1 : not joined,2 joined,3 : removed")
    private Integer status = 0;

    @ApiModelProperty("order field")
    private String orderField = "create_time";

    @ApiModelProperty("order type,1:asc,2:desc,default:desc")
    private Integer orderRule = 2;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;
}
