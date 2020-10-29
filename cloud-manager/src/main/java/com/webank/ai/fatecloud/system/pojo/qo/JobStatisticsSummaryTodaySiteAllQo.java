package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "job summary qo bean for today, site dimension")
public class JobStatisticsSummaryTodaySiteAllQo {

    @NotNull(message = "finish date can't be null!")
    @ApiModelProperty(value = "finish date")
    private Date dateToday;

    @NotNull(message = "institutions can't be null!")
    @ApiModelProperty(value = "institutions name")
    private String institutions;
}
