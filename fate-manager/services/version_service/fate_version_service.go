package version_service

import (
	"fate.manager/comm/enum"
	"fate.manager/models"
)

func GetFateVersionDropDownList(productType int) ([]*string, error) {
	fateVersion := models.FateVersion{
		ProductType: int(enum.PRODUCT_TYPE_FATE),
	}
	fateVersionList, err := models.GetFateVersionList(&fateVersion)
	if err != nil {
		return nil, err
	}
	var fatelist []*string
	for i := 0; i < len(fateVersionList); i++ {
		fatelist = append(fatelist, &fateVersionList[i].FateVersion)
	}
	return fatelist, nil
}

func GetComponetVersionList(name string)([]*string,error){
	componentVersion := models.ComponentVersion{
		ComponentName:    name,
		ProductType:  int(enum.PRODUCT_TYPE_FATE_SERVING),
	}
	componentVersionList,err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return nil,err
	}
	var list []*string
	for i :=0; i < len(componentVersionList); i++  {
		list = append(list,&componentVersionList[i].FateVersion)
	}
	return list,nil
}