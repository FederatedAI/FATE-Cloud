package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "institution authority apply information for this sequence")
public class AuthorityApplySequence implements Serializable {

    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty(value = "create time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    private Date updateTime;

    @ApiModelProperty(value = "admin activate status, 1:no, 1:yes")
    private Integer status;

    @ApiModelProperty(value = "insert sequence")
    private Long sequence;


}
