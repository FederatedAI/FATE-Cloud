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
package job_service

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
	"fate.manager/services/federated_service"
	"fate.manager/services/k8s_service"
	"fate.manager/services/user_service"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"strconv"
	"strings"
	"time"
)

func JobTask() {

	deployJob := models.DeployJob{
		Status: int(enum.JOB_STATUS_RUNNING),
	}
	deployJobList, err := models.GetDeployJob(deployJob, true)
	if err != nil {
		return
	}
	for i := 0; i < len(deployJobList); i++ {
		if deployJobList[i].DeployType == int(enum.DeployType_ANSIBLE) {
			AnsibleJobQuery(deployJobList[i])
		} else {
			user := util.User{
				UserName: "admin",
				Password: "admin",
			}
			kubefateUrl := k8s_service.GetKubenetesUrl(enum.DeployType_K8S)
			token, err := util.GetToken(kubefateUrl, user)
			if err != nil {
				continue
			}
			authorization := fmt.Sprintf("Bearer %s", token)
			head := make(map[string]interface{})
			head["Authorization"] = authorization
			result, err := http.GET(http.Url(kubefateUrl+"/v1/job/"+deployJobList[i].JobId), nil, head)
			if err != nil || result == nil {
				continue
			}
			var jobQueryResp entity.JobQueryResp
			index := bytes.IndexByte([]byte(result.Body), 0)
			err = json.Unmarshal([]byte(result.Body)[:index], &jobQueryResp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				continue
			}

			var clusterConfig entity.ClusterConfig
			if result.StatusCode == 200 {

				deployComponent := models.DeployComponent{
					FederatedId: deployJobList[i].FederatedId,
					PartyId:     deployJobList[i].PartyId,
					ProductType: deployJobList[i].ProductType,
					IsValid:     int(enum.IS_VALID_YES),
				}
				deployComponentList, err := models.GetDeployComponent(deployComponent)
				if err != nil || len(deployComponentList) == 0 {
					logging.Error("no site info")
					continue
				}
				deploySite := models.DeploySite{
					FederatedId: deployJobList[i].FederatedId,
					PartyId:     deployJobList[i].PartyId,
					ProductType: deployJobList[i].ProductType,
					IsValid:     int(enum.IS_VALID_YES),
				}
				deploySiteList, err := models.GetDeploySite(&deploySite)
				if err != nil || len(deploySiteList) == 0 {
					continue
				}

				deployJob := models.DeployJob{
					FederatedId: deployJobList[i].FederatedId,
					PartyId:     deployJobList[i].PartyId,
					ProductType: deployJobList[i].ProductType,
					JobId:       deployJobList[i].JobId,
				}
				deployStatus := enum.DeployStatus_INSTALLED
				logging.Debug(jobQueryResp)
				if jobQueryResp.Data.Status == "Success" {
					if err := json.Unmarshal([]byte(deploySiteList[0].Config), &clusterConfig); err != nil {
						continue
					}
					deployJob.Status = int(enum.JOB_STATUS_SUCCESS)
					var componentVersonMap = make(map[string]entity.ComponentVersionDetail)
					for j := 0; j < len(deployComponentList); j++ {
						componentVersionDetail := entity.ComponentVersionDetail{
							Version: deployComponentList[j].ComponentVersion,
							Address: deployComponentList[j].Address,
						}
						componentVersonMap[deployComponentList[j].ComponentName] = componentVersionDetail
						autoTest := models.AutoTest{
							FederatedId: deployJobList[i].FederatedId,
							PartyId:     deployJobList[i].PartyId,
							ProductType: deployJobList[i].ProductType,
							FateVersion: deployComponentList[j].FateVersion,
							TestItem:    deployComponentList[j].ComponentName,
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
						if j == len(deployComponentList)-1 {
							InserTestItem(autoTest, enum.TEST_ITEM_TOY)
							InserTestItem(autoTest, enum.TEST_ITEM_FAST)
							InserTestItem(autoTest, enum.TEST_ITEM_NORMAL)
							InserTestItem(autoTest, enum.TEST_ITEM_SINGLE)
						}
					}

					componentVersonMapjson, _ := json.Marshal(componentVersonMap)
					site := models.SiteInfo{
						FederatedId:      deployJobList[i].FederatedId,
						PartyId:          deployJobList[i].PartyId,
						FateVersion:      deploySiteList[0].FateVersion,
						ComponentVersion: string(componentVersonMapjson),
						ServiceStatus:    int(enum.SERVICE_STATUS_UNAVAILABLE),
						//FateServingVersion: "1.2.1",
					}
					models.UpdateSite(&site)
				} else if jobQueryResp.Data.Status == "Running" {
					deployJob.Status = int(enum.JOB_STATUS_RUNNING)
					deployStatus = enum.DeployStatus_INSTALLING
				} else if jobQueryResp.Data.Status == "Failed" {
					deployJob.Status = int(enum.JOB_STATUS_FAILED)
					deployStatus = enum.DeployStatus_INSTALLED_FAILED
				} else if jobQueryResp.Data.Status == "Created" {
					deployStatus = enum.DeployStatus_INSTALLING
				}
				var data = make(map[string]interface{})
				data["status"] = deployJob.Status
				data["cluster_id"] = jobQueryResp.Data.ClusterId
				data["start_time"] = jobQueryResp.Data.StartTime
				data["end_time"] = jobQueryResp.Data.EndTime
				data["result"] = jobQueryResp.Data.Result
				data["deploy_status"] = int(deployStatus)
				data["update_time"] = time.Now()
				models.UpdateDeployJob(data, &deployJob)

				data = make(map[string]interface{})
				data["deploy_status"] = int(deployStatus)
				if jobQueryResp.Data.Status == "Success" {
					data["cluster_id"] = jobQueryResp.Data.ClusterId
				}
				starttime := jobQueryResp.Data.StartTime.UnixNano() / 1e6
				endtime := jobQueryResp.Data.EndTime.UnixNano() / 1e6

				var duration int64
				duration = 0
				if endtime-starttime >= 0 {
					duration = endtime - starttime
				}
				data["duration"] = duration
				if jobQueryResp.Data.Status == "Success" {
					data["proxy_port"] = clusterConfig.Proxy.NodePort
				}
				deploySite.JobId = deployJobList[i].JobId
				data["click_type"] = int(enum.ClickType_PULL)
				models.UpdateDeploySite(data, deploySite)

				data = make(map[string]interface{})
				data["deploy_status"] = int(deployStatus)
				data["duration"] = duration
				data["start_time"] = jobQueryResp.Data.StartTime
				data["end_time"] = jobQueryResp.Data.EndTime
				models.UpdateDeployComponent(data, deployComponent)

				if jobQueryResp.Data.Status == "Success" || jobQueryResp.Data.Status == "Failed" {
					siteInfo, err := models.GetSiteInfo(deploySiteList[0].PartyId, deploySiteList[0].FederatedId)
					if err != nil {
						continue
					}
					federatedInfo, err := models.GetFederatedUrlById(deploySiteList[0].FederatedId)
					if err != nil {
						continue
					}
					models.GetFederatedUrlById(deploySiteList[0].FederatedId)
					var cloudSystemAddList []entity.CloudSystemAdd
					for k := 0; k < len(deployComponentList); k++ {
						if deployJob.Status == int(enum.JOB_STATUS_SUCCESS) || deployJob.Status == int(enum.JOB_STATUS_FAILED) {
							cloudSystemAdd := entity.CloudSystemAdd{
								DetectiveStatus:  1,
								SiteId:           siteInfo.SiteId,
								ComponentName:    deployComponentList[k].ComponentName,
								JobType:          enum.GetJobTypeString(enum.JobType(deployJobList[i].JobType)),
								JobStatus:        1,
								UpdateTime:       jobQueryResp.Data.EndTime.UnixNano() / 1e6,
								ComponentVersion: deployComponentList[k].ComponentVersion,
							}
							if deployJob.Status == int(enum.JOB_STATUS_SUCCESS) {
								cloudSystemAdd.JobStatus = 2
							}
							cloudSystemAddList = append(cloudSystemAddList, cloudSystemAdd)
						}
					}
					if len(cloudSystemAddList) > 0 {
						cloudSystemAddJson, _ := json.Marshal(cloudSystemAddList)
						headInfo := util.HeaderInfo{
							AppKey:    siteInfo.AppKey,
							AppSecret: siteInfo.AppSecret,
							PartyId:   siteInfo.PartyId,
							Body:      string(cloudSystemAddJson),
							Role:      siteInfo.Role,
							Uri:       setting.SystemAddUri,
						}
						headerInfoMap := util.GetHeaderInfo(headInfo)
						result, err := http.POST(http.Url(federatedInfo.FederatedUrl+setting.SystemAddUri), cloudSystemAddList, headerInfoMap)
						if err != nil {
							logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
							continue
						}
						if len(result.Body) > 0 {
							var updateResp entity.CloudCommResp
							err = json.Unmarshal([]byte(result.Body), &updateResp)
							if err != nil {
								logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
								return
							}
							if updateResp.Code == e.SUCCESS {
								msg := "federatedId:" + strconv.Itoa(siteInfo.FederatedId) + ",partyId:" + strconv.Itoa(siteInfo.PartyId) + ",status:system add success"
								logging.Debug(msg)
							}
						}
					}
				}
			}
		}
	}
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
		deployComponent := models.DeployComponent{
			PartyId:     DeployJob.PartyId,
			ProductType: DeployJob.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil || len(deployComponentList) == 0 {
			logging.Error("no site info")
			return
		}
		deploySite := models.DeploySite{
			PartyId:     DeployJob.PartyId,
			ProductType: DeployJob.ProductType,
			IsValid:     int(enum.IS_VALID_YES),
		}
		deploySiteList, err := models.GetDeploySite(&deploySite)
		if err != nil || len(deploySiteList) == 0 {
			return
		}

		deployJob := models.DeployJob{
			PartyId:     DeployJob.PartyId,
			ProductType: DeployJob.ProductType,
			JobId:       DeployJob.JobId,
		}
		deployStatus := enum.DeployStatus_INSTALLED
		logging.Debug(queryResponse)
		if queryResponse.Data.Status == "success" {
			deployJob.Status = int(enum.JOB_STATUS_SUCCESS)
			var componentVersonMap = make(map[string]entity.ComponentVersionDetail)
			for j := 0; j < len(deployComponentList); j++ {
				componentVersionDetail := entity.ComponentVersionDetail{
					Version: deployComponentList[j].ComponentVersion,
					Address: deployComponentList[j].Address,
				}
				componentVersonMap[deployComponentList[j].ComponentName] = componentVersionDetail
				autoTest := models.AutoTest{
					PartyId:     DeployJob.PartyId,
					ProductType: DeployJob.ProductType,
					FateVersion: deployComponentList[j].FateVersion,
					TestItem:    deployComponentList[j].ComponentName,
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
				if j == len(deployComponentList)-1 {
					InserTestItem(autoTest, enum.TEST_ITEM_TOY)
					InserTestItem(autoTest, enum.TEST_ITEM_FAST)
					InserTestItem(autoTest, enum.TEST_ITEM_NORMAL)
					InserTestItem(autoTest, enum.TEST_ITEM_SINGLE)
				}
			}

			componentVersonMapjson, _ := json.Marshal(componentVersonMap)
			site := models.SiteInfo{
				PartyId:          DeployJob.PartyId,
				FateVersion:      deploySiteList[0].FateVersion,
				ComponentVersion: string(componentVersonMapjson),
				ServiceStatus:    int(enum.SERVICE_STATUS_UNAVAILABLE),
			}
			models.UpdateSite(&site)
		} else if queryResponse.Data.Status == "running" || queryResponse.Data.Status == "ready" {
			deployJob.Status = int(enum.JOB_STATUS_RUNNING)
			deployStatus = enum.DeployStatus_INSTALLING
		} else if queryResponse.Data.Status == "failed" || queryResponse.Data.Status == "canceled" || queryResponse.Data.Status == "timeout" || queryResponse.Data.Status == "skipped" {
			deployJob.Status = int(enum.JOB_STATUS_FAILED)
			deployStatus = enum.DeployStatus_INSTALLED_FAILED
		}
		startTime := time.Unix(queryResponse.Data.StartTime/1000, 0)
		endTime := time.Unix(queryResponse.Data.EndTime/1000, 0)
		var data = make(map[string]interface{})
		data["status"] = deployJob.Status
		data["start_time"] = startTime
		data["end_time"] = endTime
		data["deploy_status"] = int(deployStatus)
		data["update_time"] = time.Now()
		models.UpdateDeployJob(data, &deployJob)

		data = make(map[string]interface{})
		data["deploy_status"] = int(deployStatus)

		var duration int64
		duration = 0
		if queryResponse.Data.EndTime-queryResponse.Data.StartTime >= 0 {
			duration = queryResponse.Data.EndTime - queryResponse.Data.StartTime
		}
		data["duration"] = duration
		deploySite.JobId = DeployJob.JobId
		data["click_type"] = int(enum.AnsibleClickType_ACQUISITON)
		models.UpdateDeploySite(data, deploySite)

		data = make(map[string]interface{})
		data["deploy_status"] = int(deployStatus)
		data["duration"] = duration
		data["start_time"] = startTime
		data["end_time"] = endTime
		models.UpdateDeployComponent(data, deployComponent)

		if queryResponse.Data.Status == "success" || queryResponse.Data.Status == "Failed" {
			siteInfo, err := models.GetSiteInfo(deploySiteList[0].PartyId, deploySiteList[0].FederatedId)
			if err != nil {
				return
			}
			federatedInfo, err := models.GetFederatedUrlById(deploySiteList[0].FederatedId)
			if err != nil {
				return
			}
			models.GetFederatedUrlById(deploySiteList[0].FederatedId)
			var cloudSystemAddList []entity.CloudSystemAdd
			for k := 0; k < len(deployComponentList); k++ {
				if deployJob.Status == int(enum.JOB_STATUS_SUCCESS) || deployJob.Status == int(enum.JOB_STATUS_FAILED) {
					cloudSystemAdd := entity.CloudSystemAdd{
						DetectiveStatus:  1,
						SiteId:           siteInfo.SiteId,
						ComponentName:    deployComponentList[k].ComponentName,
						JobType:          enum.GetJobTypeString(enum.JobType(DeployJob.JobType)),
						JobStatus:        1,
						UpdateTime:       queryResponse.Data.EndTime,
						ComponentVersion: deployComponentList[k].ComponentVersion,
					}
					cloudSystemAddList = append(cloudSystemAddList, cloudSystemAdd)
				}
			}
			if len(cloudSystemAddList) > 0 {
				cloudSystemAddJson, _ := json.Marshal(cloudSystemAddList)
				headInfo := util.HeaderInfo{
					AppKey:    siteInfo.AppKey,
					AppSecret: siteInfo.AppSecret,
					PartyId:   siteInfo.PartyId,
					Body:      string(cloudSystemAddJson),
					Role:      siteInfo.Role,
					Uri:       setting.SystemAddUri,
				}
				headerInfoMap := util.GetHeaderInfo(headInfo)
				result, err := http.POST(http.Url(federatedInfo.FederatedUrl+setting.SystemAddUri), cloudSystemAddList, headerInfoMap)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
					return
				}
				if len(result.Body) > 0 {
					var updateResp entity.CloudCommResp
					err = json.Unmarshal([]byte(result.Body), &updateResp)
					if err != nil {
						logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
						return
					}
					if updateResp.Code == e.SUCCESS {
						msg := "federatedId:" + strconv.Itoa(siteInfo.FederatedId) + ",partyId:" + strconv.Itoa(siteInfo.PartyId) + ",status:system add success"
						logging.Debug(msg)
					}
				}
			}
		}
	}
}

func InserTestItem(autoTest models.AutoTest, testItemType enum.TestItemType) {
	autoTest.TestItem = enum.GetTestItemString(testItemType)
	autoTestList, _ := models.GetAutoTest(autoTest)
	if len(autoTestList) == 0 {
		models.AddAutoTest(autoTest)
	}
}

func TestOnlyTask() {
	deploySite := models.DeploySite{
		ProductType: int(enum.PRODUCT_TYPE_FATE),
		ToyTestOnly: int(enum.ToyTestOnly_TESTING),
		IsValid:     int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return
	}
	for i := 0; i < len(deploySiteList); i++ {
		if deploySiteList[i].DeployType == int(enum.DeployType_ANSIBLE) {
			go AnsibleTestOnly(deploySiteList[i])
		} else {
			logdir := fmt.Sprintf("./testLog/toy/fate-%d.log", deploySiteList[i].FederatedId, deploySiteList[i].PartyId)
			if !util.FileExists(logdir) {
				continue
			}
			file, err := os.Open(logdir)
			if err != nil {
				log.Fatal(err)
			}
			defer file.Close()
			scanner := bufio.NewScanner(file)
			testtag := false
			existfile := false
			for scanner.Scan() {
				existfile = true
				lineText := scanner.Text()
				ret := strings.Index(lineText, "success to calculate secure_sum")
				if ret > 0 {
					testtag = true
					break
				}
			}
			if existfile {
				var data = make(map[string]interface{})
				data["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_NO)
				data["toy_test_only"] = int(enum.ToyTestOnly_FAILED)
				if testtag {
					data["toy_test_only"] = int(enum.ToyTestOnly_SUCCESS)
				}
				deploySite := models.DeploySite{
					FederatedId: deploySiteList[i].FederatedId,
					PartyId:     deploySiteList[i].PartyId,
					ProductType: deploySiteList[i].ProductType,
					IsValid:     int(enum.IS_VALID_YES),
				}
				models.UpdateDeploySite(data, deploySite)
			}
		}
	}
}

func AnsibleTestOnly(deploySite models.DeploySite) {

	deployComponent := models.DeployComponent{
		PartyId:       deploySite.PartyId,
		ComponentName: "fateflow",
		ProductType:   int(enum.PRODUCT_TYPE_FATE),
		DeployType:    int(enum.DeployType_ANSIBLE),
		IsValid:       int(enum.IS_VALID_YES),
	}
	deployComponentList, err := models.GetDeployComponent(deployComponent)
	if err != nil || len(deployComponentList) == 0 {
		return
	}
	address := strings.Split(deployComponentList[0].Address, ":")

	ResultReq := entity.AnsibleToyTestResultReq{
		Limit:    500,
		Ip:       address[0],
		TestType: "toy",
	}
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/log"), ResultReq, nil)
	if result == nil || err != nil {
		return
	}
	var resultResp entity.AnsibleTestResultResponse
	err = json.Unmarshal([]byte(result.Body), &resultResp)
	if err != nil {
		return
	}
	if resultResp.Code == e.SUCCESS {
		var sitedata = make(map[string]interface{})
		findKey := "success to calculate secure_sum"
		if JudgeResult(deploySite.PartyId, "toy", findKey, resultResp.Data) {
			sitedata["toy_test_only"] = int(enum.ToyTestOnly_SUCCESS)
			sitedata["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_NO)
		} else if len(resultResp.Data) <= 1 {
			sitedata["toy_test_only"] = int(enum.ToyTestOnly_TESTING)
		} else if len(resultResp.Data) > 1 {
			sitedata["toy_test_only"] = int(enum.ToyTestOnly_FAILED)
			sitedata["toy_test_only_read"] = int(enum.ToyTestOnlyTypeRead_NO)
		}
		models.UpdateDeploySite(sitedata, deploySite)
	}
}

type ApplyResultReq struct {
	Institutions string `json:"institutions"`
}

func ApplyResultTask(info *models.AccountInfo) {
	approvedReq := entity.InstitutionsReq{
		Institutions: info.Institutions,
		PageNum:      1,
		PageSize:     100,
	}
	approvedReqJson, _ := json.Marshal(approvedReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    info.AppKey,
		UserAppSecret: info.AppSecret,
		FateManagerId: info.CloudUserId,
		Body:          string(approvedReqJson),
		Uri:           setting.ApprovedUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.ApprovedUri), approvedReq, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return
	}
	var approveResp entity.ApproveResp
	err = json.Unmarshal([]byte(result.Body), &approveResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return
	}
	if approveResp.Code == e.SUCCESS {
		applySiteInfo := models.ApplySiteInfo{
			Status: int(enum.IS_VALID_ING),
		}
		waitAuditList, err := models.GetApplySiteInfo(applySiteInfo)
		if err != nil {
			return
		}
		var waitAudit []entity.IdPair
		if len(waitAuditList) > 0 {
			err = json.Unmarshal([]byte(waitAuditList[0].Institutions), &waitAudit)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
		}
		var validAudit []entity.AuditPair
		applySiteInfo.Status = int(enum.IS_VALID_YES)
		validAuditList, err := models.GetApplySiteInfo(applySiteInfo)
		if err != nil {
			return
		}
		if len(validAuditList) > 0 {
			err = json.Unmarshal([]byte(validAuditList[0].Institutions), &validAudit)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
		}

		updateWait := false
		var cancelAudit []entity.IdPair
		for i := 0; i < len(approveResp.Data.List); i++ {
			item := approveResp.Data.List[i]
			if len(waitAudit) != 0 {
				for j := 0; j < len(waitAudit); j++ {
					if item.Institutions == waitAudit[j].Desc {
						if item.Status != waitAudit[j].Code {
							updateWait = true
						}
						waitAudit[j].Code = item.Status
						break
					}
				}
			}
			hitvalid := false
			for k := 0; k < len(validAudit); k++ {
				if item.Institutions == validAudit[k].Desc {
					if item.Status == int(enum.AuditStatus_AGREED) {
						go flushOtherSiteInfo(info, item.Institutions, true)
					}
					if item.Status != validAudit[k].Code {
						validAudit[k].Code = item.Status
						validAudit[k].ReadCode = int(enum.APPLY_READ_STATUS_NOT_READ)
						if item.Status == int(enum.AuditStatus_CANCEL) {
							calcelItem := entity.IdPair{
								Code: item.Status,
								Desc: item.Institutions,
							}
							cancelAudit = append(cancelAudit, calcelItem)
							go flushOtherSiteInfo(info, item.Institutions, false)
						}
					}
					hitvalid = true
					break
				}
			}
			if !hitvalid {
				validAuditItem := entity.AuditPair{
					Code:     item.Status,
					Desc:     item.Institutions,
					ReadCode: int(enum.APPLY_READ_STATUS_NOT_READ),
				}
				if item.Status == int(enum.AuditStatus_AGREED) {
					go flushOtherSiteInfo(info, item.Institutions, true)
				}
				validAudit = append(validAudit, validAuditItem)
			}
		}
		var data = make(map[string]interface{})
		if updateWait {
			waitAuditJson, _ := json.Marshal(waitAudit)
			data["status"] = int(enum.IS_VALID_NO)
			data["institutions"] = string(waitAuditJson)
			applySiteInfo.Status = int(enum.IS_VALID_ING)
			models.UpdateApplySiteInfo(data, &applySiteInfo)
		}
		if len(cancelAudit) > 0 {
			cancelAuditJson, _ := json.Marshal(cancelAudit)
			applySiteInfo = models.ApplySiteInfo{
				Institutions: string(cancelAuditJson),
				Status:       int(enum.IS_VALID_NO),
				CreateTime:   time.Now(),
				UpdateTime:   time.Now(),
			}
			models.AddApplySiteInfo(&applySiteInfo)
		}
		data["status"] = int(enum.IS_VALID_YES)
		validAuditJson, _ := json.Marshal(validAudit)
		data["institutions"] = string(validAuditJson)
		data["update_time"] = time.Now()
		applySiteInfo.Status = int(enum.IS_VALID_YES)
		if len(validAuditList) > 0 {
			models.UpdateApplySiteInfo(data, &applySiteInfo)
		} else if len(validAudit) > 0 {
			applySiteInfo = models.ApplySiteInfo{
				Institutions: string(validAuditJson),
				Status:       int(enum.IS_VALID_YES),
				CreateTime:   time.Now(),
				UpdateTime:   time.Now(),
			}
			models.AddApplySiteInfo(&applySiteInfo)
		}
	}
}

func flushOtherSiteInfo(accountInfo *models.AccountInfo, institution string, valid bool) {
	querySiteReq := entity.QuerySiteReq{
		Institutions: institution,
		PageNum:      1,
		PageSize:     100,
	}
	applySiteReqJson, _ := json.Marshal(querySiteReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          string(applySiteReqJson),
		Uri:           setting.OtherSiteUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.OtherSiteUri), querySiteReq, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return
	}
	var resp entity.ApplyResp
	err = json.Unmarshal([]byte(result.Body), &resp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return
	}
	if resp.Code == e.SUCCESS {
		for k := 0; k < len(resp.Data.List); k++ {
			item := resp.Data.List[k]
			var data = make(map[string]interface{})
			data["is_valid"] = int(enum.IS_VALID_NO)
			if valid {
				data["is_valid"] = int(enum.IS_VALID_YES)
			}
			data["update_time"] = time.Now()
			otherSiteInfo := models.OtherSiteInfo{
				PartyId:         item.PartyId,
				SiteId:          item.Id,
				SiteName:        item.SiteName,
				Institutions:    item.Institutions,
				Role:            item.Role,
				ServiceStatus:   item.ServiceStatus,
				Status:          item.Status,
				IsValid:         int(enum.IS_VALID_YES),
				CreateTime:      time.Unix(item.CreateTime/1000, 0),
				AcativationTime: time.Unix(item.ActivationTime/1000, 0),
				UpdateTime:      time.Unix(item.UpdateTime/1000, 0),
			}

			otherSiteInfoList, _ := models.GetOtherSiteInfoList(&otherSiteInfo)
			if len(otherSiteInfoList) > 0 {
				models.UpdateOterhSiteInfoByCondition(data, otherSiteInfo)
			} else {
				models.AddOterhSiteInfo(&otherSiteInfo)
			}
		}
	}
}

type ApplyiedReq struct {
	Institutions string `json:"institutions"`
}

func AllowApplyTask(info *models.AccountInfo) {

	applyiedReq := ApplyiedReq{Institutions: info.Institutions}
	applyiedReqJson, _ := json.Marshal(applyiedReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    info.AppKey,
		UserAppSecret: info.AppSecret,
		FateManagerId: info.CloudUserId,
		Body:          string(applyiedReqJson),
		Uri:           setting.AuthorityApplied,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.AuthorityApplied), applyiedReq, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return
	}
	var applySiteResultResp entity.AppliedResultResp
	err = json.Unmarshal([]byte(result.Body), &applySiteResultResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return
	}

	if applySiteResultResp.Code == e.SUCCESS {
		var institutions string
		for i := 0; i < len(applySiteResultResp.Data); i++ {
			item := applySiteResultResp.Data[i]
			institutions = fmt.Sprintf("%s,%s", item, institutions)
		}

		var data = make(map[string]interface{})
		data["allow_instituions"] = ""
		if len(institutions) > 0 {
			data["allow_instituions"] = institutions[0 : len(institutions)-1]
		}
		accountInfo := models.AccountInfo{
			UserId:   info.UserId,
			UserName: info.UserName,
			Role:     int(enum.UserRole_ADMIN),
			Status:   int(enum.IS_VALID_YES),
		}
		models.UpdateAccountInfo(data, accountInfo)
	}
}

func ComponentStatusTask() {
	deploySite := models.DeploySite{IsValid: int(enum.IS_VALID_YES)}
	deploySiteList, err := models.GetDeploySuccessSite(&deploySite)
	if err != nil {
		return
	}
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return
	}
	for i := 0; i < len(deploySiteList); i++ {
		deployComponent := models.DeployComponent{
			PartyId: deploySiteList[i].PartyId,
			IsValid: int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil {
			return
		}

		serviceStatus := true
		if deploySiteList[i].DeployType == int(enum.DeployType_K8S) {
			for j := 0; j < len(deployComponentList); j++ {
				testname := deployComponentList[j].ComponentName
				if deployComponentList[j].ComponentName == "meta-service" {
					testname = "roll"
				}
				if deployComponentList[j].ComponentName == "fateboard" || deployComponentList[j].ComponentName == "fateflow" {
					testname = "python"
				}
				cmdSub := fmt.Sprintf("kubectl get pods -n fate-%d |grep %s* | grep Running |wc -l", deployComponentList[j].PartyId, testname)
				if setting.DeploySetting.SudoTag {
					cmdSub = fmt.Sprintf("sudo kubectl get pods -n fate-%d |grep %s* | grep Running |wc -l", deployComponentList[j].PartyId, testname)
				}
				result, _ := util.ExecCommand(cmdSub)
				cnt, _ := strconv.Atoi(result[0:1])
				var data = make(map[string]interface{})
				if cnt < 1 {
					data["status"] = int(enum.SITE_RUN_STATUS_STOPPED)

					DeploySite := models.DeploySite{
						PartyId:     deployComponentList[j].PartyId,
						ProductType: deployComponentList[j].ProductType,
						IsValid:     int(enum.IS_VALID_YES),
					}
					models.UpdateDeploySite(data, DeploySite)
					serviceStatus = false
				} else {
					data["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
				}
				deployComponent = models.DeployComponent{
					PartyId:       deployComponentList[j].PartyId,
					ProductType:   deployComponentList[j].ProductType,
					ComponentName: deployComponentList[j].ComponentName,
					IsValid:       int(enum.IS_VALID_YES),
				}
				models.UpdateDeployComponent(data, deployComponent)
			}
		} else {
			var connectReq entity.ConnectAnsible
			connectReq.PartyId = deploySiteList[i].PartyId
			result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsibleConnectUri), connectReq, nil)
			if err != nil || result == nil {
				logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
				return
			}
			var connectResp entity.AnsibleConnectResp
			err = json.Unmarshal([]byte(result.Body), &connectResp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
			if connectResp.Code == e.SUCCESS {
				for k := 0; k < len(connectResp.Data.List); k++ {
					ConnectItem := connectResp.Data.List[k]
					if ConnectItem.Module == "fateflow" ||
						ConnectItem.Module == "mysql" ||
						ConnectItem.Module == "fateboard" ||
						ConnectItem.Module == "clustermanager" ||
						ConnectItem.Module == "nodemanager" ||
						ConnectItem.Module == "rollsite" {

						deployComponent = models.DeployComponent{
							PartyId:       deploySiteList[i].PartyId,
							ComponentName: ConnectItem.Module,
							IsValid:       int(enum.IS_VALID_YES),
						}
						data := make(map[string]interface{})
						data["status"] = int(enum.SITE_RUN_STATUS_STOPPED)
						serviceStatus = false
						if ConnectItem.Status == "running" && (deploySite.DeployStatus == int(enum.ANSIBLE_DeployStatus_SUCCESS) || deploySite.DeployStatus == int(enum.ANSIBLE_DeployStatus_TEST_PASSED)) {
							data["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
							serviceStatus = true
						}
						models.UpdateDeployComponent(data, deployComponent)
					}
				}
			}
		}
		var site = make(map[string]interface{})
		var deploy = make(map[string]interface{})
		site["service_status"] = int(enum.SERVICE_STATUS_UNAVAILABLE)
		deploy["status"] = int(enum.SITE_RUN_STATUS_STOPPED)
		if serviceStatus {
			site["service_status"] = int(enum.SERVICE_STATUS_AVAILABLE)
			deploy["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
		}
		siteInfo := models.SiteInfo{PartyId: deploySiteList[i].PartyId, Status: int(enum.SITE_STATUS_JOINED)}
		models.UpdateSiteByCondition(site, siteInfo)
		models.UpdateDeploySite(deploy, deploySiteList[i])
	}
}

func AutoTestTask() {
	deploySite := models.DeploySite{
		DeployStatus: int(enum.DeployStatus_IN_TESTING),
		IsValid:      int(enum.IS_VALID_YES),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return
	}
	for i := 0; i < len(deploySiteList); i++ {
		item := deploySiteList[i]
		var autoTestData, deploySiteData = make(map[string]interface{}), make(map[string]interface{})
		autoTestData["status"] = int(enum.TEST_STATUS_NO)
		autoTest := models.AutoTest{
			FederatedId: item.FederatedId,
			PartyId:     item.PartyId,
			ProductType: item.ProductType,
		}
		if item.SingleTest == int(enum.TEST_STATUS_TESTING) {
			deploySiteData["single_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["toy_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["minimize_normal_test"] = int(enum.TEST_STATUS_NO)

			models.UpdateAutoTest(autoTestData, autoTest)
		} else if item.ToyTest == int(enum.TEST_STATUS_TESTING) {
			deploySiteData["toy_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["minimize_normal_test"] = int(enum.TEST_STATUS_NO)

			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_TOY)
			models.UpdateAutoTest(autoTestData, autoTest)
			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_FAST)
			models.UpdateAutoTest(autoTestData, autoTest)
			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_NORMAL)
			models.UpdateAutoTest(autoTestData, autoTest)
		} else if item.MinimizeFastTest == int(enum.TEST_STATUS_TESTING) {
			deploySiteData["minimize_fast_test"] = int(enum.TEST_STATUS_NO)
			deploySiteData["minimize_normal_test"] = int(enum.TEST_STATUS_NO)

			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_FAST)
			models.UpdateAutoTest(autoTestData, autoTest)
			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_NORMAL)
			models.UpdateAutoTest(autoTestData, autoTest)
		} else if item.MinimizeNormalTest == int(enum.TEST_STATUS_TESTING) {
			deploySiteData["minimize_normal_test"] = int(enum.TEST_STATUS_NO)

			autoTest.TestItem = enum.GetTestItemString(enum.TEST_ITEM_NORMAL)
			models.UpdateAutoTest(autoTestData, autoTest)
		}
		models.UpdateDeploySite(deploySiteData, deploySite)
	}
}

type FlowJobQuery struct {
	PartyId int `json:"party_id"`
}
type FlowVersionQuery struct {
	Module string `json:"module"`
}
type SiteInstitution struct {
	SiteName    string `json:"siteName"`
	Institution string `json:"institution"`
}

func MonitorTask(accountInfo *models.AccountInfo) {
	siteInfo := models.SiteInfo{
		Status: int(enum.SITE_STATUS_JOINED),
	}
	flowAddressList, err := models.GetFlowAddressList(&siteInfo)
	if err != nil {
		return
	}
	SiteNameMap, _ := GetOtherPartyIdInstitution()
	if SiteNameMap == nil {
		SiteNameMap = make(map[int]SiteInstitution)
	}
	for i := 0; i < len(flowAddressList); i++ {
		siteInstitution := SiteInstitution{
			SiteName:    flowAddressList[i].SiteName,
			Institution: accountInfo.Institutions,
		}
		SiteNameMap[flowAddressList[i].PartyId] = siteInstitution
	}
	for i := 0; i < len(flowAddressList); i++ {
		flowJobQuery := FlowJobQuery{PartyId: flowAddressList[i].PartyId}
		result, err := http.POST(http.Url("http://"+flowAddressList[i].Address+setting.FlowJobQuery), flowJobQuery, nil)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			continue
		}
		var flowJobQueryResp entity.FlowJobQueryResp
		err = json.Unmarshal([]byte(result.Body), &flowJobQueryResp)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			continue
		}
		if flowJobQueryResp.Code == e.SUCCESS {
			for j := 0; j < len(flowJobQueryResp.Data); j++ {
				flowJobQuery := flowJobQueryResp.Data[j]
				ds, _ := strconv.Atoi(time.Unix(flowJobQuery.CreateTime/1000, 0).Format("20060102"))
				monitorDetail := models.MonitorDetail{
					Ds:         ds,
					JobId:      flowJobQuery.JobId,
					Status:     flowJobQuery.Status,
					CreateTime: flowJobQuery.CreateTime,
					UpdateTime: flowJobQuery.UpdateTime,
				}
				if flowJobQuery.StartTime != nil {
					monitorDetail.StartTime = flowJobQuery.StartTime.(float64)
				}
				if flowJobQuery.EndTime != nil {
					monitorDetail.EndTime = flowJobQuery.EndTime.(float64)
				}
				var roles entity.Roles
				err = json.Unmarshal([]byte(flowJobQuery.Roles), &roles)
				if err != nil {
					logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					continue
				}
				if len(roles.Guest) > 0 {
					monitorDetail.GuestPartyId = roles.Guest[0]
					_, ok := SiteNameMap[monitorDetail.GuestPartyId]
					if ok {
						monitorDetail.GuestSiteName = SiteNameMap[monitorDetail.GuestPartyId].SiteName
						monitorDetail.GuestInstitution = SiteNameMap[monitorDetail.GuestPartyId].Institution
					}
				}
				if len(roles.Host) > 0 {
					monitorDetail.HostPartyId = roles.Host[0]
					_, ok := SiteNameMap[monitorDetail.HostPartyId]
					if ok {
						monitorDetail.HostSiteName = SiteNameMap[monitorDetail.HostPartyId].SiteName
						monitorDetail.HostInstitution = SiteNameMap[monitorDetail.HostPartyId].Institution
					}
				}
				if len(roles.Arbiter) > 0 {
					monitorDetail.ArbiterPartyId = roles.Arbiter[0]
					_, ok := SiteNameMap[monitorDetail.ArbiterPartyId]
					if ok {
						monitorDetail.ArbiterSiteName = SiteNameMap[monitorDetail.ArbiterPartyId].SiteName
						monitorDetail.ArbiterInstitution = SiteNameMap[monitorDetail.ArbiterPartyId].Institution
					}
				}
				monitorDetailList, _ := models.GetMonitorDetail(&monitorDetail)
				if len(monitorDetailList) == 0 {
					models.AddMonitorDetail(&monitorDetail)
				} else {
					models.UpdateMonitorDetail(&monitorDetail)
				}
			}
		}
		flowVersionQuery := FlowVersionQuery{Module: "FATE"}
		result, err = http.POST(http.Url("http://"+flowAddressList[i].Address+setting.FlowVersion), flowVersionQuery, nil)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			continue
		}
		var flowVersionQueryResp entity.FlowVersionQueryResp
		err = json.Unmarshal([]byte(result.Body), &flowVersionQueryResp)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			continue
		}
		if flowVersionQueryResp.Code == e.SUCCESS {
			siteInfo := models.SiteInfo{PartyId: flowAddressList[i].PartyId}
			var data = make(map[string]interface{})
			data["fate_version"] = flowVersionQuery.Module
			models.UpdateSiteByCondition(data, siteInfo)
		}
	}
	for i := -1; i <= 0; i++ {
		timeunix := time.Now().AddDate(0, 0, i).UnixNano() / 1e6
		curTime := time.Now().AddDate(0, 0, i).Format("20060102")
		monitorReq := entity.MonitorReq{
			StartDate: curTime,
			EndDate:   curTime,
		}
		InstitutionReport(accountInfo.Institutions, monitorReq)
		SiteReport(accountInfo.Institutions, monitorReq)
		MonitorPush(monitorReq, accountInfo, timeunix)
	}
}

func InstitutionReport(institution string, monitorReq entity.MonitorReq) {
	list, err := models.CalReportInstitutionByOne(monitorReq)
	if err != nil {
		return
	}
	for i := 0; i < len(list); i++ {
		item := list[i]
		reportInstitution := models.ReportInstitution{
			Ds:          item.Ds,
			Institution: item.Institution,
			Total:       item.Total,
			Success:     item.Success,
			Running:     item.Running,
			Waiting:     item.Waiting,
			Timeout:     item.Timeout,
			Failed:      item.Failed,
			Canceled:    item.Canceled,
			CreateTime:  time.Now(),
			UpdateTime:  time.Now(),
		}
		var data = make(map[string]interface{})
		data["total"] = item.Total
		data["success"] = item.Success
		data["running"] = item.Running
		data["waiting"] = item.Waiting
		data["timeout"] = item.Timeout
		data["failed"] = item.Failed
		data["canceled"] = item.Canceled
		models.UpdateReportInstitution(data, &reportInstitution)
	}
	list, err = models.CalReportInstitutionByTwo(institution, monitorReq)
	if err != nil {
		return
	}
	for i := 0; i < len(list); i++ {
		item := list[i]
		reportInstitution := models.ReportInstitution{
			Ds:          item.Ds,
			Institution: item.Institution,
			Total:       item.Total,
			Success:     item.Success,
			Running:     item.Running,
			Waiting:     item.Waiting,
			Timeout:     item.Timeout,
			Failed:      item.Failed,
			Canceled:    item.Canceled,
			CreateTime:  time.Now(),
			UpdateTime:  time.Now(),
		}
		var data = make(map[string]interface{})
		data["total"] = item.Total
		data["success"] = item.Success
		data["running"] = item.Running
		data["waiting"] = item.Waiting
		data["timeout"] = item.Timeout
		data["failed"] = item.Failed
		data["canceled"] = item.Canceled
		models.UpdateReportInstitution(data, &reportInstitution)
	}
}

type SiteSiteMonitor struct {
	entity.SitePair
	models.MonitorBase
}

func SiteReport(institution string, monitorReq entity.MonitorReq) {
	list, err := models.CalReportSiteByOne(monitorReq)
	if err != nil {
		return
	}
	for i := 0; i < len(list); i++ {
		item := list[i]
		reportSite := models.ReportSite{
			Ds:                  item.Ds,
			Institution:         item.Institution,
			InstitutionSiteName: item.InstitutionSiteName,
			SiteName:            item.SiteName,
			Total:               item.Total,
			Success:             item.Success,
			Running:             item.Running,
			Waiting:             item.Waiting,
			Timeout:             item.Timeout,
			Failed:              item.Failed,
			Canceled:            item.Canceled,
			CreateTime:          time.Now(),
			UpdateTime:          time.Now(),
		}
		var data = make(map[string]interface{})
		data["total"] = item.Total
		data["success"] = item.Success
		data["running"] = item.Running
		data["waiting"] = item.Waiting
		data["timeout"] = item.Timeout
		data["failed"] = item.Failed
		data["canceled"] = item.Canceled
		models.UpdateReportSite(data, &reportSite)
	}
	list, err = models.CalReportSiteByTwo(institution, monitorReq)
	if err != nil {
		return
	}
	for i := 0; i < len(list); i++ {
		item := list[i]
		reportSite := models.ReportSite{
			Ds:                  item.Ds,
			Institution:         item.Institution,
			InstitutionSiteName: item.InstitutionSiteName,
			SiteName:            item.SiteName,
			Total:               item.Total,
			Success:             item.Success,
			Running:             item.Running,
			Waiting:             item.Waiting,
			Timeout:             item.Timeout,
			Failed:              item.Failed,
			Canceled:            item.Canceled,
			CreateTime:          time.Now(),
			UpdateTime:          time.Now(),
		}
		var data = make(map[string]interface{})
		data["total"] = item.Total
		data["success"] = item.Success
		data["running"] = item.Running
		data["waiting"] = item.Waiting
		data["timeout"] = item.Timeout
		data["failed"] = item.Failed
		data["canceled"] = item.Canceled
		models.UpdateReportSite(data, &reportSite)
	}
}

func GetOtherSiteList() ([]entity.FederatedItem, error) {
	var federatedItemList []entity.FederatedItem
	federatedInfo, err := models.GetFederatedInfo()
	if err != nil || len(federatedInfo) == 0 {
		return nil, err
	}
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return nil, err
	}

	ApplyResultTask(accountInfo)
	applySiteInfo := models.ApplySiteInfo{
		Status: int(enum.IS_VALID_YES),
	}
	applySiteInfoList, err := models.GetApplySiteInfo(applySiteInfo)
	if len(applySiteInfoList) > 0 {
		var auditresultlist []entity.IdPair
		err = json.Unmarshal([]byte(applySiteInfoList[0].Institutions), &auditresultlist)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return nil, err
		}
		for i := 0; i < len(auditresultlist); i++ {
			if auditresultlist[i].Code != int(enum.AuditStatus_AGREED) {
				continue
			}
			querySiteReq := entity.QuerySiteReq{
				Institutions: auditresultlist[i].Desc,
				PageNum:      1,
				PageSize:     100,
			}
			applySiteReqJson, _ := json.Marshal(querySiteReq)
			headInfo := util.UserHeaderInfo{
				UserAppKey:    accountInfo.AppKey,
				UserAppSecret: accountInfo.AppSecret,
				FateManagerId: accountInfo.CloudUserId,
				Body:          string(applySiteReqJson),
				Uri:           setting.OtherSiteUri,
			}
			headerInfoMap := util.GetUserHeadInfo(headInfo)
			federationList, err := federated_service.GetFederationInfo()
			if err != nil || len(federationList) == 0 {
				return nil, err
			}
			result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.OtherSiteUri), querySiteReq, headerInfoMap)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
				return nil, err
			}
			var resp entity.ApplyResp
			err = json.Unmarshal([]byte(result.Body), &resp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return nil, err
			}
			if resp.Code == e.SUCCESS {
				var siteItemList []entity.SiteItem
				for k := 0; k < len(resp.Data.List); k++ {
					item := resp.Data.List[k]
					siteItem := entity.SiteItem{
						Id:       item.Id,
						PartyId:  item.PartyId,
						SiteName: item.SiteName,
						Role: entity.IdPair{
							Code: item.Role,
							Desc: enum.GetRoleString(enum.RoleType(item.Role)),
						},
						Status: entity.IdPair{
							Code: item.Status,
							Desc: enum.GetSiteString(enum.SiteStatusType(item.Status)),
						},
						AcativationTime: item.ActivationTime,
					}
					siteItemList = append(siteItemList, siteItem)
				}
				federatedItem := entity.FederatedItem{
					FederatedId:             federatedInfo[0].Id,
					FateManagerInstitutions: auditresultlist[i].Desc,
					Size:                    len(siteItemList),
					SiteItemList:            siteItemList,
				}
				federatedItemList = append(federatedItemList, federatedItem)
			}
		}
	}
	return federatedItemList, nil
}

func GetOtherPartyIdInstitution() (map[int]SiteInstitution, error) {
	siteItemList, err := GetOtherSiteList()
	if err != nil {
		return nil, err
	}
	var siteInstitutionMap = make(map[int]SiteInstitution)
	for i := 0; i < len(siteItemList); i++ {
		federatedItem := siteItemList[i]
		for j := 0; j < len(federatedItem.SiteItemList); j++ {
			SiteItem := federatedItem.SiteItemList[j]
			SiteInstitution := SiteInstitution{
				SiteName:    SiteItem.SiteName,
				Institution: federatedItem.FateManagerInstitutions,
			}
			siteInstitutionMap[SiteItem.PartyId] = SiteInstitution
		}
	}
	return siteInstitutionMap, nil
}

type MonitorPushSite struct {
	Failed     int   `json:"jobFailedCount"`
	Running    int   `json:"jobRunningCount"`
	Success    int   `json:"jobSuccessCount"`
	Waiting    int   `json:"jobWaitingCount"`
	FinishDate int64 `json:"jobFinishDate"`
	GuestId    int64 `json:"siteGuestId"`
	HostId     int64 `json:"siteHostId"`
}

func MonitorPush(monitorReq entity.MonitorReq, accountInfo *models.AccountInfo, timeunix int64) {
	siteInfo := models.SiteInfo{Status: int(enum.SITE_STATUS_JOINED)}
	siteInfoList, _ := models.GetSiteList(&siteInfo)
	otherSiteInfo := models.OtherSiteInfo{Status: int(enum.SITE_STATUS_JOINED)}
	otherSiteInfoList, _ := models.GetOtherSiteInfoList(&otherSiteInfo)
	var partyIdSiteId = make(map[int]int64)
	for i := 0; i < len(siteInfoList); i++ {
		partyIdSiteId[siteInfoList[i].PartyId] = siteInfoList[i].SiteId
	}
	for i := 0; i < len(otherSiteInfoList); i++ {
		partyIdSiteId[otherSiteInfoList[i].PartyId] = otherSiteInfoList[i].SiteId
	}

	pushSiteList, err := models.GetPushSiteMonitorList(monitorReq)
	if err != nil {
		return
	}
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return
	}
	var data []MonitorPushSite
	for i := 0; i < len(pushSiteList); i++ {
		pushSite := pushSiteList[i]
		guestPartyId, _ := strconv.Atoi(pushSite.GuestPartyId)
		hostPartyId, _ := strconv.Atoi(pushSite.HostPartyId)
		gsite, ok := partyIdSiteId[guestPartyId]
		if !ok {
			continue
		}
		hsite, ok := partyIdSiteId[hostPartyId]
		if !ok {
			continue
		}

		monitorPushSite := MonitorPushSite{
			Failed:     pushSite.Failed + pushSite.Timeout + pushSite.Canceled,
			FinishDate: timeunix,
			Running:    pushSite.Running,
			Success:    pushSite.Success,
			Waiting:    pushSite.Waiting,
			GuestId:    gsite,
			HostId:     hsite,
		}
		data = append(data, monitorPushSite)
	}
	if len(data) > 0 {
		dataJson, _ := json.Marshal(data)
		headInfo := util.UserHeaderInfo{
			UserAppKey:    accountInfo.AppKey,
			UserAppSecret: accountInfo.AppSecret,
			FateManagerId: accountInfo.CloudUserId,
			Body:          string(dataJson),
			Uri:           setting.MonitorPushUri,
		}
		headerInfoMap := util.GetUserHeadInfo(headInfo)
		result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.MonitorPushUri), data, headerInfoMap)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			return
		}
		if len(result.Body) > 0 {
			var updateResp entity.CloudCommResp
			err = json.Unmarshal([]byte(result.Body), &updateResp)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
			if updateResp.Code == e.SUCCESS {
				msg := fmt.Sprintf("partyid:%d system heart success! content:%s", accountInfo.PartyId, string(dataJson))
				logging.Debug(msg)
			}
		}
	}
}

type VersionDownloadReq struct {
	FateVersion string `json:"version"`
}

func PackageStatusTask() {
	conf, err := models.GetKubenetesConf(enum.DeployType_ANSIBLE)
	if err != nil {
		return
	}
	if conf.Id == 0 {
		return
	}

	fateVersion := models.FateVersion{
		ProductType: int(enum.PRODUCT_TYPE_FATE),
	}

	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil {
		return
	}

	for i := 0; i < len(fateVersionList); i++ {
		if fateVersionList[i].PackageStatus == int(enum.PACKAGE_STATUS_YES) {
			continue
		} else if fateVersionList[i].PackageStatus == int(enum.PACKAGE_STATUS_NO) || fateVersionList[i].PackageStatus == int(enum.PACKAGE_STATUS_FAILED) {
			autoAcquireReq := entity.AutoAcquireReq{
				FateVersion: fateVersionList[i].FateVersion,
				DownloadUrl: fateVersionList[i].PackageUrl,
			}
			result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePackageDownUri), autoAcquireReq, nil)
			if err != nil || result == nil {
				logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
				continue
			}
			var ansibleInstallListResponse entity.AnsibleInstallListResponse
			err = json.Unmarshal([]byte(result.Body), &ansibleInstallListResponse)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				continue
			}
			var data = make(map[string]interface{})
			data["package_status"] = int(enum.PACKAGE_STATUS_PULLING)
			if ansibleInstallListResponse.Code == e.SUCCESS {
				models.UpdateFateVersion(data, fateVersionList[i])
			}
		} else if fateVersionList[i].PackageStatus == int(enum.PULL_STATUS_PULLING) {
			req := VersionDownloadReq{FateVersion: fateVersionList[i].FateVersion}
			result, err := http.POST(http.Url(conf.KubenetesUrl+setting.AnsiblePackageQueryUri), req, nil)
			if err != nil || result == nil {
				logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
				return
			}
			var versionDownloadResponse entity.VersionDownloadResponse
			err = json.Unmarshal([]byte(result.Body), &versionDownloadResponse)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return
			}
			if versionDownloadResponse.Code == e.SUCCESS {
				var data = make(map[string]interface{})
				data["package_status"] = int(enum.PACKAGE_STATUS_NO)
				if versionDownloadResponse.Data.Status == "success" {
					data["package_status"] = int(enum.PACKAGE_STATUS_YES)
				} else if versionDownloadResponse.Data.Status == "running" {
					data["package_status"] = int(enum.PACKAGE_STATUS_PULLING)
				}
				fateVersion.FateVersion = fateVersionList[i].FateVersion
				models.UpdateFateVersion(data, &fateVersion)

				componentVersion := models.ComponentVersion{
					FateVersion: fateVersionList[i].FateVersion,
					ProductType: int(enum.PRODUCT_TYPE_FATE),
				}
				models.UpdateComponentVersionByCondition(data, &componentVersion)

				deploySite := models.DeploySite{
					FateVersion: fateVersionList[i].FateVersion,
					IsValid:     int(enum.IS_VALID_YES),
					DeployType:  int(enum.DeployType_ANSIBLE),
				}
				data = make(map[string]interface{})
				data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOADED)
				if versionDownloadResponse.Data.Status == "failed" {
					data["deploy_status"] = int(enum.ANSIBLE_DeployStatus_LOAD_FAILED)
				}
				models.UpdateDeploySite(data, deploySite)
			}
		}
	}
}

func AutotestTask() {
	deploySite := models.DeploySite{
		DeployStatus: int(enum.ANSIBLE_DeployStatus_IN_TESTING),
		IsValid:      int(enum.IS_VALID_YES),
		DeployType:   int(enum.DeployType_ANSIBLE),
	}
	deploySiteList, err := models.GetDeploySite(&deploySite)
	if err != nil {
		return
	}

	for i := 0; i < len(deploySiteList); i++ {
		deployComponent := models.DeployComponent{
			PartyId:       deploySiteList[i].PartyId,
			ComponentName: "fateflow",
			ProductType:   int(enum.PRODUCT_TYPE_FATE),
			DeployType:    int(enum.DeployType_ANSIBLE),
			IsValid:       int(enum.IS_VALID_YES),
		}
		deployComponentList, err := models.GetDeployComponent(deployComponent)
		if err != nil || len(deployComponentList) == 0 {
			continue
		}
		address := strings.Split(deployComponentList[0].Address, ":")

		if deploySiteList[i].SingleTest == int(enum.TEST_STATUS_TESTING) {
			go DoProcess("single", "toy", deploySiteList[i], address[0])
		} else if deploySiteList[i].ToyTest == int(enum.TEST_STATUS_TESTING) {
			go DoProcess("toy", "fast", deploySiteList[i], address[0])
		} else if deploySiteList[i].MinimizeFastTest == int(enum.TEST_STATUS_TESTING) {
			go DoProcess("fast", "normal", deploySiteList[i], address[0])
		} else if deploySiteList[i].MinimizeNormalTest == int(enum.TEST_STATUS_TESTING) {
			go DoProcess("normal", "normal", deploySiteList[i], address[0])
		}
	}
}

func JudgeResult(partyId int, testType string, testItem string, data []string) bool {
	var content string
	result := false
	for i := 0; i < len(data); i++ {
		if strings.Contains(data[i], testItem) {
			result = true
		}
		if i == 0 {
			content = fmt.Sprintf("%s\n", data[i])
		} else {
			content = fmt.Sprintf("%s\n%s", content, data[i])
		}
	}
	if len(content) > 0 {
		path := fmt.Sprintf("./testLog/%s/fate-%d.log", testType, partyId)
		if ioutil.WriteFile(path, []byte(content), 0644) == nil {
			logging.Info("WriteFile " + path)
		} else {
			logging.Error("No WriteFile " + path)
		}
		cmd := fmt.Sprintf("cat ./testLog/%s/fate-%d.log >> ./testLog/all/fate-%d.log", testType, partyId, partyId)
		if testType == "single" {
			cmd = fmt.Sprintf("cat ./testLog/%s/fate-%d.log > ./testLog/all/fate-%d.log", testType, partyId, partyId)
		}
		util.ExecCommand(cmd)
	}
	return result
}

func DoProcess(curItem string, NextItem string, deploySite models.DeploySite, Ip string) {
	var sitedata = make(map[string]interface{})
	var testdata = make(map[string]interface{})
	ResultReq := entity.AnsibleToyTestResultReq{
		Limit:    500,
		Ip:       Ip,
		TestType: curItem,
	}
	TestReq := entity.AnsibleToyTestReq{
		GuestPartyId: deploySite.PartyId,
		HostPartyId:  setting.DeploySetting.TestPartyId,
		Ip:           Ip,
		WorkMode:     setting.DeploySetting.WorkMode,
	}
	MinReq := entity.AnsibleMinTestReq{
		ArbiterPartyId: setting.DeploySetting.TestPartyId,
		GuestPartyId:   deploySite.PartyId,
		HostPartyId:    setting.DeploySetting.TestPartyId,
		Ip:             Ip,
	}
	autotest := models.AutoTest{
		PartyId: deploySite.PartyId,
	}

	ResultReq.TestType = curItem
	result, err := http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/log"), ResultReq, nil)
	if result == nil || err != nil {
		return
	}
	var resultResp entity.AnsibleTestResultResponse
	err = json.Unmarshal([]byte(result.Body), &resultResp)
	if err != nil {
		return
	}
	if resultResp.Code == e.SUCCESS {
		deployKey, testKey := "", ""
		if curItem == "single" {
			deployKey = "single_test"
			testKey = "Single Test"
		} else if curItem == "toy" {
			deployKey = "toy_test"
			testKey = "Toy Test"
		} else if curItem == "fast" {
			deployKey = "minimize_fast_test"
			testKey = "Minimize Fast Test"
		} else if curItem == "normal" {
			deployKey = "minimize_normal_test"
			testKey = "Minimize Normal Test"
		}
		sitedata[deployKey] = int(enum.TEST_STATUS_NO)
		testdata["status"] = int(enum.TEST_STATUS_NO)
		findKey := "success to calculate secure_sum"
		if curItem == "fast" || curItem == "normal" {
			findKey = "success"
		}
		successTag := false
		if JudgeResult(deploySite.PartyId, curItem, findKey, resultResp.Data) {
			sitedata[deployKey] = int(enum.TEST_STATUS_YES)
			testdata["status"] = int(enum.TEST_STATUS_YES)
			successTag = true
			//if curItem == "normal" {
			//	site_deploy_service.UploadStatusToCloud(deploySite.PartyId, 0, enum.DeployType_ANSIBLE,2)
			//}
		}
		if len(resultResp.Data) <= 1 {
			sitedata[deployKey] = int(enum.TEST_STATUS_TESTING)
			testdata["status"] = int(enum.TEST_STATUS_TESTING)
		}
		if curItem == "normal" {
			if successTag {
				sitedata["status"] = int(enum.SITE_RUN_STATUS_RUNNING)
				sitedata["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_PASSED)

				siteInfo := models.SiteInfo{PartyId: deploySite.PartyId, Status: int(enum.SITE_STATUS_JOINED)}
				var data = make(map[string]interface{})
				data["service_status"] = int(enum.SERVICE_STATUS_AVAILABLE)
				models.UpdateSiteByCondition(data, siteInfo)

			} else if sitedata[deployKey] == int(enum.TEST_STATUS_NO) {
				sitedata["status"] = int(enum.SITE_RUN_STATUS_STOPPED)
				sitedata["deploy_status"] = int(enum.ANSIBLE_DeployStatus_TEST_FAILED)
			}
			deployComponent := models.DeployComponent{
				PartyId:    deploySite.PartyId,
				DeployType: int(enum.DeployType_ANSIBLE),
				IsValid:    int(enum.IS_VALID_YES),
			}
			var componentData = make(map[string]interface{})
			componentData["status"] = sitedata["status"]
			componentData["deploy_status"] = sitedata["deploy_status"]
			models.UpdateDeployComponent(componentData, deployComponent)
		}
		models.UpdateDeploySite(sitedata, deploySite)

		testdata["end_time"] = time.Now()

		autotest.TestItem = testKey
		models.UpdateAutoTest(testdata, autotest)

		if successTag {
			if NextItem == "toy" {
				result, err = http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+NextItem), TestReq, nil)
				var commresp entity.AnsibleCommResp
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					return
				}
				if commresp.Code == e.SUCCESS {
					testdata = make(map[string]interface{})
					testdata["status"] = int(enum.TEST_STATUS_TESTING)
					testdata["start_time"] = time.Now()
					autotest.TestItem = "Toy Test"
					models.UpdateAutoTest(testdata, autotest)

					sitedata = make(map[string]interface{})
					sitedata[NextItem+"_test"] = int(enum.TEST_STATUS_TESTING)
					models.UpdateDeploySite(sitedata, deploySite)
				}
			} else if NextItem == "fast" {
				dataUploadReq := entity.DataUploadReq{Ip: Ip}
				result, err = http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/upload"), dataUploadReq, nil)
				var commresp entity.AnsibleCommResp
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					return
				}
				if commresp.Code == e.SUCCESS {
					result, err = http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+NextItem), MinReq, nil)
					var commresp entity.AnsibleCommResp
					err = json.Unmarshal([]byte(result.Body), &commresp)
					if err != nil {
						return
					}
					if commresp.Code == e.SUCCESS {
						testdata = make(map[string]interface{})
						testdata["status"] = int(enum.TEST_STATUS_TESTING)
						testdata["start_time"] = time.Now()
						autotest.TestItem = "Minimize Fast Test"
						models.UpdateAutoTest(testdata, autotest)

						sitedata = make(map[string]interface{})
						sitedata["minimize_fast_test"] = int(enum.TEST_STATUS_TESTING)
						models.UpdateDeploySite(sitedata, deploySite)
					}
				}
			} else if NextItem == "normal" && curItem != "normal" {
				result, err = http.POST(http.Url(k8s_service.GetKubenetesUrl(enum.DeployType_ANSIBLE)+setting.AnsibleAutoTestUri+"/"+NextItem), MinReq, nil)
				var commresp entity.AnsibleCommResp
				err = json.Unmarshal([]byte(result.Body), &commresp)
				if err != nil {
					return
				}
				if commresp.Code == e.SUCCESS {
					testdata = make(map[string]interface{})
					testdata["status"] = int(enum.TEST_STATUS_TESTING)
					testdata["start_time"] = time.Now()
					autotest.TestItem = "Minimize Normal Test"
					models.UpdateAutoTest(testdata, autotest)

					sitedata = make(map[string]interface{})
					sitedata["minimize_normal_test"] = int(enum.TEST_STATUS_TESTING)
					models.UpdateDeploySite(sitedata, deploySite)
				}
			}
		}
	}
}

func VersionUpdateTask(info *models.AccountInfo) {
	versionProduct := entity.PageReq{
		PageNum:  1,
		PageSize: 100,
	}
	versionProductJson, _ := json.Marshal(versionProduct)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    info.AppKey,
		UserAppSecret: info.AppSecret,
		FateManagerId: info.CloudUserId,
		Body:          string(versionProductJson),
		Uri:           setting.ProductVersionUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.ProductVersionUri), versionProduct, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return
	}
	var resp entity.VersionProductResp
	err = json.Unmarshal([]byte(result.Body), &resp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return
	}
	if resp.Code == e.SUCCESS {
		for i := 0; i < len(resp.Data.List); i++ {
			versionProductItem := resp.Data.List[i]
			for j := 0; j < len(versionProductItem.FederatedComponentVersionDos); j++ {
				federatedComponentVersionDos := versionProductItem.FederatedComponentVersionDos[j]
				if len(federatedComponentVersionDos.ComponentVersion) < 5 {
					continue
				}
				versionIndex, _ := strconv.Atoi(strings.ReplaceAll(federatedComponentVersionDos.ComponentVersion[0:5], ".", ""))
				componentVersion := models.ComponentVersion{
					FateVersion:   versionProductItem.ProductVersion,
					ProductType:   int(enum.PRODUCT_TYPE_FATE),
					ComponentName: federatedComponentVersionDos.ComponentName,
				}
				componentVersionList, err := models.GetComponetVersionList(componentVersion)
				if err != nil || len(componentVersionList) > 0 {
					var data = make(map[string]interface{})
					data["component_version"] = federatedComponentVersionDos.ComponentVersion
					data["image_name"] = federatedComponentVersionDos.ImageRepository
					data["image_version"] = federatedComponentVersionDos.ImageTag
					data["image_tag"] = federatedComponentVersionDos.ImageTag
					data["image_description"] = federatedComponentVersionDos.ImageTag
					data["version_index"] = versionIndex
					if federatedComponentVersionDos.ComponentName == "mysql" {
						data["component_version"] = federatedComponentVersionDos.ImageTag
						data["version_index"], _ = strconv.Atoi(federatedComponentVersionDos.ImageTag)
					}
					models.UpdateComponentVersionByCondition(data, &componentVersion)
				} else {
					componentVersion.ComponentVersion = federatedComponentVersionDos.ComponentVersion
					componentVersion.ImageName = federatedComponentVersionDos.ImageRepository
					componentVersion.ImageVersion = federatedComponentVersionDos.ImageTag
					componentVersion.ImageTag = federatedComponentVersionDos.ImageTag
					componentVersion.ImageDescription = federatedComponentVersionDos.ImageTag
					componentVersion.VersionIndex = versionIndex
					componentVersion.PullStatus = int(enum.PULL_STATUS_NO)
					componentVersion.PackageStatus = int(enum.PACKAGE_STATUS_NO)
					componentVersion.CreateTime = time.Now()
					componentVersion.UpdateTime = time.Now()
					if federatedComponentVersionDos.ComponentName == "mysql" {
						componentVersion.ComponentVersion = componentVersion.ImageTag
						componentVersion.VersionIndex, _ = strconv.Atoi(componentVersion.ImageTag)
					}
					models.AddComponentVersion(&componentVersion)
				}
			}
			if len(versionProductItem.ProductVersion) < 5 {
				continue
			}
			versionIndex, _ := strconv.Atoi(strings.ReplaceAll(versionProductItem.ProductVersion[0:5], ".", ""))
			fateVersion := models.FateVersion{
				FateVersion: versionProductItem.ProductVersion,
				ProductType: int(enum.PRODUCT_TYPE_FATE),
			}
			fateVersionList, err := models.GetFateVersionList(&fateVersion)
			if err != nil || len(fateVersionList) > 0 {
				var data = make(map[string]interface{})
				data["chart_version"] = "v" + versionProductItem.ChartVersion
				data["version_index"] = versionIndex
				data["package_url"] = versionProductItem.PackageDownloadUrl
				models.UpdateFateVersion(data, &fateVersion)
			} else {
				fateVersion.ChartVersion = "v" + versionProductItem.ChartVersion
				fateVersion.VersionIndex = versionIndex
				fateVersion.PullStatus = int(enum.PULL_STATUS_NO)
				fateVersion.PackageStatus = int(enum.PACKAGE_STATUS_NO)
				fateVersion.PackageUrl = versionProductItem.PackageDownloadUrl
				fateVersion.CreateTime = time.Now()
				fateVersion.UpdateTime = time.Now()
				models.AddFateVersion(&fateVersion)
			}
		}
	}
}
