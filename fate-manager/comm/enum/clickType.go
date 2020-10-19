package enum

import "fate.manager/entity"

type ClickType int32

const (
	ClickType_UNKONWN ClickType = -1
	ClickType_CONNECT ClickType = 1
	ClickType_PAGE  ClickType = 2
	ClickType_PULL ClickType = 3
	ClickType_INSTALL ClickType = 4
	ClickType_TEST ClickType = 5
)

func GetClickTypeString(p ClickType) string {
	switch p {
	case ClickType_CONNECT:
		return "connect success"
	case ClickType_PAGE:
		return "click page start next"
	case ClickType_PULL:
		return "click pull next"
	case ClickType_INSTALL:
		return "click install next"
	case ClickType_TEST:
		return "click test finish"
	default:
		return "unknown"
	}
}

func GetClickTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 6; i++ {
		idPair := entity.IdPair{i, GetClickTypeString(ClickType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
