package controllers

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/services/component_deploy_service"
	"fate.manager/services/site_deploy_service"
	"fate.manager/services/version_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
)


// @Summary Get Prepare Details
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.PrepareResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/prepare [get]
func GetPrepare(c *gin.Context) {
	appG := app.Gin{C: c}
	result, err := site_deploy_service.GetPrepare()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_PREPARE_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Pull Site Component List
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.PullComponentListReq true "request param"
// @Success 200 {object} app.PullListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/pulllist [post]
func GetPullComponentList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var pullComponentListReq entity.PullComponentListReq
	if jsonError := json.Unmarshal(body, &pullComponentListReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := version_service.GetPullComponentList(pullComponentListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_PULL_COMPONENT_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Pull Fate Series Image
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.PullReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/pull [post]
func Pull(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var pullReq entity.PullReq
	if jsonError := json.Unmarshal(body, &pullReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	version_service.Pull(pullReq)
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Commit Pull Images
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.CommitImagePullReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/commit [post]
func CommitImagePull(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var commitImagePullReq entity.CommitImagePullReq
	if jsonError := json.Unmarshal(body, &commitImagePullReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := version_service.CommitImagePull(commitImagePullReq)
	if err != nil || result > 0 {
		appG.Response(http.StatusInternalServerError, result, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Get Pull Site Component List
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.InstallComponentListReq true "request param"
// @Success 200 {object} app.InstallListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/installlist [post]
func GetInstallComponentList(c *gin.Context) {
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
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.InstallReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/install [post]
func Install(c *gin.Context) {
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
	ret, err := site_deploy_service.Install(installReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, e.GetMsg(ret))
}

// @Summary Uprade Site
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.UpgradeReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/upgrade [post]
func Upgrade(c *gin.Context) {
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
	ret, err := site_deploy_service.Upgrade(upgradeReq)
	if err != nil || ret != e.SUCCESS {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Update Component Ip Address
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.UpdateReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/update [post]
func Update(c *gin.Context) {
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
	ret, err := site_deploy_service.Update(updateReq)
	if err != nil || ret != e.SUCCESS {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Get Auto Test Site List
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoTestListReq true "request param"
// @Success 200 {object} app.AutoTestListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/testlist [post]
func GetAutoTestList(c *gin.Context) {
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
	autoTestListResponse, err := site_deploy_service.GetAutoTestList(autoTestListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_AUTO_TEST_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, autoTestListResponse)
}

// @Summary Do Auto Test Start
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoTestReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/autotest [post]
func AutoTest(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var autoTestReq entity.AutoTestReq
	if jsonError := json.Unmarshal(body, &autoTestReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := site_deploy_service.AutoTest(autoTestReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Get Toy Test Result
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.TestResultReq true "request param"
// @Success 200 {object} app.PairResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/testresult [post]
func TestResult(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var testResultReq entity.TestResultReq
	if jsonError := json.Unmarshal(body, &testResultReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.TestResult(testResultReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_TOY_TEST_ONLY_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Click page
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.ClickReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/click [post]
func Click(c *gin.Context) {
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
	result := site_deploy_service.Click(clickReq)
	if !result {
		appG.Response(http.StatusOK, e.ERROR_CLICK_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, nil)
}

// @Summary Get Toy Test Only Read
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.ToyResultReadReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/testresultread [post]
func ToyResultRead(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var toyResultReadReq entity.ToyResultReadReq
	if jsonError := json.Unmarshal(body, &toyResultReadReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result := site_deploy_service.ToyResultRead(toyResultReadReq)
	if !result {
		appG.Response(http.StatusOK, e.ERROR_TOY_TEST_ONLY_READ_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Autotest Log
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.LogReq true "request param"
// @Success 200 {object} app.LogResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/testlog [post]
func GetTestLog(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var logReq entity.LogReq
	if jsonError := json.Unmarshal(body, &logReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	logResponse, err := site_deploy_service.GetTestLog(logReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_LOG_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, logResponse)
}

// @Summary Get Fateboard Url
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.FederatedSite true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/fateboard [post]
func GetFateBoardUrl(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var federatedSite entity.FederatedSite
	if jsonError := json.Unmarshal(body, &federatedSite); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.GetFateBoardUrl(federatedSite)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_LOG_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}

// @Summary Get Install Version
// @Tags DeployController
// @Accept  json
// @Produce  json
// @Param request body entity.FederatedSite true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/deploy/version [post]
func GetInstallVersion(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var federatedSite entity.FederatedSite
	if jsonError := json.Unmarshal(body, &federatedSite); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	result, err := site_deploy_service.GetInstallVersion(federatedSite)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_LOG_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, result)
}