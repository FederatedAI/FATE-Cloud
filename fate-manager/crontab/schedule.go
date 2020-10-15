package crontab

import (
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/models"
	"fate.manager/services/changelog_service"
	"fate.manager/services/job_service"
	"fate.manager/services/site_service"
	"fate.manager/services/user_service"
	"time"
)

var AutoTestCheck = true

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
		AutoTestTask()
		AutoTestCheck = false
	}
}

func SiteStatusTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.Heart))
	for {
		logging.Debug("SiteStatusTask start...")
		site_service.GetHomeSiteList()
		logging.Debug("SiteStatusTask end...")
		<-ticker.C
	}
}

func IpManagerTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.IpManager))
	for {
		logging.Debug("IpManagerTask start...")
		changelog_service.GetChangeLogTask()
		logging.Debug("IpManagerTask end...")
		<-ticker.C
	}
}

func HeartTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.Heart))
	for {
		logging.Debug("HeartTask start...")
		site_service.HeartTask()
		logging.Debug("HeartTask end...")
		<-ticker.C
	}
}

func JobTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.Job))
	for {
		logging.Debug("JobTask start...")
		job_service.JobTask()
		logging.Debug("JobTask end...")
		<-ticker.C
	}
}

func TestOnlyTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.Test))
	for {
		logging.Debug("TestOnlyTask start...")
		job_service.TestOnlyTask()
		logging.Debug("TestOnlyTask end...")
		<-ticker.C
	}
}

func ApplyResultTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.IpManager))
	for {
		logging.Debug("ApplyResultTask start...")
		job_service.ApplyResultTask(accountInfo)
		logging.Debug("ApplyResultTask end...")
		<-ticker.C
	}
}
func AllowApplyTask(accountInfo *models.AccountInfo) {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.IpManager))
	for {
		logging.Debug("AllowApplyTask start...")
		job_service.AllowApplyTask(accountInfo)
		logging.Debug("AllowApplyTask end...")
		<-ticker.C
	}
}
func ComponentStatusTask() {
	ticker := time.NewTicker(time.Second * time.Duration(setting.ScheduleSetting.IpManager))
	for {
		logging.Debug("ComponentStatusTask start...")
		job_service.ComponentStatusTask()
		logging.Debug("ComponentStatusTask end...")
		<-ticker.C
	}
}
func AutoTestTask() {
	logging.Debug("AutoTestTask start...")
	job_service.AutoTestTask()
	logging.Debug("AutoTestTask end...")
}
