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

type ComponentVersion struct {
	Id               int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FateVersion      string
	ProductType      int
	ComponentVersion string
	ComponentName    string
	ImageId          string
	ImageName        string
	ImageVersion     string
	ImageTag         string
	ImageDescription string
	ImageSize        string
	ImageCreateTime  time.Time
	VersionIndex     int
	PullStatus       int
	PackageStatus    int
	CreateTime       time.Time
	UpdateTime       time.Time
}

func GetComponetVersionList(componentVersion ComponentVersion) ([]ComponentVersion, error) {
	var componentVersionList []ComponentVersion
	Db := db
	if len(componentVersion.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", componentVersion.FateVersion)
	}
	if componentVersion.ProductType > 0 {
		Db = Db.Where("product_type = ?", componentVersion.ProductType)
	}
	if len(componentVersion.ComponentName) > 0 {
		Db = Db.Where("component_name = ?", componentVersion.ComponentName)
	}
	if componentVersion.PullStatus > 0 {
		Db = Db.Where("pull_status = ?", componentVersion.PullStatus)
	}
	err := Db.Find(&componentVersionList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return componentVersionList, nil
}

func UpdateComponentVersion(info *ComponentVersion) error {
	if err := db.Model(&ComponentVersion{}).Where("fate_version = ? AND product_type = ? and component_name =? ", info.FateVersion, info.ProductType, info.ComponentName).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateComponentVersionByCondition(condition map[string]interface{},info *ComponentVersion) error {
	Db := db
	if len(info.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", info.FateVersion)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if err := Db.Model(&ComponentVersion{}).Updates(condition).Error; err != nil {
		return err
	}
	return nil
}
