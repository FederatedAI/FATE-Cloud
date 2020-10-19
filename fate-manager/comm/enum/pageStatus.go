package enum

import "fate.manager/entity"

type PageStatus int32

const (
	PAGE_STATUS_UNKNOWN   PageStatus = -1
	PAGE_STATUS_PREPARE   PageStatus = 0
	PAGE_STATUS_PAGE      PageStatus = 1
	PAGE_STATUS_PULLIMAGE PageStatus = 2
	PAGE_STATUS_INSTALL   PageStatus = 3
	PAGE_STATUS_TEST      PageStatus = 4
	PAGE_STATUS_SERVICE   PageStatus = 5
)

func GetPageStatusString(p PageStatus) string {
	switch p {
	case PAGE_STATUS_PREPARE:
		return "Prepare Page"
	case PAGE_STATUS_PAGE:
		return "Page start next"
	case PAGE_STATUS_PULLIMAGE:
		return "Pull Image Page"
	case PAGE_STATUS_INSTALL:
		return "Install Page"
	case PAGE_STATUS_TEST:
		return "Auto Test Page"
	case PAGE_STATUS_SERVICE:
		return "Service Managerment"
	default:
		return "unknown"
	}
}

func GetPageStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 6; i++ {
		idPair := entity.IdPair{i, GetPageStatusString(PageStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
