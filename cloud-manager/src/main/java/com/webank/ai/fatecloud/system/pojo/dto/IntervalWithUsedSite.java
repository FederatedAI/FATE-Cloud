package com.webank.ai.fatecloud.system.pojo.dto;

import com.webank.ai.fatecloud.common.Interval;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class IntervalWithUsedSite implements Serializable {
    public Interval interval;
    public List<Long> useSiteId;
}
