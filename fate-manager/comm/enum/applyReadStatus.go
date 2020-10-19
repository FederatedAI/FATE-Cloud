package enum

import "fate.manager/entity"

type ApplyReadStatusType int32

const (
	APPLY_READ_STATUS_UNKNOWN  ApplyReadStatusType = -1
	APPLY_READ_STATUS_NOT_READ ApplyReadStatusType = 0
	APPLY_READ_STATUS_READ     ApplyReadStatusType = 1
)

func GetApplyReadStatusString(p ApplyReadStatusType) string {
	switch p {
	case APPLY_READ_STATUS_NOT_READ:
		return "Apply Site No Read"
	case APPLY_READ_STATUS_READ:
		return "Apply Site Read"
	default:
		return "unknown"
	}
}
func GetApplyReadStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetApplyReadStatusString(ApplyReadStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
