package enum

import "fate.manager/entity"

type EditType int32

const (
	EDIT_UNKNOWN EditType = -1
	EDIT_NO      EditType = 1
	EDIT_YES     EditType = 2
)

func GetEditString(p EditType) string {
	switch p {
	case EDIT_NO:
		return "unedit"
	case EDIT_YES:
		return "edit"
	default:
		return "unknown"
	}
}

func GetEditList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetEditString(EditType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
