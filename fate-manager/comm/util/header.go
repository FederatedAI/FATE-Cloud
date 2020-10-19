package util

import (
	"encoding/json"
	"fate.manager/comm/logging"
	"fate.manager/services/user_service"
	uuid "github.com/satori/go.uuid"
	"strconv"
	"strings"
	"time"
)

type HeaderInfo struct {
	AppKey    string
	AppSecret string
	Role      int
	PartyId   int
	Body      string
	Uri       string
}
type UserHeaderInfo struct {
	UserAppKey    string
	UserAppSecret string
	FateManagerId string
	Body          string
	Uri           string
}

func GetUserHeadInfo(headInfo UserHeaderInfo) map[string]interface{} {
	head := make(map[string]interface{})
	curtime := time.Now().UnixNano() / 1e6
	uid := uuid.NewV4()
	nonce := strings.ReplaceAll(uid.String(), "-", "")

	head["TIMESTAMP"] = strconv.FormatInt(curtime, 10)
	head["FATE_MANAGER_USER_ID"] = headInfo.FateManagerId
	head["NONCE"] = nonce
	head["FATE_MANAGER_APP_KEY"] = headInfo.UserAppKey

	signture := UserSignature{

		UserAppSecret: headInfo.UserAppSecret,
		FateManagerId: headInfo.FateManagerId,
		UserAppKey:    headInfo.UserAppKey,
		Time:          curtime,
		Nonce:         nonce,
		Uri:           headInfo.Uri,
		Body:          headInfo.Body,
	}
	result := Base64EncodeWithStringForUser(signture)
	head["SIGNATURE"] = result
	headJson, _ := json.Marshal(head)
	logging.Debug(string(headJson))
	return head
}

func GetHeaderInfo(headInfo HeaderInfo) map[string]interface{} {
	head := make(map[string]interface{})
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return head
	}
	curtime := time.Now().UnixNano() / 1e6
	uid := uuid.NewV4()
	nonce := strings.ReplaceAll(uid.String(), "-", "")

	head["TIMESTAMP"] = strconv.FormatInt(curtime, 10)
	head["PARTY_ID"] = strconv.Itoa(headInfo.PartyId)
	head["NONCE"] = nonce
	head["ROLE"] = strconv.Itoa(headInfo.Role)
	head["APP_KEY"] = headInfo.AppKey
	head["FATE_MANAGER_USER_ID"] = accountInfo.CloudUserId
	head["FATE_MANAGER_APP_KEY"] = accountInfo.AppKey

	signture := Signature{
		AppKey:        headInfo.AppKey,
		AppSecret:     headInfo.AppSecret,
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		PartyId:       headInfo.PartyId,
		Role:          headInfo.Role,
		Nonce:         nonce,
		Body:          headInfo.Body,
		Uri:           headInfo.Uri,
		Time:          curtime,
	}
	result := Base64EncodeWithString(signture)
	head["SIGNATURE"] = result
	headJson, _ := json.Marshal(head)
	logging.Debug(string(headJson))
	return head
}
