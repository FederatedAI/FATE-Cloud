package com.webank.ai.fatecloud.system.pojo.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ApiModel(value = "Site name bean")
public class SiteNameQo implements Serializable {
    public SiteNameQo(SiteAddQo siteAddQo) {
        if (siteAddQo.getSiteName() != null) {
            this.siteName = siteAddQo.getSiteName();
        }
    }

    public SiteNameQo(SiteUpdateQo siteUpdateQo) {
        if (siteUpdateQo.getSiteName() != null) {
            this.siteName = siteUpdateQo.getSiteName();
        }
        if (siteUpdateQo.getId() != null) {
            this.id = siteUpdateQo.getId();
        }
    }

    @ApiModelProperty(value = "site name")
    private String siteName;

    @ApiModelProperty(value = "primary key")
    private Long id;

}
