package models

import (
	"fate.manager/entity"
	"github.com/jinzhu/gorm"
	"time"
)

type FederatedInfo struct {
	Id                    int `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	FederationId          int
	FederatedOrganization string
	Institutions          string
	FederatedUrl          string
	Status                int
	CreateTime            time.Time
	UpdateTime            time.Time
}

func GetFederatedInfo() ([]*FederatedInfo, error) {
	var federatedInfo []*FederatedInfo
	err := db.Where("status = 1").Find(&federatedInfo).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return federatedInfo, nil
}
func GetAll() ([]*entity.FederatedSiteItem, error) {
	var homeSiteListItem []*entity.FederatedSiteItem
	err := db.Table("t_fate_federated_info t1").Select("t1.id as federated_id, t1.federated_organization, t1.institutions,t2.institutions as fate_manager_institutions,t1.federated_url, " +
		"t1.federated_url,t1.create_time,t2.site_id,t2.party_id,t2.site_name,t2.role,t2.status,t2.acativation_time,t2.app_key," +
		"t2.app_secret,t2.fate_version,t2.fate_serving_version,t2.component_version").
		Joins("left join t_fate_site_info t2 on t1.id = t2.federated_id where t1.status=1").
		Scan(&homeSiteListItem).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return homeSiteListItem, nil
}

func GetPartyIdInfo(partyId int, federatedId int) ([]*entity.FederatedSiteItem, error) {
	var homeSiteListItem []*entity.FederatedSiteItem
	err := db.Table("t_fate_federated_info").Select("t_fate_federated_info.id as federated_id, t_fate_federated_info.federated_organization, t_fate_federated_info.institutions,t_fate_federated_info.federated_url, "+
		"t_fate_federated_info.federated_url,t_fate_federated_info.create_time,t_fate_site_info.party_id,t_fate_site_info.site_name,t_fate_site_info.role,t_fate_site_info.status,t_fate_site_info.acativation_time,t_fate_site_info.app_key,"+
		"t_fate_site_info.app_secret,t_fate_site_info.fate_version,t_fate_site_info.fate_serving_version").
		Joins(" join t_fate_site_info  on t_fate_federated_info.id = t_fate_site_info.federated_id where t_fate_federated_info.status=1 and t_fate_site_info.federated_id =? and t_fate_site_info.party_id= ?", federatedId, partyId).
		Find(&homeSiteListItem).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return homeSiteListItem, nil
}

func GetFederatedUrlById(id int) (*FederatedInfo, error) {
	var federatedInfo FederatedInfo
	err := db.Where("id = ? ", id).First(&federatedInfo).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return &federatedInfo, nil
}

func GetFederatedUrlByFederationId(federationId int, federationUrl string) (*FederatedInfo, error) {
	var federatedInfo FederatedInfo
	err := db.Where("federation_id = ? and federated_url = ?", federationId, federationUrl).First(&federatedInfo).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		federatedInfo.Id = 0
		return &federatedInfo, nil
	}

	return &federatedInfo, nil
}

func AddFederated(federatedInfo FederatedInfo) (int, error) {
	if err := db.Create(&federatedInfo).Error; err != nil {
		return -1, err
	}
	result, err := GetFederatedUrlByFederationId(federatedInfo.FederationId, federatedInfo.FederatedUrl)
	if err != nil {
		return -1, err
	}
	return result.Id, nil
}

func GetFederationDropDownList() ([]*FederatedInfo, error) {
	var federationList []*FederatedInfo
	err := db.Where("status = 1 ").Find(&federationList).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return federationList, nil
}
