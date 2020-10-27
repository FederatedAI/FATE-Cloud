package monitor_service

import (
	"fate.manager/entity"
	"fate.manager/models"
)

func GetMonitorTotal(monitorReq entity.MonitorReq) (*entity.MonitorTotalResp, error) {
	monitorBase, err := models.GetMonitorTotal(monitorReq)
	if err != nil {
		return nil, err
	}
	monitorBySiteList, err := models.GetMonitorBySite(monitorReq)
	if err != nil {
		return nil, err
	}
	var data []entity.SiteModelingItem
	for i := 0; i < len(monitorBySiteList); i++ {
		monitorBySite := monitorBySiteList[i]
		siteModelingItem := entity.SiteModelingItem{
			PartyId:  monitorBySite.PartyId,
			SiteName: monitorBySite.SiteName,
			JobBase: entity.JobBase{
				TotalJobs:       monitorBySite.Total,
				CompleteJobs:    monitorBySite.Complete,
				CompletePercent: float32(monitorBySite.Complete / monitorBySite.Total),
				FailedJobs:      monitorBySite.Failed,
				FailedPercent:   float32(monitorBySite.Failed / monitorBySite.Total),
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

func GetInstitutionBaseStatics(monitorReq entity.MonitorReq) ([]entity.InstitutionBaseStaticsResp, error) {

}

func GetSiteBaseStatistics(monitorReq entity.MonitorReq) ([]entity.SiteBaseStatisticsResp, error) {

}
