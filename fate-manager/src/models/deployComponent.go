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

type DeployComponent struct {
	Id               int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId      int
	PartyId          int
	SiteName         string
	ProductType      int
	JobId            string
	FateVersion      string
	ComponentVersion string
	ComponentName    string
	Address          string
	Label            string
	StartTime        time.Time
	EndTime          time.Time
	Duration         int
	VersionIndex     int
	DeployStatus     int
	Status           int
	DeployType       int
	IsValid          int
	FinishTime       time.Time
	CreateTime       time.Time
	UpdateTime       time.Time
}

func GetDeployComponent(info DeployComponent) ([]*DeployComponent, error) {
	var deployComponentList []*DeployComponent
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if len(info.ComponentName) > 0 {
		Db = Db.Where("component_name = ?", info.ComponentName)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if info.IsValid > 0 {
		Db = Db.Where("is_valid = ?", info.IsValid)
	}
	if info.DeployStatus > 0 {
		Db = Db.Where("deploy_status = ?", info.DeployStatus)
	}
	if len(info.Address) >0 {
		Db = Db.Where("address = ?", info.Address)
	}
	if info.DeployType > 0 {
		Db = Db.Where("deploy_type = ?", info.DeployType)
	}
	err := Db.Order("component_name").Find(&deployComponentList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return deployComponentList, nil
}

func UpdateDeployComponent(info map[string]interface{}, condition DeployComponent) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if len(condition.ComponentName) > 0 {
		Db = Db.Where("component_name = ?", condition.ComponentName)
	}
	if condition.ProductType > 0 {
		Db = Db.Where("product_type = ?", condition.ProductType)
	}
	if condition.IsValid >= 0 {
		Db = Db.Where("is_valid = ?", condition.IsValid)
	}
	if condition.DeployStatus > 0 {
		Db = Db.Where("deploy_status = ?", condition.DeployStatus)
	}
	if err := Db.Model(&DeployComponent{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
func UpdateComponent(info *DeployComponent) error {
	if err := db.Model(&DeployComponent{}).Where("product_type = ? and component_name =? and is_valid=?", info.ProductType, info.ComponentName,info.IsValid).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
func AddDeployComponent(deployComponent *DeployComponent) error {
	if err := db.Create(&deployComponent).Error; err != nil {
		return err
	}
	return nil
}

func DeployComponentConditon(condition DeployComponent) ([]*DeployComponent, error) {
	var deployComponentList []*DeployComponent
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if len(condition.ComponentName) > 0 {
		Db = Db.Where("component_name != ?", condition.ComponentName)
	}
	if condition.ProductType > 0 {
		Db = Db.Where("product_type = ?", condition.ProductType)
	}
	if condition.IsValid >= 0 {
		Db = Db.Where("is_valid = ?", condition.IsValid)
	}
	if len(condition.Address) > 0 {
		Db = Db.Where("address = ?", condition.Address)
	}
	err := Db.Find(&deployComponentList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return deployComponentList, nil
}