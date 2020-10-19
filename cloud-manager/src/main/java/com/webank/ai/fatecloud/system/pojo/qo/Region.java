package com.webank.ai.fatecloud.system.pojo.qo;

import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Region implements Serializable {
    private Long leftRegion;
    private Long rightRegion;
}
