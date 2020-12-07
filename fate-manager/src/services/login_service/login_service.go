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
package login_service

import (
	"encoding/json"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/comm/util"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/user_service"
	"fmt"
	"github.com/astaxie/beego/validation"
	"time"
)

func SignIn(loginReq entity.LoginReq) (int, *entity.LoginResp, error) {
	var loginResp entity.LoginResp
	var code int
	valid := validation.Validation{}
	accountInfo := models.AccountInfo{
		UserName: loginReq.AccountName,
		Status:   int(enum.IS_VALID_YES),
	}
	ok, _ := valid.Valid(&accountInfo)
	if ok {
		result, err := models.GetAccountInfo(accountInfo)
		if err != nil {
			return e.ERROR_SELECT_DB_FAIL, nil, err
		}
		if len(result) > 0 {
			if result[0].Role != int(enum.UserRole_ADMIN) && result[0].Role != int(enum.UserRole_DEVELOPER) && !loginReq.SubTag{
				return e.ERROR_SIGN_IN_FAIL, nil, nil
			}
			userInfo := models.UserInfo{
				UserName: loginReq.AccountName,
				Password: loginReq.Password,
			}
			ok, _ := valid.Valid(&userInfo)
			if ok {
				ret, err := user_service.GetUserInfo(userInfo)
				if err != nil {
					return e.ERROR_SELECT_DB_FAIL, nil, err
				}
				if len(ret) > 0 {
					token, err := util.GenerateToken(loginReq.AccountName, loginReq.Password)
					if err != nil {
						return e.ERROR_AUTH_TOKEN, nil, err
					} else {
						loginResp.Token = token

						tokenInfo := models.TokenInfo{
							UserId:     ret[0].UserId,
							UserName:   ret[0].UserName,
							Token:      token,
							ExpireTime: time.Now().Add(5 * time.Minute),
							CreateTime: time.Now(),
							UpdateTime: time.Now(),
						}
						models.AddTokenInfo(&tokenInfo)
					}
				} else {
					code = e.ERROR_USERNAME_OR_PASSWORD_FAIL
				}
			}
		} else {
			result, err := models.GetAccountInfo(models.AccountInfo{})
			if err != nil {
				return e.ERROR_SELECT_DB_FAIL, nil, err
			}
			if len(result) == 0 {
				code = e.ERROR_SITE_MANAGER_NOT_ACTIVE_FAIL
			} else {
				code = e.ERROR_CONCACT_SITE_MANAGER_FAIL
			}
		}
	} else {
		code = e.INVALID_PARAMS
	}
	return code, &loginResp, nil
}

func SignOut(logoutReq entity.LogoutReq, token string) (int, error) {

	accountInfo := models.AccountInfo{
		UserId:   logoutReq.AccountId,
		UserName: logoutReq.AccountName,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil || len(accountInfoList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}

	tokenInfo := models.TokenInfo{
		UserId:   logoutReq.AccountId,
		UserName: logoutReq.AccountName,
		Token:    token,
	}

	var data = make(map[string]interface{})
	data["expire_time"] = time.Now()
	data["update_time"] = time.Now()
	models.UpdateTokenInfo(data, tokenInfo)
	return e.SUCCESS, nil
}
func CheckJwt(checkJwtReq entity.CheckJwtReq) (int, *entity.CheckJwtResp, error) {
	tokenInfo := models.TokenInfo{
		Token: checkJwtReq.Token,
	}
	tokenInfoList, err := models.GetTokenInfo(tokenInfo)
	if err != nil || len(tokenInfoList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, nil, err
	}
	if time.Now().Unix() >= tokenInfoList[0].ExpireTime.Unix() {
		return e.ERROR_AUTH_CHECK_TOKEN_TIMEOUT, nil, err
	}
	checkJwtResp := entity.CheckJwtResp{
		UserId:   tokenInfoList[0].UserId,
		UserName: tokenInfoList[0].UserName,
	}
	accountInfo := models.AccountInfo{
		UserId:   tokenInfoList[0].UserId,
		UserName: tokenInfoList[0].UserName,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	checkJwtResp.Institutions = accountInfoList[0].Institutions

	var data = make(map[string]interface{})
	data["expire_time"] = time.Now().Add(30 * time.Minute)
	data["update_time"] = time.Now()
	models.UpdateTokenInfo(data, tokenInfo)

	return e.SUCCESS, &checkJwtResp, nil
}
func Activate(accountActivateReq entity.AccountActivateReq) (int, error) {

	accountInfo := models.AccountInfo{
		Role:   int(enum.UserRole_ADMIN),
		Status: int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if len(accountInfoList) > 0 {
		logging.Debug("The site was activated,do not actvivate again!")
		//return e.ERROR_ACCOUNT_ACTIVATE_FAIL, nil
	}
	if len(accountActivateReq.AccountName) == 0 || len(accountActivateReq.Password) == 0 {
		return e.ERROR_USERNAME_OR_PASSWORD_FAIL, nil
	}
	if len(accountActivateReq.Institution) == 0 ||
		len(accountActivateReq.AppKey) == 0 ||
		len(accountActivateReq.AppSecret) == 0 ||
		len(accountActivateReq.FateManagerId) == 0 {
		return e.INVALID_PARAMS, nil
	}
	userInfo := models.UserInfo{
		UserName: accountActivateReq.AccountName,
		Password: accountActivateReq.Password,
	}
	userInfoList, err := user_service.GetUserInfo(userInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	if len(userInfoList) == 0 {
		return e.ERROR_USERNAME_OR_PASSWORD_FAIL, err
	}
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountActivateReq.AppKey,
		UserAppSecret: accountActivateReq.AppSecret,
		FateManagerId: accountActivateReq.FateManagerId,
		Body:          "",
		Uri:           setting.UserActivateUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	result, err := http.POST(http.Url(accountActivateReq.FederatedUrl+setting.UserActivateUri), nil, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_ACCOUNT_ACTIVATE_FAIL, err
	}
	var activateResp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &activateResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if activateResp.Code == e.SUCCESS {
		accountInfo.UserName = userInfoList[0].UserName
		accountInfo.UserId = userInfoList[0].UserId
		accountInfo.Creator = userInfoList[0].UserName
		accountInfo.AppKey = accountActivateReq.AppKey
		accountInfo.AppSecret = accountActivateReq.AppSecret
		accountInfo.CloudUserId = accountActivateReq.FateManagerId
		accountInfo.Institutions = accountActivateReq.FateManagerInstitution
		accountInfo.AccountActiveUrl = accountActivateReq.ActivateUrl
		accountInfo.PermissionList = fmt.Sprintf("%d,%d,%d,%d", enum.PermissionType_BASIC, enum.PermissionType_DEPLOY, enum.PermissionType_FATEBOARD, enum.PermissionType_FATESTUDIO)
		accountInfo.CreateTime = time.Now()
		accountInfo.UpdateTime = time.Now()

		models.AddAccountInfo(&accountInfo)

		federated, err := models.GetFederatedUrlByFederationId(accountActivateReq.FederatedId, accountActivateReq.FederatedUrl)
		federatedId := federated.Id
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_GET_FEDERATED_FAIL))
			return e.ERROR_SELECT_DB_FAIL, err
		} else if federated.Id == 0 {
			federatedInfo := models.FederatedInfo{
				FederationId:          accountActivateReq.FederatedId,
				FederatedOrganization: accountActivateReq.FederatedOrganization,
				Institutions:          accountActivateReq.Institution,
				FederatedUrl:          accountActivateReq.FederatedUrl,
				Status:                1,
				CreateTime:            time.Now(),
				UpdateTime:            time.Now(),
			}
			federatedId, err = models.AddFederated(federatedInfo)
			if err != nil || federatedId == e.UNKONWN {
				logging.Error("add federated failed")
			}
		}
		return e.SUCCESS, nil
	}
	return e.ERROR_ACCOUNT_ACTIVATE_FAIL, nil
}
