package enum

import "fate.manager/entity"

type SiteStatusType int32

const (
	SITE_STATUS_UNKNOWN SiteStatusType = -1
	SITE_STATUS_NO_JOIN SiteStatusType = 1
	SITE_STATUS_JOINED  SiteStatusType = 2
	SITE_STATUS_REMOVED SiteStatusType = 3
)

func GetSiteString(p SiteStatusType) string {
	switch p {
	case SITE_STATUS_NO_JOIN:
		return "Not Join"
	case SITE_STATUS_JOINED:
		return "Joined"
	case SITE_STATUS_REMOVED:
		return "Deleted"
	default:
		return "unknown"
	}
}

func GetSiteStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetSiteString(SiteStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
