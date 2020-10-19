package enum

import "fate.manager/entity"

type LogDealType int32

const (
	LOG_DEAL_UNKNOWN  LogDealType = -1
	LOG_DEAL_NO       LogDealType = 0
	LOG_DEAL_AGREED   LogDealType = 1
	LOG_DEAL_REJECTED LogDealType = 2
)

func GetLogDealTypeString(p LogDealType) string {
	switch p {
	case LOG_DEAL_NO:
		return "not deal"
	case LOG_DEAL_AGREED:
		return "agreed"
	case LOG_DEAL_REJECTED:
		return "rejected"
	default:
		return "unknown"
	}
}

func GetLogDealList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetLogDealTypeString(LogDealType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
