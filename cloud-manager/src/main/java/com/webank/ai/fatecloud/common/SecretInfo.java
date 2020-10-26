package com.webank.ai.fatecloud.common;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel("secret detail")
public class SecretInfo {
    public String key;
    public String secret;
}
