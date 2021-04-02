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

type ChangeLog struct {
	Id                     int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId            int
	FederatedOrganization  string
	PartyId                int
	NetworkAccessEntrances string
	NetworkAccessExits     string
	Status                 int
	CaseId                 string
	CreateTime             time.Time
	UpdateTime             time.Time
}

type ChangeLogItem struct {
	FederatedId            int
	PartyId                int
	AppKey                 string
	AppSecret              string
	CaseId                 string
	Status                 int
	Role                   int
	NetworkAccessEntrances string
	NetworkAccessExits     string
}

func AddChangeLog(changeLog ChangeLog) error {
	if err := db.Create(&changeLog).Error; err != nil {
		return err
	}
	return nil
}

func GetNoDealList() []*ChangeLogItem {
	var ChangeLogList []*ChangeLogItem
	err := db.Table("t_fate_change_log").Select("t_fate_change_log.federated_id, t_fate_change_log.party_id, t_fate_site_info.app_key,t_fate_change_log.case_id,t_fate_site_info.app_secret," +
		"t_fate_change_log.status,t_fate_site_info.role,t_fate_change_log.network_access_entrances,t_fate_change_log.network_access_exits").
		Joins("left join t_fate_site_info on t_fate_change_log.party_id = t_fate_site_info.party_id and t_fate_change_log.federated_id = t_fate_site_info.federated_id where t_fate_change_log.status=0").
		Scan(&ChangeLogList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil
	}

	return ChangeLogList
}

func UpdateChangeLog(changelog ChangeLog) error {
	if err := db.Model(&ChangeLog{}).Where("case_id = ? AND  party_id= ? ", changelog.CaseId, changelog.PartyId).Updates(changelog).Error; err != nil {
		return err
	}
	return nil
}
