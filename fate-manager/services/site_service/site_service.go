package site_service

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
	"fate.manager/services/changelog_service"
	"fate.manager/services/federated_service"
	"fate.manager/services/job_service"
	"fate.manager/services/user_service"
	"fmt"
	"strings"
	"time"
)

func GetSiteDetail(siteDetailReq entity.SiteDetailReq) (*entity.SiteDetailResp, error) {
	var siteInfo *models.SiteInfo
	siteInfo, err := models.GetSiteInfo(siteDetailReq.PartyId, siteDetailReq.FederatedId)
	if err != nil || siteInfo.Id == 0 {
		return nil, err
	}
	siteDetail := entity.SiteDetailResp{
		Id: siteInfo.SiteId,
		FederatedSite: entity.FederatedSite{
			PartyId:     siteInfo.PartyId,
			FederatedId: siteInfo.FederatedId,
		},
		FederatedOrganization:  siteInfo.FederatedOrganization,
		SiteName:               siteInfo.SiteName,
		Institutions:           siteInfo.Institutions,
		Role:                   entity.IdPair{siteInfo.Role, enum.GetRoleString((enum.RoleType(siteInfo.Role)))},
		AppKey:                 siteInfo.AppKey,
		AppSecret:              siteInfo.AppSecret,
		RegistrationLink:       siteInfo.RegistrationLink,
		NetworkAccessEntrances: siteInfo.NetworkAccessEntrances,
		NetworkAccessExits:     siteInfo.NetworkAccessExits,
		FateVersion:            siteInfo.FateVersion,
		FateServingVersion:     siteInfo.FateServingVersion,
		ComponentVersion:       siteInfo.ComponentVersion,
		Status:                 entity.IdPair{siteInfo.Status, enum.GetSiteString(enum.SiteStatusType(siteInfo.Status))},
		EditStatus:             entity.IdPair{siteInfo.EditStatus, enum.GetEditString(enum.EditType(siteInfo.EditStatus))},
		CreateTime:             siteInfo.CreateTime.UnixNano() / 1e6,
		AcativationTime:        siteInfo.AcativationTime.UnixNano() / 1e6,
	}

	return &siteDetail, nil
}

func GetHomeSiteList() ([]*entity.FederatedItem, error) {
	federatedSiteList, err := federated_service.GetHomeSiteList()
	if err != nil {
		return nil, err
	}
	fedetatedMap := make(map[int]*entity.FederatedItem)
	if len(federatedSiteList) == 1 && len(federatedSiteList[0].SiteName) == 0 {
		var federatedItem entity.FederatedItem
		federatedItem.FederatedOrganization = federatedSiteList[0].FederatedOrganization
		federatedItem.Institutions = federatedSiteList[0].Institutions
		fedetatedMap[federatedSiteList[0].FederatedId] = &federatedItem
	} else {
		for i := 0; i < len(federatedSiteList); i++ {
			federatedSiteItem := federatedSiteList[i]

			var federatedItem entity.FederatedItem
			federatedItem.FederatedId = federatedSiteItem.FederatedId
			headInfo := util.HeaderInfo{
				AppKey:    federatedSiteItem.AppKey,
				AppSecret: federatedSiteItem.AppSecret,
				PartyId:   federatedSiteItem.PartyId,
				Body:      "",
				Role:      federatedSiteItem.Role,
				Uri:       setting.FederationUri,
			}
			headerInfoMap := util.GetHeaderInfo(headInfo)
			_, ftag := fedetatedMap[federatedSiteItem.FederatedId]
			if ftag == false {

				result, err := http.GET(http.Url(federatedSiteItem.FederatedUrl+setting.FederationUri), nil, headerInfoMap)
				if err != nil {
					logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
					continue
				}
				var federationResp entity.FederationResp
				err = json.Unmarshal([]byte(result.Body), &federationResp)
				if err != nil {
					logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					continue
				}
				if federationResp.Code == int(e.SUCCESS) {
					accountInfo, err := user_service.GetAdminInfo()
					if err != nil || accountInfo == nil {
						continue
					}
					federatedItem.Size = federationResp.Data.Total
					federatedItem.CreateTime = federationResp.Data.Federation.CreateTime
					federatedItem.FederatedOrganization = federationResp.Data.Federation.Name
					federatedItem.Institutions = federationResp.Data.Federation.Institution
					federatedItem.FateManagerInstitutions = federatedSiteItem.FateManagerInstitutions
				} else {
					logging.Debug(federationResp.Message)
					continue
				}
			}
			var siteItem entity.SiteItem
			siteItem.Id = federatedSiteItem.SiteId
			siteItem.Role = entity.IdPair{federatedSiteItem.Role, enum.GetRoleString(enum.RoleType(federatedSiteItem.Role))}
			siteItem.Status = entity.IdPair{federatedSiteItem.Status, enum.GetSiteString(enum.SiteStatusType(federatedSiteItem.Status))}
			siteItem.AcativationTime = federatedSiteItem.AcativationTime.UnixNano() / 1e6
			siteItem.PartyId = federatedSiteItem.PartyId
			siteItem.SiteName = federatedSiteItem.SiteName

			if federatedSiteItem.Status == int(enum.SITE_STATUS_JOINED) {
				headInfo.Uri = setting.SiteQueryUri
				headerInfoMap := util.GetHeaderInfo(headInfo)
				result, err := http.POST(http.Url(federatedSiteItem.FederatedUrl+setting.SiteQueryUri), nil, headerInfoMap)
				if err != nil {
					logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
					return nil, err
				}
				var findOneSiteResp entity.FindOneSiteResp
				err = json.Unmarshal([]byte(result.Body), &findOneSiteResp)
				if err != nil {
					logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return nil, err
				}

				if findOneSiteResp.Code == int(e.SUCCESS) {
					siteItem.Status = entity.IdPair{findOneSiteResp.Data.Status, enum.GetSiteString(enum.SiteStatusType(findOneSiteResp.Data.Status))}

					var siteInfo models.SiteInfo
					siteInfo.Status = siteItem.Status.Code
					siteInfo.PartyId = federatedSiteItem.PartyId
					siteInfo.FederatedId = federatedSiteItem.FederatedId
					siteInfo.CreateTime = time.Unix(findOneSiteResp.Data.CreateTime/1000, 0)
					siteInfo.AcativationTime = time.Unix(findOneSiteResp.Data.ActivationTime/1000, 0)
					siteInfo.SiteId = findOneSiteResp.Data.Id
					models.UpdateSite(&siteInfo)

					if len(federatedSiteItem.FateVersion) > 0 || len(federatedSiteItem.FateServingVersion) > 0 {
						logging.Debug("Cloud Manger Fate version is empty!Start to update...")
						go updateVersionToCloudManager(federatedSiteItem)
					}
				}
			}

			_, ok := fedetatedMap[federatedSiteItem.FederatedId]
			if ok {
				fedetatedMap[federatedSiteItem.FederatedId].SiteItemList = append(fedetatedMap[federatedSiteItem.FederatedId].SiteItemList, siteItem)
			} else {
				federatedItem.SiteItemList = append(federatedItem.SiteItemList, siteItem)
				fedetatedMap[federatedSiteItem.FederatedId] = &federatedItem
			}
		}
	}
	var homeSiteList []*entity.FederatedItem
	for _, v := range fedetatedMap {
		homeSiteList = append(homeSiteList, v)
	}
	return homeSiteList, nil
}

func UpdateSite(updateSiteReq *entity.UpdateSiteReq) (int, error) {
	siteInfo, err := models.GetSiteInfo(updateSiteReq.PartyId, updateSiteReq.FederatedId)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_GET_SITE_FAIL))
		return e.ERROR_GET_SITE_FAIL, err
	} else if siteInfo.PartyId == 0 {
		logging.Debug(e.GetMsg(e.ERROR_PARTY_ID_NOT_EXIST))
		return e.ERROR_PARTY_ID_NOT_EXIST, err
	} else if siteInfo.Status != int(enum.SITE_STATUS_JOINED) {
		logging.Debug(e.GetMsg(e.ERROR_SITE_NOT_JOINED))
		return e.ERROR_SITE_NOT_JOINED, err
	} else if siteInfo.EditStatus == int(enum.EDIT_NO) {
		logging.Debug(e.GetMsg(e.ERROR_SITE_NOT_ALLOW_UPDATE))
		return e.ERROR_SITE_NOT_ALLOW_UPDATE, err
	}
	federated, err := models.GetFederatedUrlById(updateSiteReq.FederatedId)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_GET_FEDERATED_FAIL))
		return e.ERROR_GET_FEDERATED_FAIL, err
	} else if federated.Id == 0 {
		logging.Debug(e.GetMsg(e.ERROR_FEDERATED_NOT_EXIST))
		return e.ERROR_FEDERATED_NOT_EXIST, err
	}
	ipAcceptReq := entity.IpAcceptReq{
		PartyId:                updateSiteReq.PartyId,
		NetworkAccessExits:     updateSiteReq.NetworkAccessExits,
		NetworkAccessEntrances: updateSiteReq.NetworkAccessEntrances,
	}
	ipAcceptReqJson, _ := json.Marshal(ipAcceptReq)
	headInfo := util.HeaderInfo{
		AppKey:    updateSiteReq.AppKey,
		AppSecret: updateSiteReq.AppSecret,
		PartyId:   updateSiteReq.PartyId,
		Role:      updateSiteReq.Role,
		Body:      string(ipAcceptReqJson),
		Uri:       setting.IpAcceptUri,
	}
	headerInfoMap := util.GetHeaderInfo(headInfo)
	result, err := http.POST(http.Url(federated.FederatedUrl+setting.IpAcceptUri), ipAcceptReq, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var updateResp entity.CloudManagerResp
	if len(result.Body) > 0 {
		err = json.Unmarshal([]byte(result.Body), &updateResp)
		if err != nil {
			logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return e.ERROR_PARSE_JSON_ERROR, err
		}
		if updateResp.Code == e.SUCCESS {
			caseId := updateResp.Data["caseId"]
			err = changelog_service.AddChangeLog(updateSiteReq, caseId)
			if err != nil {
				logging.Debug("add change log failed")
			}
			siteInfo.EditStatus = int(enum.EDIT_NO)
			err = models.UpdateSite(siteInfo)
			if err != nil {
				logging.Debug("update site info failed")
			}
		}
	}
	return e.SUCCESS, nil
}

func HeartTask() {
	federatedSiteList, err := federated_service.GetHomeSiteList()
	if err != nil {
		return
	}
	for i := 0; i < len(federatedSiteList); i++ {
		federatedSiteItem := federatedSiteList[i]
		if federatedSiteItem.Status == int(enum.SITE_STATUS_JOINED) {
			headInfo := util.HeaderInfo{
				AppKey:    federatedSiteItem.AppKey,
				AppSecret: federatedSiteItem.AppSecret,
				PartyId:   federatedSiteItem.PartyId,
				Body:      "",
				Role:      federatedSiteItem.Role,
				Uri:       setting.HeartUri,
			}
			headerInfoMap := util.GetHeaderInfo(headInfo)
			result, err := http.POST(http.Url(federatedSiteItem.FederatedUrl+setting.HeartUri), nil, headerInfoMap)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
			}
			var updateResp entity.CloudManagerResp
			if result != nil {
				err = json.Unmarshal([]byte(result.Body), &updateResp)
				if err != nil {
					logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
					return
				}
				if updateResp.Code == e.SUCCESS {
					msg, _ := fmt.Scanf("federatedId:%d,partyId:%d,status:joined", federatedSiteItem.FederatedId, federatedSiteItem.PartyId)
					logging.Debug(msg)
				}
			}
		}
	}
}

func updateVersionToCloudManager(item *entity.FederatedSiteItem) {
	updateVersionReq := entity.UpdateVersionReq{
		FateServingVersion: item.FateServingVersion,
		FateVersion:        item.FateVersion,
		ComponentVersion:   item.ComponentVersion,
	}
	updateVersionReqJson, _ := json.Marshal(updateVersionReq)
	headInfo := util.HeaderInfo{
		AppKey:    item.AppKey,
		AppSecret: item.AppSecret,
		PartyId:   item.PartyId,
		Body:      string(updateVersionReqJson),
		Role:      item.Role,
		Uri:       setting.UpdateVersionUri,
	}
	headerInfoMap := util.GetHeaderInfo(headInfo)
	result, err := http.POST(http.Url(item.FederatedUrl+setting.UpdateVersionUri), updateVersionReq, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
	}
	var updateResp entity.CloudManagerResp
	if len(result.Body) > 0 {
		err = json.Unmarshal([]byte(result.Body), &updateResp)
		if err != nil {
			logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return
		}
		if updateResp.Code == e.SUCCESS {
			msg := fmt.Sprintf("federatedId:%d,partyId:%d,fateVersion:%s,fateServingVersion:%s", item.FederatedId, item.PartyId, item.FateVersion, item.FateServingVersion)
			logging.Debug(msg)
		}
	}
}

type HostBody struct {
	PartyId int `json:"partyId"`
}
type HttpHeader struct {
	AppKey    string `json:"APP_KEY"`
	TimeStamp string `json:"TIMESTAMP"`
	Signature string `json:"SIGNATURE"`
	PartyId   string `json:"PARTY_ID"`
	Nonce     string `json:"NONCE"`
	Role      string `json:"ROLE"`
}
type CheckSiteBody struct {
	HttpBody   HostBody   `json:"HTTP_BODY"`
	HttpUri    string     `json:"HTTP_URI"`
	HttpHeader HttpHeader `json:"HTTP_HEADER"`
}

func CheckSite(checkSiteReq entity.CheckSiteReq) (int, error) {
	federatedInfo, err := federated_service.GetPartyIdInfo(checkSiteReq.HostPartyId, checkSiteReq.FederatedId)
	if err != nil {
		return e.ERROR_GET_SITE_FAIL, err
	}
	hostBody := HostBody{PartyId: checkSiteReq.GuestPartyId}
	hostBodyJson, _ := json.Marshal(hostBody)
	headInfo := util.HeaderInfo{
		AppKey:    checkSiteReq.AppKey,
		AppSecret: checkSiteReq.AppSecret,
		PartyId:   checkSiteReq.GuestPartyId,
		Body:      string(hostBodyJson),
		Role:      enum.GetRoleValue(checkSiteReq.Role),
		Uri:       setting.CheckAuthorityUri,
	}
	headerInfoMap := util.GetHeaderInfo(headInfo)
	httpHeader := HttpHeader{
		AppKey:    headerInfoMap["APP_KEY"].(string),
		TimeStamp: headerInfoMap["TIMESTAMP"].(string),
		Signature: headerInfoMap["SIGNATURE"].(string),
		PartyId:   headerInfoMap["PARTY_ID"].(string),
		Nonce:     headerInfoMap["NONCE"].(string),
		Role:      headerInfoMap["ROLE"].(string),
	}

	checkSiteBody := CheckSiteBody{
		HttpBody:   hostBody,
		HttpUri:    setting.CheckAuthorityUri,
		HttpHeader: httpHeader,
	}
	checkSiteBodyJson, _ := json.Marshal(checkSiteBody)
	headInfoHost := util.HeaderInfo{
		AppKey:    federatedInfo[0].AppKey,
		AppSecret: federatedInfo[0].AppSecret,
		Role:      federatedInfo[0].Role,
		PartyId:   checkSiteReq.HostPartyId,
		Body:      string(checkSiteBodyJson),
		Uri:       setting.CheckAuthorityUri,
	}
	headerInfoHostMap := util.GetHeaderInfo(headInfoHost)
	result, err := http.POST(http.Url(federatedInfo[0].FederatedUrl+setting.CheckAuthorityUri), checkSiteBody, headerInfoHostMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var checkSiteResp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &checkSiteResp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if checkSiteResp.Code == e.SUCCESS {
		return e.SUCCESS, nil
	}
	return e.ERROR_CHECK_SITE_FAIL, err
}

func GetSecretInfo(siteDetailReq entity.SiteDetailReq) (*entity.SiteSecretResp, error) {
	federatedInfo, err := federated_service.GetPartyIdInfo(siteDetailReq.PartyId, siteDetailReq.FederatedId)
	if err != nil {
		return nil, err
	}
	siteSecretResp := entity.SiteSecretResp{
		AppKey:    federatedInfo[0].AppKey,
		AppSecret: federatedInfo[0].AppSecret,
		Role:      enum.GetRoleString(enum.RoleType(federatedInfo[0].Role)),
	}
	return &siteSecretResp, nil
}

func TelnetIp(telnetIp entity.TelnetReq) (int, error) {
	federatedInfo, err := federated_service.GetPartyIdInfo(telnetIp.PartyId, telnetIp.FederatedId)
	if err != nil {
		return e.ERROR_GET_SITE_FAIL, err
	}
	result, err := http.POST(http.Url(federatedInfo[0].FederatedUrl+setting.CheckWebUri), telnetIp, nil)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var checkWebResp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &checkWebResp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if checkWebResp.Code == e.SUCCESS {
		return e.SUCCESS, nil
	}
	return e.ERROR_CHECK_WEB_FAIL, err
}

func GetSiteDropDownList(federatedId int) ([]*entity.SiteListItem, error) {
	siteInfoList, err := models.GetSiteDropDownList(federatedId)
	if err != nil {
		return nil, err
	}
	var siteDropDownList []*entity.SiteListItem
	for i := 0; i < len(siteInfoList); i++ {
		if siteInfoList[i].Status == int(enum.SITE_STATUS_REMOVED) {
			continue
		}
		deploySite := models.DeploySite{
			FederatedId: federatedId,
			PartyId:     siteInfoList[i].PartyId,
			ProductType: int(enum.PRODUCT_TYPE_FATE),
			IsValid:     int(enum.IS_VALID_YES),
		}
		deploySiteList, err := models.GetDeploySite(&deploySite)
		if err != nil {
			continue
		}
		deployTag := false
		if len(deploySiteList) > 0 {
			if deploySiteList[0].DeployStatus == int(enum.DeployStatus_SUCCESS) {
				deployTag = true
			}
		}

		siteListItem := entity.SiteListItem{
			PartyId:      siteInfoList[i].PartyId,
			SiteName:     siteInfoList[i].SiteName,
			DeployStatus: deployTag,
		}
		siteDropDownList = append(siteDropDownList, &siteListItem)
	}
	return siteDropDownList, nil
}

func ReadChangeMsg(readChangeReq entity.ReadChangeReq) (int, error) {
	var siteInfo models.SiteInfo
	siteInfo.ReadStatus = readChangeReq.ReadStatus
	siteInfo.FederatedId = readChangeReq.FederatedId
	siteInfo.PartyId = readChangeReq.PartyId
	err := models.UpdateSite(&siteInfo)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_READ_CHANGE_MSG_FAIL))
		return e.ERROR_READ_CHANGE_MSG_FAIL, err
	}
	return e.SUCCESS, nil
}

func GetChangeMsg(getChangeReq entity.GetChangeReq) (*entity.GetChangeResp, error) {

	siteInfo, err := models.GetSiteInfo(getChangeReq.PartyId, getChangeReq.FederatedId)
	if err != nil {
		return nil, err
	}

	getChangeResp := entity.GetChangeResp{
		EditStatus: entity.IdPair{Code: siteInfo.EditStatus, Desc: enum.GetEditString(enum.EditType(siteInfo.EditStatus))},
		ReadStatus: entity.IdPair{siteInfo.ReadStatus, enum.GetReadStatusString(enum.ReadStatusType(siteInfo.ReadStatus))},
	}
	return &getChangeResp, nil
}

func GetApplyInstitutions() ([]entity.ApplyResult, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil {
		return nil, err
	}

	institutionsReq := entity.InstitutionsReq{
		Institutions: accountInfo.Institutions,
		PageNum:      1,
		PageSize:     10,
	}
	institutionsReqJson, _ := json.Marshal(institutionsReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          string(institutionsReqJson),
		Uri:           setting.AuthorityInstitutions,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return nil, err
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.AuthorityInstitutions), institutionsReq, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var resp entity.CloudInstitutionResp
	err = json.Unmarshal([]byte(result.Body), &resp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	var data []entity.ApplyResult
	if resp.Code == e.SUCCESS {
		applySiteInfo, err := models.GetApplySiteOne()

		var applist []entity.IdPair
		if len(applySiteInfo.Institutions) > 0 {
			err = json.Unmarshal([]byte(applySiteInfo.Institutions), &applist)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return nil, err
			}
		}
		for i := 0; i < len(resp.Data.List); i++ {
			item := entity.ApplyResult{
				Status: entity.IdPair{
					Code: resp.Data.List[i].Status,
					Desc: enum.GetIsValidString(enum.IsValidType(resp.Data.List[i].Status)),
				},
				Institutions: resp.Data.List[i].Institutions,
			}
			if resp.Data.List[i].Status == int(enum.AuditStatus_REJECTED) {
				item.Status.Code = int(enum.IS_VALID_YES)
				item.Status.Desc = enum.GetIsValidString(enum.IS_VALID_YES)
			} else if resp.Data.List[i].Status == int(enum.AuditStatus_AGREED) {
				item.Status.Desc = enum.GetIsValidString(enum.IS_VALID_NO)
			}
			//hitTag := false
			//if len(applist) > 0 {
			//	for j := 0; j < len(applist); j++ {
			//		if applist[j].Desc == resp.Data.List[i].Institutions && resp.Data.List[i].Status == int(enum.AuditStatus_AGREED) {
			//			hitTag = true
			//			break
			//		}
			//	}
			//}
			//if hitTag {
			//	item.Status.Code = int(enum.AuditStatus_AGREED)
			//}
			data = append(data, item)
		}
	}
	return data, nil
}

func ApplySites(applySiteReq entity.ApplySiteReq) (int, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil {
		return e.ERROR_APPLY_SITES_FAIL, err
	}

	applySiteReq.Institutions = accountInfo.Institutions
	applySiteReqJson, _ := json.Marshal(applySiteReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          string(applySiteReqJson),
		Uri:           setting.AuthorityApply,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return e.ERROR_SELECT_DB_FAIL, err
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.AuthorityApply), applySiteReq, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return e.ERROR_HTTP_FAIL, err
	}
	var resp entity.CloudManagerResp
	err = json.Unmarshal([]byte(result.Body), &resp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return e.ERROR_PARSE_JSON_ERROR, err
	}
	if resp.Code == e.SUCCESS {

		var auditResult []entity.IdPair
		for i := 0; i < len(applySiteReq.ApplyList); i++ {
			audit := entity.IdPair{
				Code: int(enum.AuditStatus_AUDITING),
				Desc: applySiteReq.ApplyList[i],
			}
			auditResult = append(auditResult, audit)
		}

		applySiteInfo, _ := models.GetApplySiteOne()
		if len(applySiteInfo.Institutions) > 0 {
			var item []entity.IdPair
			err = json.Unmarshal([]byte(applySiteInfo.Institutions), &item)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return e.ERROR_PARSE_JSON_ERROR, err
			}
			for j := 0; j < len(item); j++ {
				if item[j].Code == int(enum.AuditStatus_AGREED) {
					auditResult = append(auditResult, item[j])
				}
			}
		}
		auditResultJson, _ := json.Marshal(auditResult)
		applySiteInfo = &models.ApplySiteInfo{
			UserId:       applySiteInfo.UserId,
			UserName:     applySiteInfo.UserName,
			Institutions: string(auditResultJson),
			ReadStatus:   int(enum.APPLY_READ_STATUS_NOT_READ),
			Status:       int(enum.IS_VALID_ING),
			CreateTime:   time.Now(),
			UpdateTime:   time.Now(),
		}

		models.AddApplySiteInfo(applySiteInfo)
		return e.SUCCESS, nil
	}
	return e.ERROR_APPLY_SITES_FAIL, nil
}

func QueryApplySites() ([]entity.IdPair, error) {
	applySiteInfo := models.ApplySiteInfo{
		ReadStatus: int(enum.APPLY_READ_STATUS_NOT_READ),
		Status:     int(enum.IS_VALID_YES),
	}
	applySiteInfoList, err := models.GetApplySiteInfo(applySiteInfo)
	if err != nil {
		return nil, err
	}
	if len(applySiteInfoList) > 0 {
		if applySiteInfoList[0].ReadStatus != int(enum.APPLY_READ_STATUS_READ) {
			var auditResult []entity.IdPair
			err = json.Unmarshal([]byte(applySiteInfoList[0].Institutions), &auditResult)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return nil, err
			}
			return auditResult, nil
		}
	}
	return nil, nil
}

func ReadApplySites() (int, error) {
	applySiteInfo := models.ApplySiteInfo{
		ReadStatus: int(enum.APPLY_READ_STATUS_NOT_READ),
	}
	var data = make(map[string]interface{})
	data["read_status"] = int(enum.APPLY_READ_STATUS_READ)
	models.UpdateApplySiteInfo(data, applySiteInfo)
	return e.SUCCESS, nil
}

func GetFunction() ([]entity.FunctionResp, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil {
		return nil, err
	}
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          "",
		Uri:           setting.FunctionAllUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return nil, err
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.FunctionAllUri), nil, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var resp entity.CloudFunctionResp
	err = json.Unmarshal([]byte(result.Body), &resp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}

	var functionRespList []entity.FunctionResp
	if len(accountInfo.BlockMsg) > 0 {
		err = json.Unmarshal([]byte(accountInfo.BlockMsg), &functionRespList)
		if err != nil {
			logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return nil, err
		}
	}
	var respList []entity.FunctionResp
	for i := 0; i < len(resp.Data); i++ {
		functionResp := entity.FunctionResp{
			Function:   resp.Data[i],
			ReadStatus: int(enum.APPLY_READ_STATUS_READ),
		}
		for j := 0; j < len(functionRespList); j++ {
			if resp.Data[i].FunctionName == functionRespList[j].FunctionName {
				if resp.Data[i].Status == functionRespList[j].Status {
					functionResp.ReadStatus = functionRespList[j].ReadStatus
				} else if resp.Data[i].Status == int(enum.FuncDebug_ON) {
					functionResp.ReadStatus = int(enum.APPLY_READ_STATUS_READ)
				} else {
					functionResp.ReadStatus = int(enum.APPLY_READ_STATUS_NOT_READ)
				}
				break
			}
		}
		respList = append(respList, functionResp)
	}
	respListJson, _ := json.Marshal(respList)
	var data = make(map[string]interface{})
	data["block_msg"] = string(respListJson)
	account := models.AccountInfo{
		UserId: accountInfo.UserId,
	}
	models.UpdateAccountInfo(data, account)
	return respList, nil
}

func FunctionRead() (int, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil {
		return e.ERROR_READ_CHANGE_MSG_FAIL, err
	}
	var functionRespList []entity.FunctionResp
	if len(accountInfo.BlockMsg) > 0 {
		err = json.Unmarshal([]byte(accountInfo.BlockMsg), &functionRespList)
		if err != nil {
			logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return e.ERROR_READ_CHANGE_MSG_FAIL, err
		}
	}
	var respList []entity.FunctionResp
	for i := 0; i < len(functionRespList); i++ {
		functionResp := entity.FunctionResp{
			Function:   functionRespList[i].Function,
			ReadStatus: int(enum.APPLY_READ_STATUS_READ),
		}
		respList = append(respList, functionResp)
	}
	respListJson, _ := json.Marshal(respList)
	var data = make(map[string]interface{})
	data["block_msg"] = string(respListJson)
	account := models.AccountInfo{
		UserId: accountInfo.UserId,
	}
	models.UpdateAccountInfo(data, account)
	return e.SUCCESS, nil
}

func GetOtherSiteList() ([]entity.FederatedItem, error) {
	var federatedItemList []entity.FederatedItem
	federatedInfo, err := models.GetFederatedInfo()
	if err != nil || len(federatedInfo) == 0 {
		return nil, err
	}
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return nil, err
	}
	approvedReq := entity.InstitutionsReq{
		Institutions: accountInfo.Institutions,
		PageNum:      1,
		PageSize:     15,
	}
	approvedReqJson, _ := json.Marshal(approvedReq)
	headInfo := util.UserHeaderInfo{
		UserAppKey:    accountInfo.AppKey,
		UserAppSecret: accountInfo.AppSecret,
		FateManagerId: accountInfo.CloudUserId,
		Body:          string(approvedReqJson),
		Uri:           setting.ApprovedUri,
	}
	headerInfoMap := util.GetUserHeadInfo(headInfo)
	federationList, err := federated_service.GetFederationInfo()
	if err != nil || len(federationList) == 0 {
		return nil, err
	}
	result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.ApprovedUri), approvedReq, headerInfoMap)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
		return nil, err
	}
	var approveResp entity.ApproveResp
	err = json.Unmarshal([]byte(result.Body), &approveResp)
	if err != nil {
		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
		return nil, err
	}
	if approveResp.Code == e.SUCCESS {
		for i := 0; i < len(approveResp.Data.List); i++ {
			querySiteReq := entity.QuerySiteReq{
				Institutions: approveResp.Data.List[i],
				PageNum:      1,
				PageSize:     10,
			}
			applySiteReqJson, _ := json.Marshal(querySiteReq)
			headInfo := util.UserHeaderInfo{
				UserAppKey:    accountInfo.AppKey,
				UserAppSecret: accountInfo.AppSecret,
				FateManagerId: accountInfo.CloudUserId,
				Body:          string(applySiteReqJson),
				Uri:           setting.OtherSiteUri,
			}
			headerInfoMap := util.GetUserHeadInfo(headInfo)
			federationList, err := federated_service.GetFederationInfo()
			if err != nil || len(federationList) == 0 {
				return nil, err
			}
			result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.OtherSiteUri), querySiteReq, headerInfoMap)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
				return nil, err
			}
			var resp entity.ApplyResp
			err = json.Unmarshal([]byte(result.Body), &resp)
			if err != nil {
				logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
				return nil, err
			}
			if resp.Code == e.SUCCESS {
				var siteItemList []entity.SiteItem
				for k := 0; k < len(resp.Data.List); k++ {
					item := resp.Data.List[k]
					siteItem := entity.SiteItem{
						Id:       item.Id,
						PartyId:  item.PartyId,
						SiteName: item.SiteName,
						Role: entity.IdPair{
							Code: item.Role,
							Desc: enum.GetRoleString(enum.RoleType(item.Role)),
						},
						Status: entity.IdPair{
							Code: item.Status,
							Desc: enum.GetSiteString(enum.SiteStatusType(item.Status)),
						},
						AcativationTime: item.ActivationTime,
					}
					siteItemList = append(siteItemList, siteItem)
				}
				federatedItem := entity.FederatedItem{
					FederatedId:             federatedInfo[0].Id,
					FateManagerInstitutions: approveResp.Data.List[i],
					Size:                    len(siteItemList),
					SiteItemList:            siteItemList,
				}
				federatedItemList = append(federatedItemList, federatedItem)
			}
		}
	}
	//
	//
	//
	//applySiteInfo := models.ApplySiteInfo{Status: int(enum.IS_VALID_YES), ReadStatus: -1}
	//applySiteInfoList, err := models.GetApplySiteInfo(applySiteInfo)
	//if err != nil {
	//	return nil, err
	//}
	//for i := 0; i < len(applySiteInfoList); i++ {
	//	var auditResult []entity.IdPair
	//	err = json.Unmarshal([]byte(applySiteInfoList[i].Institutions), &auditResult)
	//	if err != nil {
	//		logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
	//		continue
	//	}
	//
	//	for j := 0; j < len(auditResult); j++ {
	//
	//		if auditResult[j].Code == int(enum.AuditStatus_AGREED) {
	//			querySiteReq := entity.QuerySiteReq{
	//				Institutions: auditResult[j].Desc,
	//				PageNum:      1,
	//				PageSize:     10,
	//			}
	//			applySiteReqJson, _ := json.Marshal(querySiteReq)
	//			headInfo := util.UserHeaderInfo{
	//				UserAppKey:    accountInfo.AppKey,
	//				UserAppSecret: accountInfo.AppSecret,
	//				FateManagerId: accountInfo.CloudUserId,
	//				Body:          string(applySiteReqJson),
	//				Uri:           setting.OtherSiteUri,
	//			}
	//			headerInfoMap := util.GetUserHeadInfo(headInfo)
	//			federationList, err := federated_service.GetFederationInfo()
	//			if err != nil || len(federationList) == 0 {
	//				return nil, err
	//			}
	//			result, err := http.POST(http.Url(federationList[0].FederatedUrl+setting.OtherSiteUri), querySiteReq, headerInfoMap)
	//			if err != nil {
	//				logging.Debug(e.GetMsg(e.ERROR_HTTP_FAIL))
	//				return nil, err
	//			}
	//			var resp entity.ApplyResp
	//			err = json.Unmarshal([]byte(result.Body), &resp)
	//			if err != nil {
	//				logging.Debug(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
	//				return nil, err
	//			}
	//			if resp.Code == e.SUCCESS {
	//				var siteItemList []entity.SiteItem
	//				for k := 0; k < len(resp.Data.List); k++ {
	//					item := resp.Data.List[k]
	//					siteItem := entity.SiteItem{
	//						Id:       item.Id,
	//						PartyId:  item.PartyId,
	//						SiteName: item.SiteName,
	//						Role: entity.IdPair{
	//							Code: item.Role,
	//							Desc: enum.GetRoleString(enum.RoleType(item.Role)),
	//						},
	//						Status: entity.IdPair{
	//							Code: item.Status,
	//							Desc: enum.GetSiteString(enum.SiteStatusType(item.Status)),
	//						},
	//						AcativationTime: item.ActivationTime,
	//					}
	//					siteItemList = append(siteItemList, siteItem)
	//				}
	//				federatedItem := entity.FederatedItem{
	//					FederatedId:             federatedInfo[0].Id,
	//					FateManagerInstitutions: auditResult[j].Desc,
	//					Size:                    len(siteItemList),
	//					SiteItemList:            siteItemList,
	//				}
	//				federatedItemList = append(federatedItemList, federatedItem)
	//			}
	//		}
	//	}
	//}
	return federatedItemList, nil
}

func GetFateManagerList() (*entity.ApplyFateManager, error) {
	accountInfo, err := user_service.GetAdminInfo()
	if err != nil || accountInfo == nil {
		return nil, err
	}
	job_service.ApplyResultTask(accountInfo)
	job_service.AllowApplyTask(accountInfo)
	allowInstitutions := strings.Split(accountInfo.AllowInstituions, ",")
	applyFateManager := entity.ApplyFateManager{
		Institutions: allowInstitutions,
		Size:         len(allowInstitutions),
	}
	if len(accountInfo.AllowInstituions) == 0 {
		applyFateManager = entity.ApplyFateManager{
			Institutions: nil,
			Size:         0,
		}
	}
	return &applyFateManager, nil
}

func UpdateComponentVersion(updateVersionReq entity.UpdateComponentVersionReq) (int, error) {

	siteInfo := models.SiteInfo{
		FederatedId:      updateVersionReq.FederatedId,
		PartyId:          updateVersionReq.PartyId,
		ComponentVersion: updateVersionReq.ComponentVersion,
	}
	if len(updateVersionReq.ComponentVersion) == 0 {
		return e.INVALID_PARAMS, nil
	}
	err := models.UpdateSite(&siteInfo)
	if err != nil {
		return e.ERROR_UPDATE_COMPONENT_VERSION_FAIL, err
	}
	return e.SUCCESS, nil
}

func GetApplyLog() ([]entity.ApplyLog, error) {
	applySiteInfo := models.ApplySiteInfo{ReadStatus: -1}
	applySiteInfoList, err := models.GetApplySiteInfo(applySiteInfo)
	if err != nil {
		return nil, err
	}
	var applyLogList []entity.ApplyLog
	for i := 0; i < len(applySiteInfoList); i++ {
		applyLog := entity.ApplyLog{
			ApplyTime: applySiteInfoList[i].CreateTime.UnixNano() / 1e6,
			Content:   applySiteInfoList[i].Institutions,
		}
		applyLogList = append(applyLogList, applyLog)
	}
	return applyLogList, nil
}
