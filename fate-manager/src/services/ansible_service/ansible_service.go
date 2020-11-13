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
		if conf.ClickType > 0 {
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

func CheckSystem(checkSystemReq entity.CheckSystemReq) ([]entity.Prepare, error) {
	conf, err := models.GetKubenetesConf(int(enum.DeployType_ANSIBLE))
	if err != nil {
		return nil, err
	}
	if conf.Id == 0 {
		return nil, err
	}
	var prepareReq entity.PrepareReq
	err = json.Unmarshal([]byte(conf.NodeList), &prepareReq)
	if err != nil {
		return nil, err
	}
	startTime := time.Now().UnixNano()
	result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePrepareUri), prepareReq, nil)
	endTime := time.Now().UnixNano()
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var ansiblePrepareResp app.AnsibleCheckResp
	err = json.Unmarshal([]byte(result.Body), &ansiblePrepareResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	if ansiblePrepareResp.Code == e.SUCCESS {
		ansiblePrepareItemJson, _ := json.Marshal(ansiblePrepareResp.Data)
		kubenetesConf := models.KubenetesConf{
			DeployType: int(enum.DeployType_ANSIBLE),
		}
		var data = make(map[string]interface{})
		data["ansible_check"] = string(ansiblePrepareItemJson)

		var list []entity.Prepare
		for i := 0; i < len(ansiblePrepareResp.Data); {
			ansiblePrepareItem := ansiblePrepareResp.Data[i]
			if ansiblePrepareItem.Ip == checkSystemReq.Ip {
				list = ansiblePrepareItem.List
				for j := 0; j < len(list); j++ {
					list[j].Duration = (endTime - startTime)/1e6
				}
				break
			}
		}
		models.UpdateKubenetesConf(data, &kubenetesConf)
		return list, nil
	}
	return nil, nil
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

func GetDeployAnsibleList(deployAnsibleReq entity.DeployAnsibleReq) (*entity.DeployAnsibleResp, error) {
	return nil, nil
}
