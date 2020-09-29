package enum

import "fate.manager/entity"

type TestStatusType int32

const (
	TEST_STATUS_UNKNOWN TestStatusType = -1
	TEST_STATUS_WAITING TestStatusType = 0
	TEST_STATUS_TESTING TestStatusType = 1
	TEST_STATUS_YES     TestStatusType = 2
	TEST_STATUS_NO      TestStatusType = 3
)

func GetTestStatusString(p TestStatusType) string {
	switch p {
	case TEST_STATUS_WAITING:
		return "waiting"
	case TEST_STATUS_TESTING:
		return "In Testing"
	case TEST_STATUS_YES:
		return "Test Success"
	case TEST_STATUS_NO:
		return "Test Failed"
	default:
		return "unknown"
	}
}

func GetTestStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetTestStatusString(TestStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
