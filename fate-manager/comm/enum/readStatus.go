package enum

import "fate.manager/entity"

type ReadStatusType int32

const (
	READ_STATUS_UNKNOWN  ReadStatusType = -1
	READ_STATUS_AGREED   ReadStatusType = 1
	READ_STATUS_REJECTED ReadStatusType = 2
	READ_STATUS_READ     ReadStatusType = 3
)

func GetReadStatusString(p ReadStatusType) string {
	switch p {
	case READ_STATUS_AGREED:
		return "Successfully changed the Network configuration!"
	case READ_STATUS_REJECTED:
		return "Changed the Network configuration failed!"
	case READ_STATUS_READ:
		return "Read"
	default:
		return "unknown"
	}
}
func GetReadStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetReadStatusString(ReadStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
