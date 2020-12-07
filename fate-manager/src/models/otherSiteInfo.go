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

type OtherSiteInfo struct {
	Id                     int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	PartyId                int
	SiteId                 int64
	SiteName               string
	Institutions           string
	Role                   int
	ServiceStatus          int
	Status                 int
	IsValid                int
	CreateTime             time.Time
	AcativationTime        time.Time
	UpdateTime             time.Time
}

func GetOtherSiteInfoList(info *OtherSiteInfo) ([]*OtherSiteInfo, error) {
	var siteInfoList []*OtherSiteInfo
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if len(info.Institutions) > 0 {
		Db = Db.Where("institutions = ?", info.Institutions)
	}
	err := Db.Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return siteInfoList, nil
}

func AddOterhSiteInfo(siteInfo *OtherSiteInfo) error {
	if err := db.Create(&siteInfo).Error; err != nil {
		return err
	}
	return nil
}

func UpdateOterhSiteInfoByCondition(info map[string]interface{}, condition OtherSiteInfo) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if err := Db.Model(&OtherSiteInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateOterhSiteInfo(info *OtherSiteInfo) error {
	if err := db.Model(&OtherSiteInfo{}).Where("party_id  = ? and institutions =? and is_valid=1", info.PartyId,info.Institutions).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
type OtherInstitution struct {
	SiteName string `json:"siteName"`
	Institution string `json:"institution"`
}
func GetOtherInstitutionList()(map[int]OtherInstitution,error){
	var siteInfoList []*OtherSiteInfo
	Db := db
	err := Db.Group("party_id").Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	var data = make(map[int]OtherInstitution)
	for i :=0 ;i< len(siteInfoList) ;i++  {
		other := OtherInstitution{
			SiteName:    siteInfoList[i].SiteName,
			Institution: siteInfoList[i].Institutions,
		}
		data[siteInfoList[i].PartyId] = other
	}
	return data,nil
}