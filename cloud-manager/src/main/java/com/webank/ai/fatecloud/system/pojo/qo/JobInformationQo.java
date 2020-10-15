package com.webank.ai.fatecloud.system.pojo.qo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job information")
public class JobInformationQo implements Serializable {

    @ApiModelProperty(value = "guest id")
    private String siteGuestId;

    @ApiModelProperty(value = "host id")
    private String siteHostId;

    @ApiModelProperty(value = "success count")
    private String jobSuccessCount;

    @ApiModelProperty(value = "failed count")
    private String jobFailedCount;

    @ApiModelProperty(value = "finish date")
    private String jobFinishDate;

}
