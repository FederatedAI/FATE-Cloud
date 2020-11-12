package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type AnsibleInfo struct {
	Id                 int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	Ip                 string
	NodeType           int
	IsValid            int
	CreateTime         time.Time
	UpdateTime         time.Time
}

func GetAnsibleInfoList(info AnsibleInfo) ([]*AnsibleInfo, error) {
	var result []*AnsibleInfo
	Db := db
	if len(info.Ip) > 0 {
		Db = Db.Where("ip = ?", info.Ip)
	}
	if info.NodeType > 0 {
		Db = Db.Where("node_type = ?", info.NodeType)
	}
	if info.IsValid > 0 {
		Db = Db.Where("is_valid = ?", info.IsValid)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func UpdateAnsibleInfo(info *AnsibleInfo) error {
	Db := db
	if len(info.Ip) > 0 {
		Db = Db.Where("ip = ?", info.Ip)
	}
	if info.NodeType > 0 {
		Db = Db.Where("node_type = ?", info.NodeType)
	}
	if info.IsValid > 0 {
		Db = Db.Where("is_valid = ?", info.IsValid)
	}
	if err := Db.Model(&AnsibleInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
func UpdateAnsibleInfoByCondition(info map[string]interface{}, condition AnsibleInfo) error {
	Db := db
	if condition.NodeType > 0 {
		Db = Db.Where("node_type = ?", condition.NodeType)
	}
	if condition.IsValid > 0 {
		Db = Db.Where("is_valid = ?", condition.IsValid)
	}
	if err := Db.Model(&AnsibleInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func AddAnsibleInfo(info *AnsibleInfo) error {
	if err := db.Create(&info).Error; err != nil {
		return err
	}
	return nil
}