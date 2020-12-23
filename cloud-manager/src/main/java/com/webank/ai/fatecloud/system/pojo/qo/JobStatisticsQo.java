package com.webank.ai.fatecloud.system.pojo.qo;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(ordinal = 5)
    @NotNull(message = "site guest id can't be null!")
    @ApiModelProperty(value = "guest id")
    private Long siteGuestId;

    @JSONField(ordinal = 6)
    @NotNull(message = "site host id can't be null!")
    @ApiModelProperty(value = "host id")
    private Long siteHostId;

    @JSONField(ordinal = 3)
    @NotNull(message = "success count can't be null!")
    @ApiModelProperty(value = "success count")
    private Long jobSuccessCount;

    @JSONField(ordinal = 1)
    @NotNull(message = "failed count can't be null!")
    @ApiModelProperty(value = "failed count")
    private Long jobFailedCount;

    @JSONField(ordinal = 2)
    @NotNull(message = "running count can't be null!")
    @ApiModelProperty(value = "running count")
    private Long jobRunningCount;

    @JSONField(ordinal = 4)
    @NotNull(message = "finish date can't be null!")
    @ApiModelProperty(value = "finish date")
    private Long jobFinishDate;

}
