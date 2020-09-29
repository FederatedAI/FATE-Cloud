package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type DeploySite struct {
	Id                 int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId        int
	PartyId            int
	ProductType        int
	FateVersion        string
	JobId              string
	Name               string
	NameSpace          string
	Revision           int
	DeployStatus       int
	Status             int
	Chart              string
	ChartVersion       string
	ClusterValues      string
	ClusterInfo        string
	UpgradeStatus      int
	VersionIndex       int
	Fateboard          string
	ClusterId          string
	Duration           int
	SingleTest         int
	ToyTest            int
	ToyTestOnly        int
	ToyTestOnlyRead    int
	KubenetesId        int64
	Config             string
	PythonPort          int
	RollsitePort       int
	MinimizeFastTest   int
	MinimizeNormalTest int
	IsValid            int
	ClickType          int
	FinishTime         time.Time
	CreateTime         time.Time
	UpdateTime         time.Time
}

func AddDeploySite(deploySite *DeploySite) error {
	if err := db.Create(&deploySite).Error; err != nil {
		return err
	}
	return nil
}

func GetDeploySite(info *DeploySite) ([]DeploySite, error) {
	var deploySite []DeploySite
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.ProductType > 0 {
		Db = Db.Where("product_type = ?", info.ProductType)
	}
	if info.IsValid > 0 {
		Db = Db.Where("is_valid = ?", info.IsValid)
	}
	if info.DeployStatus > 0 {
		Db = Db.Where("deploy_status >= ?", info.DeployStatus)
	}
	if info.ToyTestOnly > 0 {
		Db = Db.Where("toy_test_only = ?", info.ToyTestOnly)
	}
	if info.ToyTestOnlyRead > 0 {
		Db = Db.Where("toy_test_only_read = ?", info.ToyTestOnlyRead)
	}
	err := Db.Find(&deploySite).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return deploySite, nil
}

func UpdateDeploySite(info map[string]interface{}, condition DeploySite) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if condition.ProductType > 0 {
		Db = Db.Where("product_type = ?", condition.ProductType)
	}
	if condition.IsValid > 0 {
		Db = Db.Where("is_valid = ?", condition.IsValid)
	}
	if condition.DeployStatus > 0 {
		Db = Db.Where("deploy_status = ?", condition.DeployStatus)
	}
	if err := Db.Model(&DeploySite{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}
