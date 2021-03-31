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

type TestStatusType int32

const (
	TEST_STATUS_UNKNOWN TestStatusType = -1
	TEST_STATUS_WAITING TestStatusType = 0
	TEST_STATUS_TESTING TestStatusType = 1
	TEST_STATUS_YES     TestStatusType = 2
	TEST_STATUS_NO      TestStatusType = 3
)

func GetTestStatusString(p TestStatusType) string {
	switch p {
	case TEST_STATUS_WAITING:
		return "waiting"
	case TEST_STATUS_TESTING:
		return "In Testing"
	case TEST_STATUS_YES:
		return "Test Success"
	case TEST_STATUS_NO:
		return "Test Failed"
	default:
		return "unknown"
	}
}

func GetTestStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetTestStatusString(TestStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
