package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.ProductVersionAddQo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@TableName("t_product_version")
@ApiModel(value = "product version do bean")
public class FederatedProductVersionDo implements Serializable {

//    @ApiModelProperty(value = "primary key")
//    @TableId(value = "product_version_id", type = IdType.AUTO)
//    private Long id;

    @ApiModelProperty(value = "product name")
    @TableField(value = "product_name")
    private String productName;

    @ApiModelProperty(value = "product version")
    @TableField(value = "product_version")
    private String productVersion;

    @ApiModelProperty(value = "component name")
    @TableField(value = "component_name")
    private String componentName;

    @ApiModelProperty(value = "component version")
    @TableField(value = "component_version")
    private String componentVersion;

    @ApiModelProperty(value = "image repository")
    @TableField(value = "image_repository")
    private String imageRepository;

    @ApiModelProperty(value = "image tag")
    @TableField(value = "image_tag")
    private String imageTag;

    @ApiModelProperty(value = "image name")
    @TableField(value = "image_name")
    private String imageName;

    @ApiModelProperty(value = "url for download image")
    @TableField(value = "image_download_url")
    private String imageDownloadUrl;

    @ApiModelProperty(value = "package name")
    @TableField(value = "package_name")
    private String packageName;

    @ApiModelProperty(value = "url for download package")
    @TableField(value = "package_download_url")
    private String packageDownloadUrl;

    @ApiModelProperty(value = "public status: 1 public,2 private")
    @TableField(value = "public_status")
    private Integer publicStatus;

    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    public FederatedProductVersionDo(ProductVersionAddQo productVersionAddQo){
        this.productName= productVersionAddQo.getProductName();
        this.productVersion= productVersionAddQo.getProductVersion();
        this.componentName= productVersionAddQo.getComponentName();
        this.componentVersion= productVersionAddQo.getComponentVersion();
        this.imageRepository= productVersionAddQo.getImageRepository();
        this.imageTag= productVersionAddQo.getImageTag();
        this.imageName= productVersionAddQo.getImageName();
        this.imageDownloadUrl= productVersionAddQo.getPackageDownloadUrl();
        this.packageName= productVersionAddQo.getPackageName();
        this.packageDownloadUrl= productVersionAddQo.getPackageDownloadUrl();
        this.publicStatus= productVersionAddQo.getPublicStatus();
    }

}
