package app

import "fate.manager/entity"

type PrepareResponse struct {
	CommResp
	Data []entity.PrepareItem `json:"data"`
}

type PullListResponse struct {
	CommResp
	Data []entity.PullComponentListRespItem `json:"data"`
}

type InstallListResponse struct {
	CommResp
	Data []entity.InstallComponentListRespItem `json:"data"`
}
type AutoTestListResponse struct {
	CommResp
	Data entity.AutoTestListRespItem `json:"data"`
}
