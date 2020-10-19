package com.webank.ai.fatecloud.common;

import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Interval implements Serializable {
    public Long start;
    public Long end;
}
