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

type SiteInfo struct {
	Id                     int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId            int
	FederatedOrganization  string
	PartyId                int
	SiteId                 int64
	SiteName               string
	Institutions           string
	Role                   int
	AppKey                 string
	AppSecret              string
	RegistrationLink       string
	NetworkAccessEntrances string
	NetworkAccessExits     string
	FateVersion            string
	FateServingVersion     string
	ComponentVersion       string
	Status                 int
	EditStatus             int
	ReadStatus             int
	CreateTime             time.Time
	AcativationTime        time.Time
	UpdateTime             time.Time
}

//func GetSiteInfo(partyId int,federatedId int) (*SiteInfo, error) {
func GetSiteInfo(partyId int, federatedId int) (*SiteInfo, error) {
	var siteInfo SiteInfo
	err := db.Where("party_id  = ? ", partyId).First(&siteInfo).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &siteInfo, nil
}

func GetSiteList(info *SiteInfo) ([]*SiteInfo, error) {
	var siteInfoList []*SiteInfo
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.Role > 0 {
		Db = Db.Where("role = ?", info.Role)
	}
	if len(info.SiteName) > 0 {
		Db = Db.Where("site_name like ? or party_id like ?", "%"+info.SiteName+"%", "%"+info.SiteName+"%")
	}
	if info.Status > 0 {
		Db = Db.Where("status = ?", info.Status)
	}
	err := Db.Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return siteInfoList, nil
}

func AddSite(siteInfo *SiteInfo) error {
	if err := db.Create(&siteInfo).Error; err != nil {
		return err
	}
	return nil
}

func UpdateSite(info *SiteInfo) error {
	if err := db.Model(&SiteInfo{}).Where("party_id  = ? ", info.PartyId).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateSiteByCondition(info map[string]interface{}, condition SiteInfo) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if err := Db.Model(&SiteInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func GetSiteDropDownList(federatedId int) ([]*SiteInfo, error) {
	var siteInfoList []*SiteInfo
	err := db.Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return siteInfoList, nil
}
