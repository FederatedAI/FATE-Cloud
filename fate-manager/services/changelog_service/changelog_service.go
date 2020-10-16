package changelog_service

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

func AddChangeLog(updateSiteReq *entity.UpdateSiteReq, caseId string) error {
	changeLog := models.ChangeLog{
		FederatedId:            updateSiteReq.FederatedId,
		FederatedOrganization:  updateSiteReq.FederatedOrganization,
		PartyId:                updateSiteReq.PartyId,
		NetworkAccessEntrances: updateSiteReq.NetworkAccessEntrances,
		NetworkAccessExits:     updateSiteReq.NetworkAccessExits,
		Status:                 int(enum.LOG_DEAL_NO),
		CaseId:                 caseId,
		CreateTime:             time.Now(),
		UpdateTime:             time.Now(),
	}
	err := models.AddChangeLog(changeLog)
	if err != nil {
		return err
	}
	return nil
}

func GetChangeLogTask() {
	changeLogList := models.GetNoDealList()
	for i := 0; i < len(changeLogList); i++ {
		changelog := changeLogList[i]
		ipQueryItem := entity.IpQueryItem{
			PartyId: changelog.PartyId,
			CaseId:  changelog.CaseId,
		}
		federatedInfo, err := models.GetFederatedUrlById(changelog.FederatedId)
		if err != nil {
			continue
		}
		ipQueryItemJson, _ := json.Marshal(ipQueryItem)
		headInfo := util.HeaderInfo{
			AppKey:    changelog.AppKey,
			AppSecret: changelog.AppSecret,
			PartyId:   changelog.PartyId,
			Role:      changelog.Role,
			Body:      string(ipQueryItemJson),
			Uri:       setting.IpQueryUri,
		}
		headerInfoMap := util.GetHeaderInfo(headInfo)
		result, err := http.POST(http.Url(federatedInfo.FederatedUrl+setting.IpQueryUri), ipQueryItem, headerInfoMap)
		if err != nil || result == nil {
			logging.Error(e.GetMsg(e.ERROR_HTTP_FAIL))
			continue
		}
		var ipQueryResp entity.IpQueryResp
		err = json.Unmarshal([]byte(result.Body), &ipQueryResp)
		if err != nil {
			logging.Error(e.GetMsg(e.ERROR_PARSE_JSON_ERROR))
			return
		}

		if int(ipQueryResp.Data["status"]) > int(enum.LOG_DEAL_NO) {
			changelog.Status = ipQueryResp.Data["status"]
			value := models.ChangeLog{
				FederatedId: changelog.FederatedId,
				PartyId:     changelog.PartyId,
				Status:      changelog.Status,
				CaseId:      changelog.CaseId,
				UpdateTime:  time.Now(),
			}
			err = models.UpdateChangeLog(value)
			if err != nil {
				logging.Error("update change log failed")
			}
			siteInfo, err := models.GetSiteInfo(changelog.PartyId, changelog.FederatedId)
			if err != nil {
				logging.Error(e.GetMsg(e.ERROR_PARTY_ID_NOT_EXIST))
			}
			siteInfo.EditStatus = int(enum.EDIT_YES)
			siteInfo.UpdateTime = time.Now()
			siteInfo.ReadStatus = int(ipQueryResp.Data["status"])
			if int(ipQueryResp.Data["status"]) == int(enum.LOG_DEAL_AGREED) {
				if len(changelog.NetworkAccessEntrances) != 0 {
					siteInfo.NetworkAccessEntrances = changelog.NetworkAccessEntrances
				}
				if len(changelog.NetworkAccessExits) != 0 {
					siteInfo.NetworkAccessExits = changelog.NetworkAccessExits
				}
			}
			err = models.UpdateSite(siteInfo)
			if err != nil {
				logging.Error("update siteinfo failed")
			}
		}
	}
}
