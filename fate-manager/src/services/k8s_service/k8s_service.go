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
	"fate.manager/models"
	"math/rand"
	"strings"
)

func GetKubenetesUrl(federatedId int, partyId int) string {
	kubenetsConf, err := models.GetKubenetesUrl(federatedId, partyId)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return ""
	}
	return kubenetsConf.KubenetesUrl
}

func GetNodeIp(federatedId int, partyId int) []string {
	kubenetsConf, err := models.GetKubenetesUrl(federatedId, partyId)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return nil
	}
	nodeList := strings.Split(kubenetsConf.NodeList, ",")
	var node string
	if len(nodeList) >0 {
		node = nodeList[rand.Intn(len(nodeList))]
	}
	list := strings.Split(node,":")
	return list
}

func GetLabel(address string)string{
	label := ""
	if len(address) ==0{
		return label
	}
	kubenetsConf, err := models.GetKubenetesConf()
	if err != nil {
		return label
	}
	addressList := strings.Split(address,":")
	nodelist := strings.Split(kubenetsConf.NodeList,",")

	for i :=0;i<len(nodelist) ;i++  {
		node := strings.Split(nodelist[i],":")
		if len(node) ==2 {
			if node[1] == addressList[0] {
				label = node[0]
				break
			}
		}
	}
	return label
}
func CheckNodeIp(address string, federatedId int, partyId int) bool {
	kubenetsConf, err := models.GetKubenetesUrl(federatedId, partyId)
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
		if nodeList[i] == ipList[0] {
			tag = true
			break
		}
	}
	return tag
}
