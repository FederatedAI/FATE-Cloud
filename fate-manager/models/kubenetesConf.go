package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type KubenetesConf struct {
	Id           int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	KubenetesUrl string
	PythonPort  int
	RollsitePort int
	NodeList     string
	CreateTime   time.Time
	UpdateTime   time.Time
}

func GetKubenetesConf() (*KubenetesConf, error) {
	var kubenetesConf KubenetesConf
	Db := db
	err := Db.First(&kubenetesConf).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &kubenetesConf, nil
}

func AddKubenetesConf(kubenetesConf *KubenetesConf) error {
	if err := db.Create(&kubenetesConf).Error; err != nil {
		return err
	}
	return nil
}

func GetKubenetesUrl(federatedId int, partyId int) (*KubenetesConf, error) {
	var kubenetesConf KubenetesConf
	err := db.Table("t_fate_kubenetes_conf").Select("t_fate_kubenetes_conf.id,t_fate_kubenetes_conf.kubenetes_url,t_fate_kubenetes_conf.node_list").
		Joins(" join t_fate_deploy_site on t_fate_kubenetes_conf.id = t_fate_deploy_site.kubenetes_id and t_fate_deploy_site.is_valid = 1").
		First(&kubenetesConf).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return &kubenetesConf, nil
}

func UpdateKubenetesConf(info map[string]interface{}, condition KubenetesConf) error {
	Db := db
	if len(condition.KubenetesUrl) > 0 {
		Db = Db.Where("kubenetes_url = ?", condition.KubenetesUrl)
	}
	if err := Db.Model(&KubenetesConf{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
