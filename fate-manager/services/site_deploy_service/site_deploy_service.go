package site_deploy_service

import (
	"bufio"
	"bytes"
	"encoding/json"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/comm/util"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/component_deploy_service"
	"fate.manager/services/k8s_service"
	"fate.manager/services/version_service"
	"fmt"
	jsoniter "github.com/json-iterator/go"
	"io"
	"log"
	"os"
	"strconv"
	"strings"
	"time"
)

func GetPrepare() ([]entity.PrepareItem, error) {
	deployPrepareList, err := models.GetPrepareList()
	if err != nil {
		return nil, err
	}
	var prepareList []entity.PrepareItem
	for i := 0; i < len(deployPrepareList); i++ {
		prepareItem := entity.PrepareItem{
			ProcName: deployPrepareList[i].PrepareTitle,
			ProcDesc: deployPrepareList[i].PrepareDesc,
		}
		prepareList = append(prepareList, prepareItem)
	}
	return prepareList, nil
}

func GetClusterConfig(site models.DeploySite, versionIndex int, name string, nameSpace string, chartVersion string) ([]byte, error) {

	serviceInfoReq := entity.ServiceInfoReq{
		FederatedSite: entity.FederatedSite{
			FederatedId: site.FederatedId,
			PartyId:     site.PartyId,
		},
		ProductType: site.ProductType,
		FateVersion: site.FateVersion,
	}

	modules, err := component_deploy_service.GetComponentNameList(serviceInfoReq)
	if err != nil {
		return nil, err
	}
	proxyPort := site.RollsitePort
	pythonPort := site.PythonPort
	clusterConfig := entity.ClusterConfig{
		ChartVersion: chartVersion,
		Egg: entity.Egg{
			Count: 1,
		},
		Modules:    modules,
		Name:       name,
		NameSpace:  nameSpace,
		SrcPartyId: site.PartyId,
		Proxy: entity.Proxy{
			Exchange: entity.Exchange{
				ExchangeIp:   setting.KubenetesSetting.ExchangeIp,
				ExchangePort: setting.KubenetesSetting.ExchangePort,
			},
			NodePort: proxyPort,
			Type:     "NodePort",
		},
	}
	node := entity.Node{
		AccessMode:               "ReadWriteOnce",
		ExistingClaim:            "",
		Name:                     "nodemanager",
		NodeSelector:             entity.NodeSelector{},
		SessionProcessorsPerNode: setting.KubenetesSetting.SessionProcessorsPerNode,
		Size:                     "1Gi",
		StorageClass:             "nodemanager",
		SubPath:                  "nodemanager",
	}
	var nodeList []entity.Node
	nodeList = append(nodeList, node)
	clusterConfigNew := entity.ClusterConfig140{
		ChartName:    "fate",
		ChartVersion: chartVersion,
		Istio:        entity.Istio{Enabled: false},
		Modules:      modules,
		Mysql: entity.Mysql{
			AccessMode:    "ReadWriteOnce",
			Database:      "eggroll_meta",
			ExistingClaim: "",
			Ip:            "mysql",
			NodeSelector:  entity.NodeSelector{},
			Password:      "fate_dev",
			Port:          version_service.GetDefaultPort("mysql"),
			Size:          "1Gi",
			StorageClass:  "mysql",
			SubPath:       "",
			User:          "fate",
		},
		Name:      name,
		NameSpace: nameSpace,
		NodeManager: entity.NodeManager{
			Count:                    setting.KubenetesSetting.NodeManager,
			List:                     nodeList,
			SessionProcessorsPerNode: setting.KubenetesSetting.SessionProcessorsPerNode,
		},
		SrcPartyId:  site.PartyId,
		Persistence: false,
		PullPolicy:  entity.PullPolicy{},
		Python: entity.Python{
			FateFlowNodePort: pythonPort,
			FateFlowType:     "NodePort",
			NodeSelector:     entity.NodeSelector{},
		},
		Registry: setting.KubenetesSetting.Registry,
		Rollsite: entity.Rollsite{
			Exchange: entity.Exchange{
				ExchangeIp:   setting.KubenetesSetting.ExchangeIp,
				ExchangePort: setting.KubenetesSetting.ExchangePort,
			},
			Proxy: entity.Proxy{
				NodePort: proxyPort,
				Type:     "NodePort",
			},
			NodeSelector: entity.NodeSelector{},
		},
	}
	var json = jsoniter.ConfigCompatibleWithStandardLibrary
	valBj, err := json.Marshal(clusterConfig)
	if err != nil {
		return nil, err
	}
	if versionIndex >= 140 {
		valBj, err = json.Marshal(clusterConfigNew)
		if err != nil {
			return nil, err
		}
	}
	return valBj, nil
}

func Install(installReq entity.InstallReq) (int, error) {
	deploySite := models.DeploySite{
		FederatedId: installReq.FederatedId,
		PartyId:     installReq.PartyId,
		ProductType: installReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return e.ERROR_PARTY_INSTALLED_FAIL, err
	} else {
		if deploySiteList[0].DeployStatus == int(enum.DeployStatus_INSTALLING) ||
			deploySiteList[0].DeployStatus == int(enum.DeployStatus_UNDER_INSTALLATION) {
			return e.ERROR_PARTY_IS_INSTALLING_FAIL, err
		}
	}

	name := fmt.Sprintf("fate-%d", deploySiteList[0].PartyId)
	nameSpace := fmt.Sprintf("fate-%d", deploySiteList[0].PartyId)
	cmd := fmt.Sprintf("kubectl get namespace |awk '{if($1==\"%s\"){print $0}}' |grep Active|wc -l", nameSpace)
	if setting.KubenetesSetting.SudoTag {
		cmd = fmt.Sprintf("sudo %s", cmd)
	}
	value, _ := util.ExecCommand(cmd)
	logging.Debug(value)
	if value[0:1] != "1" {
		cmd = fmt.Sprintf("kubectl create namespace %s", nameSpace)
		if setting.KubenetesSetting.SudoTag {
			cmd = fmt.Sprintf("sudo %s", cmd)
		}
		value, _ := util.ExecCommand(cmd)
		logging.Debug(value)
	}

	valBj, err := GetClusterConfig(deploySiteList[0], deploySiteList[0].VersionIndex, name, nameSpace, deploySiteList[0].ChartVersion)
	if err != nil {
		return e.ERROR_INSTALL_ALL_FAIL, err
	}

	clusterInstallReq := entity.ClusterInstallReq{
		Name:         name,
		NameSpace:    nameSpace,
		ChartName:    "fate",
		ChartVersion: deploySiteList[0].ChartVersion,
		Cover:        false,
		Data:         valBj,
	}
	user := util.User{
		UserName: "admin",
		Password: "admin",
	}
	kubefateUrl := k8s_service.GetKubenetesUrl(installReq.FederatedId, installReq.PartyId)
	token, err := util.GetToken(kubefateUrl, user)
	if err != nil {
		return e.ERROR_GET_TOKEN_FAIL, err
	}
	authorization := fmt.Sprintf("Bearer %s", token)
	head := make(map[string]interface{})
	head["Authorization"] = authorization
	if len(deploySiteList[0].ClusterId) > 0 {
		_, err := http.DELETE(http.Url(kubefateUrl+"/v1/cluster/"+deploySiteList[0].ClusterId), nil, head)
		if err != nil {
			return e.ERROR_INSTALL_ALL_FAIL, err
		}
		var data = make(map[string]interface{})
		data["status"] = 0
		data["deploy_status"] = int(enum.DeployStatus_PULLED)
		data["duration"] = 0
		deployComponent := models.DeployComponent{
			FederatedId: installReq.FederatedId,
			PartyId:     installReq.PartyId,
			ProductType: installReq.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		models.UpdateDeployComponent(data, deployComponent)

		data["cluster_id"] = ""
		models.UpdateDeploySite(data, deploySite)
		autoTest := models.AutoTest{
			FederatedId: installReq.FederatedId,
			PartyId:     installReq.PartyId,
			ProductType: installReq.ProductType,
		}
		models.DeleteTest(autoTest)
		time.Sleep(10 * 1000 * 1000 * 1000)
	}
	result, err := http.POST(http.Url(kubefateUrl+"/v1/cluster/"), clusterInstallReq, head)
	if err != nil {
		return e.ERROR_HTTP_FAIL, err
	}
	var clusterInstallResp entity.ClusterInstallResp
	index := bytes.IndexByte([]byte(result.Body), 0)
	err = json.Unmarshal([]byte(result.Body)[:index], &clusterInstallResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if result.StatusCode == 200 {
		deploySiteinfo := make(map[string]interface{})
		deploySiteinfo["job_id"] = clusterInstallResp.Data.JobId
		deploySiteinfo["name"] = name
		deploySiteinfo["name_space"] = nameSpace
		deploySiteinfo["deploy_status"] = int(enum.DeployStatus_UNDER_INSTALLATION)
		deploySiteinfo["chart"] = deploySiteList[0].Chart
		deploySiteinfo["chart_version"] = deploySiteList[0].ChartVersion
		deploySiteinfo["cluster_id"] = clusterInstallResp.Data.ClusterId
		deploySiteinfo["config"] = string(valBj)

		if deploySiteList[0].DeployStatus >= int(enum.DeployStatus_INSTALLED) {
			deploySiteinfo["click_type"] = int(enum.ClickType_PULL)
		}

		err = models.UpdateDeploySite(deploySiteinfo, deploySite)
		if err != nil {
			logging.Debug("update deploySite failed")
		}

		deployComponent := models.DeployComponent{
			FederatedId: installReq.FederatedId,
			PartyId:     installReq.PartyId,
			ProductType: installReq.ProductType,
			FateVersion: deploySite.FateVersion,
			IsValid:     int(enum.IS_VALID_YES),
		}
		deployComponentInfo := make(map[string]interface{})
		deployComponentInfo["start_time"] = time.Now()
		deployComponentInfo["deploy_status"] = int(enum.DeployStatus_UNDER_INSTALLATION)
		deployComponentInfo["job_id"] = clusterInstallResp.Data.JobId
		err = models.UpdateDeployComponent(deployComponentInfo, deployComponent)
		if err != nil {
			logging.Error("update deployComponent failed")
		}

		deployJob := models.DeployJob{
			JobId:       clusterInstallResp.Data.JobId,
			JobType:     int(enum.JOB_TYPE_INSTALL),
			Creator:     clusterInstallResp.Data.Creator,
			StartTime:   time.Now(),
			FederatedId: installReq.FederatedId,
			PartyId:     installReq.PartyId,
			ProductType: installReq.ProductType,
			Status:      int(enum.JOB_STATUS_RUNNING),
			CreateTime:  time.Now(),
			UpdateTime:  time.Now(),
		}
		err = models.AddDeployJob(&deployJob)
		if err != nil {
			logging.Debug("Add deployJob failed")
		}
		return e.SUCCESS, nil
	}
	return e.ERROR_INSTALL_ALL_FAIL, nil
}

func Upgrade(upgradeReq entity.UpgradeReq) (int, error) {
	deploySite := models.DeploySite{
		PartyId:     upgradeReq.PartyId,
		ProductType: upgradeReq.ProductType,
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
		Name:         deploySiteList[0].Name,
		NameSpace:    deploySiteList[0].NameSpace,
		DeployStatus: int(enum.DeployStatus_UNKNOWN),
		Status:       int(enum.SITE_RUN_STATUS_UNKNOWN),
		KubenetesId:  deploySiteList[0].KubenetesId,
		PythonPort:   deploySiteList[0].PythonPort,
		RollsitePort: deploySiteList[0].RollsitePort,
		IsValid:      int(enum.IS_VALID_YES),
		ClickType:    int(enum.ClickType_PAGE),
		ClusterId:    deploySiteList[0].ClusterId,
		FinishTime:   time.Time{},
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
		FateVersion: deploySiteList[0].FateVersion,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil {
		return e.ERROR_UPGRADE_ALL_FAIL, nil
	}
	models.UpdateDeployComponent(data, deployComponent)
	for i := 0; i < len(componentVersionList); i++ {
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
			Address:          k8s_service.GetNodeIp(upgradeReq.FederatedId, upgradeReq.PartyId) + ":" + strconv.Itoa(version_service.GetDefaultPort(componentVersionList[i].ComponentName)),
			DeployStatus:     int(enum.DeployStatus_PULLED),
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
		IsValid:       int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil || len(deployComponentList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if deployComponentList[0].DeployStatus == int(enum.DeployStatus_PULLED) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_INSTALLED) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_UNDER_INSTALLATION) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_INSTALLING) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_TEST_FAILED) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_IN_TESTING) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_INSTALLED_FAILED) ||
		deployComponentList[0].DeployStatus == int(enum.DeployStatus_TEST_PASSED) {

		ret := k8s_service.CheckNodeIp(updateReq.Address, updateReq.FederatedId, updateReq.PartyId)
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
		data["click_type"] = int(enum.ClickType_PULL)
		if updateReq.ComponentName == "rollsite" {
			addr := strings.Split(updateReq.Address, ":")
			if len(addr) == 2 {
				port, _ := strconv.Atoi(addr[1])
				data["rollsite_port"] = port
			}
		}

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
func GetAutoTestList(autoTestListReq entity.AutoTestListReq) (*entity.AutoTestListRespItem, error) {
	deploySite := models.DeploySite{
		FederatedId: autoTestListReq.FederatedId,
		PartyId:     autoTestListReq.PartyId,
		ProductType: autoTestListReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return nil, err
	}
	autoTest := models.AutoTest{
		FederatedId: autoTestListReq.FederatedId,
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

func AutoTest(autoTestReq entity.AutoTestReq) (int, error) {
	if len(autoTestReq.TestItemList) == 0 {
		return e.ERROR_AUTO_TEST_FAIL, nil
	}
	deploySite := models.DeploySite{
		FederatedId: autoTestReq.FederatedId,
		PartyId:     autoTestReq.PartyId,
		ProductType: autoTestReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return e.ERROR_AUTO_TEST_FAIL, err
	}
	cmd := fmt.Sprintf("kubectl get pods -n %s", deploySiteList[0].NameSpace)
	if setting.KubenetesSetting.SudoTag {
		cmd = fmt.Sprintf("sudo %s", cmd)
	}
	result, _ := util.ExecCommand(cmd)
	if result == "No resources found." {
		return e.ERROR_AUTO_TEST_FAIL, err
	}
	autoTest := models.AutoTest{
		FederatedId: autoTestReq.FederatedId,
		PartyId:     autoTestReq.PartyId,
		ProductType: autoTestReq.ProductType,
	}
	autoTestList, err := models.GetAutoTest(autoTest)
	if err != nil || len(autoTestList) == 0 {
		return e.ERROR_AUTO_TEST_FAIL, nil
	}
	var data = make(map[string]interface{})
	toytesttag := true
	for k, v := range autoTestReq.TestItemList {
		if k == enum.GetTestItemString(enum.TEST_ITEM_POD) {
			for i := 0; i < len(v); i++ {
				testname := v[i]
				if testname == "meta-service" {
					testname = "roll"
				}
				if testname == "fateboard" || testname == "fateflow" {
					testname = "python"
				}
				cmdSub := fmt.Sprintf("%s |grep %s* | grep Running |wc -l", cmd, testname)
				data["start_time"] = time.Now()
				result, _ := util.ExecCommand(cmdSub)
				data["end_time"] = time.Now()

				autoTest.TestItem = v[i]
				var temp = make(map[string]interface{})
				temp["deploy_status"] = int(enum.DeployStatus_TEST_PASSED)

				data["status"] = int(enum.TEST_STATUS_YES)
				cnt, _ := strconv.Atoi(result[0:1])
				logging.Info(cnt)
				if cnt < 1 {
					toytesttag = false
					temp["deploy_status"] = int(enum.DeployStatus_TEST_FAILED)
					data["status"] = int(enum.TEST_STATUS_NO)
				} else {
					temp["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
				}
				models.UpdateAutoTest(data, autoTest)
				deployComponent := models.DeployComponent{
					FederatedId:   autoTestReq.FederatedId,
					PartyId:       autoTestReq.PartyId,
					ProductType:   autoTestReq.ProductType,
					ComponentName: v[i],
					IsValid:       int(enum.IS_VALID_YES),
				}
				models.UpdateDeployComponent(temp, deployComponent)
			}
		}
		if !autoTestReq.IfOnly {
			data = make(map[string]interface{})
			data["deploy_status"] = int(enum.DeployStatus_IN_TESTING)
			models.UpdateDeploySite(data, deploySite)
		} else {
			go ToyTestOnly(autoTestReq)
		}
	}
	if toytesttag {
		go DoAutoTest(autoTestReq)
	} else {
		data = make(map[string]interface{})
		data["status"] = int(enum.TEST_STATUS_NO)
		data["update_time"] = time.Now()
		autoTest.TestItem = "Single Test"
		models.UpdateAutoTest(data, autoTest)
		autoTest.TestItem = "Toy Test"
		models.UpdateAutoTest(data, autoTest)
		autoTest.TestItem = "Minimize Fast Test"
		models.UpdateAutoTest(data, autoTest)
		autoTest.TestItem = "Minimize Normal Test"
		models.UpdateAutoTest(data, autoTest)

		data = make(map[string]interface{})
		data["single_test"] = int(enum.TEST_STATUS_NO)
		data["toy_test"] = int(enum.TEST_STATUS_NO)
		data["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
		data["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
		data["update_time"] = time.Now()
		models.UpdateDeploySite(data, deploySite)
	}

	return e.SUCCESS, nil
}
func DoAutoTest(autoTestReq entity.AutoTestReq) {
	autoTest := models.AutoTest{
		PartyId:     autoTestReq.PartyId,
		ProductType: autoTestReq.ProductType,
	}
	autoTestList, _ := models.GetAutoTest(autoTest)
	deploySite := models.DeploySite{
		PartyId:     autoTestReq.PartyId,
		ProductType: autoTestReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	for i := 0; i < len(autoTestList); i++ {
		item := autoTestList[i]
		if item.Status == int(enum.TEST_STATUS_NO) {
			var dataTest = make(map[string]interface{})
			var siteTest = make(map[string]interface{})
			dataTest["update_time"] = time.Now()
			dataTest["start_time"] = time.Now()
			dataTest["status"] = int(enum.TEST_STATUS_WAITING)
			siteTest["update_time"] = time.Now()
			siteTest["single_test"] = int(enum.TEST_STATUS_WAITING)

			models.UpdateAutoTest(dataTest, item)
			models.UpdateDeploySite(siteTest, deploySite)
		}
	}
	var dataTest = make(map[string]interface{})
	var siteTest = make(map[string]interface{})

	//test single
	autoTest.TestItem = "Single Test"
	autoTestList, _ = models.GetAutoTest(autoTest)
	if len(autoTestList) > 0 && autoTestList[0].Status == int(enum.TEST_STATUS_YES) {
		cmd := fmt.Sprintf("cat ./testLog/single/fate-%d.log > ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
	} else {
		dataTest["update_time"] = time.Now()
		dataTest["start_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_TESTING)
		siteTest["update_time"] = time.Now()
		siteTest["single_test"] = int(enum.TEST_STATUS_TESTING)
		models.UpdateAutoTest(dataTest, autoTest)
		models.UpdateDeploySite(siteTest, deploySite)

		cmd := fmt.Sprintf("sh ./shell/autoTest.sh Single %d > ./testLog/single/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		cmd = fmt.Sprintf("cat ./testLog/single/fate-%d.log > ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		dataTest["update_time"] = time.Now()
		dataTest["end_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_YES)
		siteTest["single_test"] = int(enum.TEST_STATUS_YES)

		cmd = fmt.Sprintf("grep \"success to calculate secure_sum\" ./testLogt/single/fate-%d.log|wc -l", autoTestReq.PartyId)
		result, _ := util.ExecCommand(cmd)
		logging.Debug(result)
		if result[0:1] != "1" {
			dataTest["status"] = int(enum.TEST_STATUS_NO)
			models.UpdateAutoTest(dataTest, autoTest)

			siteTest["update_time"] = time.Now()
			siteTest["single_test"] = int(enum.TEST_STATUS_NO)
			siteTest["toy_test"] = int(enum.TEST_STATUS_NO)
			siteTest["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
			models.UpdateDeploySite(siteTest, deploySite)
			return
		}
		models.UpdateAutoTest(dataTest, autoTest)
		models.UpdateDeploySite(siteTest, deploySite)
	}
	dataTest = make(map[string]interface{})
	siteTest = make(map[string]interface{})
	//test toy
	autoTest.TestItem = "Toy Test"
	autoTestList, _ = models.GetAutoTest(autoTest)
	if len(autoTestList) > 0 && autoTestList[0].Status == int(enum.TEST_STATUS_YES) {
		cmd := fmt.Sprintf("cat ./testLog/toy/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
	} else {
		dataTest["update_time"] = time.Now()
		dataTest["start_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_TESTING)
		siteTest["update_time"] = time.Now()
		siteTest["toy_test"] = int(enum.TEST_STATUS_TESTING)
		models.UpdateAutoTest(dataTest, autoTest)
		models.UpdateDeploySite(siteTest, deploySite)
		cmd := fmt.Sprintf("sh  ./shell/autoTest.sh Toy %d > ./testLog/toy/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		cmd = fmt.Sprintf("cat ./testLog/toy/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		dataTest["end_time"] = time.Now()
		dataTest["update_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_YES)
		siteTest["toy_test"] = int(enum.TEST_STATUS_YES)

		cmd = fmt.Sprintf("grep \"success to calculate secure_sum\" ./testLog/toy/fate-%d.log|wc -l", autoTestReq.PartyId)
		result, _ := util.ExecCommand(cmd)
		logging.Debug(result)
		if result[0:1] != "1" {
			dataTest["status"] = int(enum.TEST_STATUS_NO)
			models.UpdateAutoTest(dataTest, autoTest)

			siteTest["update_time"] = time.Now()
			siteTest["toy_test"] = int(enum.TEST_STATUS_NO)
			siteTest["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
			models.UpdateDeploySite(siteTest, deploySite)
			return
		}
		models.UpdateAutoTest(dataTest, autoTest)
		models.UpdateDeploySite(siteTest, deploySite)
	}
	dataTest = make(map[string]interface{})
	siteTest = make(map[string]interface{})

	//test fast
	autoTest.TestItem = "Mininmize Fast Test"
	autoTestList, _ = models.GetAutoTest(autoTest)
	if len(autoTestList) > 0 && autoTestList[0].Status == int(enum.TEST_STATUS_YES) {
		cmd := fmt.Sprintf("cat ./testLog/fast/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		//test normal
		autoTest.TestItem = "Minimize Normal Test"
		autoTestList, _ = models.GetAutoTest(autoTest)
		if len(autoTestList) > 0 && autoTestList[0].Status == int(enum.TEST_STATUS_YES) {
			cmd = fmt.Sprintf("cat ./testLog/normal/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
			util.ExecCommand(cmd)
		} else {
			dataTest["update_time"] = time.Now()
			dataTest["start_time"] = time.Now()
			dataTest["status"] = int(enum.TEST_STATUS_TESTING)
			siteTest["update_time"] = time.Now()
			siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_TESTING)
			models.UpdateAutoTest(dataTest, autoTest)
			models.UpdateDeploySite(siteTest, deploySite)
			cmd = fmt.Sprintf("sh  ./shell/autoTest.sh Normal %d > ./testLog/normal/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
			util.ExecCommand(cmd)
			cmd = fmt.Sprintf("cat ./testLog/normal/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
			util.ExecCommand(cmd)
			dataTest["end_time"] = time.Now()
			autoTest.TestItem = "Minimize Normal Test"
			dataTest["update_time"] = time.Now()
			dataTest["status"] = int(enum.TEST_STATUS_YES)
			siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_YES)

			cmd = fmt.Sprintf("grep success ./testLog/normal/fate-%d.log|wc -l", autoTestReq.PartyId)
			result, _ := util.ExecCommand(cmd)
			logging.Debug(result)
			if len(result) > 0 {
				num, _ := strconv.Atoi(result[0:1])
				if num < 0 {
					dataTest["status"] = int(enum.TEST_STATUS_NO)
					models.UpdateAutoTest(dataTest, autoTest)

					siteTest["update_time"] = time.Now()
					siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
					models.UpdateDeploySite(siteTest, deploySite)
					return
				}
				siteTest["deploy_status"] = int(enum.DeployStatus_TEST_PASSED)
				siteTest["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
				models.UpdateAutoTest(dataTest, autoTest)
				models.UpdateDeploySite(siteTest, deploySite)

				var deployData = make(map[string]interface{})
				deployData["deploy_status"] = int(enum.DeployStatus_TEST_PASSED)
				deployComponent := models.DeployComponent{
					FederatedId: autoTestReq.FederatedId,
					PartyId:     autoTestReq.PartyId,
					ProductType: autoTestReq.ProductType,
					IsValid:     int(enum.IS_VALID_YES),
				}
				models.UpdateDeployComponent(deployData, deployComponent)
			}
		}
	} else {
		dataTest["update_time"] = time.Now()
		dataTest["start_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_TESTING)
		siteTest["update_time"] = time.Now()
		siteTest["minimize_fast_test"] = int(enum.TEST_STATUS_TESTING)
		models.UpdateAutoTest(dataTest, autoTest)
		models.UpdateDeploySite(siteTest, deploySite)
		cmd := fmt.Sprintf("sh  ./shell/autoTest.sh Fast %d > ./testLog/fast/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		cmd = fmt.Sprintf("cat ./testLog/fast/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
		util.ExecCommand(cmd)
		dataTest["end_time"] = time.Now()
		dataTest["update_time"] = time.Now()
		dataTest["status"] = int(enum.TEST_STATUS_YES)
		siteTest["minimize_fast_test"] = int(enum.TEST_STATUS_YES)

		cmd = fmt.Sprintf("grep success ./testLog/fast/fate-%d.log|wc -l", autoTestReq.PartyId)
		result, _ := util.ExecCommand(cmd)
		logging.Debug(result)
		if len(result) > 0 {
			num, _ := strconv.Atoi(result[0:1])
			if num < 0 {
				dataTest["status"] = int(enum.TEST_STATUS_NO)
				models.UpdateAutoTest(dataTest, autoTest)

				siteTest["update_time"] = time.Now()
				siteTest["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
				siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
				models.UpdateDeploySite(siteTest, deploySite)
				return
			}
			models.UpdateAutoTest(dataTest, autoTest)
			models.UpdateDeploySite(siteTest, deploySite)

			dataTest = make(map[string]interface{})
			siteTest = make(map[string]interface{})

			//test normal
			autoTest.TestItem = "Minimize Normal Test"
			autoTestList, _ = models.GetAutoTest(autoTest)
			if len(autoTestList) > 0 && autoTestList[0].Status == int(enum.TEST_STATUS_YES) {
				cmd = fmt.Sprintf("cat ./testLog/normal/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
				util.ExecCommand(cmd)
			} else {
				dataTest["update_time"] = time.Now()
				dataTest["start_time"] = time.Now()
				dataTest["status"] = int(enum.TEST_STATUS_TESTING)
				siteTest["update_time"] = time.Now()
				siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_TESTING)
				models.UpdateAutoTest(dataTest, autoTest)
				models.UpdateDeploySite(siteTest, deploySite)
				cmd = fmt.Sprintf("sh  ./shell/autoTest.sh Normal %d > ./testLog/normal/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
				util.ExecCommand(cmd)
				cmd = fmt.Sprintf("cat ./testLog/normal/fate-%d.log >> ./testLog/all/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
				util.ExecCommand(cmd)
				dataTest["end_time"] = time.Now()
				autoTest.TestItem = "Minimize Normal Test"
				dataTest["update_time"] = time.Now()
				dataTest["status"] = int(enum.TEST_STATUS_YES)
				siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_YES)

				cmd = fmt.Sprintf("grep success ./testLog/normal/fate-%d.log|wc -l", autoTestReq.PartyId)
				result, _ = util.ExecCommand(cmd)
				logging.Debug(result)
				if len(result) > 0 {
					num, _ = strconv.Atoi(result[0:1])
					if num < 0 {
						dataTest["status"] = int(enum.TEST_STATUS_NO)
						models.UpdateAutoTest(dataTest, autoTest)

						siteTest["update_time"] = time.Now()
						siteTest["minimize_normal_test"] = int(enum.TEST_STATUS_NO)
						models.UpdateDeploySite(siteTest, deploySite)
						return
					}
					siteTest["deploy_status"] = int(enum.DeployStatus_TEST_PASSED)
					siteTest["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
					models.UpdateAutoTest(dataTest, autoTest)
					models.UpdateDeploySite(siteTest, deploySite)

					var deployData = make(map[string]interface{})
					deployData["deploy_status"] = int(enum.DeployStatus_TEST_PASSED)
					deployComponent := models.DeployComponent{
						FederatedId: autoTestReq.FederatedId,
						PartyId:     autoTestReq.PartyId,
						ProductType: autoTestReq.ProductType,
						IsValid:     int(enum.IS_VALID_YES),
					}
					models.UpdateDeployComponent(deployData, deployComponent)
				}
			}
		}
	}
}

func ToyTestOnly(autoTestReq entity.AutoTestReq) {
	//test toy
	deploySite := models.DeploySite{
		FederatedId: autoTestReq.FederatedId,
		PartyId:     autoTestReq.PartyId,
		ProductType: autoTestReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	var data = make(map[string]interface{})
	data["toy_test_only"] = int(enum.ToyTestOnly_TESTING)
	models.UpdateDeploySite(data, deploySite)
	cmd := fmt.Sprintf("sh  ./shell/autoTest.sh Toy %d > ./testLog/toy/fate-%d.log", autoTestReq.PartyId, autoTestReq.PartyId)
	util.ExecCommand(cmd)

	cmd = fmt.Sprintf("grep \"success to calculate secure_sum\" ./testLog/toy/fate-%d.log|wc -l", autoTestReq.PartyId)
	result, _ := util.ExecCommand(cmd)
	data["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_NO)
	data["toy_test_only"] = int(enum.ToyTestOnly_SUCCESS)
	if result[0:1] != "1" {
		data["toy_test_only"] = int(enum.ToyTestOnly_FAILED)
	}
	models.UpdateDeploySite(data, deploySite)
}

func GetServiceOverview(overViewReq entity.OverViewReq) ([]entity.OverViewRspItem, error) {
	siteInfo := models.SiteInfo{
		//FederatedId: overViewReq.FederatedId,
		SiteName: overViewReq.Condition,
		Role:     overViewReq.Role,
	}
	siteList, err := models.GetSiteList(&siteInfo)
	if err != nil {
		return nil, err
	}
	var overViewRsp []entity.OverViewRspItem
	for i := 0; i < len(siteList); i++ {
		if siteList[i].Status == int(enum.SITE_STATUS_REMOVED) {
			continue
		}
		var overViewRspItem entity.OverViewRspItem
		overViewRspItem.Role = entity.IdPair{siteList[i].Role, enum.GetRoleString(enum.RoleType(siteList[i].Role))}
		overViewRspItem.SiteName = siteList[i].SiteName
		overViewRspItem.PartyId = siteList[i].PartyId
		overViewRspItem.Institutions = siteList[i].Institutions
		overViewRspItem.FederatedId = siteList[i].FederatedId
		overViewRspItem.FederatedOrganization = siteList[i].FederatedOrganization
		overViewRspItem.FateVersion = siteList[i].FateVersion

		deployComponent := models.DeployComponent{
			//FederatedId: siteList[i].FederatedId,
			PartyId:     siteList[i].PartyId,
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			IsValid:     int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil {
			msg, _ := fmt.Scanf("federatedId:%d,partyId:%d not deploy！", siteList[i].FederatedId, siteList[i].PartyId)
			logging.Debug(msg)
		}
		for j := 0; j < len(deployComponentList); j++ {
			componentVersion := models.ComponentVersion{
				FateVersion:   deployComponentList[j].FateVersion,
				ProductType:   deployComponentList[j].ProductType,
				ComponentName: deployComponentList[j].ComponentName,
			}
			componentVersionList, _ := models.GetComponetVersionList(componentVersion)
			upgradeStatus := false
			if len(componentVersionList) > 0 {
				if componentVersionList[0].VersionIndex > deployComponentList[j].VersionIndex {
					upgradeStatus = true
				}
			}

			installItem := entity.InstallItem{
				ComponentName:    deployComponentList[j].ComponentName,
				ComponentVersion: deployComponentList[j].ComponentVersion,
				UpgradeStatus:    upgradeStatus,
			}

			deployJob := models.DeployJob{
				FederatedId: siteList[i].FederatedId,
				PartyId:     siteList[i].PartyId,
				ProductType: int(enum.PRODUCT_TYPE_FATE),
			}
			deployJobList, _ := models.GetDeployJob(deployJob, true)
			var jobMap = make(map[string]models.DeployJob)
			if len(deployJobList) > 0 {
				install_tag := true
				update_tag := true
				for k := 0; k < len(deployJobList); k++ {
					if deployJobList[k].Status == int(enum.JOB_STATUS_SUCCESS) || deployJobList[k].Status == int(enum.JOB_STATUS_FAILED) {
						if deployJobList[k].JobType == int(enum.JOB_TYPE_INSTALL) || install_tag {
							installItem.InstallTime = deployJobList[k].EndTime.UnixNano() / 1e6
							install_tag = false
						} else if deployJobList[k].JobType == int(enum.JOB_TYPE_UPDATE) || update_tag {
							installItem.UpgradeTime = deployJobList[k].EndTime.UnixNano() / 1e6
							update_tag = false
						}
						jobMap[deployJobList[k].JobId] = deployJobList[k]
					}
				}
			}
			deployComponent := models.DeployComponent{
				//FederatedId:   deployComponentList[j].FederatedId,
				PartyId:       deployComponentList[j].PartyId,
				ProductType:   deployComponentList[j].ProductType,
				ComponentName: deployComponentList[j].ComponentName,
			}
			deployComponentList, _ := models.GetDeployComponent(deployComponent)
			var operationList []entity.Operation
			for l := 0; l < len(deployComponentList); l++ {
				deployJob, ok := jobMap[deployComponentList[l].JobId]
				if ok {
					var jobDesc string
					if deployJob.Status == int(enum.JOB_STATUS_SUCCESS) {
						if deployJob.JobType == int(enum.JOB_TYPE_INSTALL) {
							jobDesc = fmt.Sprintf("install %s successfully!", deployComponentList[l].ComponentVersion)
						} else if deployJob.JobType == int(enum.JOB_TYPE_UPDATE) {
							jobDesc = fmt.Sprintf("upgrade to %s successfully!", deployComponentList[l].ComponentVersion)
						}
					} else if deployJob.Status == int(enum.JOB_STATUS_FAILED) {
						if deployJob.JobType == int(enum.JOB_TYPE_INSTALL) {
							jobDesc = fmt.Sprintf("install %s failed!", deployComponentList[l].ComponentVersion)
						} else if deployJob.JobType == int(enum.JOB_TYPE_UPDATE) {
							jobDesc = fmt.Sprintf("upgrade to %s failed!", deployComponentList[l].ComponentVersion)
						}
					}
					operation := entity.Operation{
						OperateTime:   deployJob.EndTime.UnixNano() / 1e6,
						OperateAction: entity.IdPair{deployJob.JobType, jobDesc},
					}
					operationList = append(operationList, operation)
				}
			}
			installItem.OperationList = operationList
			overViewRspItem.InstallInfo = append(overViewRspItem.InstallInfo, installItem)
		}

		overViewRsp = append(overViewRsp, overViewRspItem)
	}

	return overViewRsp, nil
}

func UpgradeFateList(upgradeFateReq entity.UpgradeFateReq) ([]entity.UpdateVersionResp, error) {
	deploySite := models.DeploySite{
		FederatedId: upgradeFateReq.FederatedId,
		PartyId:     upgradeFateReq.PartyId,
		ProductType: upgradeFateReq.ProductType,
		IsValid:     int(enum.PRODUCT_TYPE_FATE),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
		return nil, err
	}
	deploySite = deploySiteList[0]

	fateVersion := models.FateVersion{
		ProductType:  deploySite.ProductType,
		VersionIndex: deploySite.VersionIndex,
	}
	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil {
		return nil, err
	}
	var upgradeList []entity.UpdateVersionResp
	for i := 0; i < len(fateVersionList); i++ {
		item := entity.UpdateVersionResp{fateVersionList[i].VersionIndex, fateVersionList[i].FateVersion}
		upgradeList = append(upgradeList, item)
	}
	return upgradeList, nil
}

func GetPageStatus(pageStatusReq entity.PageStatusReq) (*entity.PageStatusResp, error) {
	var pageStatusResp entity.PageStatusResp
	deploySite := models.DeploySite{
		FederatedId: pageStatusReq.FederatedId,
		PartyId:     pageStatusReq.PartyId,
		ProductType: pageStatusReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return nil, err
	}
	if len(deploySiteList) == 0 {

		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_PREPARE), enum.GetPageStatusString(enum.PAGE_STATUS_PREPARE)}

	} else if deploySiteList[0].ClickType == int(enum.ClickType_CONNECT) {

		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_PAGE), enum.GetPageStatusString(enum.PAGE_STATUS_PAGE)}

	} else if deploySiteList[0].ClickType == int(enum.ClickType_PAGE) {

		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_PULLIMAGE), enum.GetPageStatusString(enum.PAGE_STATUS_PULLIMAGE)}

	} else if deploySiteList[0].ClickType == int(enum.ClickType_PULL) {

		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_INSTALL), enum.GetPageStatusString(enum.PAGE_STATUS_INSTALL)}

	} else if deploySiteList[0].ClickType == int(enum.ClickType_INSTALL) {

		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_TEST), enum.GetPageStatusString(enum.PAGE_STATUS_TEST)}

	} else if deploySiteList[0].ClickType == int(enum.ClickType_TEST) {
		pageStatusResp.PageStatus = entity.IdPair{int(enum.PAGE_STATUS_SERVICE), enum.GetPageStatusString(enum.PAGE_STATUS_SERVICE)}
	}
	return &pageStatusResp, nil
}

func TestResult(testResultReq entity.TestResultReq) (*entity.IdPair, error) {
	var result entity.IdPair
	if testResultReq.TestItem == enum.GetTestItemString(enum.TEST_ITEM_TOY) {
		deploySite := models.DeploySite{
			FederatedId:     testResultReq.FederatedId,
			PartyId:         testResultReq.PartyId,
			ProductType:     testResultReq.ProductType,
			IsValid:         int(enum.IS_VALID_YES),
			ToyTestOnlyRead: int(enum.ToyTestOnlyTypeRead_NO),
		}
		deploySiteList, err := models.GetDeploySite(&deploySite)
		if err != nil || len(deploySiteList) == 0 {
			return nil, err
		}
		result.Code = deploySiteList[0].ToyTestOnly
		result.Desc = enum.GetToyTestOnlyString(enum.ToyTestOnlyType(deploySiteList[0].ToyTestOnly))
		return &result, nil
	}
	return nil, nil
}

func Click(req entity.ClickReq) bool {
	deploySite := models.DeploySite{
		FederatedId: req.FederatedId,
		PartyId:     req.PartyId,
		ProductType: req.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
		ClickType:   req.ClickType,
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil || len(deploySiteList) == 0 {
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
	if req.ClickType == int(enum.ClickType_TEST) {
		deployComponent := models.DeployComponent{
			FederatedId: req.FederatedId,
			PartyId:     req.PartyId,
			ProductType: req.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}

		var data = make(map[string]interface{})
		data["duration"] = time.Now().UnixNano()/1e6 - deploySiteList[0].CreateTime.UnixNano()/1e6
		data["finish_time"] = time.Now()
		data["deploy_status"] = int(enum.DeployStatus_SUCCESS)
		models.UpdateDeployComponent(data, deployComponent)

		deploySite := models.DeploySite{
			FederatedId: req.FederatedId,
			PartyId:     req.PartyId,
			ProductType: req.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		models.UpdateDeploySite(data, deploySite)
	}
	return true
}
func ToyResultRead(req entity.ToyResultReadReq) bool {
	deploySite := models.DeploySite{
		FederatedId: req.FederatedId,
		PartyId:     req.PartyId,
		ProductType: req.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	var data = make(map[string]interface{})
	data["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_YES)
	err := models.UpdateDeploySite(data, deploySite)
	if err != nil {
		return false
	}
	return true
}

func GetTestLog(logReq entity.LogReq) (map[string][]string, error) {

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
	cmd := fmt.Sprintf("./testLog/all/fate-%d.log", logReq.PartyId)
	if !util.FileExists(cmd) {
		return logs, nil
	}
	file, err := os.Open(cmd)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()
	var all []string
	rd := bufio.NewReader(file)
	for {
		line, err := rd.ReadString('\n') //以'\n'为结束符读入一行
		if err != nil || io.EOF == err {
			break
		}
		all = append(all, line)
	}
	logs["all"] = all
	return logs, nil
}
func GetFateBoardUrl(federatedSite entity.FederatedSite) (string, error) {
	deploySite := models.DeploySite{
		FederatedId: federatedSite.FederatedId,
		PartyId:     federatedSite.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return "", err
	}
	if len(deploySiteList) == 0 {
		return "", nil
	}

	kubefateConf, err := models.GetKubenetesConf()
	if err != nil {
		return "", err
	}
	fateboard := fmt.Sprintf("%d.fateboard.kubefate.net", federatedSite.PartyId)
	urlList := strings.Split(kubefateConf.KubenetesUrl, ":")
	if len(urlList) == 3 {
		fateboard = fmt.Sprintf("%d.fateboard.kubefate.net:%s", federatedSite.PartyId, urlList[2])
	} else {
		return "", nil
	}
	return fateboard, nil
}

func GetInstallVersion(federatedSite entity.FederatedSite) (string, error) {
	deploySite := models.DeploySite{
		FederatedId: federatedSite.FederatedId,
		PartyId:     federatedSite.PartyId,
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return "", err
	}
	if len(deploySiteList) == 0 {
		return "", nil
	}
	return deploySiteList[0].FateVersion, nil
}
