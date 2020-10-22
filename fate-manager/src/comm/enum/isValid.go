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

type IsValidType int32

const (
	IS_VALID_UNKNOWN IsValidType = -1
	IS_VALID_NO      IsValidType = 0
	IS_VALID_YES     IsValidType = 1
	IS_VALID_ING     IsValidType = 2
)

func GetIsValidString(p IsValidType) string {
	switch p {
	case IS_VALID_YES:
		return "valid"
	case IS_VALID_NO:
		return "unvalid"
	case IS_VALID_ING:
		return "wait for valid"
	default:
		return "unknown"
	}
}

func GetIsValidList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetIsValidString(IsValidType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
