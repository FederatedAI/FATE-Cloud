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

type LogDealType int32

const (
	LOG_DEAL_UNKNOWN  LogDealType = -1
	LOG_DEAL_NO       LogDealType = 0
	LOG_DEAL_AGREED   LogDealType = 1
	LOG_DEAL_REJECTED LogDealType = 2
)

func GetLogDealTypeString(p LogDealType) string {
	switch p {
	case LOG_DEAL_NO:
		return "not deal"
	case LOG_DEAL_AGREED:
		return "agreed"
	case LOG_DEAL_REJECTED:
		return "rejected"
	default:
		return "unknown"
	}
}

func GetLogDealList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetLogDealTypeString(LogDealType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
