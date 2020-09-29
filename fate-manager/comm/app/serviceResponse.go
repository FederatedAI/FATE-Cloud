package app

import "fate.manager/entity"

type ServiceInfoResponse struct {
	CommResp
	Data []entity.ComponentDeploy `json:"data"`
}

type LogResponse struct {
	CommResp
	Data map[string][]string `json:"data"`
}

type OverViewResponse struct {
	CommResp
	Data []entity.OverViewRspItem `json:"data"`
}

type UpgradeFateResponse struct {
	CommResp
	Data []entity.UpdateVersionResp `json:"data"`
}

type GetPageStatusResponse struct {
	CommResp
	Data entity.PageStatusResp `json:"data"`
}
