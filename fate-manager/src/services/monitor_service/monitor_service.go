package monitor_service

import (
	"fate.manager/comm/enum"
	"fate.manager/entity"
	"fate.manager/models"
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
			Total:    monitorBySite.Total,
			Complete: monitorBySite.Complete,
			Failed:   monitorBySite.Failed,
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
				TotalJobs:       v.Total,
				CompleteJobs:    v.Complete,
				CompletePercent: float32(v.Complete / v.Total),
				FailedJobs:      v.Failed,
				FailedPercent:   float32(v.Failed / v.Total),
			},
		}
		data = append(data, siteModelingItem)
	}
	monitorTotalResp := entity.MonitorTotalResp{
		ActiveData: monitorBase.ActiveData,
		JobBase: entity.JobBase{
			TotalJobs:       monitorBase.Total,
			CompleteJobs:    monitorBase.Complete,
			CompletePercent: float32(monitorBase.Complete / monitorBase.Total),
			FailedJobs:      monitorBase.Failed,
			FailedPercent:   float32(monitorBase.Failed / monitorBase.Total),
		},
		SiteModeling: data,
	}
	return &monitorTotalResp, nil
}

func GetInstituionByPartyId(partyId int)(string,error){
	return "",nil
}

func GetInstitutionBaseStatics() (*entity.InstitutionBaseStaticsResp, error) {
	monitorBase, err := models.GetTotalMonitorByHis()
	if err != nil {
		return nil, err
	}
	monitorByHisList, err := models.GetSiteMonitorByHis()
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
			Total:    monitorByHis.Total,
			Complete: monitorByHis.Complete,
			Failed:   monitorByHis.Failed,
		}
		if len(siteInfoList) == 0 {
			intitution,err := GetInstituionByPartyId(monitorByHis.GuestPartyId)
			if err != nil || len(intitution) ==0{
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
				intitution,err := GetInstituionByPartyId(monitorByHis.HostPartyId)
				if err != nil{
					continue
				}
				_, ok := monitorBaseMap[intitution]
				if ok {
					itemBase = monitorBaseMap[intitution]
					monitorBase.Total += monitorByHis.Total
				} else {
					monitorBaseMap[intitution] = itemBase
				}
			}
		}
	}
	var data []entity.InstitutionModelingItem
	for k, v := range monitorBaseMap {
		institutionModelingItem := entity.InstitutionModelingItem{
			Institution: k,
			JobBase:     entity.JobBase{
				TotalJobs:v.Total,
				CompleteJobs:v.Complete,
				CompletePercent: float32(v.Complete/v.Total),
				FailedJobs:v.Failed,
				FailedPercent:float32(v.Failed/v.Total),
			},
		}
		data = append(data, institutionModelingItem)
	}
	monitorTotalResp := entity.InstitutionBaseStaticsResp{
		JobBase:             entity.JobBase{
			TotalJobs: monitorBase.Total,
			CompleteJobs:monitorBase.Complete,
			CompletePercent:float32(monitorBase.Complete/monitorBase.Total),
			FailedJobs:monitorBase.Failed,
			FailedPercent:float32(monitorBase.Failed/monitorBase.Total),
		},
		InstitutionModeling: data,
	}
	return &monitorTotalResp, nil
}

type SiteSiteMonitor struct {
	entity.SitePair
	models.MonitorBase
}
func GetSiteBaseStatistics() ([]entity.InstitutionSiteModelingItem, error) {
	monitorByHisList, err := models.GetSiteMonitorByHis()
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
					PartyId:monitorByHis.HostPartyId,
					SiteName:monitorByHis.HostSiteName,
				},
				MonitorBase: models.MonitorBase{
					Total:    monitorByHis.Total,
					Complete: monitorByHis.Complete,
					Failed:   monitorByHis.Failed,
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
				}else {
					var SiteSiteMonitorList []SiteSiteMonitor
					SiteSiteMonitorList = append(SiteSiteMonitorList,siteSiteMonitor)
					siteSiteMonitorMap[sitePair] = SiteSiteMonitorList
				}
			}else {
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
					}else {
						var SiteSiteMonitorList []SiteSiteMonitor
						SiteSiteMonitorList = append(SiteSiteMonitorList,siteSiteMonitor)
						siteSiteMonitorMap[sitePair] = SiteSiteMonitorList
					}
				}
			}
		}
	}

	var InstitutionSiteModeling []entity.InstitutionSiteModelingItem
	for k,v := range siteSiteMonitorMap {

		sitePair :=k
		siteSiteMonitorList := v

		var institutionMap = make(map[string][]entity.MixSiteModeling)
		for j := 0; j < len(siteSiteMonitorList) ; j++  {
			siteSiteMonitor := siteSiteMonitorList[j]
			institution,err := GetInstituionByPartyId(siteSiteMonitor.PartyId)
			if err != nil || len(institution) ==0{
				continue
			}
			mixSiteModeling := entity.MixSiteModeling{
				InstitutionSiteName: institution,
				JobBase:             entity.JobBase{
					TotalJobs:siteSiteMonitor.Total,
					CompleteJobs:siteSiteMonitor.Complete,
					CompletePercent:float32(siteSiteMonitor.Complete/siteSiteMonitor.Total),
					FailedJobs:siteSiteMonitor.Failed,
					FailedPercent:float32(siteSiteMonitor.Failed/siteSiteMonitor.Total),
				},
			}
			_,ok := institutionMap[institution]
			if ok {
				institutionMap[institution] = append(institutionMap[institution],mixSiteModeling)
			}else{
				var mixSiteModelingList []entity.MixSiteModeling
				mixSiteModelingList = append(mixSiteModelingList,mixSiteModeling)
				institutionMap[institution] =mixSiteModelingList
			}
		}
		for kk,vv := range institutionMap {
			institutionSiteModelingItem := entity.InstitutionSiteModelingItem{
				SiteName:        sitePair.SiteName,
				Institution:     kk,
				MixSiteModeling: vv,
			}
			InstitutionSiteModeling = append(InstitutionSiteModeling,institutionSiteModelingItem)
		}
	}
	return InstitutionSiteModeling,nil
}
