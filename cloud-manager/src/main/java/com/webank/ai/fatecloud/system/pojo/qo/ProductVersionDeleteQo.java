package com.webank.ai.fatecloud.system.pojo.qo;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductVersionDeleteQo implements Serializable {
    @NotNull(message = "institutions can't be null!")
    private String productName;

    @NotNull(message = "institutions can't be null!")
    private String productVersion;

    @NotNull(message = "institutions can't be null!")
    private String componentName;
}
