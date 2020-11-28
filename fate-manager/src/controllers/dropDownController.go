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
	"fate.manager/comm/enum"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/services/federated_service"
	"fate.manager/services/k8s_service"
	"fate.manager/services/site_service"
	"fate.manager/services/version_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
	"strconv"
)

// @Summary Get Federation List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FederationResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/federation [get]
func GetFederationDropDownList(c *gin.Context) {
	appG := app.Gin{C: c}
	federationResponse, err := federated_service.GetFederationDropDownList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_FEDERATION__DROPLIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, federationResponse)
}

// @Summary Get Site List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Param federatedId query int false "int valid"
// @Success 200 {object} app.SiteResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/site [get]
func GetSiteDropDownList(c *gin.Context) {
	appG := app.Gin{C: c}
	federatedId, _ := strconv.Atoi(c.Request.FormValue("federatedId"))
	siteResponse, err := site_service.GetSiteDropDownList(federatedId)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SITE_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, siteResponse)
}

// @Summary Get Fate Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Param productType query int false "int valid,1:Fate,2:Fate Serving"
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/version [get]
func GetFateVersionDropDownList(c *gin.Context) {
	appG := app.Gin{C: c}
	productType, _ := strconv.Atoi(c.Request.FormValue("productType"))
	fateVersionResponse, err := version_service.GetFateVersionDropDownList(productType)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get Enum Name Drop Down List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/enumname [get]
func GetEnumNameDropDownList(c *gin.Context) {
	appG := app.Gin{C: c}
	result := enum.GetEnumNameDropDownList()
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Enum Name Info List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Param enumName query string false "enum name"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/enuminfo [post]
func GetEnumNameInfo(c *gin.Context) {
	appG := app.Gin{C: c}
	enumName := c.Request.FormValue("enumName")
	result := enum.GetEnumNameInfo(enumName)
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Mysql Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/mysql [get]
func GetMysqlVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("mysql")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get Cluster Manager Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/cluster [get]
func GetClusterManagerVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("clustermanager")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get NodeManager Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/node [get]
func GetNodeManagerVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("nodemanager")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get RollSite Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/rollsite [get]
func GetRollSiteVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("rollsite")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get FateFlow Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/fateflow [get]
func GetFateFlowVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("fateflow")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get Fateboard Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/fateboard [get]
func GetFateBoardVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("fateboard")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get Serving Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FateVersionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/fateserving [get]
func GetFateServingVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	fateVersionResponse, err := version_service.GetComponetVersionList("fateserving")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, fateVersionResponse)
}

// @Summary Get Serving Version List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Param fateVersion query string false "string valid"
// @Success 200 {object} app.ComponentResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/componentversion [post]
func GetComponentVersionList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var componentversionReq entity.ComponentversionReq
	if jsonError := json.Unmarshal(body, &componentversionReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	siteResponse, err := version_service.GetComponetVersionListByFateVersion(componentversionReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_VERSION_DROP_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, siteResponse)
}

// @Summary Get Ansible Manager Ip List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/managernode [get]
func GetManagerIp(c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := k8s_service.GetManagerIp()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_MANAGER_IP_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get Manager Ip:port List
// @Tags DropDownController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/dropdown/manager [get]
func GetManagerIpPort(c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := k8s_service.GetManagerIpPort()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_MANAGER_IP_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}
