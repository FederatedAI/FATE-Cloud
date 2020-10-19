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

type KubenetesConf struct {
	Id           int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	KubenetesUrl string
	PythonPort   int
	RollsitePort int
	NodeList     string
	CreateTime   time.Time
	UpdateTime   time.Time
}

func GetKubenetesConf() (*KubenetesConf, error) {
	var kubenetesConf KubenetesConf
	Db := db
	err := Db.First(&kubenetesConf).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &kubenetesConf, nil
}

func AddKubenetesConf(kubenetesConf *KubenetesConf) error {
	if err := db.Create(&kubenetesConf).Error; err != nil {
		return err
	}
	return nil
}

func GetKubenetesUrl(federatedId int, partyId int) (*KubenetesConf, error) {
	var kubenetesConf KubenetesConf
	err := db.Table("t_fate_kubenetes_conf").Select("t_fate_kubenetes_conf.id,t_fate_kubenetes_conf.kubenetes_url,t_fate_kubenetes_conf.node_list").
		Joins(" join t_fate_deploy_site on t_fate_kubenetes_conf.id = t_fate_deploy_site.kubenetes_id and t_fate_deploy_site.is_valid = 1").
		First(&kubenetesConf).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return &kubenetesConf, nil
}

func UpdateKubenetesConf(info map[string]interface{}, condition KubenetesConf) error {
	Db := db
	if condition.Id > 0 {
		Db = Db.Where("id = ?", condition.Id)
	}
	if len(condition.KubenetesUrl) > 0 {
		Db = Db.Where("kubenetes_url = ?", condition.KubenetesUrl)
	}
	if err := Db.Model(&KubenetesConf{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
