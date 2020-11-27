package com.webank.ai.fatecloud.system.pojo.qo;

import com.webank.ai.fatecloud.system.dao.entity.FederatedComponentVersionDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionAddQo implements Serializable {

    @NotNull(message = "product name can't be null!")
    private String productName;

    @NotNull(message = "product version can't be null!")
    private String productVersion;

    private String imageName;

    private String imageDownloadUrl;

    private String packageName;

    private String packageDownloadUrl;

    private Integer publicStatus;

    private String kubernetesChart;

    @NotNull(message = "component version can't be null")
    private List<ComponentVersionAddQo> componentVersionAddQos;

}
