package monitor_service

import (
	"fate.manager/entity"
	"fate.manager/models"
	"fmt"
	"strconv"
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
			//Status:  int(enum.SITE_STATUS_JOINED),
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
				PartyId:     monitorBySite.GuestPartyId,
				SiteName:    monitorBySite.GuestSiteName,
				Institution: monitorBySite.GuestInstitution,
			}
			_, ok := monitorBaseMap[sitePair]
			if ok {
				itemBaseTmp := monitorBaseMap[sitePair]
				itemBaseTmp.Total += itemBase.Total
				itemBaseTmp.Success += itemBase.Success
				itemBaseTmp.Running += itemBase.Running
				itemBaseTmp.Failed += itemBase.Failed
				itemBaseTmp.Timeout += itemBase.Timeout
				monitorBaseMap[sitePair] = itemBaseTmp
			} else {
				monitorBaseMap[sitePair] = itemBase
			}
			continue
		} else {
			sitePair := entity.SitePair{
				PartyId:     monitorBySite.HostPartyId,
				SiteName:    monitorBySite.HostSiteName,
				Institution: monitorBySite.HostInstitution,
			}
			siteInfo.PartyId = monitorBySite.HostPartyId
			siteInfoList, err = models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			if len(siteInfoList) > 0 {
				_, ok := monitorBaseMap[sitePair]
				if ok {
					itemBaseTmp := monitorBaseMap[sitePair]
					itemBaseTmp.Total += itemBase.Total
					itemBaseTmp.Success += itemBase.Success
					itemBaseTmp.Running += itemBase.Running
					itemBaseTmp.Failed += itemBase.Failed
					itemBaseTmp.Timeout += itemBase.Timeout
					monitorBaseMap[sitePair] = itemBaseTmp
				} else {
					monitorBaseMap[sitePair] = itemBase
				}
			}
		}
	}
	var data []entity.SiteModelingItem
	for k, v := range monitorBaseMap {
		SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Success)/float64(v.Total)), 64)
		RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Running)/float64(v.Total)), 64)
		TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Timeout)/float64(v.Total)), 64)
		FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((v.Failed+v.Timeout))/float64(v.Total)), 64)
		siteModelingItem := entity.SiteModelingItem{
			PartyId:  k.PartyId,
			SiteName: k.SiteName,
			JobBase: entity.JobBase{
				TotalJobs:      v.Total,
				SuccessJobs:    v.Success,
				SuccessPercent: SuccessPercent,
				RunningJobs:    v.Running,
				RunningPercent: RunningPercent,
				TimeoutJobs:    v.Timeout,
				TimeoutPercent: TimeoutPercent,
				FailedJobs:     v.Failed + v.Timeout,
				FailedPercent:  FailedPercent,
			},
		}
		data = append(data, siteModelingItem)
	}
	if len(monitorBySiteList) > 0 {
		SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Success)/float64(monitorBase.Total)), 64)
		RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Running)/float64(monitorBase.Total)), 64)
		TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Timeout)/float64(monitorBase.Total)), 64)
		FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((monitorBase.Failed+monitorBase.Timeout))/float64(monitorBase.Total)), 64)
		monitorTotalResp := entity.MonitorTotalResp{
			ActiveData: monitorBase.ActiveData,
			JobBase: entity.JobBase{
				TotalJobs:      monitorBase.Total,
				SuccessJobs:    monitorBase.Success,
				SuccessPercent: SuccessPercent,
				RunningJobs:    monitorBase.Running,
				RunningPercent: RunningPercent,
				TimeoutJobs:    monitorBase.Timeout,
				TimeoutPercent: TimeoutPercent,
				FailedJobs:     monitorBase.Failed + monitorBase.Timeout,
				FailedPercent:  FailedPercent,
			},
			SiteModeling: data,
		}
		return &monitorTotalResp, nil
	}
	return nil, nil
}

func GetInstitutionBaseStatics(monitorReq entity.MonitorReq) (*entity.InstitutionBaseStaticsResp, error) {
	monitorBase, err := models.GetTotalMonitorByRegion(monitorReq)
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
			//Status:  int(enum.SITE_STATUS_JOINED),
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
			_, ok := monitorBaseMap[monitorByHis.GuestInstitution]
			if ok {
				itemBaseTmp := monitorBaseMap[monitorByHis.GuestInstitution]
				itemBaseTmp.Total += itemBase.Total
				itemBaseTmp.Success += itemBase.Success
				itemBaseTmp.Running += itemBase.Running
				itemBaseTmp.Failed += itemBase.Failed
				itemBaseTmp.Timeout += itemBase.Timeout
				monitorBaseMap[monitorByHis.GuestInstitution] = itemBaseTmp
			} else {
				monitorBaseMap[monitorByHis.GuestInstitution] = itemBase
			}
			continue
		} else {
			siteInfo.PartyId = monitorByHis.HostPartyId
			siteInfoList, err = models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			if len(siteInfoList) == 0 {
				_, ok := monitorBaseMap[monitorByHis.HostInstitution]
				if ok {
					itemBaseTmp := monitorBaseMap[monitorByHis.HostInstitution]
					itemBaseTmp.Total += itemBase.Total
					itemBaseTmp.Success += itemBase.Success
					itemBaseTmp.Running += itemBase.Running
					itemBaseTmp.Failed += itemBase.Failed
					itemBaseTmp.Timeout += itemBase.Timeout
					monitorBaseMap[monitorByHis.HostInstitution] = itemBaseTmp
				} else {
					monitorBaseMap[monitorByHis.HostInstitution] = itemBase
				}
			}
		}
	}
	var data []entity.InstitutionModelingItem
	for k, v := range monitorBaseMap {
		SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Success)/float64(v.Total)), 64)
		RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Running)/float64(v.Total)), 64)
		TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Timeout)/float64(v.Total)), 64)
		FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((v.Failed+v.Timeout))/float64(v.Total)), 64)
		institutionModelingItem := entity.InstitutionModelingItem{
			Institution: k,
			JobBase: entity.JobBase{
				TotalJobs:      v.Total,
				SuccessJobs:    v.Success,
				SuccessPercent: SuccessPercent,
				RunningJobs:    v.Running,
				RunningPercent: RunningPercent,
				TimeoutJobs:    v.Timeout,
				TimeoutPercent: TimeoutPercent,
				FailedJobs:     v.Failed + v.Timeout,
				FailedPercent:  FailedPercent,
			},
		}
		data = append(data, institutionModelingItem)
	}
	if len(monitorByHisList) >0 {
		SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Success)/float64(monitorBase.Total)), 64)
		RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Running)/float64(monitorBase.Total)), 64)
		TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(monitorBase.Timeout)/float64(monitorBase.Total)), 64)
		FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((monitorBase.Failed+monitorBase.Timeout))/float64(monitorBase.Total)), 64)
		monitorTotalResp := entity.InstitutionBaseStaticsResp{
			JobBase: entity.JobBase{
				TotalJobs:      monitorBase.Total,
				SuccessJobs:    monitorBase.Success,
				SuccessPercent: SuccessPercent,
				RunningJobs:    monitorBase.Running,
				RunningPercent: RunningPercent,
				TimeoutJobs:    monitorBase.Timeout,
				TimeoutPercent: TimeoutPercent,
				FailedJobs:     monitorBase.Failed + monitorBase.Timeout,
				FailedPercent:  FailedPercent,
			},
			InstitutionModeling: data,

		}
		return &monitorTotalResp, nil
	}
	return nil,nil
}

type SiteSiteMonitor struct {
	entity.SitePair
	models.MonitorBase
}

func GetSiteBaseStatistics(monitorReq entity.MonitorReq) (*entity.InsitutionSiteModeling, error) {
	monitorByHisList, err := models.GetSiteMonitorByHis(monitorReq)
	if err != nil {
		return nil, err
	}
	var siteSiteMonitorMap = make(map[entity.SitePair][]SiteSiteMonitor)
	var InstitutionSiteList []entity.SitePair
	var SiteList []string
	for i := 0; i < len(monitorByHisList); i++ {
		monitorByHis := monitorByHisList[i]
		if monitorByHis.GuestPartyId != monitorByHis.HostPartyId {
			siteInfo := models.SiteInfo{
				PartyId: monitorByHis.GuestPartyId,
				//Status:  int(enum.SITE_STATUS_JOINED),
			}
			siteInfoList, err := models.GetSiteList(&siteInfo)
			if err != nil {
				continue
			}
			siteSiteMonitor := SiteSiteMonitor{
				SitePair: entity.SitePair{
					PartyId:     monitorByHis.HostPartyId,
					SiteName:    monitorByHis.HostSiteName,
					Institution: monitorByHis.HostInstitution,
				},
				MonitorBase: models.MonitorBase{
					Total:   monitorByHis.Total,
					Success: monitorByHis.Success,
					Running: monitorByHis.Running,
					Timeout: monitorByHis.Timeout,
					Failed:  monitorByHis.Failed + monitorByHis.Timeout,
				},
			}
			sitePair := entity.SitePair{
				PartyId:     monitorByHis.GuestPartyId,
				SiteName:    monitorByHis.GuestSiteName,
				Institution: monitorByHis.GuestInstitution,
			}
			if len(siteInfoList) > 0 {
				hitSite := false
				for j := 0; j < len(SiteList); j++ {
					if SiteList[j] == monitorByHis.GuestSiteName {
						hitSite = true
						break
					}
				}
				if !hitSite {
					SiteList = append(SiteList, monitorByHis.GuestSiteName)
				}

				InstitutionSiteList = append(InstitutionSiteList, siteSiteMonitor.SitePair)
				_, ok := siteSiteMonitorMap[sitePair]
				if ok {
					siteSiteMonitorMap[sitePair] = append(siteSiteMonitorMap[sitePair], siteSiteMonitor)
				} else {
					var SiteSiteMonitorList []SiteSiteMonitor
					SiteSiteMonitorList = append(SiteSiteMonitorList, siteSiteMonitor)
					siteSiteMonitorMap[sitePair] = SiteSiteMonitorList
				}
			} else {
				InstitutionSiteList = append(InstitutionSiteList, sitePair)
				siteInfo.PartyId = monitorByHis.HostPartyId
				siteSiteMonitor.PartyId = monitorByHis.HostPartyId
				siteSiteMonitor.SiteName = monitorByHis.HostSiteName
				siteSiteMonitor.Institution = monitorByHis.HostInstitution
				siteInfoList, err = models.GetSiteList(&siteInfo)
				if err != nil {
					continue
				}
				sitePair := entity.SitePair{
					PartyId:     monitorByHis.HostPartyId,
					SiteName:    monitorByHis.HostSiteName,
					Institution: monitorByHis.HostInstitution,
				}
				if len(siteInfoList) > 0 {
					hitSite := false
					for j := 0; j < len(SiteList); j++ {
						if SiteList[j] == monitorByHis.HostSiteName {
							hitSite = true
							break
						}
					}
					if !hitSite {
						SiteList = append(SiteList, monitorByHis.HostSiteName)
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

	var InstitutionSiteMap = make(map[string][]entity.InstitutionSite)
	for i := 0; i < len(InstitutionSiteList); i++ {
		InstitutionsitePair := InstitutionSiteList[i]
		var MixSiteModelinglist []entity.MixSiteModeling
		for k, v := range siteSiteMonitorMap {
			sitePair := k
			siteSiteMonitorList := v
			hitTag := false
			for j := 0; j < len(siteSiteMonitorList); j++ {
				siteSiteMonitor := siteSiteMonitorList[j]
				if siteSiteMonitor.PartyId == InstitutionsitePair.PartyId {
					hitTag = true

					SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteSiteMonitor.Success)/float64(siteSiteMonitor.Total)), 64)
					RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteSiteMonitor.Running)/float64(siteSiteMonitor.Total)), 64)
					TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteSiteMonitor.Timeout)/float64(siteSiteMonitor.Total)), 64)
					FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((siteSiteMonitor.Failed+siteSiteMonitor.Timeout))/float64(siteSiteMonitor.Total)), 64)
					mixSiteModeling := entity.MixSiteModeling{
						SiteName: sitePair.SiteName,
						JobBase: entity.JobBase{
							TotalJobs:      siteSiteMonitor.Total,
							SuccessJobs:    siteSiteMonitor.Success,
							SuccessPercent: SuccessPercent,
							RunningJobs:    siteSiteMonitor.Running,
							RunningPercent: RunningPercent,
							TimeoutJobs:    siteSiteMonitor.Timeout,
							TimeoutPercent: TimeoutPercent,
							FailedJobs:     siteSiteMonitor.Failed + siteSiteMonitor.Timeout,
							FailedPercent:  FailedPercent,
						},
					}
					MixSiteModelinglist = append(MixSiteModelinglist, mixSiteModeling)
					break
				}
			}
			if !hitTag {
				mixSiteModeling := entity.MixSiteModeling{
					SiteName: sitePair.SiteName,
					JobBase: entity.JobBase{
						TotalJobs:      0,
						SuccessJobs:    0,
						SuccessPercent: 0.00,
						RunningJobs:    0,
						RunningPercent: 0.00,
						TimeoutJobs:    0,
						TimeoutPercent: 0.00,
						FailedJobs:     0,
						FailedPercent:  0.00,
					},
				}
				MixSiteModelinglist = append(MixSiteModelinglist, mixSiteModeling)
			}
		}
		institutionSite := entity.InstitutionSite{
			InstitutionSiteName: InstitutionsitePair.SiteName,
			MixSiteModeling:     MixSiteModelinglist,
		}
		_, ok := InstitutionSiteMap[InstitutionsitePair.Institution]
		if ok {
			InstitutionSiteMap[InstitutionsitePair.Institution] = append(InstitutionSiteMap[InstitutionsitePair.Institution], institutionSite)
		} else {
			var InsitutionSiteList []entity.InstitutionSite
			InsitutionSiteList = append(InsitutionSiteList, institutionSite)
			InstitutionSiteMap[InstitutionsitePair.Institution] = InsitutionSiteList
		}
	}

	var list []entity.InstitutionSiteModelingItem
	for k, v := range InstitutionSiteMap {

		institutionSiteModelingItem := entity.InstitutionSiteModelingItem{
			Institution:         k,
			InstitutionSiteList: v,
		}
		list = append(list, institutionSiteModelingItem)

	}
	if len(SiteList) >0 {
		insitutionSiteModeling := entity.InsitutionSiteModeling{
			SiteList:      SiteList,
			OtherSiteList: list,
		}
		return &insitutionSiteModeling, nil
	}
	return nil,nil
}
