package monitor_service

import (
	"fate.manager/comm/enum"
	"fate.manager/entity"
	"fate.manager/models"
	"fate.manager/services/job_service"
)

func GetMonitorTotal(monitorReq entity.MonitorReq) (*entity.MonitorTotalResp, error) {
	monitorBase, err := models.GetTotalMonitorByRegion(monitorReq)
	if err != nil {
		return nil, err
	}
	monitorBySiteList, err := models.GetSiteMonitorByRegion(monitorReq)
	if err != nil {
		return nil, err
	}
	var monitorBaseMap = make(map[entity.SitePair]models.MonitorBase)
	for i := 0; i < len(monitorBySiteList); i++ {
		monitorBySite := monitorBySiteList[i]
		siteInfo := models.SiteInfo{
			PartyId: monitorBySite.GuestPartyId,
			Status:  int(enum.SITE_STATUS_JOINED),
		}
		siteInfoList, err := models.GetSiteList(&siteInfo)
		if err != nil {
			continue
		}
		itemBase := models.MonitorBase{
			Total:   monitorBySite.Total,
			Success: monitorBySite.Success,
			Failed:  monitorBySite.Failed + monitorBySite.Timeout,
			Running: monitorBySite.Running,
			Timeout: monitorBySite.Timeout,
		}
		if len(siteInfoList) > 0 {
			sitePair := entity.SitePair{
				PartyId:  monitorBySite.GuestPartyId,
				SiteName: monitorBySite.GuestSiteName,
			}
			_, ok := monitorBaseMap[sitePair]
			if ok {
				itemBase = monitorBaseMap[sitePair]
				monitorBase.Total += monitorBySite.Total
			} else {
				monitorBaseMap[sitePair] = itemBase
			}
			continue
		} else {
			sitePair := entity.SitePair{
				PartyId:  monitorBySite.HostPartyId,
				SiteName: monitorBySite.HostSiteName,
			}
			siteInfo.PartyId = monitorBySite.HostPartyId
			siteInfoList, err = models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			if len(siteInfoList) > 0 {
				_, ok := monitorBaseMap[sitePair]
				if ok {
					itemBase = monitorBaseMap[sitePair]
					monitorBase.Total += monitorBySite.Total
				} else {
					monitorBaseMap[sitePair] = itemBase
				}
			}
		}
	}
	var data []entity.SiteModelingItem
	for k, v := range monitorBaseMap {
		siteModelingItem := entity.SiteModelingItem{
			PartyId:  k.PartyId,
			SiteName: k.SiteName,
			JobBase: entity.JobBase{
				TotalJobs:      v.Total,
				SuccessJobs:    v.Success,
				SuccessPercent: float32(v.Success / v.Total),
				RunningJobs:    v.Running,
				RunningPercent: float32(v.Running / v.Total),
				TimeoutJobs:    v.Timeout,
				TimeoutPercent: float32(v.Timeout / v.Total),
				FailedJobs:     v.Failed + v.Timeout,
				FailedPercent:  float32((v.Failed + v.Timeout) / v.Total),
			},
		}
		data = append(data, siteModelingItem)
	}
	monitorTotalResp := entity.MonitorTotalResp{
		ActiveData: monitorBase.ActiveData,
		JobBase: entity.JobBase{
			TotalJobs:      monitorBase.Total,
			SuccessJobs:    monitorBase.Success,
			SuccessPercent: float32(monitorBase.Success / monitorBase.Total),
			RunningJobs:    monitorBase.Running,
			RunningPercent: float32(monitorBase.Running / monitorBase.Total),
			TimeoutJobs:    monitorBase.Timeout,
			TimeoutPercent: float32(monitorBase.Timeout / monitorBase.Total),
			FailedJobs:     monitorBase.Failed + monitorBase.Timeout,
			FailedPercent:  float32((monitorBase.Failed + monitorBase.Timeout) / monitorBase.Total),
		},
		SiteModeling: data,
	}
	return &monitorTotalResp, nil
}

func GetInstituionByPartyId(partyId int) (string, error) {
	institution := ""
	_, ok := job_service.SiteNameMap[partyId]
	if ok {
		institution = job_service.SiteNameMap[partyId].Institution
	}
	return institution, nil
}

func GetInstitutionBaseStatics(monitorReq entity.MonitorReq) (*entity.InstitutionBaseStaticsResp, error) {
	monitorBase, err := models.GetTotalMonitorByHis(monitorReq)
	if err != nil {
		return nil, err
	}
	monitorByHisList, err := models.GetSiteMonitorByHis(monitorReq)
	if err != nil {
		return nil, err
	}
	var monitorBaseMap = make(map[string]models.MonitorBase)
	for i := 0; i < len(monitorByHisList); i++ {
		monitorByHis := monitorByHisList[i]
		siteInfo := models.SiteInfo{
			PartyId: monitorByHis.GuestPartyId,
			Status:  int(enum.SITE_STATUS_JOINED),
		}
		siteInfoList, err := models.GetSiteList(&siteInfo)
		if err != nil {
			continue
		}
		itemBase := models.MonitorBase{
			Total:   monitorByHis.Total,
			Success: monitorByHis.Success,
			Running: monitorByHis.Running,
			Timeout: monitorByHis.Timeout,
			Failed:  monitorByHis.Failed + monitorByHis.Timeout,
		}
		if len(siteInfoList) == 0 {
			intitution, _ := GetInstituionByPartyId(monitorByHis.GuestPartyId)
			if len(intitution) == 0 {
				continue
			}
			_, ok := monitorBaseMap[intitution]
			if ok {
				itemBase = monitorBaseMap[intitution]
				monitorBase.Total += monitorByHis.Total
			} else {
				monitorBaseMap[intitution] = itemBase
			}
			continue
		} else {
			siteInfo.PartyId = monitorByHis.HostPartyId
			siteInfoList, err = models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			if len(siteInfoList) == 0 {
				institution, _ := GetInstituionByPartyId(monitorByHis.HostPartyId)
				if len(institution) == 0 {
					continue
				}
				_, ok := monitorBaseMap[institution]
				if ok {
					itemBase = monitorBaseMap[institution]
					monitorBase.Total += monitorByHis.Total
				} else {
					monitorBaseMap[institution] = itemBase
				}
			}
		}
	}
	var data []entity.InstitutionModelingItem
	for k, v := range monitorBaseMap {
		institutionModelingItem := entity.InstitutionModelingItem{
			Institution: k,
			JobBase: entity.JobBase{
				TotalJobs:      v.Total,
				SuccessJobs:    v.Success,
				SuccessPercent: float32(v.Success / v.Total),
				RunningJobs:    v.Running,
				RunningPercent: float32(v.Running / v.Total),
				TimeoutJobs:    v.Timeout,
				TimeoutPercent: float32(v.Timeout / v.Total),
				FailedJobs:     v.Failed + v.Timeout,
				FailedPercent:  float32((v.Failed + v.Timeout) / v.Total),
			},
		}
		data = append(data, institutionModelingItem)
	}
	monitorTotalResp := entity.InstitutionBaseStaticsResp{
		JobBase: entity.JobBase{
			TotalJobs:      monitorBase.Total,
			SuccessJobs:    monitorBase.Success,
			SuccessPercent: float32(monitorBase.Success / monitorBase.Total),
			RunningJobs:    monitorBase.Running,
			RunningPercent: float32(monitorBase.Running / monitorBase.Total),
			TimeoutJobs:    monitorBase.Timeout,
			TimeoutPercent: float32(monitorBase.Timeout / monitorBase.Total),
			FailedJobs:     monitorBase.Failed + monitorBase.Timeout,
			FailedPercent:  float32((monitorBase.Failed + monitorBase.Timeout) / monitorBase.Total),
		},
		InstitutionModeling: data,
	}
	return &monitorTotalResp, nil
}

type SiteSiteMonitor struct {
	entity.SitePair
	models.MonitorBase
}

func GetSiteBaseStatistics(monitorReq entity.MonitorReq) ([]entity.InstitutionSiteModelingItem, error) {
	monitorByHisList, err := models.GetSiteMonitorByHis(monitorReq)
	if err != nil {
		return nil, err
	}
	var siteSiteMonitorMap = make(map[entity.SitePair][]SiteSiteMonitor)
	for i := 0; i < len(monitorByHisList); i++ {
		monitorByHis := monitorByHisList[i]
		if monitorByHis.GuestPartyId != monitorByHis.HostPartyId {
			siteInfo := models.SiteInfo{
				PartyId: monitorByHis.GuestPartyId,
				Status:  int(enum.SITE_STATUS_JOINED),
			}
			siteInfoList, err := models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			siteSiteMonitor := SiteSiteMonitor{
				SitePair: entity.SitePair{
					PartyId:  monitorByHis.HostPartyId,
					SiteName: monitorByHis.HostSiteName,
				},
				MonitorBase: models.MonitorBase{
					Total:   monitorByHis.Total,
					Success: monitorByHis.Success,
					Running: monitorByHis.Running,
					Timeout: monitorByHis.Timeout,
					Failed:  monitorByHis.Failed + monitorByHis.Timeout,
				},
			}
			if len(siteInfoList) > 0 {
				sitePair := entity.SitePair{
					PartyId:  monitorByHis.GuestPartyId,
					SiteName: monitorByHis.GuestSiteName,
				}
				_, ok := siteSiteMonitorMap[sitePair]
				if ok {
					siteSiteMonitorMap[sitePair] = append(siteSiteMonitorMap[sitePair], siteSiteMonitor)
				} else {
					var SiteSiteMonitorList []SiteSiteMonitor
					SiteSiteMonitorList = append(SiteSiteMonitorList, siteSiteMonitor)
					siteSiteMonitorMap[sitePair] = SiteSiteMonitorList
				}
			} else {
				siteInfo.PartyId = monitorByHis.HostPartyId
				siteSiteMonitor.PartyId = monitorByHis.HostPartyId
				siteSiteMonitor.SiteName = monitorByHis.HostSiteName
				siteInfoList, err = models.GetSiteList(&siteInfo)
				if err != nil {
					continue
				}
				if len(siteInfoList) > 0 {
					sitePair := entity.SitePair{
						PartyId:  monitorByHis.HostPartyId,
						SiteName: monitorByHis.HostSiteName,
					}
					_, ok := siteSiteMonitorMap[sitePair]
					if ok {
						siteSiteMonitorMap[sitePair] = append(siteSiteMonitorMap[sitePair], siteSiteMonitor)
					} else {
						var SiteSiteMonitorList []SiteSiteMonitor
						SiteSiteMonitorList = append(SiteSiteMonitorList, siteSiteMonitor)
						siteSiteMonitorMap[sitePair] = SiteSiteMonitorList
					}
				}
			}
		}
	}

	var InstitutionSiteModeling []entity.InstitutionSiteModelingItem
	for k, v := range siteSiteMonitorMap {

		sitePair := k
		siteSiteMonitorList := v

		var institutionMap = make(map[string][]entity.MixSiteModeling)
		for j := 0; j < len(siteSiteMonitorList); j++ {
			siteSiteMonitor := siteSiteMonitorList[j]
			institution, err := GetInstituionByPartyId(siteSiteMonitor.PartyId)
			if err != nil || len(institution) == 0 {
				continue
			}
			mixSiteModeling := entity.MixSiteModeling{
				InstitutionSiteName: institution,
				JobBase: entity.JobBase{
					TotalJobs:      siteSiteMonitor.Total,
					SuccessJobs:    siteSiteMonitor.Success,
					SuccessPercent: float32(siteSiteMonitor.Success / siteSiteMonitor.Total),
					RunningJobs:    siteSiteMonitor.Running,
					RunningPercent: float32(siteSiteMonitor.Running / siteSiteMonitor.Total),
					TimeoutJobs:    siteSiteMonitor.Timeout,
					TimeoutPercent: float32(siteSiteMonitor.Timeout / siteSiteMonitor.Total),
					FailedJobs:     siteSiteMonitor.Failed + siteSiteMonitor.Timeout,
					FailedPercent:  float32((siteSiteMonitor.Failed + siteSiteMonitor.Timeout) / siteSiteMonitor.Total),
				},
			}
			_, ok := institutionMap[institution]
			if ok {
				institutionMap[institution] = append(institutionMap[institution], mixSiteModeling)
			} else {
				var mixSiteModelingList []entity.MixSiteModeling
				mixSiteModelingList = append(mixSiteModelingList, mixSiteModeling)
				institutionMap[institution] = mixSiteModelingList
			}
		}
		for kk, vv := range institutionMap {
			institutionSiteModelingItem := entity.InstitutionSiteModelingItem{
				SiteName:        sitePair.SiteName,
				Institution:     kk,
				MixSiteModeling: vv,
			}
			InstitutionSiteModeling = append(InstitutionSiteModeling, institutionSiteModelingItem)
		}
	}
	return InstitutionSiteModeling, nil
}
