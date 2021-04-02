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

type JobType int32

const (
	JOB_TYPE_UNKNOWN JobType = -1
	JOB_TYPE_INSTALL JobType = 0
	JOB_TYPE_UPDATE  JobType = 1
	JOB_TYPE_DELETE  JobType = 2
)

func GetJobTypeString(p JobType) string {
	switch p {
	case JOB_TYPE_INSTALL:
		return "clusterInstall"
	case JOB_TYPE_UPDATE:
		return "clusterUpdate"
	case JOB_TYPE_DELETE:
		return "clusterDelete"
	default:
		return "unknown"
	}
}

func GetJobTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetJobTypeString(JobType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
