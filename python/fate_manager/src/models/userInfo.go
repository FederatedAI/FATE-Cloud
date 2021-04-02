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

type UserInfo struct {
	Id         int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	UserId     string
	UserName   string
	Password   string
	CreateTime time.Time
	UpdateTime time.Time
}
type UserItem struct {
	UserId   string
	UserName string
}

func GetUserInfo(userInfo UserInfo) ([]*UserInfo, error) {
	var result []*UserInfo
	Db := db
	if len(userInfo.UserName) > 0 {
		Db = Db.Where("user_name = ?", userInfo.UserName)
	}
	if len(userInfo.Password) > 0 {
		Db = Db.Where("password = ?", userInfo.Password)
	}
	if len(userInfo.UserId) > 0 {
		Db = Db.Where("user_id = ?", userInfo.UserId)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func GetUserList(condition string) ([]*UserInfo, error) {
	var result []*UserInfo
	Db := db
	if len(condition) > 0 {
		Db = Db.Where("user_name like ? or user_id like ?", "%"+condition+"%", "%"+condition+"%")
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}
