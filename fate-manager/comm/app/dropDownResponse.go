package app

import "fate.manager/entity"

type FederationResponse struct {
	CommResp
	Data []entity.FederationListItem `json:"data"`
}

type SiteResponse struct {
	CommResp
	Data []entity.SiteListItem `json:"data"`
}

type FateVersionResponse struct {
	CommResp
	Data []string `json:"data"`
}
