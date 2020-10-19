package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.common.Interval;
import io.swagger.annotations.Api;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Api(value = "check group set range changes when update")
public class GroupRangeCheckDto implements Serializable {
    private List<Long> sets;
    private Map<Interval,List<Long>> intervalWithPartyIds= new HashMap<>();
}
