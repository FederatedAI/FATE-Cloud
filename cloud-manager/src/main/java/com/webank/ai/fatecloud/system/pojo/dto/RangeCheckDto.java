/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
