package com.webank.ai.fatecloud.system.pojo.qo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.webank.ai.fatecloud.common.RangeInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Api(value = "check partyid qo")
public class SitePartyIdCheckQo implements Serializable {
    public SitePartyIdCheckQo(String rangeInfo, Long partyId) {
        this.partyId = partyId;
        this.rangeInfo = JSON.parseObject(rangeInfo, new TypeReference<RangeInfo>() {
        });
    }

    @ApiModelProperty(value = "party id range")
    private RangeInfo rangeInfo;

    @ApiModelProperty(value = "site partyid")
    private Long partyId;

}
