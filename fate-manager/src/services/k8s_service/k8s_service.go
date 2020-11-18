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
package k8s_service

import (
	"encoding/json"
	"fate.manager/comm/enum"
	"fate.manager/entity"
	"fate.manager/models"
	"math/rand"
	"strings"
)

func GetKubenetesUrl(deployType enum.DeployType) string {
	kubenetsConf, err := models.GetKubenetesUrl(deployType)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return ""
	}
	return kubenetsConf.KubenetesUrl
}

func GetNodeIp(deployType enum.DeployType) []string {
	kubenetsConf, err := models.GetKubenetesUrl(deployType)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return nil
	}
	nodeList := strings.Split(kubenetsConf.NodeList, ",")
	var node string
	if len(nodeList) > 0 {
		node = nodeList[rand.Intn(len(nodeList))]
	}
	list := strings.Split(node, ":")
	return list
}

func GetLabel(address string) string {
	label := ""
	if len(address) == 0 {
		return label
	}
	kubenetsConf, err := models.GetKubenetesConf(int(enum.DeployType_K8S))
	if err != nil {
		return label
	}
	addressList := strings.Split(address, ":")
	nodelist := strings.Split(kubenetsConf.NodeList, ",")

	for i := 0; i < len(nodelist); i++ {
		node := strings.Split(nodelist[i], ":")
		if len(node) == 2 {
			if node[1] == addressList[0] {
				label = node[0]
				break
			}
		}
	}
	return label
}

func CheckNodeIp(address string, deployType enum.DeployType) bool {
	kubenetsConf, err := models.GetKubenetesUrl(deployType)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return false
	}
	ipList := strings.Split(address, ":")
	if len(ipList) != 2 {
		return false
	}
	var tag = false
	nodeList := strings.Split(kubenetsConf.NodeList, ",")
	for i := 0; i < len(nodeList); i++ {
		lablist := strings.Split(nodeList[i], ":")
		if len(lablist) == 2 {
			if lablist[1] == ipList[0] {
				tag = true
				break
			}
		}
	}
	return tag
}

func GetManagerIp() ([]entity.IpStatus, error) {
	conf, err := models.GetKubenetesConf(int(enum.DeployType_ANSIBLE))
	if err != nil {
		return nil, err
	}
	if len(conf.NodeList) > 0 {
		var prepareReq entity.PrepareReq
		err = json.Unmarshal([]byte(conf.NodeList), &prepareReq)
		if err != nil {
			return nil, err
		}
		status := "success"
		if len(conf.AnsibleCheck) == 0 {

		}
		var ansiblePrepare []entity.AnsiblePrepareItem
		err = json.Unmarshal([]byte(conf.AnsibleCheck), &ansiblePrepare)
		if err != nil {
			return nil, err
		}
		var IpStatusList []entity.IpStatus
		for i := 0; i < len(prepareReq.ManagerNode); i++ {
			ipStatus := entity.IpStatus{
				Ip:     prepareReq.ManagerNode[i],
				Status: status,
			}
			for j := 0; j < len(ansiblePrepare); j++ {
				if prepareReq.ManagerNode[i] == ansiblePrepare[j].Ip {
					for k := 0; k < len(ansiblePrepare[j].List); k++ {
						if ansiblePrepare[j].List[k].Status != "success" {
							status = "failed"
							break
						}
					}
					break
				}
			}
			ipStatus.Status = status
			IpStatusList = append(IpStatusList, ipStatus)
		}
		return IpStatusList, nil
	}
	return nil, nil
}
