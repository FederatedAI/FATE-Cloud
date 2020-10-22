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

type InitStatus int32

const (
	INIT_STATUS_UNKNOWN      InitStatus = -1
	INIT_STATUS_KUBEFATE     InitStatus = 1
	INIT_STATUS_START_DEPLOY InitStatus = 2
	INIT_STATUS_SERVICE      InitStatus = 3
)

func GetInitStatusString(p InitStatus) string {
	switch p {
	case INIT_STATUS_KUBEFATE:
		return "connect kubefate"
	case INIT_STATUS_START_DEPLOY:
		return "start deploy"
	case INIT_STATUS_SERVICE:
		return "goto service management"
	default:
		return "unknown"
	}
}

func GetInitStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 4; i++ {
		idPair := entity.IdPair{i, GetInitStatusString(InitStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
