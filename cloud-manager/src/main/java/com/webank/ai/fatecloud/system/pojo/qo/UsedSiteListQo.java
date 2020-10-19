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
@ApiModel(value = "used site list for group set")
public class UsedSiteListQo implements Serializable {

    @ApiModelProperty(value = "partyid,siteanme")
    private String condition;

    @ApiModelProperty(value = "site status,1 not joined,2 joined,3 removed")
//    private Integer status;
    private int[] statusList;

    @ApiModelProperty(value = "order field")
    private String orderField;

    @ApiModelProperty(value = "order rule")
    private String orderRule;

    @ApiModelProperty(value = "institutions of site")
    private String institutions;


//    @ApiModelProperty(value = "site partyid")
//    private Boolean partyId;
//
//    @ApiModelProperty(value = "site name")
//    private Boolean siteName;
//
//    @ApiModelProperty(value = "site belongs to institutions")
//    private Boolean institutions;
//
//    @ApiModelProperty(value = "create time")
//    private Boolean createTime;

    @ApiModelProperty(value = "party id  group")
    private Long groupId;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

}
