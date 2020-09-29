package enum

import "fate.manager/entity"

type UserRole int32

const (
	UserRole_UNKNOWN   UserRole = -1
	UserRole_ADMIN     UserRole = 1
	UserRole_DEVELOPER UserRole = 2
	UserRole_BUSINESS  UserRole = 3
)

func GetUserRoleString(p UserRole) string {
	switch p {
	case UserRole_ADMIN:
		return "Admin"
	case UserRole_DEVELOPER:
		return "Developer or OP"
	case UserRole_BUSINESS:
		return "Business or Data Analyst"
	default:
		return "unknown"
	}
}

func GetUserRoleList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetUserRoleString(UserRole(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
