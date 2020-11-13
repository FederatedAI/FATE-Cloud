package controllers

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/services/ansible_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
)

// @Summary Connect To Ansible Server
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.AnsibleReq true "request param"
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
	var ansibleReq entity.AnsibleReq
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

// @Summary prepare to deploy ansible
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.PrepareReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/prepare [post]
func Prepare(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var prepareReq entity.PrepareReq
	if jsonError := json.Unmarshal(body, &prepareReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.Prepare(prepareReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary update to deploy ansible
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.PrepareReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/updatemachine [post]
func UpdateMachine(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var prepareReq entity.PrepareReq
	if jsonError := json.Unmarshal(body, &prepareReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.Prepare(prepareReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary check system to deploy ansible
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.AnsibleCheckResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/check [post]
func CheckSystem (c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := ansible_service.CheckSystem()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.GET_CHECK_SYSYTEM_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary start to deploy ansible
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.DeployAnsibleReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/deployansible [post]
func StartDeployAnsible(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var deployAnsibleReq entity.DeployAnsibleReq
	if jsonError := json.Unmarshal(body, &deployAnsibleReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.StartDeployAnsible(deployAnsibleReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary get deploy ansible list
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.DeployAnsibleReq true "request param"
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/deployansible/result [post]
func GetDeployAnsibleList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var deployAnsibleReq entity.DeployAnsibleReq
	if jsonError := json.Unmarshal(body, &deployAnsibleReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.GetDeployAnsibleList(deployAnsibleReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_DEPLOY_ANSIBLE_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary local upload package
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.LocalUploadReq true "request param"
// @Success 200 {object} app.CommResp
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
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}


// @Summary auto acquire package
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoAcquireReq true "request param"
// @Success 200 {object} app.CommResp
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
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}