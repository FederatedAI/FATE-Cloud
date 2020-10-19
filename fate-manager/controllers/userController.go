package controllers

import (
	"encoding/json"
	"fate.manager/comm/app"
	"fate.manager/comm/e"
	"fate.manager/comm/logging"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/account_service"
	"fate.manager/services/login_service"
	"github.com/gin-gonic/gin"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

func JWT() gin.HandlerFunc {
	return func(c *gin.Context) {
		appG := app.Gin{C: c}

		code := e.SUCCESS
		token := c.Request.Header.Get("token")
		authString := c.Request.Header.Get("Authorization")
		kv := strings.Split(authString, " ")
		if len(kv) == 2 && kv[0] == "Bearer" {
			token = kv[1]
		}
		if token == "" {
			code = e.INVALID_PARAMS
		} else {
			tokenInfo := models.TokenInfo{
				Token:      token,
				ExpireTime: time.Now(),
			}
			tokenInfoList, err := models.CheckTokenInfo(tokenInfo)
			if err != nil || len(tokenInfoList) == 0 {
				code = e.ERROR_AUTH_CHECK_TOKEN_TIMEOUT
			} else {
				var data = make(map[string]interface{})
				data["expire_time"] = time.Now().Add(30 * time.Minute)
				data["update_time"] = time.Now()
				models.UpdateTokenInfo(data, tokenInfo)
			}
		}
		if code != e.SUCCESS {
			appG.Response(http.StatusOK, code, nil)
			c.Abort()
			return
		} else {
			c.Next()
		}
		return
	}
}

// @Summary sign in
// @Tags UserController
// @Produce  json
// @Param request body entity.LoginReq true "request param"
// @Success 200 {object} app.LoginResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/login/login [post]
func SignIn(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var loginReq entity.LoginReq
	if jsonError := json.Unmarshal(body, &loginReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, result, err := login_service.SignIn(loginReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_SIGN_IN_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, result)
}

// @Summary sign out
// @Tags UserController
// @Produce  json
// @Param request body entity.LogoutReq true "request param"
// @Success 200 {object} app.LogoutResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/login/logout [post]
func SignOut(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	token := c.Request.Header.Get("token")
	var logoutReq entity.LogoutReq
	if jsonError := json.Unmarshal(body, &logoutReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := login_service.SignOut(logoutReq, token)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_SIGN_OUT_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary check jwt
// @Tags UserController
// @Produce  json
// @Param request body entity.CheckJwtReq true "request param"
// @Success 200 {object} app.CheckJwtResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/login/checkjwt [post]
func CheckJwt(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var checkJwtReq entity.CheckJwtReq
	if jsonError := json.Unmarshal(body, &checkJwtReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, result, err := login_service.CheckJwt(checkJwtReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_CHECK_JWT_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, result)
}

// @Summary Activate Account
// @Tags UserController
// @Produce  json
// @Param request body entity.AccountActivateReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/login/activate [post]
func Activate(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var accountActivateReq entity.AccountActivateReq
	if jsonError := json.Unmarshal(body, &accountActivateReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := login_service.Activate(accountActivateReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_ACCOUNT_ACTIVATE_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Get User Info
// @Tags UserController
// @Produce  json
// @Success 200 {object} app.UserInfoResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/info [get]
func GetUserInfo(c *gin.Context) {
	appG := app.Gin{C: c}
	token := c.Request.Header.Get("token")
	ret, err := account_service.GetUserInfo(token)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_USER_INFO_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get Account List
// @Tags UserController
// @Produce  json
// @Param request body entity.SearchReq true "request param"
// @Success 200 {object} app.UserListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/list [post]
func GetUserList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var searchReq entity.SearchReq
	if jsonError := json.Unmarshal(body, &searchReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.GetUserList(searchReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SITE_INFO_USER_LIST_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get User Access  List
// @Tags UserController
// @Produce  json
// @Param request body entity.UserAccessListReq true "request param"
// @Success 200 {object} app.UserAccessListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/accesslist [post]
func GetUserAccessList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var userAccessListReq entity.UserAccessListReq
	if jsonError := json.Unmarshal(body, &userAccessListReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.GetUserAccessList(userAccessListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_USER_ACCESS_LIST_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Add User
// @Tags UserController
// @Produce  json
// @Param request body entity.AddUserReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/add [post]
func AddUser(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var addUserReq entity.AddUserReq
	if jsonError := json.Unmarshal(body, &addUserReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.AddUser(addUserReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_EDIT_USER_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Edit User
// @Tags UserController
// @Produce  json
// @Param request body entity.EditUserReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/edit [post]
func EditUser(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var editUserReq entity.EditUserReq
	if jsonError := json.Unmarshal(body, &editUserReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.EditUser(editUserReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_EDIT_USER_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Delete User
// @Tags UserController
// @Produce  json
// @Param request body entity.DeleteUserReq true "request param"
// @Success 200 {object} app.Response
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/delete [post]
func DeleteUser(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	token := c.Request.Header.Get("token")
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var deleteUserReq entity.DeleteUserReq
	if jsonError := json.Unmarshal(body, &deleteUserReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.DeleteUser(token,deleteUserReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_DELETE_USER_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, ret, nil)
}

// @Summary Edit User Site List
// @Tags UserController
// @Produce  json
// @Success 200 {object} app.UserSiteListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/sitelist [post]
func UserSiteList(c *gin.Context) {
	appG := app.Gin{C: c}
	ret, err := account_service.UserSiteList()
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_EDIT_USER_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}

// @Summary Get Site Info Page User List
// @Tags UserController
// @Produce  json
// @Param request body entity.SiteInfoUserListReq true "request param"
// @Success 200 {object} app.SiteInfoUserListResponse
// @Failure 500 {object} app.Response
// @Router /fate-manager/api/user/siteinfouserlist [post]
func GetSiteInfoUserList(c *gin.Context) {
	appG := app.Gin{C: c}
	body, err := ioutil.ReadAll(c.Request.Body)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.INVALID_PARAMS, nil)
		return
	}
	var siteInfoUserListReq entity.SiteInfoUserListReq
	if jsonError := json.Unmarshal(body, &siteInfoUserListReq); jsonError != nil {
		logging.Error("JSONParse Error")
		panic("JSONParse Error")
	}
	ret, err := account_service.GetSiteInfoUserList(siteInfoUserListReq)
	if err != nil {
		appG.Response(http.StatusInternalServerError, e.ERROR_GET_SITE_INFO_USER_LIST_FAIL, ret)
		return
	}
	appG.Response(http.StatusOK, e.SUCCESS, ret)
}
