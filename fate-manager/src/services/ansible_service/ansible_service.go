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
		deploySite.ClickType = conf.ClickType
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

func CheckSystem() (int, error) {
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
	startTime := time.Now().UnixNano()
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePrepareUri), prepareReq, nil)
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
		for i := 0; i < len(ansiblePrepareResp.Data); {
			ansiblePrepareItem := &ansiblePrepareResp.Data[i]
			for j := 0; j < len(ansiblePrepareItem.List); j++ {
				ansiblePrepareItem.List[j].Duration = (endTime - startTime) / 1e6
			}
			break
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

func GetAnsibleList()(*entity.AnsibleList,error){

	return nil, nil
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
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleInstallUri), deployAnsibleReq, nil)
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
		return e.SUCCESS, nil
	}
	return e.ERROR_DEPLOY_ANSIBLE_FAIL, nil
}

func LocalUpload(localUploadReq entity.LocalUploadReq) (*entity.AcquireResp, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return nil, err
	}
	if conf.Id == 0 {
		return nil, err
	}
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleLocalUpload), localUploadReq, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var ansibleInstallListResponse entity.AnsibleInstallListResponse
	err = json.Unmarshal([]byte(result.Body), &ansibleInstallListResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	if ansibleInstallListResponse.Code == e.SUCCESS {
		UpdatePackage(localUploadReq.PartyId, ansibleInstallListResponse.Data)
		return &ansibleInstallListResponse.Data, nil
	}
	return nil, nil
}

func AutoAcquire(autoAcquireReq entity.AutoAcquireReq) (*entity.AcquireResp, error) {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return nil, err
	}
	if conf.Id == 0 {
		return nil, err
	}
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleAutoAcquire), autoAcquireReq, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var ansibleInstallListResponse entity.AnsibleInstallListResponse
	err = json.Unmarshal([]byte(result.Body), &ansibleInstallListResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	if ansibleInstallListResponse.Code == e.SUCCESS {
		UpdatePackage(autoAcquireReq.PartyId, ansibleInstallListResponse.Data)
		return &ansibleInstallListResponse.Data, nil
	}
	return nil, nil
}

func UpdatePackage(partyId int, Data entity.AcquireResp) {
	deploySite := models.DeploySite{
		PartyId:      partyId,
		ProductType:  int(enum.PRODUCT_TYPE_FATE),
		FateVersion:  Data.FateVersion,
		DeployStatus: int(enum.ANSIBLE_DeployStatus_LOADED),
		IsValid:      int(enum.IS_VALID_YES),
		DeployType:   int(enum.DeployType_ANSIBLE),
	}
	var data = make(map[string]interface{})
	data["fate_version"] = Data.FateVersion
	data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
	data["update_time"] = time.Now()
	models.UpdateDeploySite(data, deploySite)
	for i := 0; i < len(Data.AcquireRespItemList); i++ {
		AcquireRespItem := Data.AcquireRespItemList[i]
		deployComponent := models.DeployComponent{
			PartyId:          partyId,
			ProductType:      int(enum.PRODUCT_TYPE_FATE),
			FateVersion:      Data.FateVersion,
			ComponentVersion: AcquireRespItem.ComponentVersion,
			ComponentName:    AcquireRespItem.Item,
			Address:          "",
			VersionIndex:     0,
			DeployStatus:     int(enum.ANSIBLE_DeployStatus_LOADED),
			DeployType:       int(enum.DeployType_ANSIBLE),
			IsValid:          int(enum.IS_VALID_YES),
			CreateTime:       time.Now(),
			UpdateTime:       time.Now(),
		}
		models.AddDeployComponent(&deployComponent)
	}
}

type IpNode struct {
	Ip   []string `json:"ips"`
	Port int      `json:"port"`
}
type AnsibleMysql struct {
	IpNode
	Password string `json:"dbpasswd"`
	User     string `json:"dbuser"`
}
type AnsibleFlow struct {
	Ip       []string `json:"nodeSelector"`
	HttpPort int      `json:"httpport"`
	GrpcPort int      `json:"grpcPort"`
}
type Rule struct {
	Name string `json:"name"`
	Ip   string `json:"ip"`
	Port int    `json:"port"`
}
type Rollsite struct {
	IpNode
	Port  int    `json:"port"`
	Rules []Rule `json:"default_rules"`
}
type Modules struct {
	Mysql          AnsibleMysql `json:"mysql"`
	Clustermanager IpNode       `json:"clustermanager"`
	Nodemanager    IpNode       `json:"nodemanager"`
	Flow           IpNode       `json:"fate_flow"`
	Fateboard      IpNode       `json:"fateboard"`
	Java           IpNode       `json:"java"`
	Python         IpNode       `json:"python"`
	Eggroll        IpNode       `json:"eggroll"`
	Rollsite       Rollsite     `json:"rollsite"`
}
type ClusterInstallByAnsible struct {
	PartyId     int     `json:"partyId"`
	Role        string  `json:"role"`
	FateVersion string  `json:"version"`
	Modules     Modules `json:"modules"`
}

func InstallByAnsible(installReq entity.InstallReq, deploySite models.DeploySite) (int, error) {
	if deploySite.DeployStatus != int(enum.ANSIBLE_DeployStatus_LOADED) {
		return e.ERROR_INSTALL_ALL_FAIL, nil
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
		FateVersion: deploySite.FateVersion,
	}
	var modules []string
	for i := 0; i < len(deployComponentList); i++ {

		item := deployComponentList[i]
		modules = append(modules, item.ComponentName)
		arr := strings.Split(item.Address, ":")
		port, _ := strconv.Atoi(arr[1])
		IpNode := IpNode{
			Ip:   []string{arr[0]},
			Port: port,
		}
		if item.ComponentName == "mysql" {
			req.Modules.Mysql.IpNode = IpNode
			req.Modules.Mysql.Password = "fate_pass"
			req.Modules.Mysql.User = "fate_user"
		} else if item.ComponentName == "clustermanager" {
			req.Modules.Clustermanager = IpNode
		} else if item.ComponentName == "nodemanger" {
			req.Modules.Nodemanager = IpNode
		} else if item.ComponentName == "fateflow" {
			req.Modules.Flow.Ip = []string{arr[0]}
		} else if item.ComponentName == "fateboard" {
			req.Modules.Fateboard = IpNode
		} else if item.ComponentName == "rollsite" {
			req.Modules.Rollsite.IpNode = IpNode
			req.Modules.Rollsite.Port = port
			rule := Rule{
				Name: "default",
				Ip:   setting.KubenetesSetting.ExchangeIp,
				Port: setting.KubenetesSetting.ExchangePort,
			}
			req.Modules.Rollsite.Rules = append(req.Modules.Rollsite.Rules, rule)
		}
	}

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

func AnsibleJobQuery(DeployJob models.DeployJob) {
	req := entity.AnsibleSubmitData{JobId: DeployJob.JobId}
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleJobQueryUri), req, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return
	}
	var queryResponse entity.QueryResponse
	err = json.Unmarshal([]byte(result.Body), &queryResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return
	}
	if queryResponse.Code == e.SUCCESS {
		var data = make(map[string]interface{})
		deployStatus := enum.ANSIBLE_DeployStatus_INSTALLED
		if queryResponse.Data.Status == "success" {
			data["status"] = int(enum.JOB_STATUS_SUCCESS)
			data["end_time"] = queryResponse.Data.EndTime
		} else if queryResponse.Data.Status == "running" {
			data["status"] = int(enum.JOB_STATUS_RUNNING)
			deployStatus = enum.ANSIBLE_DeployStatus_INSTALLING
		} else if queryResponse.Data.Status == "failed" {
			deployStatus = enum.ANSIBLE_DeployStatus_INSTALLED_FAILED
			data["status"] = int(enum.JOB_STATUS_FAILED)
		}
		data["start_time"] = queryResponse.Data.StartTime
		data["update_time"] = time.Now()
		models.UpdateDeployJob(data, &DeployJob)

		data = make(map[string]interface{})
		data["deploy_status"] = int(deployStatus)

		starttime := queryResponse.Data.StartTime
		endtime := queryResponse.Data.EndTime

		var duration int64
		duration = 0
		if endtime-starttime >= 0 {
			duration = endtime - starttime
		}
		deploySite := models.DeploySite{
			PartyId:    DeployJob.PartyId,
			IsValid:    int(enum.IS_VALID_YES),
			DeployType: int(enum.DeployType_ANSIBLE),
		}
		data["duration"] = duration
		deploySite.JobId = DeployJob.JobId
		models.UpdateDeploySite(data, deploySite)

		for j := 0; j < len(queryResponse.Data.Plays); j++ {
			data = make(map[string]interface{})
			data["deploy_status"] = int(deployStatus)
			data["duration"] = duration
			data["start_time"] = queryResponse.Data.StartTime
			data["end_time"] = queryResponse.Data.EndTime

			deployComponent := models.DeployComponent{
				PartyId:       DeployJob.PartyId,
				ComponentName: queryResponse.Data.Plays[j].Module,
				IsValid:       int(enum.IS_VALID_YES),
				DeployType:    int(enum.DeployType_ANSIBLE),
			}
			models.UpdateDeployComponent(data, deployComponent)
		}

	}
}

func AnsibleAutoTest(autoTestReq entity.AutoTestReq, site models.DeploySite) (int, error) {
	req := entity.ConnectAnsible{PartyId: autoTestReq.PartyId}
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleTestStatusUri), req, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var queryResponse entity.QueryResponse
	err = json.Unmarshal([]byte(result.Body), &queryResponse)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if queryResponse.Code == e.SUCCESS {

	}

	return e.SUCCESS, nil
}
