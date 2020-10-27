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
package enum

import "fate.manager/entity"

type ServiceStatusType int32

const (
	SERVICE_STATUS_UNKNOWN   ServiceStatusType = -1
	SERVICE_STATUS_UNAVAILABLE   ServiceStatusType = 0
	SERVICE_STATUS_AVAILABLE ServiceStatusType = 1
)

func GetServiceStatusString(p ServiceStatusType) string {
	switch p {
	case SERVICE_STATUS_AVAILABLE:
		return "Available"
	case SERVICE_STATUS_UNAVAILABLE:
		return "Unavaiable"
	default:
		return "unknown"
	}
}

func GetServiceStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetServiceStatusString(ServiceStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
