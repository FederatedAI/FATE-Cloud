package enum

import "fate.manager/entity"

type IsValidType int32

const (
	IS_VALID_UNKNOWN IsValidType = -1
	IS_VALID_NO      IsValidType = 0
	IS_VALID_YES     IsValidType = 1
	IS_VALID_ING     IsValidType = 2
)

func GetIsValidString(p IsValidType) string {
	switch p {
	case IS_VALID_YES:
		return "valid"
	case IS_VALID_NO:
		return "unvalid"
	case IS_VALID_ING:
		return "wait for valid"
	default:
		return "unknown"
	}
}

func GetIsValidList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetIsValidString(IsValidType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
