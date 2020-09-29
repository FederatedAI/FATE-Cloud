package enum

import "fate.manager/entity"

type RoleType int32

const (
	ROLE_UNKNOWN RoleType = -1
	ROLE_GUEST   RoleType = 1
	ROLE_HOST    RoleType = 2
	ROLE_ARBITER RoleType = 3
)

func GetRoleString(p RoleType) string {
	switch p {
	case ROLE_GUEST:
		return "Guest"
	case ROLE_HOST:
		return "Host"
	case ROLE_ARBITER:
		return "Arbiter"
	default:
		return "Unknown"
	}
}

func GetRoleValue(p string) int {
	var roleype RoleType
	switch p {
	case "Guest":
		roleype = ROLE_GUEST
	case "Host":
		roleype = ROLE_HOST
	case "Arbiter":
		roleype = ROLE_ARBITER
	default:
		roleype = ROLE_UNKNOWN
	}
	return int(roleype)
}

func GetRoleTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 4; i++ {
		idPair := entity.IdPair{i, GetRoleString(RoleType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
