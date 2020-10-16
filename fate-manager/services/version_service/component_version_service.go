package version_service

import (
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/comm/util"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/k8s_service"
	"fmt"
	"strconv"
	"time"
)

func GetPullComponentList(componentListReq entity.PullComponentListReq) ([]entity.PullComponentListRespItem, error) {
	if len(componentListReq.FateVersion) == 0 {
		return nil, nil
	}
	componentVersion := models.ComponentVersion{
		FateVersion: componentListReq.FateVersion,
		ProductType: componentListReq.ProductType,
	}

	if componentListReq.PartyId > 0 {
		deploySite := models.DeploySite{
			FederatedId: componentListReq.FederatedId,
			PartyId:     componentListReq.PartyId,
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			IsValid:     int(enum.IS_VALID_YES),
		}
		deploySiteList, err := models.GetDeploySite(&deploySite)
		if err != nil || len(deploySiteList) == 0 {
			return nil, err
		}
		if len(deploySiteList[0].FateVersion) != 0 {
			componentVersion.FateVersion = deploySiteList[0].FateVersion
		}
	}

	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return nil, err
	}
	var pullComponentListResp []entity.PullComponentListRespItem
	for i := 0; i < len(componentVersionList); i++ {
		pullComponentListRespItem := entity.PullComponentListRespItem{
			ImageName:        componentVersionList[i].ComponentName,
			ImageId:          componentVersionList[i].ImageId,
			ImageTag:         componentVersionList[i].ImageTag,
			ImageDescription: componentVersionList[i].ImageDescription,
			ImageVersion:     componentVersionList[i].ImageVersion,
			ImageSize:        componentVersionList[i].ImageSize,
			ImageCreateTime:  componentVersionList[i].ImageCreateTime.UnixNano() / 1e6,
			PullStatus:       entity.IdPair{componentVersionList[i].PullStatus, enum.GetPullStatusString(enum.PullStatusType(componentVersionList[i].PullStatus))},
		}
		pullComponentListResp = append(pullComponentListResp, pullComponentListRespItem)
	}
	return pullComponentListResp, nil
}

func Pull(pullReq entity.PullReq) {
	componentVersion := models.ComponentVersion{
		FateVersion: pullReq.FateVersion,
		ProductType: pullReq.ProductType,
	}
	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return
	}
	for i := 0; i < len(pullReq.PullComponentList); i++ {
		componentName := pullReq.PullComponentList[i]
		for j := 0; j < len(componentVersionList); j++ {
			if componentVersionList[j].ComponentName == componentName {
				if componentVersionList[j].PullStatus == int(enum.PULL_STATUS_YES) {
					logging.Debug(componentName, " is pulled")
					continue
				}
				cmd := fmt.Sprintf("docker pull %s:%s", componentVersionList[j].ImageName, componentVersionList[j].ImageTag)
				if len(setting.KubenetesSetting.Registry) > 0 {
					cmd = fmt.Sprintf("docker pull %s/%s:%s", setting.KubenetesSetting.Registry, componentVersionList[j].ImageName, componentVersionList[j].ImageTag)
				}
				if setting.KubenetesSetting.SudoTag {
					cmd = fmt.Sprintf("sudo %s", cmd)
				}
				logging.Debug(cmd)
				go PullDockerImage(cmd, pullReq.FateVersion, pullReq.ProductType, componentVersionList[j])
				break
			}
		}
	}
}
func PullDockerImage(cmd string, fateVersion string, productType int, info models.ComponentVersion) {
	pullStatus := enum.PULL_STATUS_PULLING
	componentVersion := models.ComponentVersion{
		FateVersion:   fateVersion,
		ProductType:   productType,
		ComponentName: info.ComponentName,
		PullStatus:    int(pullStatus),
	}
	err := models.UpdateComponentVersion(&componentVersion)
	if err != nil {
		logging.Error("update docker pulling status failed")
	}
	result, err := util.ExecCommand(cmd)
	pullStatus = enum.PULL_STATUS_YES
	if err != nil || result == "" {
		pullStatus = enum.PULL_STATUS_FAILED
		logging.Error(cmd, " failed")
	}

	command := fmt.Sprintf("docker images|grep %s|grep %s|awk '{print $2}'", info.ImageName, info.ImageTag)
	if setting.KubenetesSetting.SudoTag {
		command = fmt.Sprintf("sudo %s", command)
	}
	result, _ = util.ExecCommand(command)
	if len(result) > 0 {
		componentVersion.ImageTag = result[0 : len(result)-1]
		componentVersion.ComponentVersion = result[0 : len(result)-1]
		componentVersion.ImageDescription = result[0 : len(result)-1]
		componentVersion.ImageVersion = result[0 : len(result)-1]
	}

	command = fmt.Sprintf("docker images|grep %s|grep %s|awk '{print $3}'", info.ImageName, info.ImageTag)
	if setting.KubenetesSetting.SudoTag {
		command = fmt.Sprintf("sudo %s", command)
	}
	result, _ = util.ExecCommand(command)
	if len(result) > 0 {
		componentVersion.ImageId = result[0 : len(result)-1]
	}

	command = fmt.Sprintf("docker images|grep %s|grep %s|awk '{print $7}'", info.ImageName, info.ImageTag)
	if setting.KubenetesSetting.SudoTag {
		command = fmt.Sprintf("sudo %s", command)
	}
	result, _ = util.ExecCommand(command)
	if len(result) > 0 {
		componentVersion.ImageSize = result[0 : len(result)-1]
	}
	componentVersion.PullStatus = int(pullStatus)
	err = models.UpdateComponentVersion(&componentVersion)
	if err != nil {
		logging.Error("update docker pull status failed")
	}
	return
}

func GetDefaultPort(componentName string) int {
	var port int
	k8sinfo, err := models.GetKubenetesConf()
	if err != nil || k8sinfo.Id == 0 {
		return 0
	}
	if componentName == "proxy" {
		port = 9330
	} else if componentName == "roll" {
		port = 30001
	} else if componentName == "meta-service" {
		port = 30002
	} else if componentName == "egg" {
		port = 30003
	} else if componentName == "mysql" {
		port = 3306
	} else if componentName == "federation" {
		port = 9320
	} else if componentName == "fateboard" {
		port = 8080
	} else if componentName == "fateflow" {
		port = k8sinfo.PythonPort + 1
	} else if componentName == "serving-server" {
		port = 9340
	} else if componentName == "serving-proxy" {
		port = 9360
	} else if componentName == "clustermanager" {
		port = 4670
	} else if componentName == "nodemanager" {
		port = 4671
	} else if componentName == "rollsite" {
		port = k8sinfo.RollsitePort + 1
	}
	str := fmt.Sprintf("componentName:%smPythonPort:%d,RollsitePort:%d,port:%d", componentName, k8sinfo.PythonPort, k8sinfo.RollsitePort, port)
	logging.Debug(str)
	return port
}
func CommitImagePull(commitImagePullReq entity.CommitImagePullReq) (int, error) {
	if commitImagePullReq.FateVersion == "" {
		return e.INVALID_PARAMS, nil
	}
	componentVersion := models.ComponentVersion{
		FateVersion: commitImagePullReq.FateVersion,
		ProductType: commitImagePullReq.ProductType,
	}
	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	commitTag := false
	for i := 0; i < len(componentVersionList); i++ {
		if componentVersionList[i].PullStatus == int(enum.PULL_STATUS_NO) {
			commitTag = true
			break
		}
	}
	if commitTag {
		return e.ERROR_IMAGE_NOT_ALL_PULL_FAIL, err
	}
	siteInfo, err := models.GetSiteInfo(commitImagePullReq.PartyId, commitImagePullReq.FederatedId)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	var pythonPort int
	var proxyPort int
	updatePortTag := false
	for i := 0; i < len(componentVersionList); i++ {
		port := GetDefaultPort(componentVersionList[i].ComponentName)

		deployComponent := models.DeployComponent{
			FederatedId:      commitImagePullReq.FederatedId,
			PartyId:          commitImagePullReq.PartyId,
			SiteName:         siteInfo.SiteName,
			ProductType:      commitImagePullReq.ProductType,
			FateVersion:      commitImagePullReq.FateVersion,
			ComponentVersion: componentVersionList[i].ComponentVersion,
			ComponentName:    componentVersionList[i].ComponentName,
			VersionIndex:     componentVersionList[i].VersionIndex,
			StartTime:        time.Now(),
			Address:          k8s_service.GetNodeIp(commitImagePullReq.FederatedId, commitImagePullReq.PartyId) + ":" + strconv.Itoa(port),
			DeployStatus:     int(enum.DeployStatus_PULLED),
			IsValid:          int(enum.IS_VALID_YES),
			CreateTime:       time.Now(),
			UpdateTime:       time.Now(),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if len(deployComponentList) > 0 {
			updatePortTag = true
			continue
		}
		err = models.AddDeployComponent(&deployComponent)
		if err != nil {
			logging.Error("Add Deploy Component Filed")
		}
		if componentVersionList[i].ComponentName == "fateflow" {
			pythonPort = port
		} else if componentVersionList[i].ComponentName == "rollsite" {
			proxyPort = port
		}
		str := fmt.Sprintf("componentName:%s,PythonPort:%d,RollsitePort:%d,port:%d", componentVersionList[i].ComponentName, pythonPort, proxyPort, port)
		logging.Debug(str)
	}
	fateVersion := models.FateVersion{
		FateVersion: commitImagePullReq.FateVersion,
		ProductType: commitImagePullReq.ProductType,
	}
	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil || len(fateVersionList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	deploySite := models.DeploySite{
		FederatedId: commitImagePullReq.FederatedId,
		PartyId:     commitImagePullReq.PartyId,
		ProductType: commitImagePullReq.ProductType,
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if len(deploySiteList) == 0 {
		deploySite.FateVersion = commitImagePullReq.FateVersion
		deploySite.DeployStatus = int(enum.DeployStatus_PULLED)
		deploySite.ChartVersion = fateVersionList[0].ChartVersion
		deploySite.VersionIndex = fateVersionList[0].VersionIndex
		deploySite.CreateTime = time.Now()
		deploySite.UpdateTime = time.Now()

		if !updatePortTag {
			deploySite.PythonPort = pythonPort
			deploySite.RollsitePort = proxyPort
		}
		deploySite.ClickType = int(enum.ClickType_PULL)
		err = models.AddDeploySite(&deploySite)
		if err != nil {
			logging.Error("Add Deploy Site Filed")
			return e.ERROR_UPDATE_DB_FAIL, err
		}
	} else {
		var data = make(map[string]interface{})
		data["fate_version"] = commitImagePullReq.FateVersion
		data["deploy_status"] = int(enum.DeployStatus_PULLED)
		data["chart_version"] = fateVersionList[0].ChartVersion
		data["version_index"] = fateVersionList[0].VersionIndex
		if !updatePortTag {
			data["python_port"] = pythonPort
			data["rollsite_port"] = proxyPort
		}
		data["create_time"] = time.Now()
		data["update_time"] = time.Now()
		data["click_type"] = int(enum.ClickType_PULL)
		models.UpdateDeploySite(data, deploySite)
	}
	kubefateConf, _ := models.GetKubenetesConf()
	kubenetesConf := models.KubenetesConf{
		KubenetesUrl: kubefateConf.KubenetesUrl,
	}
	if !updatePortTag {
		var kubeconf = make(map[string]interface{})

		kubeconf["python_port"] = pythonPort
		kubeconf["rollsite_port"] = proxyPort

		models.UpdateKubenetesConf(kubeconf, kubenetesConf)
	}
	info := models.SiteInfo{
		FederatedId: commitImagePullReq.FederatedId,
		PartyId:     commitImagePullReq.PartyId,
		Status:      int(enum.SITE_STATUS_JOINED),
	}
	var data = make(map[string]interface{})
	data["fate_version"] = commitImagePullReq.FateVersion
	models.UpdateSiteByCondition(data, info)
	return e.SUCCESS, nil
}
