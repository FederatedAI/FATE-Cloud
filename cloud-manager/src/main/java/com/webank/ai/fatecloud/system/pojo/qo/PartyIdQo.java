package com.webank.ai.fatecloud.system.pojo.qo;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class PartyIdQo implements Serializable {
    public PartyIdQo(SiteAddQo siteAddQo) {
        if (siteAddQo.getPartyId() != null) {
            this.partyId = siteAddQo.getPartyId();
        }
    }

    public PartyIdQo(SiteUpdateQo siteUpdateQo) {
        if (siteUpdateQo.getPartyId() != null) {
            this.partyId = siteUpdateQo.getPartyId();
        }
        if (siteUpdateQo.getId() != null) {
            this.id = siteUpdateQo.getId();
        }
    }

    public Long partyId;
    private Long id;
}
