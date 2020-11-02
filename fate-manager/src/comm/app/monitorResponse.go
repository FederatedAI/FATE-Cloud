package app

import "fate.manager/entity"

type MonitorResponse struct {
	CommResp
	Data entity.MonitorTotalResp `json:"data"`
}

type InstitutionBaseStaticsResponse struct {
	CommResp
	Data entity.InstitutionBaseStaticsResp `json:"data"`
}

type SiteBaseStatisticsResponse struct {
	CommResp
	Data []entity.InstitutionSiteModelingItem `json:"data"`
}