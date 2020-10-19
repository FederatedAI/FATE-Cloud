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

type ActionType int32

const (
	ActionType_UNKNOWN ActionType = -1
	ActionType_STOP    ActionType = 0
	ActionType_RESTART ActionType = 1
)

func GetActionTypeString(p ActionType) string {
	switch p {
	case ActionType_STOP:
		return "stopped"
	case ActionType_RESTART:
		return "restart"
	default:
		return "unknown"
	}
}

func GetActionTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetActionTypeString(ActionType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
