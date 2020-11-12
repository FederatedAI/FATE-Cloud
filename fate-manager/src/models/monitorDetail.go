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
	"fate.manager/entity"
	"github.com/jinzhu/gorm"
)

type MonitorDetail struct {
	Id                 int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	Ds                 int
	GuestPartyId       int
	GuestSiteName      string
	GuestInstitution   string
	HostPartyId        int
	HostSiteName       string
	HostInstitution    string
	ArbiterPartyId     int
	ArbiterSiteName    string
	ArbiterInstitution string
	JobId              string
	StartTime          int64
	EndTime            int64
	Status             string
	CreateTime         int64
	UpdateTime         int64
}
type MonitorBase struct {
	Total   int
	Success int
	Running int
	Timeout int
	Failed  int
}
type MonitorTotal struct {
	MonitorBase
	ActiveData int
}
type MonitorBySite struct {
	GuestPartyId  int
	GuestSiteName string
	GuestInstitution string
	HostPartyId   int
	HostSiteName  string
	HostInstitution string
	MonitorBase
}

func GetTotalMonitorByRegion(monitorReq entity.MonitorReq) (*MonitorTotal, error) {
	var monitorTotal MonitorTotal
	err := db.Table("t_fate_monitor_detail").
		Select("COUNT(DISTINCT guest_party_id) active_data,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Find(&monitorTotal).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &monitorTotal, nil
}

func GetSiteMonitorByRegion(monitorReq entity.MonitorReq) ([]*MonitorBySite, error) {
	var monitorBySiteList []*MonitorBySite
	Db := db
	if monitorReq.PageNum > 0 && monitorReq.PageSize > 0 {
		Db = Db.Limit(monitorReq.PageSize).Offset((monitorReq.PageNum - 1) * monitorReq.PageSize)
	}
	err := Db.Table("t_fate_monitor_detail").
		Select("guest_party_id,guest_site_name,guest_institution,host_institution,host_party_id,host_site_name,COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("guest_party_id,host_party_id").
		Find(&monitorBySiteList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorBySiteList, nil
}

//func GetTotalMonitorByHis(monitorReq entity.MonitorReq) (*MonitorBase, error) {
//	var monitorBase MonitorBase
//	err := db.Table("t_fate_monitor_detail").
//		Select("COUNT(job_id) total,"+
//			"SUM(if(status='success',1,0)) success,"+
//			"SUM(if(status='running',1,0)) running,"+
//			"SUM(if(status='timeout',1,0)) timeout,"+
//			"SUM(if(status='failed',1,0)) failed").
//		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
//		//Group("guest_party_id,host_party_id").
//		Find(&monitorBase).Error
//	if err != nil && err != gorm.ErrRecordNotFound {
//		return nil, err
//	}
//	return &monitorBase, nil
//}
func GetSiteMonitorByHis(monitorReq entity.MonitorReq) ([]*MonitorBySite, error) {
	var monitorByHisList []*MonitorBySite
	Db := db
	if monitorReq.PageNum > 0 && monitorReq.PageSize > 0 {
		Db = Db.Limit(monitorReq.PageSize).Offset((monitorReq.PageNum - 1) * monitorReq.PageSize)
	}
	err := Db.Table("t_fate_monitor_detail").
		Select("guest_party_id,host_party_id,guest_institution,host_institution,guest_site_name,host_site_name,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("guest_party_id,host_party_id").
		Find(&monitorByHisList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorByHisList, nil
}

func AddMonitorDetail(info *MonitorDetail) error {
	if err := db.Create(&info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateMonitorDetail(info *MonitorDetail) error {
	if err := db.Model(&MonitorDetail{}).Where("job_id  = ? ", info.JobId).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func GetMonitorDetail(info *MonitorDetail) ([]*MonitorDetail, error) {
	var result []*MonitorDetail
	Db := db
	if len(info.JobId) > 0 {
		Db = Db.Where("job_id = ?", info.JobId)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}
