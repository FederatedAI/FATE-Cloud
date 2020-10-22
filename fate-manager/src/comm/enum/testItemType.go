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

type TestItemType int32

const (
	TEST_ITEM_UNKNOWN TestItemType = -1
	TEST_ITEM_POD     TestItemType = 0
	TEST_ITEM_SINGLE  TestItemType = 1
	TEST_ITEM_TOY     TestItemType = 2
	TEST_ITEM_FAST    TestItemType = 3
	TEST_ITEM_NORMAL  TestItemType = 4
)

func GetTestItemString(p TestItemType) string {
	switch p {
	case TEST_ITEM_POD:
		return "Pod/SVC Status Of Each Component"
	case TEST_ITEM_SINGLE:
		return "Single Test"
	case TEST_ITEM_TOY:
		return "Toy Test"
	case TEST_ITEM_FAST:
		return "Mininmize Fast Test"
	case TEST_ITEM_NORMAL:
		return "Minimize Normal Test"
	default:
		return "unknown"
	}
}

func GetTestItemTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 5; i++ {
		idPair := entity.IdPair{i, GetTestItemString(TestItemType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
