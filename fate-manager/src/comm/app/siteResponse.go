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
	SiteItemMap []entity.FederatedItem `json:"data"`
}
type ExchangeResponse struct {
	CommResp
	Data entity.ExchangeResponse `json:"data"`
}