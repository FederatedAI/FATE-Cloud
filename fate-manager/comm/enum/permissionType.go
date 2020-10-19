package enum

import "fate.manager/entity"

type PermissionType int32

const (
	PermissionType_UNKNOWN    PermissionType = -1
	PermissionType_BASIC      PermissionType = 1
	PermissionType_DEPLOY     PermissionType = 2
	PermissionType_FATEBOARD  PermissionType = 3
	PermissionType_FATESTUDIO PermissionType = 4
	PermissionType_FDN        PermissionType = 5
)

func GetPermissionTypeString(p PermissionType) string {
	switch p {
	case PermissionType_BASIC:
		return "FATE Cloud: Basic management"
	case PermissionType_DEPLOY:
		return "FATE Cloud: Auto-deploy"
	case PermissionType_FATEBOARD:
		return "FATE-Board"
	case PermissionType_FATESTUDIO:
		return "FATE-Studio"
	case PermissionType_FDN:
		return "FDN"
	default:
		return "unknown"
	}
}

func GetPermissionTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 5; i++ {
		idPair := entity.IdPair{i, GetPermissionTypeString(PermissionType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
