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

type ClickType int32
type AnsibleClickType int32

const (
	ClickType_UNKONWN ClickType = -1
	ClickType_CONNECT ClickType = 1
	ClickType_PAGE    ClickType = 2
	ClickType_PULL    ClickType = 3
	ClickType_INSTALL ClickType = 4
	ClickType_TEST    ClickType = 5
)
const (
	AnsibleClickType_UNKONWN         AnsibleClickType = -1
	AnsibleClickType_CONNECT         AnsibleClickType = 1
	AnsibleClickType_PREPARE         AnsibleClickType = 2
	AnsibleClickType_SYSTEM_CHECK    AnsibleClickType = 3
	AnsibleClickType_ANSIBLE_INSTALL AnsibleClickType = 4
	AnsibleClickType_ACQUISITON      AnsibleClickType = 5
	AnsibleClickType_INSTALL         AnsibleClickType = 6
	AnsibleClickType_TEST            AnsibleClickType = 7
)

func GetClickTypeString(p ClickType) string {
	switch p {
	case ClickType_CONNECT:
		return "connect success"
	case ClickType_PAGE:
		return "click page start next"
	case ClickType_PULL:
		return "click pull next"
	case ClickType_INSTALL:
		return "click install next"
	case ClickType_TEST:
		return "click test finish"
	default:
		return "unknown"
	}
}

func GetAnsibleClickTypeString(p AnsibleClickType) string {
	switch p {
	case AnsibleClickType_CONNECT:
		return "click ansible connect"
	case AnsibleClickType_PREPARE:
		return "click prepare"
	case AnsibleClickType_SYSTEM_CHECK:
		return "click system check"
	case AnsibleClickType_ANSIBLE_INSTALL:
		return "click ansible install"
	case AnsibleClickType_ACQUISITON:
		return "click package acquisition"
	case AnsibleClickType_INSTALL:
		return "click install"
	case AnsibleClickType_TEST:
		return "click test finish"
	default:
		return "unknown"
	}
}

func GetClickTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 6; i++ {
		idPair := entity.IdPair{i, GetClickTypeString(ClickType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}

func GetAnsibleClickTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 8; i++ {
		idPair := entity.IdPair{i, GetAnsibleClickTypeString(AnsibleClickType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
