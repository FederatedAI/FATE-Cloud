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
	"fate.manager/services/k8s_service"
	"fate.manager/services/version_service"
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
	if connectResp.Code == e.SUCCESS || true {
		kubenetesConf := models.KubenetesConf{
			KubenetesUrl: ansibleReq.Url,
			DeployType:   int(enum.DeployType_ANSIBLE),
			ClickType:    int(enum.AnsibleClickType_CONNECT),
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
			ClickType:          int(enum.AnsibleClickType_CONNECT),
			KubenetesId:        kubenetesConf.Id,
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
		if conf.Id > 0 {
			deploySite.KubenetesId = conf.Id
		}

		if kubenetesConf.ClickType > 0 {
			deploySite.ClickType = conf.ClickType
		}
		deploySiteList, _ := models.GetDeploySite(&deploySite)
		if len(deploySiteList) == 0 {
			models.AddDeploySite(&deploySite)
		}
		for i := 0; i < len(connectResp.Data.List); i++ {
			connectItem := connectResp.Data.List[i]
			address := ""
			for j := 0; j < len(connectItem.Ips); j++ {
				ipport := fmt.Sprintf("%s:%d", connectItem.Ips[j], connectItem.Port)
				if connectItem.Module == "fateflow" {
					ipport = fmt.Sprintf("%s:%d", connectItem.Ips[j], connectItem.GrpcPort)
				}
				address = fmt.Sprintf("%s;%s", ipport, address)
			}
			deployComponent := models.DeployComponent{
				PartyId:          ansibleReq.PartyId,
				ProductType:      int(enum.PRODUCT_TYPE_FATE),
				ComponentName:    connectItem.Module,
				ComponentVersion: connectResp.Data.FateVersion,
				Address:          address,
				DeployStatus:     int(enum.ANSIBLE_DeployStatus_INSTALLED),
				DeployType:       int(enum.DeployType_ANSIBLE),
				IsValid:          int(enum.IS_VALID_YES),
				CreateTime:       time.Now(),
				UpdateTime:       time.Now(),
			}
			if connectItem.Status == "RUNNING" {
				deployComponent.Status = int(enum.SITE_RUN_STATUS_RUNNING)
			} else if connectItem.Status == "STOPPED" {
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
		}
		return e.SUCCESS, nil
	}
	return e.ERROR_CONNECT_ANSIBLE_FAIL, nil
}

func Prepare(prepareReq entity.PrepareReq) (int, error) {
	if len(prepareReq.ManagerNode) == 0 || len(prepareReq.ControlNode) == 0 {
		return e.INVALID_PARAMS, nil
	}
	prepareReqJson, _ := json.Marshal(prepareReq)
	kubenetesConf := models.KubenetesConf{
		DeployType: int(enum.DeployType_ANSIBLE),
	}
	var data = make(map[string]interface{})
	data["node_list"] = string(prepareReqJson)
	models.UpdateKubenetesConf(data, &kubenetesConf)
	return e.SUCCESS, nil
}

func CheckSystem(checkSystem entity.CheckSystem) (int, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	var prepareReq entity.PrepareReq
	err = json.Unmarshal([]byte(conf.NodeList), &prepareReq)
	if err != nil {
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	checkSystem.ManagerNode = prepareReq.ManagerNode
	startTime := time.Now().UnixNano()
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePrepareUri), checkSystem, nil)
	endTime := time.Now().UnixNano()
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var ansiblePrepareResp entity.AnsibleCheckResp
	err = json.Unmarshal([]byte(result.Body), &ansiblePrepareResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if ansiblePrepareResp.Code == e.SUCCESS {

		for i := 0; i < len(ansiblePrepareResp.Data); i++ {
			ansiblePrepareItem := &ansiblePrepareResp.Data[i]
			if len(checkSystem.CheckName) > 0 {
				var list []entity.AnsiblePrepareItem
				err = json.Unmarshal([]byte(conf.AnsibleCheck), &list)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return e.ERROR_PARSE_JSON_ERROR, err
				}
				for j := 0; j < len(list); j++ {
					if list[j].Ip == ansiblePrepareItem.Ip {
						for k := 0; k < len(list[j].List); k++ {
							if list[j].List[j].Name == ansiblePrepareItem.List[0].Name {
								list[i].List[j].Status = ansiblePrepareItem.List[0].Status
								if ansiblePrepareItem.List[0].Status == "warning" || ansiblePrepareItem.List[0].Status == "waring" {
									list[i].List[j].Status = "success"
								}
								list[i].List[j].Duration = (endTime - startTime) / 1e6
								list[i].List[j].Details = ansiblePrepareItem.List[0].Details
								break
							}
						}
						break
					}
				}
			} else {
				for j := 0; j < len(ansiblePrepareItem.List); j++ {
					if ansiblePrepareItem.List[j].Status == "warning" || ansiblePrepareItem.List[j].Status == "waring" {
						ansiblePrepareItem.List[j].Status = "success"
					}
					ansiblePrepareItem.List[j].Duration = (endTime - startTime) / 1e6
				}
			}
		}
		ansiblePrepareItemJson, _ := json.Marshal(ansiblePrepareResp.Data)
		kubenetesConf := models.KubenetesConf{
			DeployType: int(enum.DeployType_ANSIBLE),
		}
		var data = make(map[string]interface{})
		data["ansible_check"] = string(ansiblePrepareItemJson)
		models.UpdateKubenetesConf(data, &kubenetesConf)

		return e.SUCCESS, nil
	}
	return e.GET_CHECK_SYSYTEM_LIST_FAIL, nil
}
func GetCheck(checkSystemReq entity.CheckSystemReq) ([]entity.Prepare, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return nil, err
	}
	if conf.Id == 0 {
		return nil, err
	}
	if len(conf.AnsibleCheck) > 0 {
		var Data []entity.AnsiblePrepareItem
		err = json.Unmarshal([]byte(conf.AnsibleCheck), &Data)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return nil, err
		}
		var list []entity.Prepare
		for i := 0; i < len(Data); {
			ansiblePrepareItem := Data[i]
			if ansiblePrepareItem.Ip == checkSystemReq.Ip {
				list = ansiblePrepareItem.List
				break
			}
		}
		return list, nil
	}
	return nil, nil
}

func GetAnsibleList() ([]entity.AnsibleListItem, error) {
	var prepareReq entity.PrepareReq
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return nil, err
	}
	var ansiblelist []entity.AnsibleListItem
	if len(conf.NodeList) > 0 {
		err := json.Unmarshal([]byte(conf.NodeList), &prepareReq)
		if err != nil {
			return nil, err
		}
		ansibleListItem := entity.AnsibleListItem{
			Ip:       prepareReq.ControlNode,
			Duration: conf.AnsibleDuration,
			Status:   conf.AnsibleStatus,
		}
		ansiblelist = append(ansiblelist, ansibleListItem)
	}
	return ansiblelist, nil
}

func StartDeployAnsible() (int, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	var deployAnsibleReq entity.DeployAnsibleReq
	var PrepareReq entity.PrepareReq
	err = json.Unmarshal([]byte(conf.NodeList), &PrepareReq)
	if err != nil {
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	deployAnsibleReq.ControlNode = PrepareReq.ControlNode
	startTime := time.Now()
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleInstallUri), deployAnsibleReq, nil)
	endTime := time.Now()
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var deployAnsibleResp entity.DeployAnsibleResp
	err = json.Unmarshal([]byte(result.Body), &deployAnsibleResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if deployAnsibleResp.Code == e.SUCCESS {

		kubenetesConf := models.KubenetesConf{
			DeployType: int(enum.DeployType_ANSIBLE),
		}
		var data = make(map[string]interface{})
		data["ansible_status"] = deployAnsibleResp.Data.Status
		data["ansible_duration"] = endTime.UnixNano()/1e6 - startTime.UnixNano()/1e6
		models.UpdateKubenetesConf(data, &kubenetesConf)
		if deployAnsibleResp.Data.Status == "success" {
			return e.SUCCESS, nil
		}
	}
	return e.ERROR_DEPLOY_ANSIBLE_FAIL, nil
}

func LocalUpload(localUploadReq entity.LocalUploadReq) (int, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleLocalUploadUri), localUploadReq, nil)
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

func GetComponentList(connectAnsible entity.ConnectAnsible) (*entity.AcquireResp, error) {
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
	Enable bool     `json:"enable"`
	Ip     []string `json:"ips"`
	Port   int      `json:"port"`
}
type Base struct {
	Ip []string `json:"ips"`
}
type Java struct {
	Enable bool     `json:"enable"`
	Ip     []string `json:"ips"`
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
	Enable bool   `json:"enable"`
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
	Base           Base         `json:"base"`
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
		arr1 := strings.Split(item.Address, ";")
		var port int
		var address []string
		for j := 0; j < len(arr1); j++ {
			arr2 := strings.Split(arr1[j], ":")
			port, _ = strconv.Atoi(arr2[1])
			address = append(address, arr2[0])
		}
		IpNode := IpNode{
			Enable: true,
			Ip:     address,
			Port:   port,
		}
		Java := Java{
			Ip: address,
		}
		if item.ComponentName == "mysql" {
			req.Modules.Mysql.IpNode = IpNode
			req.Modules.Mysql.Password = "fate_pass"
			req.Modules.Mysql.User = "fate_user"
			req.Modules.Base.Ip = address
		} else if item.ComponentName == "clustermanager" {
			req.Modules.Clustermanager = IpNode
		} else if item.ComponentName == "nodemanager" {
			req.Modules.Nodemanager = IpNode
		} else if item.ComponentName == "fateflow" {
			req.Modules.Flow.Ip = address
			req.Modules.Flow.Dbname = "fae_flow"
			req.Modules.Flow.Enable = true
			req.Modules.Flow.GrpcPort = 9370
			req.Modules.Flow.HttpPort = 9380
			req.Modules.Python.Enable = true
			req.Modules.Python.Ip = address
		} else if item.ComponentName == "fateboard" {
			req.Modules.Fateboard = Fateboard{
				Dbname: "fate_flow",
				IpNode: IpNode,
			}
			req.Modules.Java.Ip = IpNode.Ip
			req.Modules.Java = Java
			req.Modules.Supervisor.Ip = address
			req.Modules.Supervisor.Enable = true
		} else if item.ComponentName == "rollsite" {
			req.Modules.Rollsite.IpNode = IpNode
			req.Modules.Rollsite.Port = port
			rule := Rule{
				Name: "default",
				Ip:   setting.KubenetesSetting.ExchangeIp,
				Port: setting.KubenetesSetting.ExchangePort,
			}
			req.Modules.Rollsite.DefaultRules = append(req.Modules.Rollsite.DefaultRules, rule)
			req.Modules.Rollsite.Rules = append(req.Modules.Rollsite.Rules, rule)
			req.Modules.Eggroll.Ip = address
			req.Modules.Eggroll.Dbname = "eggroll_meta"
			req.Modules.Eggroll.Egg = setting.KubenetesSetting.SessionProcessorsPerNode
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
		DeployType:  int(enum.DeployType_ANSIBLE),
		IsValid:     int(enum.IS_VALID_YES),
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
	var data = make(map[string]interface{})
	data["is_valid"] = int(enum.IS_VALID_NO)
	models.UpdateDeploySite(data, deploySite)
	deploySite = models.DeploySite{
		FederatedId:  deploySiteList[0].FederatedId,
		PartyId:      deploySiteList[0].PartyId,
		ProductType:  deploySiteList[0].ProductType,
		FateVersion:  upgradeReq.FateVersion,
		DeployStatus: int(enum.ANSIBLE_DeployStatus_UNKNOWN),
		Status:       int(enum.SITE_RUN_STATUS_UNKNOWN),
		KubenetesId:  deploySiteList[0].KubenetesId,
		IsValid:      int(enum.IS_VALID_YES),
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
			ComponentVersion: componentVersionList[i].ComponentVersion,
			ComponentName:    componentVersionList[i].ComponentName,
			StartTime:        time.Now(),
			VersionIndex:     fateVersonList[0].VersionIndex,
			Address:          nodelist[0] + ":" + strconv.Itoa(version_service.GetDefaultPort(componentVersionList[i].ComponentName, enum.DeployType_ANSIBLE)),
			DeployStatus:     int(enum.ANSIBLE_DeployStatus_LOADED),
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
	return e.SUCCESS, nil
}

func CommitPackage(commitImagePullReq entity.CommitImagePullReq) (int, error) {
	var data = make(map[string]interface{})
	data["fate_version"] = commitImagePullReq.FateVersion
	data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
	data["click_type"] = int(enum.AnsibleClickType_ACQUISITON)
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
		port := version_service.GetDefaultPort(componentVersionList[i].ComponentName, enum.DeployType_ANSIBLE)
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
			PartyId:          commitImagePullReq.PartyId,
			ProductType:      int(enum.PRODUCT_TYPE_FATE),
			FateVersion:      commitImagePullReq.FateVersion,
			ComponentVersion: componentVersionList[i].ComponentVersion,
			ComponentName:    componentVersionList[i].ComponentName,
			Address:          nodelist[0] + ":" + strconv.Itoa(port),
			VersionIndex:     componentVersionList[i].VersionIndex,
			DeployStatus:     int(enum.ANSIBLE_DeployStatus_LOADED),
			DeployType:       int(enum.DeployType_ANSIBLE),
			Status:           int(enum.ANSIBLE_DeployStatus_UNKNOWN),
			IsValid:          int(enum.IS_VALID_YES),
			CreateTime:       time.Now(),
			UpdateTime:       time.Now(),
		}
		models.AddDeployComponent(&deployComponent)
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
		if conf.Id >0 {
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

		ret := k8s_service.CheckNodeIp(updateReq.Address, enum.DeployType_ANSIBLE)
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
	toytest := false
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
				if ansibleAutoTestRespItem.Name == "fateflow" {
					toytest = true
				}
				deployData["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_PASSED)
			} else {
				deployData["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
				data["status"] = int(enum.TEST_STATUS_NO)
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
		deployComponent := models.DeployComponent{
			PartyId:       autoTestReq.PartyId,
			ComponentName: "fateflow",
			ProductType:   int(enum.PRODUCT_TYPE_FATE),
			DeployType:    int(enum.DeployType_ANSIBLE),
			IsValid:       int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil {
			return e.ERROR_SELECT_DB_FAIL, nil
		}
		if len(deployComponentList) > 0 {
			address := strings.Split(deployComponentList[0].Address, ":")
			if len(address) == 2 {
				ansibleSingleTestReq := entity.AnsibleSingleTestReq{
					PartyId: autoTestReq.PartyId,
					Ip:      address[0],
				}
				result, err = http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/single"), ansibleSingleTestReq, nil)
				var commresp entity.AnsibleCommResp
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					//return e.ERROR_PARSE_JSON_ERROR, err
				}
				if commresp.Code == e.SUCCESS {
					data["status"] = int(enum.TEST_STATUS_TESTING)
					data["start_time"] = time.Now()
					autotest.TestItem = "Single Test"
					models.UpdateAutoTest(data, autotest)

					data = make(map[string]interface{})
					data["single_test"] = int(enum.TEST_STATUS_TESTING)
					models.UpdateDeploySite(data, deploySite)
				}
			}
		}
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
	return e.SUCCESS, nil
}
