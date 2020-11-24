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
// @Success 200 {object} app.Response
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

// @Summary get check system list
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.CheckSystemReq true "request param"
// @Success 200 {object} app.CheckSystemResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/getcheck [post]
func GetCheck (c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var checkSystemReq entity.CheckSystemReq
	if jsonError := json.Unmarshal(body, &checkSystemReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := ansible_service.GetCheck(checkSystemReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.GET_CHECK_SYSYTEM_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary get Ansible list
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.AnsibleListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/list [get]
func GetAnsibleList(c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := ansible_service.GetAnsibleList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.GET_ANSIBLE_LIST_FAIL, nil)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary start to deploy ansible
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Success 200 {object} app.CommResp
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/ansible/deployansible [post]
func StartDeployAnsible(c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := ansible_service.StartDeployAnsible()
	if err != nil {
		appG.Response(http.StatusInternalServerError, ret, nil)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary local upload package
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.LocalUploadReq true "request param"
// @Success 200 {object} app.AnsiblePackageResponse
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
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}


// @Summary auto acquire package
// @Tags AnsibleController
// @Accept  json
// @Produce  json
// @Param request body entity.AutoAcquireReq true "request param"
// @Success 200 {object} app.AnsiblePackageResponse
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
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}