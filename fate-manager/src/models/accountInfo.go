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
package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type AccountInfo struct {
	Id               int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	UserId           string
	UserName         string
	Institutions     string
	AppKey           string
	AppSecret        string
	CloudUserId      string
	Role             int
	PartyId          int
	SiteName         string
	PermissionList   string
	AccountActiveUrl string
	BlockMsg         string
	Creator          string
	Status           int
	AllowInstituions string
	CreateTime       time.Time
	UpdateTime       time.Time
}

func GetAccountInfo(accountInfo AccountInfo) ([]*AccountInfo, error) {
	var result []*AccountInfo
	Db := db
	if len(accountInfo.UserName) > 0 {
		Db = Db.Where("user_name = ?", accountInfo.UserName)
	}
	if len(accountInfo.UserId) > 0 {
		Db = Db.Where("user_id = ?", accountInfo.UserId)
	}
	if accountInfo.PartyId > 0 {
		Db = Db.Where("party_id = ?", accountInfo.PartyId)
	}
	if accountInfo.Status > 0 {
		Db = Db.Where("status = ?", accountInfo.Status)
	}
	if accountInfo.Role > 0 {
		Db = Db.Where("role = ?", accountInfo.Role)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}
func GetInstitution(accountInfo AccountInfo) ([]*AccountInfo, error) {
	var result []*AccountInfo
	Db := db
	if accountInfo.Status > 0 {
		Db = Db.Where("status = ?", accountInfo.Status)
	}
	if accountInfo.Role > 0 {
		Db = Db.Where("role = ?", accountInfo.Role)
	}
	err := Db.Where("cloud_user_id is not null").Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}
func UpdateAccountInfo(info map[string]interface{}, condition AccountInfo) error {
	Db := db
	if len(condition.UserId) > 0 {
		Db = Db.Where("user_id = ?", condition.UserId)
	}
	if len(condition.UserName) > 0 {
		Db = Db.Where("user_name = ?", condition.UserName)
	}
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if len(condition.SiteName) > 0 {
		Db = Db.Where("site_name = ?", condition.SiteName)
	}
	if condition.Status > 0 {
		Db = Db.Where("status = ?", condition.Status)
	}
	if err := Db.Model(&AccountInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func GetAccountSiteInfo(accountSiteInfo AccountInfo) ([]*AccountInfo, error) {
	var result []*AccountInfo
	Db := db
	if len(accountSiteInfo.UserName) > 0 {
		Db = Db.Where("user_name like ?", "%"+accountSiteInfo.UserName+"%")
	}
	if accountSiteInfo.Status > 0 {
		Db = Db.Where("status = ?", accountSiteInfo.Status)
	}
	if accountSiteInfo.PartyId > 0 {
		Db = Db.Where("party_id = ?", accountSiteInfo.PartyId)
	}
	if accountSiteInfo.Role > 0 {
		Db = Db.Where("role = ?", accountSiteInfo.Role)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func CheckAccountSiteInfo(accountSiteInfo AccountInfo) ([]*AccountInfo, error) {
	var result []*AccountInfo
	Db := db
	if len(accountSiteInfo.UserName) > 0 {
		Db = Db.Where("user_name = ?", accountSiteInfo.UserName)
	}
	if accountSiteInfo.Status > 0 {
		Db = Db.Where("status = ?", accountSiteInfo.Status)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func AddAccountInfo(accountInfo *AccountInfo) error {
	if err := db.Create(&accountInfo).Error; err != nil {
		return err
	}
	return nil
}
func CheckUser(accountInfo *AccountInfo) ([]*AccountInfo, error) {
	var result []*AccountInfo
	Db := db
	if len(accountInfo.UserName) > 0 {
		Db = Db.Where("user_name = ?", accountInfo.UserName)
	}
	if len(accountInfo.UserId) > 0 {
		Db = Db.Where("user_id = ?", accountInfo.UserId)
	}
	err := Db.Where("status =1 and role in(1,2)").Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}
