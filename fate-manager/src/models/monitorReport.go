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

type ReportInstitution struct {
	Id          int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	Ds          string
	Institution string
	Total       int
	Success     int
	Running     int
	Timeout     int
	Failed      int
	CreateTime  time.Time
	UpdateTime  time.Time
}

type ReportSite struct {
	Id                  int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	Ds                  string
	Institution         string
	InstitutionSiteName string
	SiteName            string
	Total               int
	Success             int
	Running             int
	Timeout             int
	Failed              int
	CreateTime          time.Time
	UpdateTime          time.Time
}

type InstitutionMonitorByRegion struct {
	Institution string
	MonitorBase
}
type SiteMonitorByRegion struct {
	Institution         string
	InstitutionSiteName string
	SiteName            string
	MonitorBase
}

func GetReportInstitutionRegion(monitorReq entity.MonitorReq) ([]*InstitutionMonitorByRegion, error) {
	var MonitorByInstitutionList []*InstitutionMonitorByRegion
	Db := db
	if monitorReq.PageNum > 0 && monitorReq.PageSize > 0 {
		Db = Db.Limit(monitorReq.PageSize).Offset((monitorReq.PageNum - 1) * monitorReq.PageSize)
	}
	err := Db.Table("t_fate_report_institution").
		Select("institution,"+
			"sum(total) total,"+
			"SUM(success) success,"+
			"SUM(running) running,"+
			"SUM(timeout) timeout,"+
			"SUM(failed) failed").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("institution").
		Find(&MonitorByInstitutionList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return MonitorByInstitutionList, nil
}
type OtherSiteName struct {
	InstitutionSiteName string
}
func GetInstitutionSiteList(monitorReq entity.MonitorReq) ([]string, error) {
	var InstitutionSiteNameList []OtherSiteName
	Db := db
	if monitorReq.PageNum > 0 && monitorReq.PageSize > 0 {
		Db = Db.Limit(monitorReq.PageSize).Offset((monitorReq.PageNum - 1) * monitorReq.PageSize)
	}
	err := Db.Table("t_fate_report_site").
		Select("institution_site_name").
		Where("ds >= ? and ds <= ?", monitorReq.StartDate, monitorReq.EndDate).
		Group("institution_site_name").
		Order("create_time").
		Find(&InstitutionSiteNameList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	var list []string
	for i :=0 ;i< len(InstitutionSiteNameList);i++{
		list = append(list,InstitutionSiteNameList[i].InstitutionSiteName)
	}
	return list, nil
}
func GetReportSiteRegion(monitorReq entity.MonitorReq,list []string) ([]*SiteMonitorByRegion, error) {
	var monitorByHisList []*SiteMonitorByRegion
	Db := db
	if monitorReq.PageNum > 0 && monitorReq.PageSize > 0 {
		Db = Db.Limit(monitorReq.PageSize).Offset((monitorReq.PageNum - 1) * monitorReq.PageSize)
	}
	err := Db.Table("t_fate_report_site").
		Select("institution,institution_site_name,site_name,"+
			"sum(total) total,"+
			"SUM(success) success,"+
			"SUM(running) running,"+
			"SUM(timeout) timeout,"+
			"SUM(failed) failed").
		Where("ds >= ? and ds <= ? and institution_site_name in (?)", monitorReq.StartDate, monitorReq.EndDate,list).
		Group("institution,institution_site_name,site_name").
		Find(&monitorByHisList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return monitorByHisList, nil
}
func GetReportInstitution(reportInstitution *ReportInstitution) ([]*ReportInstitution, error) {
	var result []*ReportInstitution
	Db := db
	if len(reportInstitution.Ds) > 0 {
		Db = Db.Where("ds = ?", reportInstitution.Ds)
	}
	if len(reportInstitution.Institution) > 0 {
		Db = Db.Where("institution = ?", reportInstitution.Institution)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func GetReportSite(reportSite *ReportSite) ([]*ReportSite, error) {
	var result []*ReportSite
	Db := db
	if len(reportSite.Ds) > 0 {
		Db = Db.Where("ds = ?", reportSite.Ds)
	}
	if len(reportSite.Institution) > 0 {
		Db = Db.Where("institution = ?", reportSite.Institution)
	}
	if len(reportSite.InstitutionSiteName) > 0 {
		Db = Db.Where("institution_site_name = ?", reportSite.InstitutionSiteName)
	}
	if len(reportSite.SiteName) > 0 {
		Db = Db.Where("site_name = ?", reportSite.SiteName)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func UpdateReportInstitution(info map[string]interface{}, condition ReportInstitution) error {
	Db := db
	if len(condition.Ds) > 0 {
		Db = Db.Where("ds = ?", condition.Ds)
	}
	if len(condition.Institution) > 0 {
		Db = Db.Where("institution = ?", condition.Institution)
	}
	if err := Db.Model(&ReportInstitution{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateReportSite(info map[string]interface{}, condition ReportSite) error {
	Db := db
	if len(condition.Ds) > 0 {
		Db = Db.Where("ds = ?", condition.Ds)
	}
	if len(condition.Institution) > 0 {
		Db = Db.Where("institution = ?", condition.Institution)
	}
	if len(condition.InstitutionSiteName) > 0 {
		Db = Db.Where("institution_site_name = ?", condition.InstitutionSiteName)
	}
	if err := Db.Model(&ReportSite{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func AddReportInstitution(reportInstitution *ReportInstitution) error {
	if err := db.Create(&reportInstitution).Error; err != nil {
		return err
	}
	return nil
}
func AddReportSite(reportSite *ReportSite) error {
	if err := db.Create(&reportSite).Error; err != nil {
		return err
	}
	return nil
}
