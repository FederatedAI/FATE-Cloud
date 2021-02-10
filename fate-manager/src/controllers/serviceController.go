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
	"fate.manager/services/component_deploy_service"
	"fate.manager/services/site_deploy_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
)

// @Summary Get Service Info
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.ServiceInfoReq true "request param"
// @Success 200 {object} app.ServiceInfoResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/info [post]
func GetService(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var serviceInfoReq entity.ServiceInfoReq
	if jsonError := json.Unmarshal(body, &serviceInfoReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	serviceInfoResponse, err := component_deploy_service.GetComponentList(serviceInfoReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SERVICE_INFO_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, serviceInfoResponse)
}

// @Summary Do stopped Or Running Action
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.ActionReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/action [post]
func DoAcation(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var actionReq entity.ActionReq
	if jsonError := json.Unmarshal(body, &actionReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := component_deploy_service.DoAction(actionReq)
	if err != nil || ret != e.SUCCESS {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Get PartyId If Install
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.InstallStatus true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/installstatus [post]
func InstallStatus(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var installStatus entity.InstallStatus
	if jsonError := json.Unmarshal(body, &installStatus); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := component_deploy_service.GetInstallStatus(installStatus)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_INSTALL_STATUS_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get Service Overview List
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.OverViewReq true "request param"
// @Success 200 {object} app.OverViewResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/overview [post]
func GetServiceOverview(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var overViewReq entity.OverViewReq
	if jsonError := json.Unmarshal(body, &overViewReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.GetServiceOverview(overViewReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SERVICE_OVERVIEW_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Upgrade Fate Version List
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.UpgradeFateReq true "request param"
// @Success 200 {object} app.UpgradeFateResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/upgradelist [post]
func UpgradeFateList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var upgradeFateReq entity.UpgradeFateReq
	if jsonError := json.Unmarshal(body, &upgradeFateReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.UpgradeFateList(upgradeFateReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_UPGRADE_FATE_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Page Status
// @Tags ServiceController
// @Accept  json
// @Produce  json
// @Param request body entity.PageStatusReq true "request param"
// @Success 200 {object} app.GetPageStatusResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/service/pagestatus [post]
func GetPageStatus(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var pageStatusReq entity.PageStatusReq
	if jsonError := json.Unmarshal(body, &pageStatusReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.GetPageStatus(pageStatusReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_PAGE_STATUS_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}
