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
@ApiModel(value = "the institutions for getting the job statistics for a period")
public class JobOfSiteDimensionPeriodQo implements Serializable {

    @NotNull(message = "institutions can't be null!")
    @ApiModelProperty("institutions name")
    private String institutions;

    @NotNull(message = "date can't be null!")
    @ApiModelProperty("date begin")
    private Date beginDate;

    @NotNull(message = "date can't be null!")
    @ApiModelProperty("date end")
    private Date endDate;
}
