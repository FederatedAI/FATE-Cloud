package com.webank.ai.fatecloud.common;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RangeInfo implements Serializable {
    public List<Interval> intervals;
    public List<Long> sets;
}
