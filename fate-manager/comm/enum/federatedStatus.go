package enum

import "fate.manager/entity"

type FederatedType int32

const (
	FEDERATED_UNKNOWN FederatedType = -1
	FEDERATED_UNVALID FederatedType = 0
	FEDERATED_VALID   FederatedType = 1
)

func GetFederatedString(p FederatedType) string {
	switch p {
	case FEDERATED_UNVALID:
		return "unvalid federated"
	case FEDERATED_VALID:
		return "valid federated"
	default:
		return "unknown"
	}
}

func GetFederatedStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 2; i++ {
		idPair := entity.IdPair{i, GetFederatedString(FederatedType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
