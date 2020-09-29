package enum

import "fate.manager/entity"

type InitStatus int32

const (
	INIT_STATUS_UNKNOWN      InitStatus = -1
	INIT_STATUS_KUBEFATE     InitStatus = 1
	INIT_STATUS_START_DEPLOY InitStatus = 2
	INIT_STATUS_SERVICE      InitStatus = 3
)

func GetInitStatusString(p InitStatus) string {
	switch p {
	case INIT_STATUS_KUBEFATE:
		return "connect kubefate"
	case INIT_STATUS_START_DEPLOY:
		return "start deploy"
	case INIT_STATUS_SERVICE:
		return "goto service management"
	default:
		return "unknown"
	}
}

func GetInitStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 4; i++ {
		idPair := entity.IdPair{i, GetInitStatusString(InitStatus(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
