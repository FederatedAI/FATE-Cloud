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

type ReadStatusType int32

const (
	READ_STATUS_UNKNOWN  ReadStatusType = -1
	READ_STATUS_AGREED   ReadStatusType = 1
	READ_STATUS_REJECTED ReadStatusType = 2
	READ_STATUS_READ     ReadStatusType = 3
)

func GetReadStatusString(p ReadStatusType) string {
	switch p {
	case READ_STATUS_AGREED:
		return "Successfully changed the Network configuration!"
	case READ_STATUS_REJECTED:
		return "Changed the Network configuration failed!"
	case READ_STATUS_READ:
		return "Read"
	default:
		return "unknown"
	}
}
func GetReadStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetReadStatusString(ReadStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
