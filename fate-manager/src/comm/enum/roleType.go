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

type RoleType int32

const (
	ROLE_UNKNOWN RoleType = -1
	ROLE_GUEST   RoleType = 1
	ROLE_HOST    RoleType = 2
	ROLE_ARBITER RoleType = 3
)

func GetRoleString(p RoleType) string {
	switch p {
	case ROLE_GUEST:
		return "Guest"
	case ROLE_HOST:
		return "Host"
	case ROLE_ARBITER:
		return "Arbiter"
	default:
		return "Unknown"
	}
}

func GetRoleValue(p string) int {
	var roleype RoleType
	switch p {
	case "Guest":
		roleype = ROLE_GUEST
	case "Host":
		roleype = ROLE_HOST
	case "Arbiter":
		roleype = ROLE_ARBITER
	default:
		roleype = ROLE_UNKNOWN
	}
	return int(roleype)
}

func GetRoleTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetRoleString(RoleType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
