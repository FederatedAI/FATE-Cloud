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
	StartTime          float64
	EndTime            float64
	Status             string
	CreateTime         int64
	UpdateTime         int64
}
type MonitorBase struct {
	Total    int
	Success  int
	Running  int
	Waiting  int
	Timeout  int
	Failed   int
	Canceled int
}
type MonitorTotal struct {
	MonitorBase
	ActiveData int
}
type MonitorBySite struct {
	GuestPartyId     int
	GuestSiteName    string
	GuestInstitution string
	HostPartyId      int
	HostSiteName     string
	HostInstitution  string
	MonitorBase
}

type PushSite struct {
	GuestPartyId string
	HostPartyId  string
	Success      int
	Running      int
	Waiting      int
	Failed       int
	Timeout      int
	Canceled     int
}

func GetTotalMonitorByRegion(monitorReq entity.MonitorReq) (*MonitorTotal, error) {
	var monitorTotal MonitorTotal
	err := db.Table("t_fate_monitor_detail").
		Select("COUNT(DISTINCT guest_party_id) active_data,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ? ", monitorReq.StartDate, monitorReq.EndDate).
		Find(&monitorTotal).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &monitorTotal, nil
}

func GetSiteMonitorByRegion(monitorReq entity.MonitorReq) ([]*MonitorBySite, error) {
	var monitorBySiteList []*MonitorBySite
	Db := db

	err := Db.Table("t_fate_monitor_detail").
		Select("guest_party_id,guest_site_name,guest_institution,host_institution,host_party_id,host_site_name,COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("guest_party_id,host_party_id").
		Find(&monitorBySiteList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorBySiteList, nil
}

func GetPushSiteMonitorList(monitorReq entity.MonitorReq) ([]*PushSite, error) {
	var pushSiteList []*PushSite
	Db := db
	err := Db.Table("t_fate_monitor_detail").
		Select("guest_party_id,host_party_id,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("guest_party_id,host_party_id").
		Find(&pushSiteList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return pushSiteList, nil
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

type ReportInstitutionItem struct {
	Ds          string
	Institution string
	Total       int
	Success     int
	Running     int
	Waiting     int
	Timeout     int
	Failed      int
	Canceled    int
}

func CalReportInstitutionByOne(monitorReq entity.MonitorReq) ([]ReportInstitutionItem, error) {
	var list []ReportInstitutionItem
	err := db.Table("t_fate_monitor_detail").
		Select("ds,guest_institution institution,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ? and guest_institution = host_institution", monitorReq.StartDate, monitorReq.EndDate).
		Group("ds,institution").
		Find(&list).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return list, nil
}
func CalReportInstitutionByTwo(institution string, monitorReq entity.MonitorReq) ([]ReportInstitutionItem, error) {
	var list []ReportInstitutionItem
	err := db.Table("t_fate_monitor_detail").
		Select("ds,if(guest_institution = ?,host_institution,guest_institution) institution,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed",institution).
		Where("ds >= ? and ds <= ? and guest_institution != host_institution AND (guest_institution =? OR host_institution=?)", monitorReq.StartDate, monitorReq.EndDate, institution, institution).
		Group("ds,institution").
		Find(&list).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return list, nil
}

type ReportSiteItem struct {
	Ds                  string
	Institution         string
	InstitutionSiteName string
	SiteName            string
	Total               int
	Success             int
	Running             int
	Waiting             int
	Timeout             int
	Failed              int
	Canceled            int
}

func CalReportSiteByOne(monitorReq entity.MonitorReq) ([]ReportSiteItem, error) {
	var list []ReportSiteItem
	err := db.Table("t_fate_monitor_detail").
		Select("ds,"+
			"guest_institution institution"+
			",guest_site_name institution_site_name,"+
			"guest_site_name site_name,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed").
		Where("ds >= ? and ds <= ? and guest_institution = host_institution", monitorReq.StartDate, monitorReq.EndDate).
		Group("ds,institution,institution_site_name,site_name").
		Find(&list).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return list, nil
}

func CalReportSiteByTwo(institution string, monitorReq entity.MonitorReq) ([]ReportSiteItem, error) {
	var list []ReportSiteItem
	err := db.Table("t_fate_monitor_detail").
		Select("ds,"+
			"if(guest_institution = ?,host_institution,guest_institution) institution,"+
			"if(guest_institution = ?,host_site_name,guest_site_name) institution_site_name,"+
			"if(guest_institution = ?,guest_site_name,host_site_name) site_name,"+
			"COUNT(job_id) total,"+
			"SUM(if(status='success',1,0)) success,"+
			"SUM(if(status='running',1,0)) running,"+
			"SUM(if(status='waiting',1,0)) waiting,"+
			"SUM(if(status='timeout',1,0)) timeout,"+
			"SUM(if(status='canceled',1,0)) canceled,"+
			"SUM(if(status='failed',1,0)) failed",institution, institution, institution).
		Where("ds >= ? and ds <= ? and guest_institution != host_institution AND (guest_institution =? OR host_institution=?)",
			 monitorReq.StartDate, monitorReq.EndDate, institution, institution).
		Group("ds,institution,institution_site_name,site_name").
		Find(&list).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return list, nil
}
