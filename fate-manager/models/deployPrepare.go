package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type DeployPrepare struct {
	Id           int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	PrepareTitle string
	PrepareDesc  string
	CreateTime   time.Time
	UpdateTime   time.Time
}

func GetPrepareList() ([]*DeployPrepare, error) {
	var deployPrepare []*DeployPrepare
	err := db.Find(&deployPrepare).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return deployPrepare, nil
}
