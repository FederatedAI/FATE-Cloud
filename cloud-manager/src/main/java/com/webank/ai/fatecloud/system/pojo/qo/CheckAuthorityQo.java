package com.webank.ai.fatecloud.system.pojo.qo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.TreeMap;

@JsonPropertyOrder(alphabetic = true)

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CheckAuthorityQo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String site;

}
