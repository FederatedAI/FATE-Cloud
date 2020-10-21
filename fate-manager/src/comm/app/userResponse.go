/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
type LoginUserListResponse struct {
	Data []entity.LoginSiteItem `json:"Data"`
}

type SubLoginResponse struct {
	Data entity.SubLoginResp `json:"data"`
}