package enum

import "fate.manager/entity"

type ProductType int32

const (
	PRODUCT_TYPE_UNKNOWN      ProductType = -1
	PRODUCT_TYPE_FATE         ProductType = 1
	PRODUCT_TYPE_FATE_SERVING ProductType = 2
)

func GetProductTypeString(p ProductType) string {
	switch p {
	case PRODUCT_TYPE_FATE:
		return "FATE"
	case PRODUCT_TYPE_FATE_SERVING:
		return "FATE-Serving"
	default:
		return "unknown"
	}
}

func GetProductTypeList() []entity.IdPair {
	var idPairList []entity.IdPair
	for i := 0; i < 3; i++ {
		idPair := entity.IdPair{i, GetProductTypeString(ProductType(i))}
		idPairList = append(idPairList, idPair)
	}
	return idPairList
}
