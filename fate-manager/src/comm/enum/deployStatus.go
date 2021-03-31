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

type DeployStatusType int32
type AnsibleDeployStatusType int32
const (
	DeployStatus_UNKNOWN            DeployStatusType = -1
	DeployStatus_SUCCESS            DeployStatusType = 0
	DeployStatus_PULLING_IMAGE      DeployStatusType = 1
	DeployStatus_PULLED             DeployStatusType = 2
	DeployStatus_PULLED_FAILED      DeployStatusType = 3
	DeployStatus_UNDER_INSTALLATION DeployStatusType = 4
	DeployStatus_INSTALLING         DeployStatusType = 5
	DeployStatus_INSTALLED          DeployStatusType = 6
	DeployStatus_INSTALLED_FAILED   DeployStatusType = 7
	DeployStatus_IN_TESTING         DeployStatusType = 8
	DeployStatus_TEST_PASSED        DeployStatusType = 9
	DeployStatus_TEST_FAILED        DeployStatusType = 10
)
const (
	ANSIBLE_DeployStatus_UNKNOWN            AnsibleDeployStatusType = -1
	ANSIBLE_DeployStatus_SUCCESS            AnsibleDeployStatusType = 0
	ANSIBLE_DeployStatus_LOADING            AnsibleDeployStatusType = 1
	ANSIBLE_DeployStatus_LOADED             AnsibleDeployStatusType = 2
	ANSIBLE_DeployStatus_LOAD_FAILED        AnsibleDeployStatusType = 3
	ANSIBLE_DeployStatus_UNDER_INSTALLATION AnsibleDeployStatusType = 4
	ANSIBLE_DeployStatus_INSTALLING         AnsibleDeployStatusType = 5
	ANSIBLE_DeployStatus_INSTALLED          AnsibleDeployStatusType = 6
	ANSIBLE_DeployStatus_INSTALLED_FAILED   AnsibleDeployStatusType = 7
	ANSIBLE_DeployStatus_IN_TESTING         AnsibleDeployStatusType = 8
	ANSIBLE_DeployStatus_TEST_PASSED        AnsibleDeployStatusType = 9
	ANSIBLE_DeployStatus_TEST_FAILED        AnsibleDeployStatusType = 10
)

func GetDeployStatusString(p DeployStatusType) string {
	switch p {
	case DeployStatus_SUCCESS:
		return "success"
	case DeployStatus_PULLING_IMAGE:
		return "In pulling image"
	case DeployStatus_PULLED:
		return "Image Pulled"
	case DeployStatus_PULLED_FAILED:
		return "image pulled failed"
	case DeployStatus_UNDER_INSTALLATION:
		return "Under installation"
	case DeployStatus_INSTALLING:
		return "Installing"
	case DeployStatus_INSTALLED:
		return "Installed"
	case DeployStatus_INSTALLED_FAILED:
		return "Installed failed"
	case DeployStatus_IN_TESTING:
		return "In testing"
	case DeployStatus_TEST_PASSED:
		return "Test passed"
	case DeployStatus_TEST_FAILED:
		return "Test failed"
	default:
		return "unknown"
	}
}
func GetAnsibleDeployStatusString(p AnsibleDeployStatusType) string {
	switch p {
	case ANSIBLE_DeployStatus_SUCCESS:
		return "success"
	case ANSIBLE_DeployStatus_LOADING:
		return "Loading Package"
	case ANSIBLE_DeployStatus_LOADED:
		return "Loaded Package"
	case ANSIBLE_DeployStatus_LOAD_FAILED:
		return "Load Package failed"
	case ANSIBLE_DeployStatus_UNDER_INSTALLATION:
		return "Under installation"
	case ANSIBLE_DeployStatus_INSTALLING:
		return "Installing"
	case ANSIBLE_DeployStatus_INSTALLED:
		return "Installed"
	case ANSIBLE_DeployStatus_INSTALLED_FAILED:
		return "Installed failed"
	case ANSIBLE_DeployStatus_IN_TESTING:
		return "In testing"
	case ANSIBLE_DeployStatus_TEST_PASSED:
		return "Test passed"
	case ANSIBLE_DeployStatus_TEST_FAILED:
		return "Test failed"
	default:
		return "unknown"
	}
}

func GetDeployStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 11; i++ {
		idPair := entity.IdPair{i, GetDeployStatusString(DeployStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
func GetAnsibleDeployStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 11; i++ {
		idPair := entity.IdPair{i, GetAnsibleDeployStatusString(AnsibleDeployStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}