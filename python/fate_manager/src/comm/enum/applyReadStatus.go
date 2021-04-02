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

type ApplyReadStatusType int32

const (
	APPLY_READ_STATUS_UNKNOWN  ApplyReadStatusType = -1
	APPLY_READ_STATUS_NOT_READ ApplyReadStatusType = 0
	APPLY_READ_STATUS_READ     ApplyReadStatusType = 1
)

func GetApplyReadStatusString(p ApplyReadStatusType) string {
	switch p {
	case APPLY_READ_STATUS_NOT_READ:
		return "Apply Site No Read"
	case APPLY_READ_STATUS_READ:
		return "Apply Site Read"
	default:
		return "unknown"
	}
}
func GetApplyReadStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetApplyReadStatusString(ApplyReadStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
