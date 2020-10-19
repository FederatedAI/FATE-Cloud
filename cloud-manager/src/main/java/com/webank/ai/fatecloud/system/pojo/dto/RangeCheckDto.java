package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.common.Interval;
import io.swagger.annotations.Api;
import lombok.*;

import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Api(value = "check group set range changes when update")
public class RangeCheckDto implements Serializable {

    public RangeCheckDto(GroupRangeCheckDto groupRangeCheckDto){
        this.sets=groupRangeCheckDto.getSets();
        Map<Interval, List<Long>> intervalWithPartyIds = groupRangeCheckDto.getIntervalWithPartyIds();
        if(intervalWithPartyIds!=null){
            Set<Map.Entry<Interval, List<Long>>> entries = intervalWithPartyIds.entrySet();
            for (Map.Entry<Interval, List<Long>> entry : entries) {
                Interval interval = entry.getKey();
                List<Long> sets = entry.getValue();
                IntervalWithUsedSite intervalWithUsedSite = new IntervalWithUsedSite();
                intervalWithUsedSite.setInterval(interval);
                intervalWithUsedSite.setUseSiteId(sets);
                this.intervalWithPartyIds.add(intervalWithUsedSite);
            }
        }

    }
    private List<Long> sets;
    private List<IntervalWithUsedSite> intervalWithPartyIds = new ArrayList<>();
}
