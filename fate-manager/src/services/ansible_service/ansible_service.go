package ansible_service

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/models"
	"time"
)

func ConnectAnsible(ansibleReq entity.AnsibleReq) (int, error) {
	if ansibleReq.PartyId == 0 || len(ansibleReq.Url) == 0 {
		return e.INVALID_PARAMS, nil
	}
	result, err := http.POST(http.Url(ansibleReq.Url), nil, nil)
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
	if commResp.Code == e.SUCCESS {
		kubenetesConf := models.KubenetesConf{
			KubenetesUrl: ansibleReq.Url,
			DeployType:   int(enum.DeployType_ANSIBLE),
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}
		models.AddKubenetesConf(&kubenetesConf)

		deploySite := models.DeploySite{
			PartyId:            ansibleReq.PartyId,
			ProductType:        ansibleReq.ProductType,
			Status:             int(enum.SITE_RUN_STATUS_UNKNOWN),
			ClickType:          int(enum.AnsibleClickType_CONNECT),
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
		models.AddDeploySite(&deploySite)
		return e.SUCCESS, nil
	}
	return e.ERROR_CONNECT_ANSIBLE_FAIL, nil
}

func Prepare(prepareRed entity.PrepareReq) (int, error) {
	return e.SUCCESS, nil
}
func UpdateMachine(prepareReq entity.PrepareReq) (int, error) {
	return e.SUCCESS, nil
}
func CheckSystem(checkSystemReq entity.CheckSystemReq) (int, error) {
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
func GetCheckSytemList(checkSystemReq entity.CheckSystemReq) (*entity.CheckSystemResp, error) {
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
	for i :=0;i<len(itemList) ;i++  {
		checkItem := entity.CheckItem{
			TestItem:     itemList[i],
			TestDuration: 0,
			Status:       0,
		}
		checkItemList = append(checkItemList,checkItem)
	}

	CheckSystemResp := entity.CheckSystemResp{
		PartyId:       checkSystemReq.PartyId,
		CheckItemList: checkItemList,
	}
	return &CheckSystemResp, nil
}
func GetDeployAnsibleList(deployAnsibleReq entity.DeployAnsibleReq) (*entity.DeployAnsibleResp, error) {
	return nil, nil
}
