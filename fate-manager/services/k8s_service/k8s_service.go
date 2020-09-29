package k8s_service

import (
	"fate.manager/models"
	"strings"
)

func GetKubenetesUrl(federatedId int, partyId int) string {
	kubenetsConf, err := models.GetKubenetesUrl(federatedId, partyId)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return ""
	}
	return kubenetsConf.KubenetesUrl
}

func GetNodeIp(federatedId int, partyId int) string {
	kubenetsConf, err := models.GetKubenetesUrl(federatedId, partyId)
	if err != nil || len(kubenetsConf.KubenetesUrl) == 0 {
		return ""
	}
	nodeList := strings.Split(kubenetsConf.NodeList, ",")
	return nodeList[0]
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
