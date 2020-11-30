package com.webank.ai.fatecloud.system.pojo.qo;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionPageForFateManagerQo implements Serializable {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

}
