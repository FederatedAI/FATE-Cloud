package com.webank.ai.fatecloud.system.pojo.qo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ProductVersionUpdateQo implements Serializable {

    @NotNull(message = "product id can't be null!")
    private Long productId;

    @NotNull(message = "product name can't be null!")
    private String productName;

    @NotNull(message = "product version can't be null!")
    private String productVersion;

    private String imageName;

    private String imageDownloadUrl;

    private String packageName;

    private String packageDownloadUrl;

    private String kubernetesChart;

    private Integer publicStatus;

    private List<ComponentVersionAddQo> componentVersionAddQos;

}
