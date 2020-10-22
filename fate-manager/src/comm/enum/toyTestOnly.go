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

type ToyTestOnlyType int32

const (
	ToyTestOnly_UNKONWN ToyTestOnlyType = -1
	ToyTestOnly_NO_TEST ToyTestOnlyType = 0
	ToyTestOnly_TESTING ToyTestOnlyType = 1
	ToyTestOnly_SUCCESS ToyTestOnlyType = 2
	ToyTestOnly_FAILED  ToyTestOnlyType = 3
)

func GetToyTestOnlyString(p ToyTestOnlyType) string {
	switch p {
	case ToyTestOnly_SUCCESS:
		return "test success"
	case ToyTestOnly_NO_TEST:
		return "no test"
	case ToyTestOnly_TESTING:
		return "in testing"
	case ToyTestOnly_FAILED:
		return "toy test failed"
	default:
		return "unknown"
	}
}

func GetToyTestOnlyTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetToyTestOnlyString(ToyTestOnlyType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
