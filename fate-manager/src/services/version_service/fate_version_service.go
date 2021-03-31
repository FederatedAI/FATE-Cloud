/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package version_service

import (
	"encoding/json"
	"fate.manager/comm/enum"
	"fate.manager/entity"
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

func GetComponetVersionList(name string) ([]*string, error) {
	componentVersion := models.ComponentVersion{
		ComponentName: name,
		ProductType:   int(enum.PRODUCT_TYPE_FATE_SERVING),
	}
	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return nil, err
	}
	var list []*string
	for i := 0; i < len(componentVersionList); i++ {
		list = append(list, &componentVersionList[i].FateVersion)
	}
	return list, nil
}
func GetComponetVersionListByFateVersion(componentversionReq entity.ComponentversionReq) (string, error) {
	componentVersion := models.ComponentVersion{
		FateVersion: componentversionReq.FateVersion,
		ProductType:   int(enum.PRODUCT_TYPE_FATE),
	}
	componentVersionList, err := models.GetComponetVersionList(componentVersion)
	if err != nil {
		return "", err
	}
	var componentVersonMap = make(map[string]interface{})
	for i := 0; i < len(componentVersionList); i++ {
		componentVersonMap[componentVersionList[i].ComponentName] = componentVersionList[i].ComponentVersion
	}
	componentVersonMapjson, _ := json.Marshal(componentVersonMap)
	return string(componentVersonMapjson), nil
}