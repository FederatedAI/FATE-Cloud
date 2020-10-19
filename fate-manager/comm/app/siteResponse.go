package app

import "fate.manager/entity"

type HomeSiteListResponse struct {
	CommResp
	Data []entity.FederatedItem `json:"data"`
}

type SiteDetailResponse struct {
	CommResp
	Data entity.SiteDetailResp `json:"data"`
}

type SiteSecretResponse struct {
	CommResp
	Data entity.SiteSecretResp `json:"data"`
}

type GetChangeResponse struct {
	CommResp
	Data entity.GetChangeResp `json:"data"`
}

type QueryApplySitesResponse struct {
	CommResp
	Data entity.Audit `json:"data"`
}
type FunctionResponse struct {
	CommResp
	Data entity.Function `json:"data"`
}
type InstitutionsResponse struct {
	CommResp
	Data []entity.ApplyResult `json:"data"`
}
type ApplyFateManagerResponse struct {
	CommResp
	Data entity.ApplyFateManager `json:"data"`
}
type OtherFateManagerResponse struct {
	SiteItemMap []entity.FederatedItem `json:"siteItemMap"`
}