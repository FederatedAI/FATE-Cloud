package enum

import "fate.manager/entity"

type SiteRunStatusType int32

const (
	SITE_RUN_STATUS_UNKNOWN SiteRunStatusType = -1
	SITE_RUN_STATUS_STOPPED SiteRunStatusType = 1
	SITE_RUN_STATUS_RUNNING SiteRunStatusType = 2
)

func GetSiteRunStatusString(p SiteRunStatusType) string {
	switch p {
	case SITE_RUN_STATUS_STOPPED:
		return "stopped"
	case SITE_RUN_STATUS_RUNNING:
		return "running"
	default:
		return "unknown"
	}
}

func GetSiteRunStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 3; i++ {
		idPair := entity.IdPair{i, GetSiteRunStatusString(SiteRunStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
