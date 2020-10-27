package monitor_service

import (
	"fate.manager/entity"
	"fate.manager/models"
)

func GetMonitorTotal(monitorReq entity.MonitorReq)(*entity.MonitorTotalResp,error){
	monitorBySiteList,err := models.GetMonitorBySite(monitorReq)
	if err != nil{
		return nil,err
	}
	for i := 0;i<len(monitorBySiteList) ; i++ {
		monitorTotalResp := entity.MonitorTotalResp{
			ActiveData:   0,
			JobBase:      entity.JobBase{
				TotalJobs:,
				CompleteJobs:,
				CompletePercent:,
				FailedJobs:,
				FailedPercent:,
			},
			SiteModeling: nil,
		}
	}
}

func GetInstitutionBaseStatics(monitorReq entity.MonitorReq)([]entity.InstitutionBaseStaticsResp,error){

}

func GetSiteBaseStatistics(monitorReq entity.MonitorReq)([]entity.SiteBaseStatisticsResp,error){

}