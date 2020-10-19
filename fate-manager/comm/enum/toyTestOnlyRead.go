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

type ToyTestOnlyTypeRead int32

const (
	ToyTestOnlyTypeRead_UNKONWN ToyTestOnlyTypeRead = -1
	ToyTestOnlyTypeRead_YES     ToyTestOnlyTypeRead = 0
	ToyTestOnlyTypeRead_NO      ToyTestOnlyTypeRead = 1
)

func GetToyTestOnlyTypeReadString(p ToyTestOnlyTypeRead) string {
	switch p {
	case ToyTestOnlyTypeRead_YES:
		return "read"
	case ToyTestOnlyTypeRead_NO:
		return "no read"
	default:
		return "unknown"
	}
}

func GetToyTestOnlyTypeReadList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetToyTestOnlyTypeReadString(ToyTestOnlyTypeRead(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
