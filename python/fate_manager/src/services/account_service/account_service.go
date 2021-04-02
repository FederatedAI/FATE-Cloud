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
package account_service

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
	"fate.manager/services/federated_service"
	"fate.manager/services/site_service"
	"fate.manager/services/user_service"
	"fmt"
	"strconv"
	"strings"
	"time"
)

func GetUserList(searchReq entity.SearchReq) ([]entity.UserListItem, error) {
	var data []entity.UserListItem
	userList, err := user_service.GetUserList(searchReq.Context)
	if err != nil {
		logging.Error("Get User List Failed!")
		return nil, err
	}
	for i := 0; i < len(userList); i++ {
		userListItem := entity.UserListItem{
			UserName: userList[i].UserName,
			UserId:   userList[i].UserId,
		}
		data = append(data, userListItem)
	}
	return data, nil
}
func GetUserAccessList(userAccessListReq entity.UserAccessListReq) ([]entity.UserAccessListItem, error) {

	accountSiteInfo := models.AccountInfo{
		UserName: userAccessListReq.UserName,
		PartyId:  userAccessListReq.PartyId,
		Role:     userAccessListReq.RoleId,
		Status:   int(enum.IS_VALID_YES),
	}
	userAccessList, err := models.GetAccountSiteInfo(accountSiteInfo)
	if err != nil {
		logging.Error("Get User Account Site Info Failed!")
		return nil, err
	}
	var data []entity.UserAccessListItem
	for i := 0; i < len(userAccessList); i++ {
		permissionList := strings.Split(userAccessList[i].PermissionList, ",")
		var permissionPairList []entity.Permission
		for j := 0; j < len(permissionList); j++ {
			permissionId, _ := strconv.Atoi(permissionList[j])
			permission := entity.Permission{
				PermissionId:   permissionId,
				PermissionName: enum.GetPermissionTypeString(enum.PermissionType(permissionId)),
			}
			permissionPairList = append(permissionPairList, permission)
		}
		userAccessListItem := entity.UserAccessListItem{
			UserId:   userAccessList[i].UserId,
			UserName: userAccessList[i].UserName,
			Site: entity.SitePair{
				PartyId:  userAccessList[i].PartyId,
				SiteName: userAccessList[i].SiteName,
			},
			Role: entity.Role{
				RoleId:   userAccessList[i].Role,
				RoleName: enum.GetUserRoleString(enum.UserRole(userAccessList[i].Role)),
			},
			CloudUser:      false,
			PermissionList: permissionPairList,
			Creator:        userAccessList[i].Creator,
			CreateTime:     userAccessList[i].CreateTime.UnixNano() / 1e6,
		}
		if len(userAccessList[i].CloudUserId) > 0 {
			userAccessListItem.CloudUser = true
		}
		data = append(data, userAccessListItem)
	}
	return data, nil
}
func AddUser(addUserReq entity.AddUserReq) (int, error) {

	if addUserReq.RoleId == int(enum.UserRole_BUSINESS) && addUserReq.PartyId == 0 {
		logging.Error(fmt.Sprintf("Add User Failed,roleId:%d,partyID:%d", addUserReq.RoleId, addUserReq.PartyId))
		return e.ERROR_ADD_USER_FAIL, nil
	}
	accountInfo := models.AccountInfo{
		UserId:   addUserReq.UserId,
		UserName: addUserReq.UserName,
	}
	accountInfoList, err := models.CheckUser(&accountInfo)
	if err != nil {
		logging.Error(fmt.Sprintf("CheckUser Failed,UserId:%s,UserName:%s", accountInfo.UserId, accountInfo.UserName))
		return e.ERROR_ADD_USER_FAIL, err
	}
	if len(accountInfoList) > 0 {
		logging.Error(fmt.Sprintf("CheckUser Empty,UserId:%s,UserName:%s", accountInfo.UserId, accountInfo.UserName))
		return e.ERROR_ADD_USER_FAIL, err
	}

	accountInfo.PartyId = addUserReq.PartyId
	if addUserReq.RoleId == int(enum.UserRole_BUSINESS) {
		accountInfoList, _ := models.GetAccountInfo(accountInfo)
		if len(accountInfoList) > 0 {
			logging.Error(fmt.Sprintf("User Has Exist,Add Failed,UserId:%s,UserName:%s", accountInfo.UserId, accountInfo.UserName))
			return e.ERROR_ADD_USER_FAIL, nil
		}
	}
	accountInfo.Role = addUserReq.RoleId
	accountInfo.SiteName = addUserReq.SiteName
	accountInfo.Creator = addUserReq.Creator
	accountInfo.UpdateTime = time.Now()
	accountInfo.CreateTime = time.Now()
	accountInfo.Status = int(enum.IS_VALID_YES)
	var permissionList string
	for i := 0; i < len(addUserReq.PermissionList); i++ {
		permissionId := addUserReq.PermissionList[i]
		permissionList = fmt.Sprintf("%s%d", permissionList, permissionId)
		if i < len(addUserReq.PermissionList)-1 {
			permissionList = permissionList + ","
		}
	}
	accountInfo.PermissionList = permissionList
	models.AddAccountInfo(&accountInfo)
	return e.SUCCESS, nil
}
func EditUser(editUserReq entity.EditUserReq) (int, error) {
	accountInfo := models.AccountInfo{
		UserId:   editUserReq.UserId,
		UserName: editUserReq.UserName,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.CheckUser(&accountInfo)
	if err != nil {
		logging.Error(fmt.Sprintf("CheckUser Failed,UserId:%s,UserName:%s", accountInfo.UserId, accountInfo.UserName))
		return e.ERROR_SELECT_DB_FAIL, nil
	}
	admin := false
	if len(accountInfoList) == 0 {
		accountInfo.PartyId = editUserReq.OldPartyId
		accountInfo.Role = editUserReq.OldRoleId
	} else {
		for i := 0; i < len(accountInfoList); i++ {
			if accountInfoList[i].Role == int(enum.UserRole_ADMIN) && len(accountInfoList[i].CloudUserId) > 0 {
				admin = true
				break
			}
		}
	}

	if admin {
		logging.Error(fmt.Sprintf("Admin User Could Not Edite,UserId:%s,UserName:%s", accountInfo.UserId, accountInfo.UserName))
		return e.ERROR_EDIT_USER_FAIL, nil
	}
	var permissionList string
	for i := 0; i < len(editUserReq.PermissionList); i++ {
		permissionId := editUserReq.PermissionList[i]
		permissionList = fmt.Sprintf("%s%d", permissionList, permissionId)
		if i < len(editUserReq.PermissionList)-1 {
			permissionList = permissionList + ","
		}
	}
	var data = make(map[string]interface{})
	data["role"] = editUserReq.RoleId
	data["party_id"] = editUserReq.PartyId
	data["site_name"] = editUserReq.SiteName
	data["permission_list"] = permissionList
	data["update_time"] = time.Now()
	if editUserReq.RoleId == int(enum.UserRole_ADMIN) || editUserReq.RoleId == int(enum.UserRole_DEVELOPER) {
		data["party_id"] = 0
		data["site_name"] = ""
	}
	models.UpdateAccountInfo(data, accountInfo)
	return e.SUCCESS, nil
}
func DeleteUser(token string, deleteUserReq entity.DeleteUserReq) (int, error) {
	tokenInfo := models.TokenInfo{
		Token: token,
	}
	tokenInfoList, err := models.GetTokenInfo(tokenInfo)
	if err != nil || len(tokenInfoList) == 0 {
		logging.Error(fmt.Sprintf("Unvalid Token:%s", token))
		return e.ERROR_AUTH_CHECK_TOKEN_TIMEOUT, err
	}
	accountInfo := models.AccountInfo{
		UserId:   deleteUserReq.UserId,
		UserName: deleteUserReq.UserName,
		PartyId:  deleteUserReq.PartyId,
		SiteName: deleteUserReq.SiteName,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil || len(accountInfoList) == 0 {
		return e.ERROR_USER_NOT_EXISTS_FAIL, err
	}
	if accountInfoList[0].Role == int(enum.UserRole_ADMIN) && len(accountInfoList[0].CloudUserId) > 0 {
		logging.Error(fmt.Sprintf("Admin Could Not Be Delete,Username:%s", accountInfoList[0].UserName))
		return e.ERROR_DELETE_USER_FAIL, err
	}
	if accountInfoList[0].UserName == tokenInfoList[0].UserName {
		logging.Error(fmt.Sprintf("Could Not Be Delete Self,Username:%s", accountInfoList[0].UserName))
		return e.ERROR_DELETE_USER_FAIL, err
	}
	var data = make(map[string]interface{})
	data["status"] = int(enum.IS_VALID_NO)
	models.UpdateAccountInfo(data, accountInfo)
	data = make(map[string]interface{})
	data["expire_time"] = time.Now()
	tokenInfo = models.TokenInfo{
		UserId:   deleteUserReq.UserId,
		UserName: deleteUserReq.UserName,
	}
	tokenInfoList, _ = models.GetNoExpireTokenInfo(time.Now(), tokenInfo)
	for i := 0; i < len(tokenInfoList); i++ {
		tokenInfo.Id = tokenInfoList[i].Id
		models.UpdateTokenInfo(data, tokenInfo)
	}
	return e.SUCCESS, nil
}
func UserSiteList() ([]entity.SitePair, error) {
	var data []entity.SitePair

	infoList, err := models.GetFederatedInfo()
	if err != nil || len(infoList) == 0 {
		return nil, err
	}
	siteInfoList, err := models.GetSiteDropDownList(infoList[0].Id)
	if err != nil {
		logging.Error(fmt.Sprintf("Get Site Drop Down List Failed!"))
		return nil, err
	}
	for i := 0; i < len(siteInfoList); i++ {
		if siteInfoList[i].Status != int(enum.SITE_STATUS_JOINED) {
			continue
		}
		item := entity.SitePair{
			PartyId:  siteInfoList[i].PartyId,
			SiteName: siteInfoList[i].SiteName,
		}
		data = append(data, item)
	}
	return data, nil
}
func GetSiteInfoUserList(siteInfoUserListReq entity.SiteInfoUserListReq) ([]entity.SiteInfoUserListItem, error) {
	var data []entity.SiteInfoUserListItem
	accountInfo := models.AccountInfo{
		PartyId: siteInfoUserListReq.PartyId,
		Status:  int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil {
		logging.Error(fmt.Sprintf("GetAccountInfo Failed!"))
		return nil, err
	}
	for i := 0; i < len(accountInfoList); i++ {
		var permissionList string
		permission := strings.Split(accountInfoList[i].PermissionList, ",")
		for j := 0; j < len(permission); j++ {
			permissionId, _ := strconv.Atoi(permission[j])
			permissionName := enum.GetPermissionTypeString(enum.PermissionType(permissionId))
			permissionList = fmt.Sprintf("%s%s", permissionList, permissionName)
			if i < len(permission)-1 {
				permissionList = permissionList + ";"
			}
		}
		accountInfo.PermissionList = permissionList
		siteInfoUserListItem := entity.SiteInfoUserListItem{
			UserName:   accountInfoList[i].UserName,
			Role:       enum.GetUserRoleString(enum.UserRole(accountInfoList[i].Role)),
			Permission: permissionList,
		}
		data = append(data, siteInfoUserListItem)
	}
	return data, nil
}
func GetUserInfo(token string) (*entity.UserInfoResp, error) {
	tokenInfo := models.TokenInfo{
		Token: token,
	}
	tokenInfoList, err := models.GetTokenInfo(tokenInfo)
	if err != nil || len(tokenInfoList) == 0 {
		return nil, err
	}
	accountInfo := models.AccountInfo{
		UserName: tokenInfoList[0].UserName,
		UserId:   tokenInfoList[0].UserId,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil || len(accountInfoList) == 0 {
		logging.Error(fmt.Sprintf("GetAccountInfo Failed!"))
		return nil, err
	}
	role := entity.Role{
		RoleId:   accountInfoList[0].Role,
		RoleName: enum.GetUserRoleString(enum.UserRole(accountInfoList[0].Role)),
	}
	var userInfoResp entity.UserInfoResp
	userInfoResp.Role = role
	permissionList := strings.Split(accountInfoList[0].PermissionList, ",")
	var permissionPairList []entity.Permission
	for j := 0; j < len(permissionList); j++ {
		permissionId, _ := strconv.Atoi(permissionList[j])
		permission := entity.Permission{
			PermissionId:   permissionId,
			PermissionName: enum.GetPermissionTypeString(enum.PermissionType(permissionId)),
		}
		permissionPairList = append(permissionPairList, permission)
	}
	userInfoResp.PermissionList = permissionPairList
	userInfoResp.UserName = accountInfoList[0].UserName
	userInfoResp.UserId = accountInfoList[0].UserId

	return &userInfoResp, nil
}


func PermissionAuthority(permissionAuthorityReq entity.PermissionAuthorityReq) (bool, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return false,err
	}
	checkPartyId := entity.CheckPartyId{
		Institutions: accountInfo.Institutions,
		PartyId:      permissionAuthorityReq.PartyId,
	}
	checkPartyIdJson,_ := json.Marshal(checkPartyId)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          string(checkPartyIdJson),
		Uri:           setting.CheckPartyUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return false,err
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.CheckPartyUri), checkPartyId, headerInfoMap)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return false,err
	}
	var checkPartyIdResp entity.CheckPartyIdResp
	err = json.Unmarshal([]byte(result.Body), &checkPartyIdResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return false,err
	}
	if checkPartyIdResp.Code == e.SUCCESS {
		return checkPartyIdResp.Data,nil
	}
	return false,nil
}

func GetLoginUserManagerList(userListItem entity.UserListItem)([]entity.LoginSiteItem,error){
	accountInfo := models.AccountInfo{
		UserId:           userListItem.UserId,
		UserName:         userListItem.UserName,
		Status:           int(enum.IS_VALID_YES),
	}
	accountInfoList,err := models.GetAccountInfo(accountInfo)
	if err != nil {
		return nil,err
	}
	var list []entity.LoginSiteItem
	for i :=0;i< len(accountInfoList) ;i++  {
		siteInfo :=models.SiteInfo{
			PartyId:                accountInfoList[i].PartyId,
			Status:                 int(enum.SITE_STATUS_JOINED),
		}
		siteInfoList,_ := models.GetSiteList(&siteInfo)

		loginSiteItem := entity.LoginSiteItem{
			PartyId:  accountInfoList[i].PartyId,
			SiteName: accountInfoList[i].SiteName,
		}
		if len(siteInfoList) >0{
			loginSiteItem.Role = entity.IdPair{
				Code: siteInfoList[0].Role,
				Desc: enum.GetRoleString(enum.RoleType(siteInfoList[0].Role)),
			}
		}
		list = append(list,loginSiteItem)
	}
	return list,nil
}

func GetAllAllowPartyList(allowReq entity.AllowReq)([]entity.FederatedItem,error){
	var list []entity.FederatedItem
	FederatedItemList,err := site_service.GetOtherSiteList()
	if err != nil{
		return nil,err
	}
	if len(allowReq.RoleName) == 0{
		return FederatedItemList,nil
	}
	for i := 0 ; i< len(FederatedItemList) ;i++  {
		federatedItem := FederatedItemList[i]
		tag := true
		for j :=0;j< len(federatedItem.SiteItemList) ;j++  {
			siteItem := federatedItem.SiteItemList[j]
			if allowReq.RoleName == siteItem.Role.Desc {
				tag = false
				break
			}
		}
		if tag {
			list = append(list,federatedItem)
		}
	}
	return list,nil
}

func SubLogin(subLogin entity.SubLoginReq)(int,*entity.SubLoginResp,error) {
	userInfo := models.UserInfo{
		UserName: subLogin.AccountName,
		Password: subLogin.Password,
	}
	userInfoList, err := user_service.GetUserInfo(userInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, nil, err
	}
	if len(userInfoList) == 0 {
		return e.ERROR_USER_NOT_EXISTS_FAIL, nil, nil
	}
	accountInfo := models.AccountInfo{
		UserId:   userInfoList[0].UserId,
		UserName: subLogin.AccountName,
		PartyId:  subLogin.PartyId,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, nil, err
	}
	if len(accountInfoList) == 0 {
		return e.ERROR_NO_PARTY_PRIVILEGE_FAIL, nil, nil
	}
	siteInfo :=models.SiteInfo{
		PartyId:                accountInfoList[0].PartyId,
		Status:                 int(enum.SITE_STATUS_JOINED),
	}
	siteInfoList,_ := models.GetSiteList(&siteInfo)

	subLoginResp := entity.SubLoginResp{
		PartyId:  accountInfoList[0].PartyId,
		SiteName: accountInfoList[0].SiteName,
	}
	if len(siteInfoList) >0 {
		subLoginResp.Role = entity.Role{
			RoleId:   siteInfoList[0].Role,
			RoleName: enum.GetRoleString(enum.RoleType(siteInfoList[0].Role)),
		}
	}
	return e.SUCCESS,&subLoginResp,nil
}

func ChangeLogin(subLogin entity.ChangeLoginReq)(int,*entity.SubLoginResp,error) {
	accountInfo := models.AccountInfo{
		UserName: subLogin.AccountName,
		PartyId:  subLogin.PartyId,
		Status:   int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetAccountInfo(accountInfo)
	if err != nil {
		return e.ERROR_SELECT_DB_FAIL, nil, err
	}
	if len(accountInfoList) == 0 {
		return e.ERROR_NO_PARTY_PRIVILEGE_FAIL, nil, nil
	}
	siteInfo :=models.SiteInfo{
		PartyId:                accountInfoList[0].PartyId,
		Status:                 int(enum.SITE_STATUS_JOINED),
	}
	siteInfoList,_ := models.GetSiteList(&siteInfo)

	subLoginResp := entity.SubLoginResp{
		PartyId:  accountInfoList[0].PartyId,
		SiteName: accountInfoList[0].SiteName,
	}
	if len(siteInfoList) >0 {
		subLoginResp.Role = entity.Role{
			RoleId:   siteInfoList[0].Role,
			RoleName: enum.GetRoleString(enum.RoleType(siteInfoList[0].Role)),
		}
	}
	return e.SUCCESS,&subLoginResp,nil
}