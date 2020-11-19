package com.webank.ai.fatecloud.system.pojo.qo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ComponentVersionAddQo implements Serializable {

    private String componentName;

    private String componentVersion;

    private String imageRepository;

    private String imageTag;

}
