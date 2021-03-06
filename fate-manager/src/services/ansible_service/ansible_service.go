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
package ansible_service

import (
	"encoding/json"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/job_service"
	"fate.manager/services/k8s_service"
	"fmt"
	"strconv"
	"strings"
	"time"
)

func ConnectAnsible(ansibleReq entity.AnsibleConnectReq) (int, error) {
	if ansibleReq.PartyId == 0 || len(ansibleReq.Url) == 0 {
		return e.INVALID_PARAMS, nil
	}
	var connectReq entity.ConnectAnsible
	connectReq.PartyId = ansibleReq.PartyId
	result, err := http.POST(http.Url(ansibleReq.Url+setting.AnsibleConnectUri), connectReq, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var connectResp entity.AnsibleConnectResp
	err = json.Unmarshal([]byte(result.Body), &connectResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if connectResp.Code == e.SUCCESS {
		kubenetesConf := models.KubenetesConf{
			KubenetesUrl: ansibleReq.Url,
			DeployType:   int(enum.DeployType_ANSIBLE),
			ClickType:    int(enum.AnsibleClickType_CONNECT),
			NodeList:     setting.DeploySetting.AnsibleNode,
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}
		conf, _ := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
		if conf.Id == 0 {
			models.AddKubenetesConf(&kubenetesConf)
		}
		deploySite := models.DeploySite{
			PartyId:            ansibleReq.PartyId,
			ProductType:        int(enum.PRODUCT_TYPE_FATE),
			Status:             int(enum.SITE_RUN_STATUS_UNKNOWN),
			ClickType:          int(enum.ANSIBLE_DeployStatus_LOADED),
			KubenetesId:        kubenetesConf.Id,
			FateVersion:        connectResp.Data.FateVersion,
			DeployStatus:       int(enum.ANSIBLE_DeployStatus_UNKNOWN),
			SingleTest:         int(enum.TEST_STATUS_WAITING),
			ToyTest:            int(enum.TEST_STATUS_WAITING),
			MinimizeNormalTest: int(enum.TEST_STATUS_WAITING),
			MinimizeFastTest:   int(enum.TEST_STATUS_WAITING),
			ToyTestOnly:        int(enum.ToyTestOnly_NO_TEST),
			ToyTestOnlyRead:    int(enum.ToyTestOnlyTypeRead_YES),
			DeployType:         int(enum.DeployType_ANSIBLE),
			IsValid:            int(enum.IS_VALID_YES),
			CreateTime:         time.Now(),
			UpdateTime:         time.Now(),
		}
		fateVersion := models.FateVersion{FateVersion: connectResp.Data.FateVersion}
		fateVersionList, err := models.GetFateVersionList(&fateVersion)
		if err == nil && len(fateVersionList) > 0 {
			deploySite.VersionIndex = fateVersionList[0].VersionIndex
		}
		if conf.Id > 0 {
			deploySite.KubenetesId = conf.Id
		}

		if kubenetesConf.ClickType > 0 {
			deploySite.ClickType = conf.ClickType
		}
		clickTag := false
		config := ClusterInstallByAnsible{
			PartyId:     ansibleReq.PartyId,
			FateVersion: connectResp.Data.FateVersion,
			Role:        connectResp.Data.Role,
		}
		var modules []string
		for i := 0; i < len(connectResp.Data.List); i++ {
			connectItem := connectResp.Data.List[i]
			if connectItem.Module == "fateflow" || connectItem.Module == "fateboard" || connectItem.Module == "mysql" ||
				connectItem.Module == "clustermanager" || connectItem.Module == "nodemanager" || connectItem.Module == "rollsite" {
				modules = append(modules, connectItem.Module)
				address := ""
				var addressList []string
				var port int
				clickTag = true
				for j := 0; j < len(connectItem.Ips); j++ {
					addressList = append(addressList, connectItem.Ips[j])
					port = connectItem.Port
					ipport := fmt.Sprintf("%s:%d", connectItem.Ips[j], connectItem.Port)
					if connectItem.Module == "fateflow" {
						ipport = fmt.Sprintf("%s:%d", connectItem.Ips[j], connectItem.Port)
					}
					address = ipport
					if j < len(connectItem.Ips)-1 {
						address = fmt.Sprintf("%s,%s", address, ipport)
					}
				}
				IpNode := IpNode{
					//Enable: true,
					Ip:   addressList,
					Port: port,
				}
				Java := Java{
					Ip: addressList,
				}
				if connectItem.Module == "mysql" {
					config.Modules.Mysql.IpNode = IpNode
					config.Modules.Mysql.Password = "fate_pass"
					config.Modules.Mysql.User = "fate_user"
					//req.Modules.Base.Ip = address
				} else if connectItem.Module == "clustermanager" {
					config.Modules.Clustermanager = IpNode
				} else if connectItem.Module == "nodemanager" {
					config.Modules.Nodemanager = IpNode
				} else if connectItem.Module == "fateflow" {
					config.Modules.Flow.Ip = addressList
					config.Modules.Flow.Dbname = "fate_flow"
					//req.Modules.Flow.Enable = true
					config.Modules.Flow.GrpcPort = 9360
					config.Modules.Flow.HttpPort = port
					//req.Modules.Python.Enable = true
					config.Modules.Python.Ip = addressList
				} else if connectItem.Module == "fateboard" {
					config.Modules.Fateboard = Fateboard{
						Dbname: "fate_flow",
						IpNode: IpNode,
					}
					config.Modules.Java.Ip = IpNode.Ip
					config.Modules.Java = Java
					config.Modules.Supervisor.Ip = addressList
					//req.Modules.Supervisor.Enable = true
				} else if connectItem.Module == "rollsite" {
					config.Modules.Rollsite.IpNode = IpNode
					config.Modules.Rollsite.Port = port
					rule := Rule{
						Name: "default",
						Ip:   setting.DeploySetting.ExchangeIp,
						Port: setting.DeploySetting.ExchangePort,
					}
					config.Modules.Rollsite.DefaultRules = append(config.Modules.Rollsite.DefaultRules, rule)
					rule.Ip = addressList[0]
					rule.Port = port
					config.Modules.Rollsite.Rules = append(config.Modules.Rollsite.Rules, rule)
					rule.Name = "fateflow"
					rule.Port = 9360
					config.Modules.Rollsite.Rules = append(config.Modules.Rollsite.Rules, rule)
					config.Modules.Eggroll.Ip = addressList
					config.Modules.Eggroll.Dbname = "eggroll_meta"
					config.Modules.Eggroll.Egg = setting.DeploySetting.SessionProcessorsPerNode
				}

				deployComponent := models.DeployComponent{
					JobId:            fmt.Sprintf("%d_connect", ansibleReq.PartyId),
					PartyId:          ansibleReq.PartyId,
					ProductType:      int(enum.PRODUCT_TYPE_FATE),
					ComponentName:    connectItem.Module,
					ComponentVersion: connectResp.Data.FateVersion,
					Address:          address,
					DeployStatus:     int(enum.ANSIBLE_DeployStatus_INSTALLED),
					DeployType:       int(enum.DeployType_ANSIBLE),
					IsValid:          int(enum.IS_VALID_YES),
					StartTime:        time.Now(),
					EndTime:          time.Now(),
					CreateTime:       time.Now(),
					UpdateTime:       time.Now(),
				}
				componentVersion := models.ComponentVersion{FateVersion: connectResp.Data.FateVersion, ComponentName: connectItem.Module}
				componentVersionList, err := models.GetComponetVersionList(componentVersion)
				if err == nil && len(componentVersionList) > 0 {
					deployComponent.VersionIndex = componentVersionList[0].VersionIndex
					deployComponent.ComponentVersion = componentVersionList[0].ComponentVersion
				}
				if connectItem.Status == "running" {
					deployComponent.Status = int(enum.SITE_RUN_STATUS_RUNNING)
				} else if connectItem.Status == "stopped" {
					deployComponent.Status = int(enum.SITE_RUN_STATUS_STOPPED)
				} else {
					deployComponent.DeployStatus = int(enum.ANSIBLE_DeployStatus_INSTALLED_FAILED)
					deployComponent.Status = int(enum.SITE_RUN_STATUS_UNKNOWN)
				}
				deployComponentList, _ := models.GetDeployComponent(deployComponent)
				if len(deployComponentList) == 0 {
					models.AddDeployComponent(&deployComponent)
				}
				autoTest := models.AutoTest{
					PartyId:     ansibleReq.PartyId,
					ProductType: int(enum.PRODUCT_TYPE_FATE),
					FateVersion: connectResp.Data.FateVersion,
					TestItem:    connectItem.Module,
					CreateTime:  time.Now(),
					UpdateTime:  time.Now(),
				}
				autoTestList, _ := models.GetAutoTest(autoTest)
				if len(autoTestList) == 0 {
					autoTest.Status = int(enum.TEST_STATUS_WAITING)
					models.AddAutoTest(autoTest)
				} else {
					var data = make(map[string]interface{})
					data["status"] = int(enum.TEST_STATUS_WAITING)
					models.UpdateAutoTest(data, autoTest)
				}
				if i == len(connectResp.Data.List)-1 {
					autoTest.TestItem = ""
					job_service.InserTestItem(autoTest, enum.TEST_ITEM_TOY)
					job_service.InserTestItem(autoTest, enum.TEST_ITEM_FAST)
					job_service.InserTestItem(autoTest, enum.TEST_ITEM_NORMAL)
					job_service.InserTestItem(autoTest, enum.TEST_ITEM_SINGLE)
				}
			}
		}
		deploySiteList, _ := models.GetDeploySite(&deploySite)
		if len(deploySiteList) == 0 {
			if clickTag {
				deploySite.ClickType = int(enum.AnsibleClickType_INSTALL)
				deploySite.DeployStatus = int(enum.ANSIBLE_DeployStatus_INSTALLED)
				if len(config.Role) == 0 {
					siteInfo := models.SiteInfo{PartyId: deploySite.PartyId, Status: int(enum.SITE_STATUS_JOINED)}
					siteInfoList, _ := models.GetSiteList(&siteInfo)
					if len(siteInfoList) > 0 {
						config.Role = enum.GetRoleString(enum.RoleType(siteInfoList[0].Role))
					}
				}
				reqs, _ := json.Marshal(config)
				deploySite.Config = string(reqs)

				deployJob := models.DeployJob{
					JobId:       fmt.Sprintf("%d_connect", ansibleReq.PartyId),
					JobType:     int(enum.JOB_TYPE_INSTALL),
					Creator:     "admin",
					Status:      int(enum.JOB_STATUS_SUCCESS),
					StartTime:   time.Now(),
					EndTime:     time.Now(),
					PartyId:     ansibleReq.PartyId,
					DeployType:  int(enum.DeployType_ANSIBLE),
					ProductType: int(enum.PRODUCT_TYPE_FATE),
					CreateTime:  time.Now(),
					UpdateTime:  time.Now(),
				}
				models.AddDeployJob(&deployJob)
			}

			models.AddDeploySite(&deploySite)
			siteInfo := models.SiteInfo{PartyId: deploySite.PartyId, Status: int(enum.SITE_STATUS_JOINED)}
			var data = make(map[string]interface{})
			data["service_status"] = int(enum.SERVICE_STATUS_UNAVAILABLE)
			models.UpdateSiteByCondition(data, siteInfo)
		}
		return e.SUCCESS, nil
	}
	return e.ERROR_CONNECT_ANSIBLE_FAIL, nil
}

func LocalUpload(localUploadReq entity.LocalUploadReq) (int, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	uploadReq := entity.UploadReq{
		PartyId: localUploadReq.PartyId,
		Ip:      localUploadReq.Ip,
		Path:    localUploadReq.Path,
	}
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleLocalUploadUri), uploadReq, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var ansibleInstallListResponse entity.AnsibleInstallListResponse
	err = json.Unmarshal([]byte(result.Body), &ansibleInstallListResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	var data = make(map[string]interface{})
	deploySite := models.DeploySite{
		PartyId:    localUploadReq.PartyId,
		IsValid:    int(enum.IS_VALID_YES),
		DeployType: int(enum.DeployType_ANSIBLE),
	}
	if ansibleInstallListResponse.Code == e.SUCCESS {
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
		data["fate_version"] = ansibleInstallListResponse.Data.FateVersion
		models.UpdateDeploySite(data, deploySite)
		return e.SUCCESS, nil
	}
	data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOAD_FAILED)
	models.UpdateDeploySite(data, deploySite)
	return e.ERROR_LOACAL_UPLOAD_FAIL, nil
}

func AutoAcquire(autoAcquireReq entity.AutoAcquireReq) (int, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	fateVersion := models.FateVersion{
		FateVersion: autoAcquireReq.FateVersion,
	}
	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil || len(fateVersionList) == 0 {
		return e.ERROR_AUTO_ACQUIRE_FAIL, err
	}
	var data = make(map[string]interface{})
	data["fate_version"] = autoAcquireReq.FateVersion
	deploySite := models.DeploySite{
		PartyId:    autoAcquireReq.PartyId,
		IsValid:    int(enum.IS_VALID_YES),
		DeployType: int(enum.DeployType_ANSIBLE),
	}
	if fateVersionList[0].PackageStatus == int(enum.PACKAGE_STATUS_NO) {
		var downloadReq entity.DownloadPackageReq
		downloadReq.DownloadUrl = fateVersionList[0].PackageUrl
		downloadReq.FateVersion = autoAcquireReq.FateVersion
		result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePackageDownUri), downloadReq, nil)
		if err != nil || result == nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			return e.ERROR_HTTP_FAIL, err
		}
		var ansibleInstallListResponse entity.AnsibleInstallListResponse
		err = json.Unmarshal([]byte(result.Body), &ansibleInstallListResponse)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return e.ERROR_PARSE_JSON_ERROR, err
		}
		if ansibleInstallListResponse.Code == e.SUCCESS {
			data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADING)
			models.UpdateDeploySite(data, deploySite)
			return e.SUCCESS, nil
		}
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOAD_FAILED)
		models.UpdateDeploySite(data, deploySite)
		return e.ERROR_AUTO_ACQUIRE_FAIL, nil
	} else if fateVersionList[0].PackageStatus == int(enum.PACKAGE_STATUS_YES) {
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
	} else if fateVersionList[0].PackageStatus == int(enum.PACKAGE_STATUS_PULLING) {
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADING)
	} else if fateVersionList[0].PackageStatus == int(enum.PACKAGE_STATUS_FAILED) {
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOAD_FAILED)
	}
	models.UpdateDeploySite(data, deploySite)
	return e.SUCCESS, nil
}

func GetComponentList(connectAnsible entity.AnsibleAutoTestReq) (*entity.AcquireResp, error) {
	deploySite := models.DeploySite{
		PartyId:     connectAnsible.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		IsValid:     int(enum.IS_VALID_YES),
		DeployType:  int(enum.DeployType_ANSIBLE),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return nil, err
	}
	var acquireResp entity.AcquireResp
	if len(deploySiteList) > 0 {
		if len(deploySiteList[0].FateVersion) == 0 {
			return nil, nil
		}
		componentVersion := models.ComponentVersion{
			FateVersion: deploySiteList[0].FateVersion,
		}
		componentVersionList, err := models.GetComponetVersionList(componentVersion)
		if err != nil {
			return nil, err
		}
		var acquireRespItemList []entity.AcquireRespItem
		for i := 0; i < len(componentVersionList); i++ {
			acquireRespItem := entity.AcquireRespItem{
				Item:             componentVersionList[i].ComponentName,
				Description:      componentVersionList[i].ImageDescription,
				ComponentVersion: componentVersionList[i].ComponentVersion,
				Size:             componentVersionList[i].ImageSize,
				Time:             componentVersionList[i].ImageCreateTime.UnixNano() / 1e6,
				Status: entity.IdPair{
					Code: componentVersionList[i].PackageStatus,
					Desc: enum.GetPackageStatusString(enum.PackageStausType(componentVersionList[i].PackageStatus)),
				},
			}
			acquireRespItemList = append(acquireRespItemList, acquireRespItem)
		}
		acquireResp.AcquireRespItemList = acquireRespItemList
		acquireResp.FateVersion = componentVersion.FateVersion

		var data = make(map[string]interface{})
		data["fate_version"] = componentVersion.FateVersion

		deploySite := models.DeploySite{
			PartyId:     connectAnsible.PartyId,
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			IsValid:     int(enum.IS_VALID_YES),
			DeployType:  int(enum.DeployType_ANSIBLE),
		}
		models.UpdateDeploySite(data, deploySite)
	}
	return &acquireResp, nil
}

type IpNode struct {
	//Enable bool     `json:"enable"`
	Ip   []string `json:"ips"`
	Port int      `json:"port"`
}
type Base struct {
	Ip []string `json:"ips"`
}
type Java struct {
	//Enable bool     `json:"enable"`
	Ip []string `json:"ips"`
}
type AnsibleMysql struct {
	IpNode
	Password string `json:"dbpasswd"`
	User     string `json:"dbuser"`
}
type AnsibleFlow struct {
	Ip       []string `json:"ips"`
	HttpPort int      `json:"httpPort"`
	GrpcPort int      `json:"grpcPort"`
}
type Rule struct {
	Name string `json:"name"`
	Ip   string `json:"ip"`
	Port int    `json:"port"`
}
type Rollsite struct {
	IpNode
	Port         int    `json:"port"`
	DefaultRules []Rule `json:"default_rules"`
	Rules        []Rule `json:"rules"`
}
type Eggroll struct {
	Ip     []string `json:"ips"`
	Dbname string   `json:"dbname"`
	Egg    int      `json:"egg"`
}
type FateFlow struct {
	//Enable bool   `json:"enable"`
	Dbname string `json:"dbname"`
	AnsibleFlow
}
type Fateboard struct {
	Dbname string `json:"dbname"`
	IpNode
}
type Modules struct {
	Mysql          AnsibleMysql `json:"mysql"`
	Clustermanager IpNode       `json:"clustermanager"`
	Nodemanager    IpNode       `json:"nodemanager"`
	Flow           FateFlow     `json:"fate_flow"`
	Fateboard      Fateboard    `json:"fateboard"`
	Java           Java         `json:"java"`
	Python         Java         `json:"python"`
	Eggroll        Eggroll      `json:"eggroll"`
	Rollsite       Rollsite     `json:"rollsite"`
	Supervisor     Java         `json:"supervisor"`
	//Base           Base         `json:"base"`
}
type ClusterInstallByAnsible struct {
	PartyId     int     `json:"party_id"`
	Role        string  `json:"role"`
	FateVersion string  `json:"version"`
	Modules     Modules `json:"modules"`
}

func InstallByAnsible(installReq entity.InstallReq) (int, error) {
	site := models.SiteInfo{
		PartyId: installReq.PartyId,
		Status:  int(enum.SITE_STATUS_JOINED),
	}
	siteList, err := models.GetSiteList(&site)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if len(siteList) == 0 {
		return e.ERROR_GET_SITE_FAIL, nil
	}
	deploySite := models.DeploySite{
		FederatedId: installReq.FederatedId,
		PartyId:     installReq.PartyId,
		ProductType: installReq.ProductType,
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return e.ERROR_PARTY_INSTALLED_FAIL, err
	} else {
		if deploySiteList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_UNDER_INSTALLATION) ||
			deploySiteList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_INSTALLING) {
			return e.ERROR_PARTY_IS_INSTALLING_FAIL, err
		}
	}
	deployComponent := models.DeployComponent{
		PartyId:     installReq.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return e.ERROR_INSTALL_ALL_FAIL, nil
	}
	req := ClusterInstallByAnsible{
		PartyId:     installReq.PartyId,
		FateVersion: deploySiteList[0].FateVersion,
		Role:        strings.ToLower(enum.GetRoleString(enum.RoleType(siteList[0].Role))),
	}
	var modules []string
	for i := 0; i < len(deployComponentList); i++ {

		item := deployComponentList[i]
		modules = append(modules, item.ComponentName)
		arr1 := strings.Split(item.Address, ",")
		var port int
		var address []string
		for j := 0; j < len(arr1); j++ {
			arr2 := strings.Split(arr1[j], ":")
			port, _ = strconv.Atoi(arr2[1])
			address = append(address, arr2[0])
		}
		IpNode := IpNode{
			//Enable: true,
			Ip:   address,
			Port: port,
		}
		Java := Java{
			Ip: address,
		}
		if item.ComponentName == "mysql" {
			req.Modules.Mysql.IpNode = IpNode
			req.Modules.Mysql.Password = "fate_pass"
			req.Modules.Mysql.User = "fate_user"
			//req.Modules.Base.Ip = address
		} else if item.ComponentName == "clustermanager" {
			req.Modules.Clustermanager = IpNode
		} else if item.ComponentName == "nodemanager" {
			req.Modules.Nodemanager = IpNode
		} else if item.ComponentName == "fateflow" {
			req.Modules.Flow.Ip = address
			req.Modules.Flow.Dbname = "fate_flow"
			//req.Modules.Flow.Enable = true
			req.Modules.Flow.GrpcPort = 9360
			req.Modules.Flow.HttpPort = port
			//req.Modules.Python.Enable = true
			req.Modules.Python.Ip = address
		} else if item.ComponentName == "fateboard" {
			req.Modules.Fateboard = Fateboard{
				Dbname: "fate_flow",
				IpNode: IpNode,
			}
			req.Modules.Java.Ip = IpNode.Ip
			req.Modules.Java = Java
			req.Modules.Supervisor.Ip = address
			//req.Modules.Supervisor.Enable = true
		} else if item.ComponentName == "rollsite" {
			req.Modules.Rollsite.IpNode = IpNode
			req.Modules.Rollsite.Port = port
			rule := Rule{
				Name: "default",
				Ip:   setting.DeploySetting.ExchangeIp,
				Port: setting.DeploySetting.ExchangePort,
			}
			req.Modules.Rollsite.DefaultRules = append(req.Modules.Rollsite.DefaultRules, rule)
			rule.Ip = address[0]
			rule.Port = port
			req.Modules.Rollsite.Rules = append(req.Modules.Rollsite.Rules, rule)
			rule.Name = "fateflow"
			rule.Port = 9360
			req.Modules.Rollsite.Rules = append(req.Modules.Rollsite.Rules, rule)
			req.Modules.Eggroll.Ip = address
			req.Modules.Eggroll.Dbname = "eggroll_meta"
			req.Modules.Eggroll.Egg = setting.DeploySetting.SessionProcessorsPerNode
		}
	}
	reqs, _ := json.Marshal(req)
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleJobSubmitUri), req, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var submitResponse entity.SubmitResponse
	err = json.Unmarshal([]byte(result.Body), &submitResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if submitResponse.Code == e.SUCCESS {

		var data = make(map[string]interface{})
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_UNDER_INSTALLATION)
		data["job_id"] = submitResponse.Data.JobId
		models.UpdateDeployComponent(data, deployComponent)
		data["config"] = string(reqs)
		models.UpdateDeploySite(data, deploySite)
		deployJob := models.DeployJob{
			JobId:       submitResponse.Data.JobId,
			JobType:     int(enum.JOB_TYPE_INSTALL),
			Status:      int(enum.JOB_STATUS_RUNNING),
			StartTime:   time.Now(),
			PartyId:     req.PartyId,
			DeployType:  int(enum.DeployType_ANSIBLE),
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			CreateTime:  time.Now(),
			UpdateTime:  time.Now(),
		}

		models.AddDeployJob(&deployJob)
	}
	return e.SUCCESS, nil
}

func UpgradeByAnsible(upgradeReq entity.UpgradeReq) (int, error) {
	deploySite := models.DeploySite{
		PartyId:     upgradeReq.PartyId,
		ProductType: upgradeReq.ProductType,
		//DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid: int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	fateVerson := models.FateVersion{
		FateVersion: upgradeReq.FateVersion,
		ProductType: upgradeReq.ProductType,
	}
	fateVersonList, err := models.GetFateVersionList(&fateVerson)
	if err != nil || len(fateVersonList) == 0 {
		return e.ERROR_IMAGE_NOT_ALL_PULL_FAIL, err
	}
	if deploySiteList[0].Status != int(enum.SITE_RUN_STATUS_RUNNING) && deploySiteList[0].Status != int(enum.SITE_RUN_STATUS_STOPPED) {
		return e.ERROR_UPDATE_SITE_FAIL, err
	}
	if deploySiteList[0].VersionIndex >= fateVersonList[0].VersionIndex {
		logging.Debug(e.GetMsg(e.ERROR_VERSION_NO_LOWER_THAN_CURRENT_FAIL))
		return e.ERROR_VERSION_NO_LOWER_THAN_CURRENT_FAIL, err
	}
	var req ClusterInstallByAnsible
	err = json.Unmarshal([]byte(deploySiteList[0].Config), &req)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	req.FateVersion = upgradeReq.FateVersion
	reqs, _ := json.Marshal(req)
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleJobSubmitUri), req, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var submitResponse entity.SubmitResponse
	err = json.Unmarshal([]byte(result.Body), &submitResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if submitResponse.Code == e.SUCCESS {
		var data = make(map[string]interface{})
		data["is_valid"] = int(enum.IS_VALID_NO)
		models.UpdateDeploySite(data, deploySite)
		deploySite = models.DeploySite{
			FederatedId:  deploySiteList[0].FederatedId,
			PartyId:      deploySiteList[0].PartyId,
			ProductType:  deploySiteList[0].ProductType,
			FateVersion:  upgradeReq.FateVersion,
			JobId:        submitResponse.Data.JobId,
			VersionIndex: fateVersonList[0].VersionIndex,
			DeployStatus: int(enum.ANSIBLE_DeployStatus_UNDER_INSTALLATION),
			Status:       int(enum.SITE_RUN_STATUS_UNKNOWN),
			KubenetesId:  deploySiteList[0].KubenetesId,
			DeployType:   int(enum.DeployType_ANSIBLE),
			IsValid:      int(enum.IS_VALID_YES),
			Config:       string(reqs),
			ClickType:    int(enum.AnsibleClickType_ACQUISITON),
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}
		models.AddDeploySite(&deploySite)

		componentVersion := models.ComponentVersion{
			FateVersion: upgradeReq.FateVersion,
			ProductType: upgradeReq.ProductType,
		}
		componentVersionList, err := models.GetComponetVersionList(componentVersion)
		if err != nil {
			logging.Error("get component version list Failed")
		}
		siteInfo, err := models.GetSiteInfo(upgradeReq.PartyId, upgradeReq.FederatedId)
		if err != nil {
			return e.ERROR_SELECT_DB_FAIL, err
		}
		deployComponent := models.DeployComponent{
			FederatedId: upgradeReq.FederatedId,
			PartyId:     upgradeReq.PartyId,
			ProductType: upgradeReq.ProductType,
			DeployType:  int(enum.DeployType_ANSIBLE),
			FateVersion: deploySiteList[0].FateVersion,
			IsValid:     int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil {
			return e.ERROR_UPGRADE_ALL_FAIL, nil
		}
		models.UpdateDeployComponent(data, deployComponent)
		for i := 0; i < len(componentVersionList); i++ {
			nodelist := k8s_service.GetNodeIp(enum.DeployType_ANSIBLE)
			if len(nodelist) == 0 {
				continue
			}
			deployComponent = models.DeployComponent{
				FederatedId:      upgradeReq.FederatedId,
				PartyId:          upgradeReq.PartyId,
				SiteName:         siteInfo.SiteName,
				ProductType:      upgradeReq.ProductType,
				FateVersion:      upgradeReq.FateVersion,
				JobId:            submitResponse.Data.JobId,
				ComponentVersion: componentVersionList[i].ComponentVersion,
				ComponentName:    componentVersionList[i].ComponentName,
				StartTime:        time.Now(),
				VersionIndex:     componentVersionList[i].VersionIndex,
				Address:          nodelist[0] + ":" + strconv.Itoa(models.GetDefaultPort(componentVersionList[i].ComponentName, enum.DeployType_ANSIBLE)),
				Status:           int(enum.SITE_RUN_STATUS_UNKNOWN),
				DeployStatus:     int(enum.ANSIBLE_DeployStatus_UNDER_INSTALLATION),
				DeployType:       int(enum.DeployType_ANSIBLE),
				IsValid:          int(enum.IS_VALID_YES),
				CreateTime:       time.Now(),
				UpdateTime:       time.Now(),
			}
			for j := 0; j < len(deployComponentList); j++ {
				if componentVersionList[i].ComponentName == deployComponentList[j].ComponentName {
					deployComponent.Address = deployComponentList[j].Address
					break
				}
			}
			err = models.AddDeployComponent(&deployComponent)
			if err != nil {
				logging.Error("Add deploy componenet failed")
			}
		}
		deployJob := models.DeployJob{
			JobId:       submitResponse.Data.JobId,
			JobType:     int(enum.JOB_TYPE_UPDATE),
			Status:      int(enum.JOB_STATUS_RUNNING),
			StartTime:   time.Now(),
			PartyId:     req.PartyId,
			DeployType:  int(enum.DeployType_ANSIBLE),
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			CreateTime:  time.Now(),
			UpdateTime:  time.Now(),
		}

		models.AddDeployJob(&deployJob)

		data = make(map[string]interface{})
		data["service_status"] = int(enum.SERVICE_STATUS_UNAVAILABLE)
		site := models.SiteInfo{PartyId: req.PartyId, Status: int(enum.SITE_STATUS_JOINED)}
		models.UpdateSiteByCondition(data, site)
	}

	return e.SUCCESS, nil
}

func CommitPackage(commitImagePullReq entity.CommitImagePullReq) (int, error) {
	var data = make(map[string]interface{})
	fateVersion := models.FateVersion{
		FateVersion: commitImagePullReq.FateVersion,
	}
	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil || len(fateVersionList) == 0 {
		return e.ERROR_UPDATE_COMPONENT_VERSION_FAIL, nil
	}

	data["fate_version"] = commitImagePullReq.FateVersion
	data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
	data["click_type"] = int(enum.AnsibleClickType_ACQUISITON)
	data["version_index"] = fateVersionList[0].VersionIndex

	deploySite := models.DeploySite{
		PartyId:     commitImagePullReq.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		IsValid:     int(enum.IS_VALID_YES),
		DeployType:  int(enum.DeployType_ANSIBLE),
	}
	models.UpdateDeploySite(data, deploySite)

	componentVersion := models.ComponentVersion{
		FateVersion: commitImagePullReq.FateVersion,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
	}
	var componentVersonMap = make(map[string]entity.ComponentVersionDetail)
	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	for i := 0; i < len(componentVersionList); i++ {
		port := models.GetDefaultPort(componentVersionList[i].ComponentName, enum.DeployType_ANSIBLE)
		nodelist := k8s_service.GetNodeIp(enum.DeployType_ANSIBLE)
		if len(nodelist) == 0 {
			continue
		}
		componentVersionDetail := entity.ComponentVersionDetail{
			Version: componentVersionList[i].ComponentVersion,
			Address: nodelist[0] + ":" + strconv.Itoa(port),
		}
		componentVersonMap[componentVersionList[i].ComponentName] = componentVersionDetail
		deployComponent := models.DeployComponent{
			PartyId:       commitImagePullReq.PartyId,
			ComponentName: componentVersionList[i].ComponentName,
			ProductType:   int(enum.PRODUCT_TYPE_FATE),
			DeployType:    int(enum.DeployType_ANSIBLE),
			IsValid:       int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil {
			continue
		}
		if len(deployComponentList) == 0 {

			deployComponent.FateVersion = commitImagePullReq.FateVersion
			deployComponent.ComponentVersion = componentVersionList[i].ComponentVersion
			deployComponent.ComponentName = componentVersionList[i].ComponentName
			deployComponent.Address = nodelist[0] + ":" + strconv.Itoa(port)
			deployComponent.VersionIndex = componentVersionList[i].VersionIndex
			deployComponent.DeployStatus = int(enum.ANSIBLE_DeployStatus_LOADED)
			deployComponent.CreateTime = time.Now()
			deployComponent.UpdateTime = time.Now()

			models.AddDeployComponent(&deployComponent)
		} else {
			var data = make(map[string]interface{})
			data["fate_version"] = componentVersionList[i].ComponentVersion
			data["address"] = nodelist[0] + ":" + strconv.Itoa(port)
			data["component_version"] = componentVersionList[i].ComponentVersion
			data["version_index"] = componentVersionList[i].VersionIndex
			data["update_time"] = time.Now()
			models.UpdateDeployComponent(data, deployComponent)
		}
	}
	return e.SUCCESS, nil
}

func Click(req entity.ClickReq) bool {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return false
	}
	if req.ClickType == int(enum.AnsibleClickType_PREPARE) || req.ClickType == int(enum.AnsibleClickType_SYSTEM_CHECK) || req.ClickType == int(enum.AnsibleClickType_ANSIBLE_INSTALL) {
		if conf.ClickType < req.ClickType {
			var data = make(map[string]interface{})
			data["click_type"] = req.ClickType
			models.UpdateKubenetesConf(data, conf)

			deploySite := models.DeploySite{
				IsValid:    int(enum.IS_VALID_YES),
				DeployType: int(enum.DeployType_ANSIBLE),
			}
			deploySiteList, _ := models.GetDeploySite(&deploySite)
			for i := 0; i < len(deploySiteList); i++ {
				if deploySiteList[i].ClickType < req.ClickType {
					data["click_type"] = req.ClickType
					models.UpdateDeploySite(data, deploySite)
				}
			}
			return true
		}
		return false
	}
	deploySite := models.DeploySite{
		FederatedId: req.FederatedId,
		PartyId:     req.PartyId,
		ProductType: req.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return false
	}
	if len(deploySiteList) == 0 {
		if conf.Id > 0 {
			deploySite := models.DeploySite{
				PartyId:            req.PartyId,
				ProductType:        int(enum.PRODUCT_TYPE_FATE),
				Status:             int(enum.SITE_RUN_STATUS_UNKNOWN),
				ClickType:          int(enum.AnsibleClickType_CONNECT),
				KubenetesId:        conf.Id,
				DeployStatus:       int(enum.ANSIBLE_DeployStatus_UNKNOWN),
				SingleTest:         int(enum.TEST_STATUS_WAITING),
				ToyTest:            int(enum.TEST_STATUS_WAITING),
				MinimizeNormalTest: int(enum.TEST_STATUS_WAITING),
				MinimizeFastTest:   int(enum.TEST_STATUS_WAITING),
				ToyTestOnly:        int(enum.ToyTestOnly_NO_TEST),
				ToyTestOnlyRead:    int(enum.ToyTestOnlyTypeRead_YES),
				DeployType:         int(enum.DeployType_ANSIBLE),
				IsValid:            int(enum.IS_VALID_YES),
				CreateTime:         time.Now(),
				UpdateTime:         time.Now(),
			}
			if conf.ClickType > 0 {
				deploySite.ClickType = conf.ClickType
			}
			deploySiteList, _ := models.GetDeploySite(&deploySite)
			if len(deploySiteList) == 0 {
				models.AddDeploySite(&deploySite)
			}
			return true
		}
		return false
	}
	if deploySiteList[0].ClickType <= req.ClickType {
		var data = make(map[string]interface{})
		data["click_type"] = req.ClickType
		err := models.UpdateDeploySite(data, deploySite)
		if err != nil {
			return false
		}
	}
	if req.ClickType == int(enum.AnsibleClickType_TEST) {
		deployComponent := models.DeployComponent{
			FederatedId: req.FederatedId,
			PartyId:     req.PartyId,
			ProductType: req.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}

		var data = make(map[string]interface{})
		data["duration"] = time.Now().UnixNano()/1e6 - deploySiteList[0].CreateTime.UnixNano()/1e6
		data["finish_time"] = time.Now()
		data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_SUCCESS)
		data["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
		models.UpdateDeployComponent(data, deployComponent)

		deploySite := models.DeploySite{
			FederatedId: req.FederatedId,
			PartyId:     req.PartyId,
			ProductType: req.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		models.UpdateDeploySite(data, deploySite)

		siteInfo := models.SiteInfo{
			FederatedId:   req.FederatedId,
			PartyId:       req.PartyId,
			ServiceStatus: int(enum.SERVICE_STATUS_AVAILABLE),
		}
		models.UpdateSite(&siteInfo)
	}
	return true
}

func Update(updateReq entity.UpdateReq) (int, error) {
	if len(updateReq.ComponentName) == 0 {
		logging.Debug("update component failed")
		return e.INVALID_PARAMS, nil
	}
	deployComponent := models.DeployComponent{
		FederatedId:   updateReq.FederatedId,
		PartyId:       updateReq.PartyId,
		ProductType:   updateReq.ProductType,
		ComponentName: updateReq.ComponentName,
		DeployType:    int(enum.DeployType_ANSIBLE),
		IsValid:       int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil || len(deployComponentList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_LOADED) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_INSTALLED) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_UNDER_INSTALLATION) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_INSTALLING) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_TEST_FAILED) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_IN_TESTING) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_INSTALLED_FAILED) ||
		deployComponentList[0].DeployStatus == int(enum.ANSIBLE_DeployStatus_TEST_PASSED) {
		ret := k8s_service.CheckNodeIp(updateReq, enum.DeployType_ANSIBLE)
		if ret == false {
			return e.ERROR_IP_NOT_COURRECT_FAIL, err
		}

		var data = make(map[string]interface{})
		data["address"] = updateReq.Address
		err = models.UpdateDeployComponent(data, deployComponent)
		if err != nil {
			logging.Error("update component failed")
			return e.ERROR_UPDATE_DB_FAIL, err
		}

		data = make(map[string]interface{})
		data["click_type"] = int(enum.AnsibleClickType_ACQUISITON)
		deploySite := models.DeploySite{
			FederatedId: updateReq.FederatedId,
			PartyId:     updateReq.PartyId,
			ProductType: updateReq.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		models.UpdateDeploySite(data, deploySite)
	}
	return e.SUCCESS, nil
}

func GetLog(ansibleLog entity.AnsibleLog) (*entity.AnsibleLogResp, error) {
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleLogUri), ansibleLog, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var ansibleLogResp entity.AnsibleLogResponse
	err = json.Unmarshal([]byte(result.Body), &ansibleLogResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	var resp entity.AnsibleLogResp
	if ansibleLogResp.Code == e.SUCCESS {
		resp.Content = ansibleLogResp.Data
		resp.Total = len(ansibleLogResp.Data)
	}

	return &resp, nil
}

func GetAutoTestList(autoTestListReq entity.AutoTestListReq) (*entity.AutoTestListRespItem, error) {
	deploySite := models.DeploySite{
		PartyId:     autoTestListReq.PartyId,
		ProductType: autoTestListReq.ProductType,
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return nil, err
	}
	autoTest := models.AutoTest{
		PartyId:     autoTestListReq.PartyId,
		ProductType: autoTestListReq.ProductType,
		FateVersion: deploySiteList[0].FateVersion,
	}
	autoTestList, err := models.GetAutoTest(autoTest)
	if err != nil {
		return nil, err
	}
	var autoTestListResp entity.AutoTestListRespItem
	var data = make(map[string][]entity.AutoTestItem)
	for i := 0; i < len(autoTestList); i++ {
		autoTestItem := entity.AutoTestItem{
			TestItem: autoTestList[i].TestItem,
			Duration: int(autoTestList[i].EndTime.UnixNano()/1e6 - autoTestList[i].StartTime.UnixNano()/1e6),
			Status:   entity.IdPair{autoTestList[i].Status, enum.GetTestStatusString(enum.TestStatusType(autoTestList[i].Status))},
		}
		if autoTestList[i].Status == int(enum.TEST_STATUS_TESTING) {
			autoTestItem.Duration = -1
		}
		if autoTestList[i].TestItem == enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_SINGLE)) ||
			autoTestList[i].TestItem == enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_NORMAL)) ||
			autoTestList[i].TestItem == enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_FAST)) ||
			autoTestList[i].TestItem == enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_TOY)) {
			var autoTestItemList []entity.AutoTestItem
			autoTestItemList = append(autoTestItemList, autoTestItem)
			data[autoTestList[i].TestItem] = autoTestItemList
		} else {
			_, ok := data[enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_POD))]
			if ok {
				data[enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_POD))] = append(data[enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_POD))], autoTestItem)
			} else {
				var autoTestItemList []entity.AutoTestItem
				autoTestItemList = append(autoTestItemList, autoTestItem)
				data[enum.GetTestItemString(enum.TestItemType(enum.TEST_ITEM_POD))] = autoTestItemList
			}
		}
	}
	autoTestListResp.AutoTest = data
	return &autoTestListResp, nil
}

func AutoTest(autoTestReq entity.AnsibleAutoTestReq) (int, error) {

	test := models.AutoTest{
		PartyId: autoTestReq.PartyId,
	}
	testList, err := models.GetAutoTest(test)
	if err != nil {
		return e.ERROR_AUTO_TEST_FAIL, err
	}
	testValueMap := map[string]bool{"fate": true}
	for i := 0; i < len(testList); i++ {
		testValue := true
		if testList[i].Status != int(enum.TEST_STATUS_YES) {
			testValue = false
		}
		key := testList[i].TestItem
		if testList[i].TestItem == "clustermanager" ||
			testList[i].TestItem == "mysql" ||
			testList[i].TestItem == "nodemanager" ||
			testList[i].TestItem == "fateflow" ||
			testList[i].TestItem == "rollsite" ||
			testList[i].TestItem == "fateboard" {
			if !testValue {
				testValueMap["fate"] = testValue
			}
		} else {
			testValueMap[key] = testValue
		}
	}
	value, ok := testValueMap["fate"]
	if ok && autoTestReq.IfOnly {
		DoTestOnly(test)
	} else if !ok || !value {
		var data = make(map[string]interface{})
		data["status"] = int(enum.TEST_STATUS_WAITING)
		data["start_time"] = time.Now()
		req := entity.ConnectAnsible{PartyId: autoTestReq.PartyId}
		result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/status"), req, nil)
		if err != nil || result == nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			return e.ERROR_HTTP_FAIL, err
		}
		var ansibleAutoTestResp entity.AnsibleAutoTestResp
		err = json.Unmarshal([]byte(result.Body), &ansibleAutoTestResp)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return e.ERROR_PARSE_JSON_ERROR, err
		}
		toytest := true
		var autotest models.AutoTest
		if ansibleAutoTestResp.Code == e.SUCCESS {
			for i := 0; i < len(ansibleAutoTestResp.Data.List); i++ {
				ansibleAutoTestRespItem := ansibleAutoTestResp.Data.List[i]
				data["end_time"] = time.Now()
				autotest = models.AutoTest{
					PartyId:  autoTestReq.PartyId,
					TestItem: ansibleAutoTestRespItem.Name,
				}
				var deployData = make(map[string]interface{})
				if ansibleAutoTestRespItem.Status == "running" {
					data["status"] = int(enum.TEST_STATUS_YES)
					deployData["deploy_status"] = int(enum.ANSIBLE_DeployStatus_IN_TESTING)
				} else {
					deployData["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
					data["status"] = int(enum.TEST_STATUS_NO)
					toytest = false
				}
				deployComponent := models.DeployComponent{
					PartyId:       autoTestReq.PartyId,
					ProductType:   int(enum.PRODUCT_TYPE_FATE),
					ComponentName: ansibleAutoTestRespItem.Name,
					DeployType:    int(enum.DeployType_ANSIBLE),
					IsValid:       int(enum.IS_VALID_YES),
				}
				models.UpdateAutoTest(data, autotest)
				models.UpdateDeployComponent(deployData, deployComponent)
			}
		} else {
			var deployData = make(map[string]interface{})
			deployData["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
			data["status"] = int(enum.TEST_STATUS_NO)
			deployComponent := models.DeployComponent{
				PartyId:     autoTestReq.PartyId,
				ProductType: int(enum.PRODUCT_TYPE_FATE),
				DeployType:  int(enum.DeployType_ANSIBLE),
				IsValid:     int(enum.IS_VALID_YES),
			}
			autotest.PartyId = autoTestReq.PartyId
			models.UpdateAutoTest(data, autotest)
			models.UpdateDeployComponent(deployData, deployComponent)
			return e.SUCCESS, nil
		}
		deploySite := models.DeploySite{
			PartyId:     autoTestReq.PartyId,
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			DeployType:  int(enum.DeployType_ANSIBLE),
			IsValid:     int(enum.IS_VALID_YES),
		}
		if toytest {
			DoTest(autotest, "single")
		} else {
			data = make(map[string]interface{})
			data["single_test"] = int(enum.TEST_STATUS_NO)
			data["toy_test"] = int(enum.TEST_STATUS_NO)
			data["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
			data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
			models.UpdateDeploySite(data, deploySite)

			autotest.TestItem = ""
			autotest.PartyId = autoTestReq.PartyId
			data = make(map[string]interface{})
			data["status"] = int(enum.TEST_STATUS_NO)
			models.UpdateAutoTest(data, autotest)
		}
	} else if !testValueMap["Single Test"] {
		DoTest(test, "single")
	} else if !testValueMap["Toy Test"] {
		DoTest(test, "toy")
	} else if !testValueMap["Minimize Fast Test"] {
		DoTest(test, "fast")
	} else if !testValueMap["Minimize Normal Test"] {
		DoTest(test, "normal")
	}
	return e.SUCCESS, nil
}

func DoTest(autotest models.AutoTest, testitem string) {
	deploySite := models.DeploySite{
		PartyId:     autotest.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
	}

	deployComponent := models.DeployComponent{
		PartyId:       autotest.PartyId,
		ComponentName: "fateflow",
		ProductType:   int(enum.PRODUCT_TYPE_FATE),
		DeployType:    int(enum.DeployType_ANSIBLE),
		IsValid:       int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return
	}
	var data = make(map[string]interface{})
	if len(deployComponentList) > 0 {
		address := strings.Split(deployComponentList[0].Address, ":")
		if len(address) == 2 {
			ansibleSingleTestReq := entity.AnsibleSingleTestReq{
				PartyId: autotest.PartyId,
				Ip:      address[0],
			}
			TestReq := entity.AnsibleToyTestReq{
				GuestPartyId: autotest.PartyId,
				HostPartyId:  setting.DeploySetting.TestPartyId,
				Ip:           address[0],
				WorkMode:     setting.DeploySetting.WorkMode,
			}
			MinReq := entity.AnsibleMinTestReq{
				ArbiterPartyId: setting.DeploySetting.TestPartyId,
				GuestPartyId:   deploySite.PartyId,
				HostPartyId:    setting.DeploySetting.TestPartyId,
				Ip:             address[0],
			}
			var commresp entity.AnsibleCommResp
			if testitem == "single" {
				result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+testitem), ansibleSingleTestReq, nil)
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return
				}
			} else if testitem == "toy" {

				result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+testitem), TestReq, nil)
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return
				}
			} else if testitem == "fast" || testitem == "normal" {
				result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+testitem), MinReq, nil)
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return
				}
			}

			if commresp.Code == e.SUCCESS {
				if testitem == "single" {
					data["status"] = int(enum.TEST_STATUS_TESTING)
					data["start_time"] = time.Now()
					autotest.TestItem = "Single Test"
					models.UpdateAutoTest(data, autotest)

					data["status"] = int(enum.TEST_STATUS_WAITING)
					autotest.TestItem = "Toy Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["single_test"] = int(enum.TEST_STATUS_TESTING)
					data["toy_test"] = int(enum.TEST_STATUS_WAITING)
					data["minimize_fast_test"] = int(enum.TEST_STATUS_WAITING)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_WAITING)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_IN_TESTING)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "toy" {
					data["status"] = int(enum.TEST_STATUS_TESTING)
					autotest.TestItem = "Toy Test"
					models.UpdateAutoTest(data, autotest)

					data["status"] = int(enum.TEST_STATUS_WAITING)
					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["toy_test"] = int(enum.TEST_STATUS_TESTING)
					data["minimize_fast_test"] = int(enum.TEST_STATUS_WAITING)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_WAITING)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_IN_TESTING)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "fast" {
					data["status"] = int(enum.TEST_STATUS_TESTING)
					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					data["status"] = int(enum.TEST_STATUS_WAITING)
					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)
					data = make(map[string]interface{})
					data["minimize_fast_test"] = int(enum.TEST_STATUS_TESTING)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_WAITING)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_IN_TESTING)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "normal" {
					data["status"] = int(enum.TEST_STATUS_TESTING)
					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["minimize_normal_test"] = int(enum.TEST_STATUS_TESTING)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_IN_TESTING)
					models.UpdateDeploySite(data, deploySite)
				}
			} else {
				if testitem == "single" {
					data["status"] = int(enum.TEST_STATUS_NO)
					data["start_time"] = time.Now()
					autotest.TestItem = "Single Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Toy Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["single_test"] = int(enum.TEST_STATUS_NO)
					data["toy_test"] = int(enum.TEST_STATUS_NO)
					data["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "toy" {
					data["status"] = int(enum.TEST_STATUS_NO)
					autotest.TestItem = "Toy Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["toy_test"] = int(enum.TEST_STATUS_NO)
					data["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "fast" {
					data["status"] = int(enum.TEST_STATUS_NO)
					autotest.TestItem = "Minimize Fast Test"
					models.UpdateAutoTest(data, autotest)

					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
					data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
					models.UpdateDeploySite(data, deploySite)
				} else if testitem == "normal" {
					data["status"] = int(enum.TEST_STATUS_NO)
					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
					models.UpdateDeploySite(data, deploySite)
				}
			}
		}
	}
}

func DoTestOnly(autotest models.AutoTest) {
	deploySite := models.DeploySite{
		PartyId:     autotest.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
	}

	deployComponent := models.DeployComponent{
		PartyId:       autotest.PartyId,
		ComponentName: "fateflow",
		ProductType:   int(enum.PRODUCT_TYPE_FATE),
		DeployType:    int(enum.DeployType_ANSIBLE),
		IsValid:       int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return
	}
	var data = make(map[string]interface{})
	if len(deployComponentList) > 0 {
		address := strings.Split(deployComponentList[0].Address, ":")
		if len(address) == 2 {
			TestReq := entity.AnsibleToyTestReq{
				GuestPartyId: autotest.PartyId,
				HostPartyId:  setting.DeploySetting.TestPartyId,
				Ip:           address[0],
				WorkMode:     setting.DeploySetting.WorkMode,
			}

			var commresp entity.AnsibleCommResp
			result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/toy"), TestReq, nil)
			err = json.Unmarshal([]byte(result.Body), &commresp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
			if commresp.Code == e.SUCCESS {
				data = make(map[string]interface{})
				data["toy_test_only"] = int(enum.TEST_STATUS_TESTING)
				data["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_NO)
				models.UpdateDeploySite(data, deploySite)
			}
		}
	}
}
