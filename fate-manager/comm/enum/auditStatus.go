package enum

import "fate.manager/entity"

type AuditStatusType int32

const (
	AuditStatus_UNKNOWN  AuditStatusType = -1
	AuditStatus_AUDITING AuditStatusType = 1
	AuditStatus_AGREED   AuditStatusType = 2
	AuditStatus_REJECTED AuditStatusType = 3
)

func GetAuditStatusString(p AuditStatusType) string {
	switch p {
	case AuditStatus_AUDITING:
		return "Waiting For Cloud Audit!"
	case AuditStatus_AGREED:
		return "Agreed Apply"
	case AuditStatus_REJECTED:
		return "Rejected Apply"
	default:
		return "unknown"
	}
}
func GetAuditStatusList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 1; i < 4; i++ {
		idPair := entity.IdPair{i, GetAuditStatusString(AuditStatusType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
