package ansible_service

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/entity"
	"fate.manager/models"
	"time"
)

func ConnectAnsible(ansibleReq entity.AnsibleReq) (int, error) {
	if ansibleReq.PartyId == 0 || len(ansibleReq.Url) == 0 {
		return e.INVALID_PARAMS, nil
	}
	result, err := http.POST(http.Url(ansibleReq.Url+setting.AnsiblePrepareUri), nil, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var commResp app.CommResp
	//err = json.Unmarshal([]byte(result.Body), &commResp)
	//if err != nil {
	//	logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
	//	return e.ERROR_PARSE_JSON_ERROR, err
	//}
	if commResp.Code == e.SUCCESS || true {
		kubenetesConf := models.KubenetesConf{
			KubenetesUrl: ansibleReq.Url,
			DeployType:   int(enum.DeployType_ANSIBLE),
			ClickType:    int(enum.AnsibleClickType_CONNECT),
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}
		conf, _ := models.GetKubenetesConf(int(enum.DeployType_ANSIBLE))
		if conf.Id == 0 {
			models.AddKubenetesConf(&kubenetesConf)
		}
		deploySite := models.DeploySite{
			PartyId:            ansibleReq.PartyId,
			ProductType:        ansibleReq.ProductType,
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
		if conf.ClickType > 0{
			deploySite.ClickType = conf.ClickType
		}
		deploySite.ClickType = conf.ClickType
		deploySiteList, _ := models.GetDeploySite(&deploySite)
		if len(deploySiteList) == 0 {
			models.AddDeploySite(&deploySite)
		}
		return e.SUCCESS, nil
	}
	return e.ERROR_CONNECT_ANSIBLE_FAIL, nil
}

func Prepare(prepareReq entity.PrepareReq) (int, error) {
	if len(prepareReq.ManagerNode) == 0 || len(prepareReq.ControlNode.Ip) == 0 {
		return e.INVALID_PARAMS, nil
	}
	ansibleInfo := models.AnsibleInfo{
		NodeType:   int(enum.NodeType_Control),
		IsValid:    int(enum.IS_VALID_YES),
		CreateTime: time.Now(),
		UpdateTime: time.Now(),
	}
	var data = make(map[string]interface{})
	data["is_valid"] = int(enum.IS_VALID_NO)
	models.UpdateAnsibleInfoByCondition(data, ansibleInfo)

	ansibleInfo.Ip = prepareReq.ControlNode.Ip
	ansibleInfo.IsValid = int(enum.IS_VALID_YES)
	models.AddAnsibleInfo(&ansibleInfo)

	ansibleInfo = models.AnsibleInfo{
		NodeType:   int(enum.NodeType_Manager),
		IsValid:    int(enum.IS_VALID_YES),
		CreateTime: time.Now(),
		UpdateTime: time.Now(),
	}
	if len(prepareReq.ManagerNode) >0 {
		data = make(map[string]interface{})
		data["is_valid"] = int(enum.IS_VALID_NO)
		models.UpdateAnsibleInfoByCondition(data, ansibleInfo)
	}
	for i := 0; i < len(prepareReq.ManagerNode); i++ {
		ansibleInfo.Id =0
		ansibleInfo.Ip = prepareReq.ManagerNode[i].Ip
		ansibleInfo.IsValid = int(enum.IS_VALID_YES)
		models.AddAnsibleInfo(&ansibleInfo)
	}
	return e.SUCCESS, nil
}
func UpdateMachine(prepareReq entity.PrepareReq) (int, error) {
	if len(prepareReq.ControlNode.Ip) > 0 {
		ansibleInfo := models.AnsibleInfo{
			NodeType: int(enum.NodeType_Control),
			IsValid:  int(enum.IS_VALID_YES),
		}
		var data = make(map[string]interface{})
		data["ip"] = prepareReq.ControlNode.Ip
		models.UpdateAnsibleInfoByCondition(data, ansibleInfo)
	}
	if len(prepareReq.ManagerNode) > 0 {
		ansibleInfo := models.AnsibleInfo{
			NodeType: int(enum.NodeType_Manager),
			IsValid:  int(enum.IS_VALID_YES),
		}
		var data = make(map[string]interface{})
		data["is_valid"] = int(enum.IS_VALID_NO)
		models.UpdateAnsibleInfoByCondition(data, ansibleInfo)
		for i := 0; i < len(prepareReq.ManagerNode); i++ {
			ansibleInfo = models.AnsibleInfo{
				Ip:         prepareReq.ManagerNode[i].Ip,
				NodeType:   int(enum.NodeType_Manager),
				IsValid:    int(enum.IS_VALID_YES),
				CreateTime: time.Now(),
				UpdateTime: time.Now(),
			}
			models.AddAnsibleInfo(&ansibleInfo)
		}
	}
	return e.SUCCESS, nil
}
func CheckSystem() (int, error) {
	conf, err := models.GetKubenetesConf(int(enum.DeployType_ANSIBLE))
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if conf.Id == 0 {
		return e.ERROR_ANSIBLE_CONNECT_FIRST, err
	}
	ansibleInfo := models.AnsibleInfo{
		IsValid: int(enum.IS_VALID_YES),
	}
	ansibleInfoList, err := models.GetAnsibleInfoList(ansibleInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	var controlNode entity.Machine
	var managerNode []entity.Machine
	for i := 0; i < len(ansibleInfoList); i++ {
		if ansibleInfoList[i].NodeType == int(enum.NodeType_Control) {
			controlNode.Ip = ansibleInfoList[i].Ip
		} else {
			item := entity.Machine{Ip: ansibleInfoList[i].Ip}
			managerNode = append(managerNode, item)
		}
	}
	prepareReq := entity.PrepareReq{
		ControlNode: controlNode,
		ManagerNode: managerNode,
	}
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePrepareUri), prepareReq, nil)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var commResp app.CommResp
	err = json.Unmarshal([]byte(result.Body), &commResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if commResp.Code == e.SUCCESS || true {

	}
	return e.SUCCESS, nil
}
func StartDeployAnsible(deployAnsibleReq entity.DeployAnsibleReq) (int, error) {
	return e.SUCCESS, nil
}

func LocalUpload(localUploadReq entity.LocalUploadReq) (int, error) {
	return e.SUCCESS, nil
}
func AutoAcquire(autoAcquireReq entity.AutoAcquireReq) (int, error) {
	return e.SUCCESS, nil
}
func GetCheckSytemList() (*entity.CheckSystemResp, error) {
	var checkItemList []entity.CheckItem
	var itemList = []string{"Host resources and operating system",
		"Network requirements",
		"Basic environment configuration",
		"Hostname configuration",
		"Join host mapping",
		"Turn off selinux",
		"Modifying Linux system parameters",
		"Turn off the firewall",
		"Software environment initialization",
		"Increase virtual memory"}
	for i := 0; i < len(itemList); i++ {
		checkItem := entity.CheckItem{
			TestItem:     itemList[i],
			TestDuration: 0,
			Status:       0,
		}
		checkItemList = append(checkItemList, checkItem)
	}

	CheckSystemResp := entity.CheckSystemResp{
		CheckItemList: checkItemList,
	}
	return &CheckSystemResp, nil
}
func GetDeployAnsibleList(deployAnsibleReq entity.DeployAnsibleReq) (*entity.DeployAnsibleResp, error) {
	return nil, nil
}
