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
	"fate.manager/comm/enum"
	"github.com/jinzhu/gorm"
	"time"
)

type KubenetesConf struct {
	Id              int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	KubenetesUrl    string
	PythonPort      int
	RollsitePort    int
	NodeList        string
	DeployType      int
	ClickType       int
	CreateTime      time.Time
	UpdateTime      time.Time
}

func GetKubenetesConf(deployType enum.DeployType) (*KubenetesConf, error) {
	var kubenetesConf KubenetesConf
	Db := db
	err := Db.Where("deploy_type=?", int(deployType)).First(&kubenetesConf).Error
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

func GetKubenetesUrl(deployType enum.DeployType) (*KubenetesConf, error) {
	var kubenetesConfList []KubenetesConf

	err := db.Table("t_fate_kubenetes_conf t1").Select("t1.id,t1.kubenetes_url,t1.node_list").
		Joins(" join t_fate_deploy_site t2 on t1.id = t2.kubenetes_id and t2.is_valid = 1 and t1.deploy_type= ?", int(deployType)).
		Scan(&kubenetesConfList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	var kubenetesConf KubenetesConf
	if len(kubenetesConfList) > 0 {
		kubenetesConf = kubenetesConfList[0]
	}
	return &kubenetesConf, nil
}

func UpdateKubenetesConf(info map[string]interface{}, condition *KubenetesConf) error {
	Db := db
	if condition.Id > 0 {
		Db = Db.Where("id = ?", condition.Id)
	}
	if len(condition.KubenetesUrl) > 0 {
		Db = Db.Where("kubenetes_url = ?", condition.KubenetesUrl)
	}
	if condition.DeployType > 0 {
		Db = Db.Where("deploy_type = ?", condition.DeployType)
	}
	if err := Db.Model(&KubenetesConf{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func GetDefaultPort(componentName string,deployType enum.DeployType) int {
	var port int
	k8sinfo, err := GetKubenetesConf(deployType)
	if err != nil || k8sinfo.Id == 0 {
		return 0
	}
	if componentName == "proxy" {
		port = 9330
	} else if componentName == "roll" {
		port = 30001
	} else if componentName == "meta-service" {
		port = 30002
	} else if componentName == "egg" {
		port = 30003
	} else if componentName == "mysql" {
		port = 3306
	} else if componentName == "federation" {
		port = 9320
	} else if componentName == "fateboard" {
		port = 8080
	} else if componentName == "fateflow" {
		port = k8sinfo.PythonPort + 1
		if k8sinfo.PythonPort ==0 {
			port = 9380
		}
	} else if componentName == "serving-server" {
		port = 9340
	} else if componentName == "serving-proxy" {
		port = 9360
	} else if componentName == "clustermanager" {
		port = 4670
	} else if componentName == "nodemanager" {
		port = 4671
	} else if componentName == "rollsite" {
		port = k8sinfo.RollsitePort + 1
		if k8sinfo.RollsitePort ==0 {
			port = 9370
		}
	}
	return port
}