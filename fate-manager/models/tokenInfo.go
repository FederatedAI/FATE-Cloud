package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type TokenInfo struct {
	Id         int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	UserId     string
	UserName   string
	Token      string
	ExpireTime time.Time
	CreateTime time.Time
	UpdateTime time.Time
}

func GetTokenInfo(tokenInfo TokenInfo) ([]*TokenInfo, error) {
	var result []*TokenInfo
	Db := db
	if len(tokenInfo.Token) > 0 {
		Db = Db.Where("token = ?", tokenInfo.Token)
	}
	if len(tokenInfo.UserId) > 0 {
		Db = Db.Where("user_id = ?", tokenInfo.UserId)
	}
	if len(tokenInfo.UserName) > 0 {
		Db = Db.Where("user_name = ?", tokenInfo.UserName)
	}
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func GetNoExpireTokenInfo(time2 time.Time, tokenInfo TokenInfo) ([]*TokenInfo, error) {
	var result []*TokenInfo
	Db := db
	if len(tokenInfo.Token) > 0 {
		Db = Db.Where("token = ?", tokenInfo.Token)
	}
	if len(tokenInfo.UserId) > 0 {
		Db = Db.Where("user_id = ?", tokenInfo.UserId)
	}
	err := Db.Where("expire_time > ?",time2).Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func CheckTokenInfo(tokenInfo TokenInfo) ([]*TokenInfo, error) {
	var result []*TokenInfo
	Db := db
	if len(tokenInfo.Token) > 0 {
		Db = Db.Where("token = ?", tokenInfo.Token)
	}
	Db = Db.Where("expire_time >= ?", tokenInfo.ExpireTime)
	err := Db.Find(&result).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}
	return result, nil
}

func UpdateTokenInfo(info map[string]interface{}, condition TokenInfo) error {
	Db := db
	if len(condition.UserId) > 0 {
		Db = Db.Where("user_id = ?", condition.UserId)
	}
	if len(condition.UserName) > 0 {
		Db = Db.Where("user_name = ?", condition.UserName)
	}

	if len(condition.Token) > 0 {
		Db = Db.Where("token = ?", condition.Token)
	}
	if err := Db.Model(&TokenInfo{}).Updates(info).Error; err != nil {
		return err
	}
	return nil
}

func AddTokenInfo(tokenInfo *TokenInfo) error {
	if err := db.Create(&tokenInfo).Error; err != nil {
		return err
	}
	return nil
}
