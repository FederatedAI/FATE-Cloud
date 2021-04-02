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

type NodeType int32

const (
	NodeType_UNKNOWN NodeType = -1
	NodeType_Control NodeType = 1
	NodeType_Manager NodeType = 2
)

func GetNodeTypeString(p NodeType) string {
	switch p {
	case NodeType_Control:
		return "control node"
	case NodeType_Manager:
		return "manager"
	default:
		return "unknown"
	}
}

func GetNodeTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 3; i++ {
		idPair := entity.IdPair{i, GetNodeTypeString(NodeType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
