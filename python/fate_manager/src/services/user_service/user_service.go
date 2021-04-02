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
package user_service

import (
	"fate.manager/comm/enum"
	"fate.manager/comm/logging"
	"fate.manager/models"
	"fmt"
)

func GetUserInfo(userInfo models.UserInfo) ([]*models.UserInfo, error) {
	userInfoList, err := models.GetUserInfo(userInfo)
	if err != nil {
		str := fmt.Sprintf("get user info failed,userid:%s,username:%s", userInfo.UserId, userInfo.UserName)
		logging.Error(str)
		return nil, err
	}
	return userInfoList, nil
}

func GetUserList(str string) ([]*models.UserInfo, error) {
	userInfoList, err := models.GetUserList(str)
	if err != nil {
		return nil, err
	}
	return userInfoList, nil
}
func GetAdminInfo() (*models.AccountInfo, error) {
	accountInfo := models.AccountInfo{
		Role:   int(enum.UserRole_ADMIN),
		Status: int(enum.IS_VALID_YES),
	}
	accountInfoList, err := models.GetInstitution(accountInfo)
	if err != nil || len(accountInfoList) == 0 {
		logging.Error("admin user not exists!")
		return nil, err
	}
	return accountInfoList[0], nil
}
