package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "authority history")
public class AuthorityHistoryDto implements Serializable {

//    @ApiModelProperty("institution authority apply information for this sequence")
//    private AuthorityApplySequence authorityApplySequence;

    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty(value = "create time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    private Date updateTime;

    @ApiModelProperty(value = "insert sequence")
    private Long sequence;

    @ApiModelProperty("information of institution to apply")
    private List<AuthorityApplyReceiver> authorityApplyReceivers;

}
