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
@ApiModel(value = "job summary qo bean for today, institutions dimension")
public class JobStatisticsSummaryTodayQo implements Serializable {
    @NotNull(message = "finish date can't be null!")
    @ApiModelProperty(value = "finish date")
    private Date dateToday;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty("pageNum")
    private Integer pageNum = 1;
}
