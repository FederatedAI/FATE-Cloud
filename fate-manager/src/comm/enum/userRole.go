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

type UserRole int32

const (
	UserRole_UNKNOWN   UserRole = -1
	UserRole_ADMIN     UserRole = 1
	UserRole_DEVELOPER UserRole = 2
	UserRole_BUSINESS  UserRole = 3
)

func GetUserRoleString(p UserRole) string {
	switch p {
	case UserRole_ADMIN:
		return "Admin"
	case UserRole_DEVELOPER:
		return "Developer or OP"
	case UserRole_BUSINESS:
		return "Business or Data Analyst"
	default:
		return "unknown"
	}
}

func GetUserRoleList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetUserRoleString(UserRole(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
