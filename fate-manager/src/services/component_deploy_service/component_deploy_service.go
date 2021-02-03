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
package component_deploy_service

import (
	"bufio"
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"os"
	"strconv"
	"strings"
	"time"

	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/kubernetes"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/comm/util"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/k8s_service"
	"fate.manager/services/version_service"
	"github.com/axgle/mahonia"
)

func GetComponentList(serviceInfoReq entity.ServiceInfoReq) ([]entity.ComponentDeploy, error) {
	deployComponent := models.DeployComponent{
		//FederatedId: serviceInfoReq.FederatedId,
		PartyId:     serviceInfoReq.PartyId,
		ProductType: serviceInfoReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return nil, err
	}
	var componentDeployList []entity.ComponentDeploy
	for i := 0; i < len(deployComponentList); i++ {
		componentDeploy := entity.ComponentDeploy{
			SiteName:         deployComponentList[i].SiteName,
			ComponentName:    deployComponentList[i].ComponentName,
			ComponentVersion: deployComponentList[i].ComponentVersion,
			Address:          deployComponentList[i].Address,
			StartTime:        deployComponentList[i].StartTime.UnixNano() / 1e6,
			FinishTime:       deployComponentList[i].FinishTime.UnixNano() / 1e6,
			Duration:         deployComponentList[i].Duration,
			Status:           entity.IdPair{deployComponentList[i].Status, enum.GetSiteRunStatusString(enum.SiteRunStatusType(deployComponentList[i].Status))},
			DeployStatus:     entity.IdPair{deployComponentList[i].DeployStatus, enum.GetDeployStatusString(enum.DeployStatusType(deployComponentList[i].DeployStatus))},
		}
		componentDeployList = append(componentDeployList, componentDeploy)
	}
	return componentDeployList, nil
}
func GetComponentNameList(serviceInfoReq entity.ServiceInfoReq) ([]string, error) {
	deployComponent := models.ComponentVersion{
		FateVersion: serviceInfoReq.FateVersion,
	}
	deployComponentList, err := models.GetComponetVersionList(deployComponent)
	if err != nil {
		return nil, err
	}
	var componentNameList []string
	for i := 0; i < len(deployComponentList); i++ {
		if deployComponentList[i].ComponentName == "fateflow" || deployComponentList[i].ComponentName == "fateboard" {
			addTag := true
			for j := 0; j < len(componentNameList); j++ {
				if componentNameList[j] == "python" {
					addTag = false
					break
				}
			}
			if addTag {
				componentNameList = append(componentNameList, "python")
			}
		} else {
			componentNameList = append(componentNameList, deployComponentList[i].ComponentName)
		}
	}
	return componentNameList, nil
}
func GetInstallComponentList(installComponentListReq entity.InstallComponentListReq) ([]entity.InstallComponentListRespItem, error) {
	deployComponent := models.DeployComponent{
		FederatedId: installComponentListReq.FederatedId,
		PartyId:     installComponentListReq.PartyId,
		ProductType: installComponentListReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return nil, err
	}
	var installComponentListResp []entity.InstallComponentListRespItem
	for i := 0; i < len(deployComponentList); i++ {
		installComponentListRespItem := entity.InstallComponentListRespItem{
			ComponentName: deployComponentList[i].ComponentName,
			Address:       deployComponentList[i].Address,
			DeployStatus:  entity.IdPair{deployComponentList[i].DeployStatus, enum.GetDeployStatusString(enum.DeployStatusType(deployComponentList[i].DeployStatus))},
			Duration:      deployComponentList[i].Duration,
		}
		installComponentListResp = append(installComponentListResp, installComponentListRespItem)
	}

	return installComponentListResp, nil
}

func DoAction(actionReq entity.ActionReq) (int, error) {
	deployComponent := models.DeployComponent{
		FederatedId:   actionReq.FederatedId,
		PartyId:       actionReq.PartyId,
		ProductType:   actionReq.ProductType,
		ComponentName: actionReq.ComponentName,
		IsValid:       int(enum.IS_VALID_YES),
	}
	var data = make(map[string]interface{})
	if actionReq.Action == int(enum.ActionType_STOP) {
		data["status"] = int(enum.SITE_RUN_STATUS_STOPPED)
	} else if actionReq.Action == int(enum.ActionType_RESTART) {
		data["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
	} else {
		return e.INVALID_PARAMS, nil
	}
	models.UpdateDeployComponent(data, deployComponent)
	return e.SUCCESS, nil
}

func GetLog(logReq entity.LogReq) (map[string][]string, error) {

	var logs = make(map[string][]string)
	deploySite := models.DeploySite{
		FederatedId: logReq.FederatedId,
		PartyId:     logReq.PartyId,
		ProductType: logReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return nil, err
	}
	result, err := kubernetes.ClientSet.GetPodWithPattern("kube-fate", "kubefate")
	if err != nil {
		return nil, fmt.Errorf("GetPodWithPattern err[%s]", err.Error())
	}
	err = kubernetes.ClientSet.WriteLogsIntoFile("kube-fate", result, "./testLog/kubefate.log", 500)
	if err != nil {
		logging.Error("kubernetes.ClientSet.WriteLogsIntoFile err[%s]", err.Error())
	}

	file, err := os.Open("./testLog/kubefate.log")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()
	var all, info, warn, debug, error, other []string
	rd := bufio.NewReader(file)
	for {
		line, err := rd.ReadString('\n') //以'\n'为结束符读入一行
		if err != nil || io.EOF == err {
			break
		}
		lineText := ConvertToString(line, "UTF-8", "GBK")

		inf := strings.Index(lineText, "INF")
		dbg := strings.Index(lineText, "DBG")
		errr := strings.Index(lineText, "ERR")
		wrn := strings.Index(lineText, "WRN")
		all = append(all, lineText)
		if inf > 0 {
			info = append(info, lineText)
		} else if dbg > 0 {
			debug = append(debug, lineText)
		} else if errr > 0 {
			error = append(error, lineText)
		} else if wrn > 0 {
			warn = append(warn, lineText)
		} else {
			other = append(other, lineText)
		}
	}
	logs["info"] = info
	logs["warn"] = warn
	logs["debug"] = debug
	logs["error"] = error
	logs["other"] = other
	logs["all"] = all
	return logs, nil
}

//GBK转utf8的方法
func ConvertToString(src string, srcCode string, tagCode string) string {

	srcCoder := mahonia.NewDecoder(srcCode)
	srcResult := srcCoder.ConvertString(src)
	tagCoder := mahonia.NewDecoder(tagCode)
	_, cdata, _ := tagCoder.Translate([]byte(srcResult), true)
	result := string(cdata)
	return result
}

func ConnectKubeFate(kubeReq entity.KubeReq) (int, error) {

	if len(kubeReq.Url) == 0 || kubeReq.PartyId == 0 {
		return e.INVALID_PARAMS, nil
	}
	if kubeReq.Url != setting.KubenetesSetting.KubeFateUrl {
		return e.INVALID_PARAMS, nil
	}
	if kubeReq.FederatedId == 0 {
		federatedInfoList, err := models.GetFederatedInfo()
		if err != nil || len(federatedInfoList) == 0 {
			return e.ERROR_CONNECT_KUBE_FATE_FAIL, nil
		}
		kubeReq.FederatedId = federatedInfoList[0].Id
	}
	item, err := models.GetKubenetesConf()
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if item.Id == 0 {
		kubenetesConf := models.KubenetesConf{
			KubenetesUrl: kubeReq.Url,
			PythonPort:   30001,
			RollsitePort: 31000,
			NodeList:     "",
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}
		models.AddKubenetesConf(&kubenetesConf)
		item, _ = models.GetKubenetesConf()
	} else if item.KubenetesUrl != kubeReq.Url {
		var data = make(map[string]interface{})
		data["kubenetes_url"] = kubeReq.Url
		data["python_port"] = 30001
		data["rollsite_port"] = 31000
		data["update_time"] = time.Now()
		kubenetesConf := models.KubenetesConf{
			Id: item.Id,
		}
		models.UpdateKubenetesConf(data, kubenetesConf)
		item, _ = models.GetKubenetesConf()
	}
	if len(item.NodeList) == 0 {
		nodes, _ := kubernetes.ClientSet.GetNodesWithoutSpecNode("master")
		if setting.KubenetesSetting.ModeAlone {
			nodes, _ = kubernetes.ClientSet.GetNodesWithoutSpecNode("master")
		}
		labels := kubernetes.ClientSet.GenerateFMNodeLabel(nodes, "fm-node-", "InternalIP")
		kubernetes.ClientSet.SetLabelsForNode(nodes, labels)
		result := kubernetes.ClientSet.GetNodeLabelOfFM(nodes, "fm-node-")
		if len(result) > 0 {
			iplist := result
			var data = make(map[string]interface{})
			data["node_list"] = iplist
			data["update_time"] = time.Now()
			kubenetesConf := models.KubenetesConf{
				Id:           item.Id,
				KubenetesUrl: item.KubenetesUrl,
			}
			models.UpdateKubenetesConf(data, kubenetesConf)
		}
	}

	deploySite := models.DeploySite{
		FederatedId: kubeReq.FederatedId,
		PartyId:     kubeReq.PartyId,
		ProductType: kubeReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if len(deploySiteList) == 0 {
		cmd := fmt.Sprintf("kubefate cluster ls |awk '{if($2==\"fate-%d\"){print $1}}'", kubeReq.PartyId)
		result, _ := util.ExecCommand(cmd)
		if len(result) > 0 {
			clusterId := result[0 : len(result)-1]
			user := util.User{
				UserName: "admin",
				Password: "admin",
			}
			token, err := util.GetToken(kubeReq.Url, user)
			if err != nil {
				return e.ERROR_CONNECT_KUBE_FATE_FAIL, err
			}
			authorization := fmt.Sprintf("Bearer %s", token)
			head := make(map[string]interface{})
			head["Authorization"] = authorization
			result, err := http.GET(http.Url(kubeReq.Url+"/v1/cluster/"+clusterId), nil, head)
			if err != nil || result == nil {
				return e.ERROR_CONNECT_KUBE_FATE_FAIL, err
			}
			var clusterQueryResp entity.ClusterQueryResp
			index := bytes.IndexByte([]byte(result.Body), 0)
			err = json.Unmarshal([]byte(result.Body)[:index], &clusterQueryResp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return e.ERROR_CONNECT_KUBE_FATE_FAIL, err
			}

			var clusterConfig140 entity.ClusterConfig140
			err = json.Unmarshal([]byte(clusterQueryResp.Data.Values), &clusterConfig140)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return e.ERROR_CONNECT_KUBE_FATE_FAIL, err
			}
			deploySite := models.DeploySite{
				FederatedId:        kubeReq.FederatedId,
				PartyId:            kubeReq.PartyId,
				ProductType:        int(enum.PRODUCT_TYPE_FATE),
				Name:               clusterQueryResp.Data.Name,
				NameSpace:          clusterQueryResp.Data.NameSpace,
				Revision:           clusterQueryResp.Data.Revision,
				Status:             int(enum.SITE_RUN_STATUS_UNKNOWN),
				ClickType:          int(enum.ClickType_INSTALL),
				Chart:              clusterQueryResp.Data.ChartName,
				ChartVersion:       clusterQueryResp.Data.ChartVersion,
				ClusterValues:      clusterQueryResp.Data.Values,
				DeployStatus:       int(enum.DeployStatus_INSTALLED),
				UpgradeStatus:      0,
				Fateboard:          "",
				PythonPort:         clusterConfig140.Python.FateFlowNodePort,
				RollsitePort:       clusterConfig140.Rollsite.NodePort,
				ClusterId:          clusterQueryResp.Data.ClusterId,
				KubenetesId:        item.Id,
				SingleTest:         int(enum.TEST_STATUS_WAITING),
				ToyTest:            int(enum.TEST_STATUS_WAITING),
				MinimizeNormalTest: int(enum.TEST_STATUS_WAITING),
				MinimizeFastTest:   int(enum.TEST_STATUS_WAITING),
				ToyTestOnly:        int(enum.ToyTestOnly_NO_TEST),
				ToyTestOnlyRead:    int(enum.ToyTestOnlyTypeRead_YES),
				IsValid:            int(enum.IS_VALID_YES),
				CreateTime:         time.Now(),
				UpdateTime:         time.Now(),
			}
			info, _ := json.Marshal(clusterQueryResp.Data.Info)
			deploySite.ClusterInfo = string(info)

			config, _ := json.Marshal(clusterQueryResp.Data.Config)
			deploySite.Config = string(config)

			fateVersion := models.FateVersion{
				ChartVersion: clusterQueryResp.Data.ChartVersion,
			}
			fateVersionList, err := models.GetFateVersionList(&fateVersion)
			if err == nil && len(fateVersionList) > 0 {
				deploySite.FateVersion = fateVersionList[0].FateVersion
				deploySite.VersionIndex = fateVersionList[0].VersionIndex
			}
			models.AddDeploySite(&deploySite)

			componentVersion := models.ComponentVersion{
				FateVersion: fateVersionList[0].FateVersion,
				ProductType: kubeReq.ProductType,
				PullStatus:  int(enum.PULL_STATUS_YES),
			}
			componentVersionList, _ := models.GetComponetVersionList(componentVersion)
			for i := 0; i < len(componentVersionList); i++ {
				port := version_service.GetDefaultPort(componentVersionList[i].ComponentName)
				if componentVersionList[i].ComponentName == "python" {
					port = clusterConfig140.Python.FateFlowNodePort
				} else if componentVersionList[i].ComponentName == "rollsite" {
					port = clusterConfig140.Rollsite.NodePort
				}

				nodelist := k8s_service.GetNodeIp(kubeReq.FederatedId, kubeReq.PartyId)
				if len(nodelist) == 0 {
					continue
				}
				deployComponent := models.DeployComponent{
					FederatedId:      kubeReq.FederatedId,
					PartyId:          kubeReq.PartyId,
					SiteName:         "",
					ProductType:      kubeReq.ProductType,
					FateVersion:      fateVersionList[0].FateVersion,
					ComponentVersion: componentVersionList[i].ComponentVersion,
					ComponentName:    componentVersionList[i].ComponentName,
					Address:          nodelist[1] + ":" + strconv.Itoa(port),
					Label:            nodelist[0],
					StartTime:        time.Now(),
					EndTime:          time.Now(),
					Duration:         0,
					VersionIndex:     componentVersionList[i].VersionIndex,
					DeployStatus:     int(enum.DeployStatus_INSTALLED),
					Status:           0,
					IsValid:          int(enum.IS_VALID_YES),
					CreateTime:       time.Now(),
					UpdateTime:       time.Now(),
				}
				models.AddDeployComponent(&deployComponent)

				autoTest := models.AutoTest{
					FederatedId: kubeReq.FederatedId,
					PartyId:     kubeReq.PartyId,
					ProductType: kubeReq.ProductType,
					FateVersion: fateVersionList[0].FateVersion,
					TestItem:    componentVersionList[i].ComponentName,
					StartTime:   time.Time{},
					EndTime:     time.Time{},
					Status:      int(enum.TEST_STATUS_WAITING),
					CreateTime:  time.Time{},
					UpdateTime:  time.Time{},
				}
				models.AddAutoTest(autoTest)
				if i == len(componentVersionList)-1 {
					autoTest.TestItem = "Single Test"
					models.AddAutoTest(autoTest)
					autoTest.TestItem = "Toy Test"
					models.AddAutoTest(autoTest)
					autoTest.TestItem = "Mininmize Fast Test"
					models.AddAutoTest(autoTest)
					autoTest.TestItem = "Minimize Normal Test"
					models.AddAutoTest(autoTest)
				}
			}
		} else {
			deploySite.KubenetesId = item.Id
			deploySite.DeployStatus = int(enum.DeployStatus_UNKNOWN)
			deploySite.Status = int(enum.SITE_RUN_STATUS_UNKNOWN)
			deploySite.ClickType = int(enum.ClickType_CONNECT)
			deploySite.ToyTestOnlyRead = int(enum.ToyTestOnlyTypeRead_YES)
			deploySite.ToyTestOnly = int(enum.ToyTestOnly_NO_TEST)
			deploySite.CreateTime = time.Now()
			deploySite.UpdateTime = time.Now()
			models.AddDeploySite(&deploySite)
		}
	} else {
		var data = make(map[string]interface{})
		data["kubenetes_id"] = item.Id
		data["toy_test_only"] = int(enum.ToyTestOnly_NO_TEST)
		data["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_YES)
		models.UpdateDeploySite(data, deploySite)
	}
	return e.SUCCESS, nil
}

func GetInstallStatus(status entity.InstallStatus) (entity.IdPair, error) {
	deploySite := models.DeploySite{
		FederatedId: status.FederatedId,
		PartyId:     status.PartyId,
		ProductType: status.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return entity.IdPair{int(enum.INIT_STATUS_KUBEFATE), enum.GetInitStatusString(enum.INIT_STATUS_KUBEFATE)}, nil
	}
	if deploySiteList[0].Status > 0 {
		return entity.IdPair{int(enum.INIT_STATUS_SERVICE), enum.GetInitStatusString(enum.INIT_STATUS_SERVICE)}, nil
	}
	return entity.IdPair{int(enum.INIT_STATUS_START_DEPLOY), enum.GetInitStatusString(enum.INIT_STATUS_START_DEPLOY)}, nil
}
