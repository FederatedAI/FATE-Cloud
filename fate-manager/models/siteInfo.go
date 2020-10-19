package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type SiteInfo struct {
	Id                     int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederatedId            int
	FederatedOrganization  string
	PartyId                int
	SiteId                 int64
	SiteName               string
	Institutions           string
	Role                   int
	AppKey                 string
	AppSecret              string
	RegistrationLink       string
	NetworkAccessEntrances string
	NetworkAccessExits     string
	FateVersion            string
	FateServingVersion     string
	ComponentVersion       string
	Status                 int
	EditStatus             int
	ReadStatus             int
	CreateTime             time.Time
	AcativationTime        time.Time
	UpdateTime             time.Time
}

//func GetSiteInfo(partyId int,federatedId int) (*SiteInfo, error) {
func GetSiteInfo(partyId int, federatedId int) (*SiteInfo, error) {
	var siteInfo SiteInfo
	err := db.Where("party_id  = ? ", partyId).First(&siteInfo).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &siteInfo, nil
}

func GetSiteList(info *SiteInfo) ([]*SiteInfo, error) {
	var siteInfoList []*SiteInfo
	Db := db
	if info.PartyId > 0 {
		Db = Db.Where("party_id = ?", info.PartyId)
	}
	if info.Role > 0 {
		Db = Db.Where("role = ?", info.Role)
	}
	if len(info.SiteName) > 0 {
		Db = Db.Where("site_name like ? or party_id like ?", "%"+info.SiteName+"%", "%"+info.SiteName+"%")
	}
	if info.Status > 0 {
		Db = Db.Where("status = ?", info.Status)
	}
	err := Db.Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return siteInfoList, nil
}

func AddSite(siteInfo *SiteInfo) error {
	if err := db.Create(&siteInfo).Error; err != nil {
		return err
	}
	return nil
}

func UpdateSite(info *SiteInfo) error {
	if err := db.Model(&SiteInfo{}).Where("party_id  = ? ", info.PartyId).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func UpdateSiteByCondition(info map[string]interface{}, condition SiteInfo) error {
	Db := db
	if condition.PartyId > 0 {
		Db = Db.Where("party_id = ?", condition.PartyId)
	}
	if err := Db.Model(&SiteInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func GetSiteDropDownList(federatedId int) ([]*SiteInfo, error) {
	var siteInfoList []*SiteInfo
	err := db.Find(&siteInfoList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return siteInfoList, nil
}
