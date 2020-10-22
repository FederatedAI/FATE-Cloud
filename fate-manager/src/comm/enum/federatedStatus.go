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

type FederatedType int32

const (
	FEDERATED_UNKNOWN FederatedType = -1
	FEDERATED_UNVALID FederatedType = 0
	FEDERATED_VALID   FederatedType = 1
)

func GetFederatedString(p FederatedType) string {
	switch p {
	case FEDERATED_UNVALID:
		return "unvalid federated"
	case FEDERATED_VALID:
		return "valid federated"
	default:
		return "unknown"
	}
}

func GetFederatedStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetFederatedString(FederatedType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
