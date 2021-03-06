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

type CheckSystemResp struct {
	CommResp
	Data []entity.Prepare `json:"data"`
}

type AnsibleListResponse struct {
	CommResp
	Data []entity.AnsibleListItem `json:"data"`
}

type AnsiblePackageResponse struct {
	CommResp
	Data entity.AnsibleCommResp `json:"data"`
}
type AnsibleLogResponse struct {
	CommResp
	Data entity.AnsibleLogResp `json:"data"`
}