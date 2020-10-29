package com.webank.ai.fatecloud.system.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@ApiModel(value = "cloud manager user information")
@TableName("t_federated_exchange")
public class FederatedExchangeDo implements Serializable {

    @ApiModelProperty(value = "primary key")
    @TableId(value = "exchange_id", type = IdType.AUTO)
    private Long exchangeId;

    @ApiModelProperty(value = "exchange name")
    @TableField(value = "exchange_name")
    private String exchangeName;

    @ApiModelProperty(value = "network access entrances")
    @TableField(value = "network_access_entrances")
    private String networkAccessEntrances;

    @ApiModelProperty(value = "network access exits")
    @TableField(value = "network_access_exits")
    private String networkAccessExits;


    @ApiModelProperty(value = "create time")
    @TableField(value = "create_time")
    private Date createTime;

    @ApiModelProperty(value = "update time")
    @TableField(value = "update_time")
    private Date updateTime;

}
