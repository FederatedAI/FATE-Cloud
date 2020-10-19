package enum

import "fate.manager/entity"

type ActionType int32

const (
	ActionType_UNKNOWN ActionType = -1
	ActionType_STOP    ActionType = 0
	ActionType_RESTART ActionType = 1
)

func GetActionTypeString(p ActionType) string {
	switch p {
	case ActionType_STOP:
		return "stopped"
	case ActionType_RESTART:
		return "restart"
	default:
		return "unknown"
	}
}

func GetActionTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetActionTypeString(ActionType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
