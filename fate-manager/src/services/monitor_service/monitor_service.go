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
	if monitorReq.PageSize == 0 && monitorReq.PageNum == 0 {
		monitorReq.PageSize = 15
		monitorReq.PageNum = 1
	}
	monitorBase, err := models.GetTotalMonitorByRegion(monitorReq)
	if err != nil {
		return nil, err
	}

	MonitorByInstitutionList, err := models.GetReportInstitutionRegion(monitorReq)
	if err != nil {
		return nil, err
	}
	var data []entity.InstitutionModelingItem
	for i := 0; i < len(MonitorByInstitutionList); i++ {
		v := MonitorByInstitutionList[i]
		SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Success)/float64(v.Total)), 64)
		RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Running)/float64(v.Total)), 64)
		TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(v.Timeout)/float64(v.Total)), 64)
		FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((v.Failed+v.Timeout))/float64(v.Total)), 64)
		institutionModelingItem := entity.InstitutionModelingItem{
			Institution: v.Institution,
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
	if len(MonitorByInstitutionList) > 0 {
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
	return nil, nil
}

type SiteMonitor struct {
	SiteName string
	models.MonitorBase
}

func GetSiteBaseStatistics(monitorReq entity.MonitorReq) (*entity.InsitutionSiteModeling, error) {
	if monitorReq.PageSize == 0 && monitorReq.PageNum == 0 {
		monitorReq.PageSize = 15
		monitorReq.PageNum = 1
	}
	InstitutionSiteList, err := models.GetInstitutionSiteList(monitorReq)
	if err != nil {
		return nil, err
	}
	if len(InstitutionSiteList) == 0 {
		return nil, nil
	}

	SiteMonitorByRegionList, err := models.GetReportSiteRegion(monitorReq, InstitutionSiteList)
	if err != nil {
		return nil, err
	}
	var data = make(map[string]map[string][]SiteMonitor)
	var rowList []string
	for i := 0; i < len(SiteMonitorByRegionList); i++ {

		siteMonitorByRegion := SiteMonitorByRegionList[i]
		institution := siteMonitorByRegion.Institution
		institutionSiteName := siteMonitorByRegion.InstitutionSiteName
		siteName := siteMonitorByRegion.SiteName

		hitTag :=false
		for j :=0 ;j< len(rowList);j++{
			if siteName == rowList[j] {
				hitTag =true
				break
			}
		}
		if !hitTag{
			rowList = append(rowList, siteName)
		}
		monitorBase := models.MonitorBase{
			Total:   siteMonitorByRegion.Total,
			Success: siteMonitorByRegion.Success,
			Running: siteMonitorByRegion.Running,
			Timeout: siteMonitorByRegion.Timeout,
			Failed:  siteMonitorByRegion.Failed + siteMonitorByRegion.Timeout,
		}
		siteMonitor := SiteMonitor{
			SiteName:    siteName,
			MonitorBase: monitorBase,
		}
		_, ok := data[institution]
		if ok {
			siteMonitorMap := data[institution]
			_, ok = siteMonitorMap[institutionSiteName]
			if ok {
				siteMonitorMap[institutionSiteName] = append(siteMonitorMap[institutionSiteName], siteMonitor)
			} else {
				var SiteMonitorList []SiteMonitor
				SiteMonitorList = append(SiteMonitorList, siteMonitor)
				var siteMonitorMap = make(map[string][]SiteMonitor)
				siteMonitorMap[institutionSiteName] = SiteMonitorList
			}
		} else {
			var SiteMonitorList []SiteMonitor
			SiteMonitorList = append(SiteMonitorList, siteMonitor)
			var siteMonitorMap = make(map[string][]SiteMonitor)
			siteMonitorMap[institutionSiteName] = SiteMonitorList
			data[institution] = siteMonitorMap
		}
	}
	var OtherSiteList []entity.InstitutionSiteModelingItem
	for k, v := range data {
		institution := k
		var InstitutionSiteList []entity.InstitutionSite
		for k2, v2 := range v {
			institutionSiteName := k2
			siteList := v2
			var MixSiteModelinglist []entity.MixSiteModeling
			for j := 0; j < len(siteList); j++ {
				siteMonitor := siteList[j]
				SuccessPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteMonitor.Success)/float64(siteMonitor.Total)), 64)
				RunningPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteMonitor.Running)/float64(siteMonitor.Total)), 64)
				TimeoutPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64(siteMonitor.Timeout)/float64(siteMonitor.Total)), 64)
				FailedPercent, _ := strconv.ParseFloat(fmt.Sprintf("%.4f", float64((siteMonitor.Failed+siteMonitor.Timeout))/float64(siteMonitor.Total)), 64)
				mixSiteModeling := entity.MixSiteModeling{
					SiteName: siteMonitor.SiteName,
					JobBase: entity.JobBase{
						TotalJobs:      siteMonitor.Total,
						SuccessJobs:    siteMonitor.Success,
						SuccessPercent: SuccessPercent,
						RunningJobs:    siteMonitor.Running,
						RunningPercent: RunningPercent,
						TimeoutJobs:    siteMonitor.Timeout,
						TimeoutPercent: TimeoutPercent,
						FailedJobs:     siteMonitor.Failed + siteMonitor.Timeout,
						FailedPercent:  FailedPercent,
					},
				}
				MixSiteModelinglist = append(MixSiteModelinglist, mixSiteModeling)
			}
			if len(siteList) < len(rowList) {
				for k := 0; k < len(rowList); k++ {
					siteName := rowList[k]
					hitTag := false
					for l := 0; l < len(siteList); l++ {
						if siteName == siteList[l].SiteName {
							hitTag = true
							break
						}
					}
					if !hitTag {
						mixSiteModeling := entity.MixSiteModeling{
							SiteName: siteName,
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
			}
			institutionSite := entity.InstitutionSite{
				InstitutionSiteName: institutionSiteName,
				MixSiteModeling:     MixSiteModelinglist,
			}
			InstitutionSiteList = append(InstitutionSiteList, institutionSite)
		}
		institutionSiteModelingItem := entity.InstitutionSiteModelingItem{
			Institution:         institution,
			InstitutionSiteList: InstitutionSiteList,
		}
		OtherSiteList = append(OtherSiteList, institutionSiteModelingItem)
	}
	if len(rowList) > 0 {
		insitutionSiteModeling := entity.InsitutionSiteModeling{
			SiteList:      rowList,
			OtherSiteList: OtherSiteList,
			Total: len(InstitutionSiteList),
		}
		return &insitutionSiteModeling, nil
	}
	return nil, nil
}
