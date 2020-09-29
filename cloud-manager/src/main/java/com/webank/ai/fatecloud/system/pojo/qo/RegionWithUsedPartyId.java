package com.webank.ai.fatecloud.system.pojo.qo;

import lombok.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RegionWithUsedPartyId implements Serializable {
    private Region region = new Region();
    private List<Long> usedPartyIds = new LinkedList<>();
}
