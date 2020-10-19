package enum

import "fate.manager/entity"

type FuncDebug int32

const (
	FuncDebug_UNKNOWN FuncDebug = -1
	FuncDebug_ON    FuncDebug = 1
	FuncDebug_OFF FuncDebug = 2
)

func GetFuncDebugString(p FuncDebug) string {
	switch p {
	case FuncDebug_ON:
		return "ON"
	case FuncDebug_OFF:
		return "OFF"
	default:
		return "unknown"
	}
}

func GetFuncDebug_OFFList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 3; i++ {
		idPair := entity.IdPair{i, GetFuncDebugString(FuncDebug(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
