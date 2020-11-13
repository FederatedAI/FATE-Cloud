package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionAddQo implements Serializable {

    @NotNull(message = "institutions can't be null!")
    private String productName;

    @NotNull(message = "institutions can't be null!")
    private String productVersion;

    @NotNull(message = "institutions can't be null!")
    private String componentName;

    @NotNull(message = "institutions can't be null!")
    private String componentVersion;

    private String imageRepository;

    private String imageTag;

    private String imageName;

    private String imageDownloadUrl;

    private String packageName;

    private String packageDownloadUrl;

    private Integer publicStatus;

}
