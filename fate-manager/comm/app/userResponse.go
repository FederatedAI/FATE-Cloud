package app

import (
	"fate.manager/entity"
)

type LoginResponse struct {
	CommResp
	Data entity.LoginResp `json:"data"`
}
type UserListResponse struct {
	CommResp
	Data []entity.UserListItem `json:"data"`
}

type UserAccessListResponse struct {
	CommResp
	Data []entity.UserAccessListItem `json:"data"`
}
type UserSiteListResponse struct {
	CommResp
	Data []entity.SitePair `json:"data"`
}

type SiteInfoUserListResponse struct {
	CommResp
	Data []entity.SiteInfoUserListItem `json:"data"`
}
type CheckJwtResponse struct {
	CommResp
	Data entity.CheckJwtResp `json:"data"`
}
type LogoutResponse struct {
	CommResp
	Data bool `json:"data"`
}

type UserInfoResponse struct {
	CommResp
	Data entity.UserInfoResp `json:"data"`
}
