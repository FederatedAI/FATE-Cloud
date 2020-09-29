package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type DeployJob struct {
	Id          int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	JobId       string
	JobType     int
	Creator     string
	Status      int
	StartTime   time.Time
	EndTime     time.Time
	ClusterId   string
	FederatedId int
	PartyId     int
	Result      string
	ProductType int
	CreateTime  time.Time
	UpdateTime  time.Time
}

func AddDeployJob(deployJob *DeployJob) error {
	if err := db.Create(&deployJob).Error; err != nil {
		return err
	}
	return nil
}

func GetDeployJob(info DeployJob, orderType bool) ([]DeployJob, error) {
	var deployJob []DeployJob
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if orderType {
		Db.Order("update_time", true)
	} else {
		Db.Order("update_time", false)
	}
	err := Db.Where("status = ?", info.Status).Find(&deployJob).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return deployJob, nil
}

func UpdateDeployJob(info map[string]interface{}, condition *DeployJob) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if condition.ProductType > 0 {
		Db = Db.Where("product_type = ?", condition.ProductType)
	}
	if condition.JobId != "" {
		Db = Db.Where("job_id = ?", condition.JobId)
	}
	if err := Db.Model(&DeployJob{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
