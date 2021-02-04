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
package controllers

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/services/ansible_service"
	"fate.manager/services/component_deploy_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
)

// @Summary Connect To Ansible Server
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AnsibleConnectReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/connectansible [post]
func ConnectAnsible(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var ansibleReq entity.AnsibleConnectReq
	if jsonError := json.Unmarshal(body, &ansibleReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.ConnectAnsible(ansibleReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary commit package
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.CommitImagePullReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/commit [post]
func CommitPackage(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var commit entity.CommitImagePullReq
	if jsonError := json.Unmarshal(body, &commit); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.CommitPackage(commit)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary local upload package
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.LocalUploadReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/upload [post]
func LocalUpload(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var localUploadReq entity.LocalUploadReq
	if jsonError := json.Unmarshal(body, &localUploadReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.LocalUpload(localUploadReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_LOACAL_UPLOAD_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary auto acquire package
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoAcquireReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/autoacquire [post]
func AutoAcquire(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var autoAcquireReq entity.AutoAcquireReq
	if jsonError := json.Unmarshal(body, &autoAcquireReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.AutoAcquire(autoAcquireReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_AUTO_ACQUIRE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary get component list
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AnsibleAutoTestReq true "request param"
// @Success 200 {object} app.AnsiblePackageResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/componentlist [post]
func GetComponentList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var connectAnsible entity.AnsibleAutoTestReq
	if jsonError := json.Unmarshal(body, &connectAnsible); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.GetComponentList(connectAnsible)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_AUTO_ACQUIRE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Click page
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.ClickReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/click [post]
func AnsibleClick(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var clickReq entity.ClickReq
	if jsonError := json.Unmarshal(body, &clickReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result := ansible_service.Click(clickReq)
	if !result {
		appG.Response(http.StatusOK, e.ERROR_CLICK_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Get Install Component List
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.InstallComponentListReq true "request param"
// @Success 200 {object} app.InstallListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/installlist [post]
func GetAnsibleInstallComponentList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var installComponentListReq entity.InstallComponentListReq
	if jsonError := json.Unmarshal(body, &installComponentListReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	installListResponse, err := component_deploy_service.GetInstallComponentList(installComponentListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_INSTALL_COMPONENT_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, installListResponse)
}

// @Summary Install Fate All Component
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.InstallReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/install [post]
func AnsibleInstall(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var installReq entity.InstallReq
	if jsonError := json.Unmarshal(body, &installReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.InstallByAnsible(installReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, e.GetMsg(ret))
}

// @Summary Uprade Site
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.UpgradeReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/upgrade [post]
func AnsibleUpgrade(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var upgradeReq entity.UpgradeReq
	if jsonError := json.Unmarshal(body, &upgradeReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.UpgradeByAnsible(upgradeReq)
	if err != nil || ret != e.SUCCESS {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Update Component Ip Address
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.UpdateReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/update [post]
func AnsibleUpdate(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var updateReq entity.UpdateReq
	if jsonError := json.Unmarshal(body, &updateReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.Update(updateReq)
	if err != nil || ret != e.SUCCESS {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Ansible Server Log
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AnsibleLog true "request param"
// @Success 200 {object} app.AnsibleLogResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/log [post]
func AnsibleLog(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var ansibleLog entity.AnsibleLog
	if jsonError := json.Unmarshal(body, &ansibleLog); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.GetLog(ansibleLog)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_LOG_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get Auto Test Site List
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoTestListReq true "request param"
// @Success 200 {object} app.AutoTestListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/testlist [post]
func GetAnsibleAutoTestList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var autoTestListReq entity.AutoTestListReq
	if jsonError := json.Unmarshal(body, &autoTestListReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	autoTestListResponse, err := ansible_service.GetAutoTestList(autoTestListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_AUTO_TEST_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, autoTestListResponse)
}

// @Summary Do Auto Test Start
// @Tags AnsibleDeployController
// @Accept  json
// @Produce  json
// @Param request body entity.ConnectAnsible true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/autotest [post]
func AnsibleAutoTest(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var autoTestReq entity.AnsibleAutoTestReq
	if jsonError := json.Unmarshal(body, &autoTestReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.AutoTest(autoTestReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}