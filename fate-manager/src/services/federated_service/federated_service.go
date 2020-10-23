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
package federated_service

import (
	"encoding/json"
	"fate.manager/comm/e"
	"fate.manager/comm/enum"
	"fate.manager/comm/http"
	"fate.manager/comm/logging"
	"fate.manager/comm/setting"
	"fate.manager/comm/util"
	"fate.manager/entity"
	"fate.manager/models"
	"time"
)

func GetHomeSiteList() ([]*entity.FederatedSiteItem, error) {
	HomeSiteList, err := models.GetAll()
	if err != nil {
		return nil, err
	}
	return HomeSiteList, nil
}

func GetFederationInfo() ([]*models.FederatedInfo, error) {
	HomeSiteList, err := models.GetFederationDropDownList()
	if err != nil {
		return nil, err
	}
	return HomeSiteList, nil
}

func GetPartyIdInfo(partyId int, federatedId int) ([]*entity.FederatedSiteItem, error) {
	HomeSiteList, err := models.GetPartyIdInfo(partyId, federatedId)
	if err != nil {
		return nil, err
	}
	return HomeSiteList, nil
}

type RegisterResp struct {
	SiteId      int64 `json:"siteId"`
	FederatedId int   `json:"federatedId"`
}

func RegisterFederated(registerReq entity.RegisterReq) (*RegisterResp, error) {
	var registerResp RegisterResp
	activateReq := entity.ActivateReq{
		RegistrationLink: registerReq.RegistrationLink,
		//RegistrationLink: strings.ReplaceAll(registerReq.RegistrationLink,"\"","\\\""),  //used in postman
	}
	activateReqJson, _ := json.Marshal(activateReq)
	headInfo := util.HeaderInfo{
		AppKey:    registerReq.AppKey,
		AppSecret: registerReq.AppSecret,
		PartyId:   registerReq.PartyId,
		Role:      registerReq.Role,
		Body:      string(activateReqJson),
		Uri:       setting.ActivateUri,
	}
	headerInfoMap := util.GetHeaderInfo(headInfo)
	result, err := http.POST(http.Url(registerReq.FederatedUrl+setting.ActivateUri), activateReq, headerInfoMap)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var activateResp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &activateResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	if activateResp.Code == e.SUCCESS {
		federated, err := models.GetFederatedUrlByFederationId(registerReq.Id, registerReq.FederatedUrl)
		federatedId := federated.Id
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_GET_FEDERATED_FAIL))
			return nil, err
		} else if federated.Id == 0 {
			federatedInfo := models.FederatedInfo{
				FederationId:          registerReq.Id,
				FederatedOrganization: registerReq.FederatedOrganization,
				Institutions:          registerReq.Institutions,
				FederatedUrl:          registerReq.FederatedUrl,
				Status:                1,
				CreateTime:            time.Now(),
				UpdateTime:            time.Now(),
			}
			federatedId, err = models.AddFederated(federatedInfo)
			if err != nil || federatedId == e.UNKONWN {
				logging.Error("add federated failed")
			}
		}

		siteInfo := models.SiteInfo{
			FederatedId:            federatedId,
			FederatedOrganization:  registerReq.FederatedOrganization,
			PartyId:                registerReq.PartyId,
			SiteName:               registerReq.SiteName,
			Institutions:           registerReq.Institutions,
			Role:                   registerReq.Role,
			AppKey:                 registerReq.AppKey,
			AppSecret:              registerReq.AppSecret,
			RegistrationLink:       registerReq.RegistrationLink,
			NetworkAccessEntrances: registerReq.NetworkAccessEntrances,
			NetworkAccessExits:     registerReq.NetworkAccessExits,
			Status:                 int(enum.SITE_STATUS_JOINED),
			EditStatus:             int(enum.EDIT_YES),
			//FateServingVersion:     "1.2.1",
			AcativationTime:        time.Now(),
			UpdateTime:             time.Now(),
		}
		site, err := models.GetSiteInfo(registerReq.PartyId, federatedId)
		if err != nil {
			logging.Error("get siteinfo failed", err)
		}
		if site.PartyId > 0 {
			err = models.UpdateSite(&siteInfo)
			if err != nil {
				logging.Error("update siteinfo failed", err)
			}
		} else {
			siteInfo.CreateTime = time.Now()
			err = models.AddSite(&siteInfo)
			if err != nil {
				logging.Error("add siteinfo failed", err)
			}
		}
		headInfo := util.HeaderInfo{
			AppKey:    registerReq.AppKey,
			AppSecret: registerReq.AppSecret,
			PartyId:   registerReq.PartyId,
			Body:      "",
			Role:      registerReq.Role,
			Uri:       setting.SiteQueryUri,
		}

		headerInfoMap := util.GetHeaderInfo(headInfo)
		result, err = http.POST(http.Url(registerReq.FederatedUrl+setting.SiteQueryUri), nil, headerInfoMap)
		if err != nil || result == nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			return nil, err
		}
		var findOneSiteResp entity.FindOneSiteResp
		err = json.Unmarshal([]byte(result.Body), &findOneSiteResp)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return nil, err
		}

		if findOneSiteResp.Code == int(e.SUCCESS) {
			registerResp.SiteId = findOneSiteResp.Data.Id
			registerResp.FederatedId = federatedId
		}
	}
	return &registerResp, nil
}

func CheckRegisterUrl(registerReq entity.RegisterReq) (int, error) {
	activateReq := entity.ActivateReq{
		RegistrationLink: registerReq.RegistrationLink,
		//RegistrationLink: strings.ReplaceAll(registerReq.RegistrationLink,"\"","\\\""), //used in postman
	}
	activateReqJson, _ := json.Marshal(activateReq)
	headInfo := util.HeaderInfo{
		AppKey:    registerReq.AppKey,
		AppSecret: registerReq.AppSecret,
		PartyId:   registerReq.PartyId,
		Role:      registerReq.Role,
		Body:      string(activateReqJson),
		Uri:       setting.CheckUri,
	}
	headerInfoMap := util.GetHeaderInfo(headInfo)
	result, err := http.POST(http.Url(registerReq.FederatedUrl+setting.CheckUri), activateReq, headerInfoMap)
	if err != nil || result == nil {
		logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var checkUrlResp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &checkUrlResp)
	if err != nil {
		logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if checkUrlResp.Code == e.SUCCESS {
		return e.SUCCESS, nil
	}
	return e.ERROR_CHECK_REGISTER_URL_FAIL, err
}

func GetFederationDropDownList() ([]*entity.FederationListItem, error) {
	federationList, err := models.GetFederationDropDownList()
	if err != nil {
		return nil, err
	}
	var federationItemList []*entity.FederationListItem
	for i := 0; i < len(federationList); i++ {
		siteInfo := models.SiteInfo{
			FederatedId: federationList[i].Id,
			Status:      int(enum.SITE_STATUS_JOINED),
		}
		siteInfoList, err := models.GetSiteList(&siteInfo)
		if err != nil {
			continue
		}
		if len(siteInfoList) > 0 {
			federationListItem := entity.FederationListItem{
				FederatedOrganization: federationList[i].FederatedOrganization,
				FederatedId:           federationList[i].Id,
			}
			federationItemList = append(federationItemList, &federationListItem)
		}
	}
	return federationItemList, nil
}
