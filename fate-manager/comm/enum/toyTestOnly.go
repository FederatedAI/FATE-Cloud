package enum

import "fate.manager/entity"

type ToyTestOnlyType int32

const (
	ToyTestOnly_UNKONWN ToyTestOnlyType = -1
	ToyTestOnly_NO_TEST ToyTestOnlyType = 0
	ToyTestOnly_TESTING ToyTestOnlyType = 1
	ToyTestOnly_SUCCESS ToyTestOnlyType = 2
	ToyTestOnly_FAILED  ToyTestOnlyType = 3
)

func GetToyTestOnlyString(p ToyTestOnlyType) string {
	switch p {
	case ToyTestOnly_SUCCESS:
		return "test success"
	case ToyTestOnly_NO_TEST:
		return "no test"
	case ToyTestOnly_TESTING:
		return "in testing"
	case ToyTestOnly_FAILED:
		return "toy test failed"
	default:
		return "unknown"
	}
}

func GetToyTestOnlyTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetToyTestOnlyString(ToyTestOnlyType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
