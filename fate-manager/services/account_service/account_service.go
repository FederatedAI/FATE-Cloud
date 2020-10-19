package account_service

import (
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/user_service"
	"fmt"
	"strconv"
	"strings"
	"time"
)

func GetInstitution(accountInfo models.AccountInfo) ([]*models.AccountInfo, error) {
	accountInfoList, err := models.GetInstitution(accountInfo)
	if err != nil {
		return nil, err
	}
	return accountInfoList, nil
}
func GetUserList(searchReq entity.SearchReq) ([]entity.UserListItem, error) {
	var data []entity.UserListItem
	userList, err := user_service.GetUserList(searchReq.Context)
	if err != nil {
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
			CloudUser:false,
			PermissionList: permissionPairList,
			Creator:        userAccessList[i].Creator,
			CreateTime:     userAccessList[i].CreateTime.UnixNano() / 1e6,
		}
		if len(userAccessList[i].CloudUserId) >0 {
			userAccessListItem.CloudUser = true
		}
		data = append(data, userAccessListItem)
	}
	return data, nil
}
func AddUser(addUserReq entity.AddUserReq) (int, error) {

	if addUserReq.RoleId == int(enum.UserRole_BUSINESS) && addUserReq.PartyId == 0 {
		return e.ERROR_ADD_USER_FAIL, nil
	}
	accountInfo := models.AccountInfo{
		UserId:   addUserReq.UserId,
		UserName: addUserReq.UserName,
	}
	accountInfoList, err := models.CheckUser(&accountInfo)
	if err != nil {
		return e.ERROR_ADD_USER_FAIL, err
	}
	if len(accountInfoList) > 0 {
		return e.ERROR_ADD_USER_FAIL, err
	}

	accountInfo.PartyId = addUserReq.PartyId
	if addUserReq.RoleId == int(enum.UserRole_BUSINESS) {
		accountInfoList, _ := models.GetAccountInfo(accountInfo)
		if len(accountInfoList) > 0 {
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
func DeleteUser(token string,deleteUserReq entity.DeleteUserReq) (int, error) {
	tokenInfo := models.TokenInfo{
		Token: token,
	}
	tokenInfoList, err := models.GetTokenInfo(tokenInfo)
	if err != nil || len(tokenInfoList) == 0 {
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
		return e.ERROR_DELETE_USER_FAIL,err
	}
	if accountInfoList[0].UserName == tokenInfoList[0].UserName {
		return e.ERROR_DELETE_USER_FAIL,err
	}
	var data = make(map[string]interface{})
	data["status"] = int(enum.IS_VALID_NO)
	models.UpdateAccountInfo(data, accountInfo)
	data=make(map[string]interface{})
	data["expire_time"]=time.Now()
	tokenInfo = models.TokenInfo{
		UserId:     deleteUserReq.UserId,
		UserName:   deleteUserReq.UserName,
	}
	tokenInfoList,_ = models.GetNoExpireTokenInfo(time.Now(),tokenInfo)
	for i := 0 ; i< len(tokenInfoList) ;i++{
		tokenInfo.Id = tokenInfoList[i].Id
		models.UpdateTokenInfo(data,tokenInfo)
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
