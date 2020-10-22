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

type AutoTest struct {
	Id          int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId int
	PartyId     int
	ProductType int
	FateVersion string
	TestItem    string
	StartTime   time.Time
	EndTime     time.Time
	Status      int
	CreateTime  time.Time
	UpdateTime  time.Time
}

func GetAutoTest(info AutoTest) ([]AutoTest, error) {
	var autoTest []AutoTest
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if len(info.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", info.FateVersion)
	}
	if len(info.TestItem) > 0 {
		Db = Db.Where("test_item = ?", info.TestItem)
	}
	if info.Status > 0 {
		Db = Db.Where("status = ?", info.Status)
	}
	err := Db.Find(&autoTest).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return autoTest, nil
}

func AddAutoTest(autoTest AutoTest) error {
	if err := db.Create(&autoTest).Error; err != nil {
		return err
	}
	return nil
}

func UpdateAutoTest(info map[string]interface{}, condition AutoTest) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if len(condition.FateVersion) > 0 {
		Db = Db.Where("fate_version = ?", condition.FateVersion)
	}
	if condition.ProductType > 0 {
		Db = Db.Where("product_type = ?", condition.ProductType)
	}
	if len(condition.TestItem) > 0 {
		Db = Db.Where("test_item = ?", condition.TestItem)
	}
	if err := Db.Model(&AutoTest{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func DeleteTest(info AutoTest) error {
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if err := Db.Delete(&AutoTest{}).Error; err != nil {
		return err
	}
	return nil
}
