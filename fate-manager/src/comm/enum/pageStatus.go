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

type PageStatus int32
type AnsiblePageStatus int32

const (
	PAGE_STATUS_UNKNOWN   PageStatus = -1
	PAGE_STATUS_PREPARE   PageStatus = 0
	PAGE_STATUS_PAGE      PageStatus = 1
	PAGE_STATUS_PULLIMAGE PageStatus = 2
	PAGE_STATUS_INSTALL   PageStatus = 3
	PAGE_STATUS_TEST      PageStatus = 4
	PAGE_STATUS_SERVICE   PageStatus = 5
)

const (
	Ansible_PAGE_STATUS_UNKNOWN         AnsiblePageStatus = -1
	Ansible_PAGE_STATUS_CHECK           AnsiblePageStatus = 1
	Ansible_PAGE_STATUS_INSTALL_ANSIBLE AnsiblePageStatus = 2
	Ansible_PAGE_STATUS_PACKAGE         AnsiblePageStatus = 3
	Ansible_PAGE_STATUS_INSTALL         AnsiblePageStatus = 4
	Ansible_PAGE_STATUS_TEST            AnsiblePageStatus = 5
	Ansible_PAGE_STATUS_SERVICE         AnsiblePageStatus = 6
)

func GetPageStatusString(p PageStatus) string {
	switch p {
	case PAGE_STATUS_PREPARE:
		return "Prepare Page"
	case PAGE_STATUS_PAGE:
		return "Page start next"
	case PAGE_STATUS_PULLIMAGE:
		return "Pull Image Page"
	case PAGE_STATUS_INSTALL:
		return "Install Page"
	case PAGE_STATUS_TEST:
		return "Auto Test Page"
	case PAGE_STATUS_SERVICE:
		return "Service Managerment"
	default:
		return "unknown"
	}
}

func GetPageStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 6; i++ {
		idPair := entity.IdPair{i, GetPageStatusString(PageStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}

func GetAnsiblePageStatusString(p AnsiblePageStatus) string {
	switch p {
	case Ansible_PAGE_STATUS_CHECK:
		return "Ansible System Check"
	case Ansible_PAGE_STATUS_INSTALL_ANSIBLE:
		return "Install Ansible"
	case Ansible_PAGE_STATUS_PACKAGE:
		return "Load Package"
	case Ansible_PAGE_STATUS_INSTALL:
		return "Install Fate"
	case Ansible_PAGE_STATUS_TEST:
		return "Auto Test Page"
	case Ansible_PAGE_STATUS_SERVICE:
		return "Service Managerment"
	default:
		return "unknown"
	}
}

func GetAnsiblePageStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 7; i++ {
		idPair := entity.IdPair{i, GetAnsiblePageStatusString(AnsiblePageStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
