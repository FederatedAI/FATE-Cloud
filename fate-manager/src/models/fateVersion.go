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

type FateVersion struct {
	Id            int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FateVersion   string
	ProductType   int
	ChartVersion  string
	VersionIndex  int
	PullStatus    int
	PackageStatus int
	PackageUrl    string
	CreateTime    time.Time
	UpdateTime    time.Time
}

func GetFateVersionList(fateVersion *FateVersion) ([]*FateVersion, error) {
	var fateVersionList []*FateVersion
	Db := db
	if fateVersion.ProductType > 0 {
		Db = Db.Where("product_type = ?", fateVersion.ProductType)
	}
	if fateVersion.VersionIndex > 0 {
		Db = Db.Where("version_index > ?", fateVersion.VersionIndex)
	}
	if len(fateVersion.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", fateVersion.FateVersion)
	}
	if fateVersion.PullStatus > 0 {
		Db = Db.Where("pull_status = ?", fateVersion.PullStatus)
	}
	if len(fateVersion.ChartVersion) > 0 {
		Db = Db.Where("chart_version = ?", fateVersion.ChartVersion)
	}
	if fateVersion.PackageStatus > 0 {
		Db = Db.Where("package_status = ?", fateVersion.PackageStatus)
	}
	err := Db.Group("fate_version").Find(&fateVersionList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return fateVersionList, nil
}

func UpdateFateVersion(condition map[string]interface{}, info *FateVersion) error {
	Db := db
	if len(info.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", info.FateVersion)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if err := Db.Model(&FateVersion{}).Updates(condition).Error; err != nil {
		return err
	}
	return nil
}
func AddFateVersion(fateVersion *FateVersion) error {
	if err := db.Create(&fateVersion).Error; err != nil {
		return err
	}
	return nil
}