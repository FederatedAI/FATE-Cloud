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

type JobStatus int32

const (
	JOB_STATUS_UNKNOWN JobStatus = -1
	JOB_STATUS_SUCCESS JobStatus = 0
	JOB_STATUS_RUNNING JobStatus = 1
	JOB_STATUS_FAILED  JobStatus = 2
)

func GetJobStatusString(p JobStatus) string {
	switch p {
	case JOB_STATUS_SUCCESS:
		return "success"
	case JOB_STATUS_RUNNING:
		return "running"
	case JOB_STATUS_FAILED:
		return "failed"
	default:
		return "unknown"
	}
}

func GetJobStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetJobStatusString(JobStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
