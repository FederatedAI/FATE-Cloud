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

type EditType int32

const (
	EDIT_UNKNOWN EditType = -1
	EDIT_NO      EditType = 1
	EDIT_YES     EditType = 2
)

func GetEditString(p EditType) string {
	switch p {
	case EDIT_NO:
		return "unedit"
	case EDIT_YES:
		return "edit"
	default:
		return "unknown"
	}
}

func GetEditList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetEditString(EditType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
