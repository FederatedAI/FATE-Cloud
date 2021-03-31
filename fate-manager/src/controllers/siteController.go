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
	"fate.manager/services/site_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
)

// @Summary get home page list
// @Tags SiteController
// @Produce  json
// @Success 200 {object} app.HomeSiteListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site [get]
func GetHomeSiteList(c *gin.Context) {
	appG := app.Gin{C: c}
	homeSiteListResp, err := site_service.GetHomeSiteList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SITE_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, homeSiteListResp)
}

// @Summary register site to federation net
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.RegisterReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/register [post]
func Register(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var registerReq entity.RegisterReq
	if jsonError := json.Unmarshal(body, &registerReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := federated_service.RegisterFederated(registerReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_REGISTER_FEDERATED_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary get site info
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.SiteDetailReq true "request param"
// @Success 200 {object} app.SiteDetailResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/info [post]
func GetSiteDetail(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var siteDetailReq entity.SiteDetailReq
	if jsonError := json.Unmarshal(body, &siteDetailReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	siteDetail, err := site_service.GetSiteDetail(siteDetailReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SITE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, siteDetail)
}

// @Summary update site info
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.UpdateSiteReq  true "update param"
// @Success 200 {object} app.BoolResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/update [post]
func UpdateSite(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var updateSiteReq *entity.UpdateSiteReq
	if jsonError := json.Unmarshal(body, &updateSiteReq); jsonError != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_PARSE_JSON_ERROR, jsonError)
	}
	ret, err := site_service.UpdateSite(updateSiteReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, true)
}

// @Summary check register url state
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.RegisterReq true "request param"
// @Success 200 {object} app.BoolResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/checkUrl [post]
func CheckRegisterUrl(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var registerReq entity.RegisterReq
	if jsonError := json.Unmarshal(body, &registerReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := federated_service.CheckRegisterUrl(registerReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, true)
}

// @Summary check site status
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.CheckSiteReq true "request param"
// @Success 200 {object} app.BoolResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/checksite [post]
func CheckSite(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var checkSiteReq entity.CheckSiteReq
	if jsonError := json.Unmarshal(body, &checkSiteReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := site_service.CheckSite(checkSiteReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary get site secret info
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.SecretInfoReq true "request param"
// @Success 200 {object} app.SiteSecretResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/secretinfo [post]
func GetSecretInfo(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var secretInfoReq entity.SecretInfoReq
	if jsonError := json.Unmarshal(body, &secretInfoReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_service.GetSecretInfo(secretInfoReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SECRET_INFO_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary telnet ip port
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.TelnetReq true "request param"
// @Success 200 {object} app.SiteSecretResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/telnet [post]
func TelnetSiteIp(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var telnetReq entity.TelnetReq
	if jsonError := json.Unmarshal(body, &telnetReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := site_service.TelnetIp(telnetReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Read Ip Change Result
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.ReadChangeReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/readmsg [post]
func ReadChangeMsg(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var readChangeReq entity.ReadChangeReq
	if jsonError := json.Unmarshal(body, &readChangeReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	if readChangeReq.ReadStatus != int(enum.READ_STATUS_READ) {
		appG.Response(http.StatusInternalServerError, e.ERROR_READ_STATUS_IS_NOT_EXISTS_FAIL, nil)
		return
	}
	ret, err := site_service.ReadChangeMsg(readChangeReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Get Ip Change Result
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.GetChangeReq true "request param"
// @Success 200 {object} app.GetChangeResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/getmsg [post]
func GetChangeMsg(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var getChangeReq entity.GetChangeReq
	if jsonError := json.Unmarshal(body, &getChangeReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_service.GetChangeMsg(getChangeReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_IP_CHANGE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Apply List
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.InstitutionsResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/institutions [get]
func GetApplyInstitutions(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetApplyInstitutions()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_APPLY_SITES_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Apply To View Other Sites
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.ApplySiteReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/applysite [post]
func ApplySites(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var applySiteReq entity.ApplySiteReq
	if jsonError := json.Unmarshal(body, &applySiteReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_service.ApplySites(applySiteReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_APPLY_SITES_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, result, nil)
}

// @Summary Query Apply Sites
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.QueryApplySitesResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/queryapply [get]
func QueryApplySites(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.QueryApplySites()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_QUERY_APPLY_SITES_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Read Apply Sites Result
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/readapplysite [post]
func ReadApplySites(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.ReadApplySites()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_UPDATE_APPLY_SITES_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, result, nil)
}

// @Summary Get All Function
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.FunctionResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/function [post]
func GetFunction(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetFunction()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_FUNCTION_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Read Function
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/functionread [get]
func FunctionRead(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.FunctionRead()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_FUNCTION_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Other Fate-Manager Site List
// @Tags SiteController
// @Produce  json
// @Success 200 {object} app.HomeSiteListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/other [get]
func GetOtherSiteList(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetOtherSiteList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_OTHER_SITE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Other Fate-Manager Apply to Current Fate Mnager list
// @Tags SiteController
// @Produce  json
// @Success 200 {object} app.ApplyFateManagerResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/fatemanager [get]
func GetFateManagerList(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetFateManagerList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_FATE_MANAGER_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get All Function
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Param request body entity.UpdateComponentVersionReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/updateVersion [post]
func UpdateComponentVersion(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var updateVersionReq entity.UpdateComponentVersionReq
	if jsonError := json.Unmarshal(body, &updateVersionReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_service.UpdateComponentVersion(updateVersionReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_UPDATE_COMPONENT_VERSION_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, result, nil)
}

// @Summary Get Apply Log
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/applylog [get]
func GetApplyLog(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetApplyLog()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_APPLY_LOG_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Exchange Info
// @Tags SiteController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.ExchangeResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/site/exchange [post]
func GetExchangeInfo(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_service.GetExchangeInfo()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_EXCHANGE_INFO_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}