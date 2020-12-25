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
package crontab

import (
	"fate.manager/comm/logging"
	"fate.manager/models"
	"fate.manager/services/changelog_service"
	"fate.manager/services/job_service"
	"fate.manager/services/site_service"
	"fate.manager/services/user_service"
	"time"
)

var (
	AutoTestCheck   = true
	SiteStatusTimer = 10
	IpManagerTimer  = 30
	HeartTimer      = 10
	JobTimer        = 30
	TestOnlyTimer   = 30
	ComponentTimer  = 10
	ApplyTimer      = 10
	AllowApplyTimer = 30
	MonitorTimer    = 120
	PageckageTimer  = 30
	AutotestTimer   = 60
	VersionTimer    = 600
)

func SetUp() {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return
	}
	go SiteStatusTask()
	go IpManagerTask()
	go HeartTask()
	go JobTask()
	go TestOnlyTask()
	go ComponentStatusTask()
	go ApplyResultTask(accountInfo)
	go AllowApplyTask(accountInfo)
	if AutoTestCheck {
		AutoTestTaskOne()
		AutoTestCheck = false
	}
	go MonitorTask(accountInfo)
	go PackageStatusTask()
	go AutotestTask()
	go VersionUpdateTask(accountInfo)
}
func SiteStatusTask() {
	ticker := time.NewTicker(time.Second * time.Duration(SiteStatusTimer))
	for {
		logging.Debug("SiteStatusTask start...")
		site_service.GetHomeSiteList()
		logging.Debug("SiteStatusTask end...")
		<-ticker.C
	}
}
func IpManagerTask() {
	ticker := time.NewTicker(time.Second * time.Duration(IpManagerTimer))
	for {
		logging.Info("IpManagerTask start...")
		changelog_service.GetChangeLogTask()
		logging.Info("IpManagerTask end...")
		<-ticker.C
	}
}
func HeartTask() {
	ticker := time.NewTicker(time.Second * time.Duration(HeartTimer))
	for {
		logging.Debug("HeartTask start...")
		site_service.HeartTask()
		logging.Debug("HeartTask end...")
		<-ticker.C
	}
}
func JobTask() {
	ticker := time.NewTicker(time.Second * time.Duration(JobTimer))
	for {
		logging.Debug("JobTask start...")
		job_service.JobTask()
		logging.Debug("JobTask end...")
		<-ticker.C
	}
}
func TestOnlyTask() {
	ticker := time.NewTicker(time.Second * time.Duration(TestOnlyTimer))
	for {
		logging.Debug("TestOnlyTask start...")
		job_service.TestOnlyTask()
		logging.Debug("TestOnlyTask end...")
		<-ticker.C
	}
}
func ApplyResultTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(ApplyTimer))
	for {
		logging.Debug("ApplyResultTask start...")
		job_service.ApplyResultTask(accountInfo)
		logging.Debug("ApplyResultTask end...")
		<-ticker.C
	}
}
func AllowApplyTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(AllowApplyTimer))
	for {
		logging.Debug("AllowApplyTask start...")
		job_service.AllowApplyTask(accountInfo)
		logging.Debug("AllowApplyTask end...")
		<-ticker.C
	}
}
func ComponentStatusTask() {
	ticker := time.NewTicker(time.Second * time.Duration(ComponentTimer))
	for {
		logging.Debug("ComponentStatusTask start...")
		job_service.ComponentStatusTask()
		logging.Debug("ComponentStatusTask end...")
		<-ticker.C
	}
}
func AutoTestTaskOne() {
	logging.Debug("AutoTestTask start...")
	job_service.AutoTestTask()
	logging.Debug("AutoTestTask end...")
}
func MonitorTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(MonitorTimer))
	for {
		logging.Debug("MonitorTask start...")
		job_service.MonitorTask(accountInfo)
		logging.Debug("MonitorTask end...")
		<-ticker.C
	}
}
func PackageStatusTask() {
	ticker := time.NewTicker(time.Second * time.Duration(PageckageTimer))
	for {
		logging.Debug("PackageStatusTask start...")
		job_service.PackageStatusTask()
		logging.Debug("PackageStatusTask end...")
		<-ticker.C
	}
}
func AutotestTask() {
	ticker := time.NewTicker(time.Second * time.Duration(AutotestTimer))
	for {
		logging.Debug("AutotestTask start...")
		job_service.AutotestTask()
		logging.Debug("AutotestTask end...")
		<-ticker.C
	}
}
func VersionUpdateTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(VersionTimer))
	for {
		logging.Debug("VersionUpdateTask start...")
		job_service.VersionUpdateTask(accountInfo)
		logging.Debug("VersionUpdateTask end...")
		<-ticker.C
	}
}
