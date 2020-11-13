package com.webank.ai.fatecloud.system.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "component version bean")

public class ComponentVersion implements Serializable {

    private String componentName;

    private String componentVersion;

    private String imageRepository;

    private String imageTag;

    private String imageName;

    private String imageDownloadUrl;

    private String packageName;

    private String packageDownloadUrl;

}
