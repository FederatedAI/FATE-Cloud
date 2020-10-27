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
	"time"
)

type MonitorDetail struct {
	Id               int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	Ds               int
	DsUnix           int64
	GuestPartyId     int
	GuestSiteName    string
	GuestRole        int
	GuestInstitution string
	HostPartyId      int
	HostSiteName     string
	HostRole         int
	HostInstitution  string
	JobId            string
	Status           int
	CreateTime       time.Time
	UpdateTime       time.Time
}
type MonitorBase struct {
	Total       int
	Complete    int
	Failed      int
}
type MonitorTotal struct {
	MonitorBase
	ActiveData  int
}
type MonitorBySite struct {
	GuestPartyId     int
	GuestSiteName    string
	HostPartyId      int
	HostSiteName     string
	MonitorBase
}

func GetTotalMonitorByRegion(monitorReq entity.MonitorReq)(*MonitorTotal,error){
	var monitorTotal *MonitorTotal
	err := db.Select("SELECT COUNT(DISTINCT guest_party_id) active_data,SUM(job_id) total,SUM(if(job_status=1,1,0)) complete,SUM(if(job_status=0,1,0)) failed  from t_fate_monitor_detail where create_time >= ? and create_time < ?",monitorReq.StartDate,monitorReq.EndDate).Find(&monitorTotal).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorTotal, nil
}

func GetSiteMonitorByRegion(monitorReq entity.MonitorReq)([]*MonitorBySite,error){
	var monitorBySiteList []*MonitorBySite
	err := db.Select("SELECT guest_party_id,guest_site_name,host_party_id,host_site_name,SUM(job_id) total,SUM(if(job_status=1,1,0)) complete,SUM(if(job_status=0,1,0)) failed  from t_fate_monitor_detail where create_time >= ? and create_time < ? group by guest_party_id,host_party_id",monitorReq.StartDate,monitorReq.EndDate).Find(&monitorBySiteList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorBySiteList, nil
}

func GetTotalMonitorByHis()(*MonitorBase,error){
	var monitorBase *MonitorBase
	err := db.Select("SELECT SUM(job_id) total,SUM(if(job_status=1,1,0)) complete,SUM(if(job_status=0,1,0)) failed  from t_fate_monitor_detail").Find(&monitorBase).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorBase, nil
}
func GetSiteMonitorByHis()([]*MonitorBySite,error){
	var monitorByHisList []*MonitorBySite
	err := db.Select("SELECT guest_party_id,host_party_id,SUM(job_id) total,SUM(if(job_status=1,1,0)) complete,SUM(if(job_status=0,1,0)) failed  from t_fate_monitor_detail where create_time >= ? and create_time < ? group by guest_party_id,host_party_id").Find(&monitorByHisList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorByHisList, nil
}