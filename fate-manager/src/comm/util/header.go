/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

func GetHeadInfoOld(headInfo HeaderInfo) map[string]interface{} {
	head := make(map[string]interface{})
	curtime := time.Now().UnixNano() / 1e6
	uid := uuid.NewV4()
	nonce := strings.ReplaceAll(uid.String(), "-", "")

	head["TIMESTAMP"] = strconv.FormatInt(curtime, 10)
	head["PARTY_ID"] = strconv.Itoa(headInfo.PartyId)
	head["NONCE"] = nonce
	head["ROLE"] = strconv.Itoa(headInfo.Role)
	head["APP_KEY"] = headInfo.AppKey

	signture := Signature{
		AppKey:        headInfo.AppKey,
		AppSecret:     headInfo.AppSecret,
		PartyId:       headInfo.PartyId,
		Role:          headInfo.Role,
		Nonce:         nonce,
		Body:          headInfo.Body,
		Uri:           headInfo.Uri,
		Time:          curtime,
	}
	result := Base64EncodeOld(signture)
	head["SIGNATURE"] = result
	headJson, _ := json.Marshal(head)
	logging.Debug(string(headJson))
	return head
}
