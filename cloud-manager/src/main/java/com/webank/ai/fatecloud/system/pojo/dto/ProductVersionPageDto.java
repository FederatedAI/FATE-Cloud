package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "product version dto bean")
public class ProductVersionPageDto implements Serializable {
    private String productName;

    private String productVersion;

    private Integer publicStatus;

    private List<ComponentVersion> componentVersions;
}
