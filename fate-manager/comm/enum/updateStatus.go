package enum

import "fate.manager/entity"

type UpdateStatusType int32

const (
	UPDATE_STATUS_UNKNOWN   UpdateStatusType = -1
	UPDATE_STATUS_UPGRADE   UpdateStatusType = 1
	UPDATE_STATUS_UPGRADING UpdateStatusType = 2
	UPDATE_STATUS_UPGRADED  UpdateStatusType = 3
)

func GetUpdateStatusString(p UpdateStatusType) string {
	switch p {
	case UPDATE_STATUS_UPGRADE:
		return "upgrade"
	case UPDATE_STATUS_UPGRADING:
		return "upgrading"
	case UPDATE_STATUS_UPGRADED:
		return "upgraded"
	default:
		return "unknown"
	}
}

func GetUpdateStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetUpdateStatusString(UpdateStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
