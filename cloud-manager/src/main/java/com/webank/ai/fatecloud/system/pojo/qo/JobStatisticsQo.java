package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job information")
public class JobStatisticsQo implements Serializable {
    @NotNull(message = "site guest id can't be null!")
    @ApiModelProperty(value = "guest id")
    private String siteGuestId;

    @NotNull(message = "site host id can't be null!")
    @ApiModelProperty(value = "host id")
    private String siteHostId;

    @NotNull(message = "success count can't be null!")
    @ApiModelProperty(value = "success count")
    private Integer jobSuccessCount;

    @NotNull(message = "failed count can't be null!")
    @ApiModelProperty(value = "failed count")
    private Integer jobFailedCount;

    @NotNull(message = "running count can't be null!")
    @ApiModelProperty(value = "running count")
    private Integer jobRunningCount;

    @NotNull(message = "finish date can't be null!")
    @ApiModelProperty(value = "finish date")
    private Date jobFinishDate;

}
