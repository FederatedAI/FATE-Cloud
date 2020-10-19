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

type PullStatusType int32

const (
	PULL_STATUS_UNKNOWN PullStatusType = -1
	PULL_STATUS_NO      PullStatusType = 0
	PULL_STATUS_YES     PullStatusType = 1
	PULL_STATUS_FAILED  PullStatusType = 2
	PULL_STATUS_PULLING PullStatusType = 3
)

func GetPullStatusString(p PullStatusType) string {
	switch p {
	case PULL_STATUS_NO:
		return "no pull"
	case PULL_STATUS_YES:
		return "pulled"
	case PULL_STATUS_FAILED:
		return "pull failed"
	case PULL_STATUS_PULLING:
		return "pulling"
	default:
		return "unknown"
	}
}

func GetPullStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetPullStatusString(PullStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
