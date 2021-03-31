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

type AuditStatusType int32

const (
	AuditStatus_UNKNOWN  AuditStatusType = -1
	AuditStatus_AUDITING AuditStatusType = 1
	AuditStatus_AGREED   AuditStatusType = 2
	AuditStatus_REJECTED AuditStatusType = 3
	AuditStatus_CANCEL   AuditStatusType = 4
)

func GetAuditStatusString(p AuditStatusType) string {
	switch p {
	case AuditStatus_AUDITING:
		return "Waiting For Cloud Audit!"
	case AuditStatus_AGREED:
		return "Agreed Apply"
	case AuditStatus_REJECTED:
		return "Rejected Apply"
	case AuditStatus_CANCEL:
		return "Cancel Apply"
	default:
		return "unknown"
	}
}
func GetAuditStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 5; i++ {
		idPair := entity.IdPair{i, GetAuditStatusString(AuditStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
