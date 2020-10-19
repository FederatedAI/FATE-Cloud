package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.webank.ai.fatecloud.system.pojo.qo.ModelAddQo;
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
@ApiModel(value = "model details")
@TableName("t_federated_site_model")
public class FederatedSiteModelDo implements Serializable {
    public FederatedSiteModelDo(ModelAddQo modelAddQo) {
        this.installItems = modelAddQo.getInstallItems();
        this.version = modelAddQo.getVersion();
        this.type = modelAddQo.getType();
        this.id = modelAddQo.getId();
        this.updateStatus=modelAddQo.getUpdateStatus();
    }

    @ApiModelProperty(value = "primary key")
    @TableId(value = "model_id", type = IdType.AUTO)
    private Long modelId;

    @ApiModelProperty(value = "model name")
    @TableField(value = "install_items")
    private String installItems;

    @ApiModelProperty(value = "model version")
    @TableField(value = "version")
    private String version;

    @ApiModelProperty(value = "install time")
    @TableField(value = "install_time")
    private Date installTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

    @ApiModelProperty(value = "model status")
    @TableField(value = "status")
    private Integer status;

    @ApiModelProperty(value = "model update status")
    @TableField(value = "update_status")
    private Integer updateStatus;

    @ApiModelProperty(value = "system type")
    @TableField(value = "type")
    private String type;

    @ApiModelProperty(value = "site id")
    @TableField(value = "id")
    private Long id;
}
