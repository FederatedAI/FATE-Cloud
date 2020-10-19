package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type ApplySiteInfo struct {
	Id           int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	UserId       string
	UserName     string
	Institutions string
	ReadStatus   int
	Status       int
	CreateTime   time.Time
	UpdateTime   time.Time
}

func GetApplySiteOne() (*ApplySiteInfo, error) {
	var result ApplySiteInfo
	Db := db
	err := Db.Where("status = 1").First(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &result, nil
}

func GetApplySiteInfo(applySiteInfo ApplySiteInfo) ([]*ApplySiteInfo, error) {
	var result []*ApplySiteInfo
	Db := db
	if applySiteInfo.ReadStatus >=0 {
		Db = Db.Where("read_status = ?", applySiteInfo.ReadStatus)
	}
	if applySiteInfo.Status > 0 {
		Db = Db.Where("status = ?", applySiteInfo.Status)
	}
	err := Db.Order("update_time desc").Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func AddApplySiteInfo(applySiteInfo *ApplySiteInfo) error {
	if err := db.Create(&applySiteInfo).Error; err != nil {
		return err
	}
	return nil
}

func UpdateApplySiteInfo(info map[string]interface{}, condition ApplySiteInfo) error {
	Db := db
	if condition.Status > 0 {
		Db = Db.Where("status = ?", condition.Status)
	}
	if len(condition.UserId) > 0 {
		Db = Db.Where("user_id = ?", condition.UserId)
	}
	if len(condition.UserName) > 0 {
		Db = Db.Where("user_name = ?", condition.UserName)
	}
	if err := Db.Model(&ApplySiteInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
