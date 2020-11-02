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
@ApiModel(value = "job summary qo bean for a period, site dimension")
public class JobStatisticsSummarySiteAllForPeriodQo implements Serializable {
    @NotNull(message = "begin date can't be null!")
    @ApiModelProperty(value = "begin date")
    private Date beginDate;

    @NotNull(message = "end date can't be null!")
    @ApiModelProperty(value = "end date")
    private Date endDate;

    @NotNull(message = "institutions can't be null!")
    @ApiModelProperty(value = "institutions name")
    private String institutions;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;
}
