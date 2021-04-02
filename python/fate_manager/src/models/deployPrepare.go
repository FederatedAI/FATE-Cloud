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
package models

import (
	"github.com/jinzhu/gorm"
	"time"
)

type DeployPrepare struct {
	Id           int64 `gorm:"type:bigint(12);column:id;primary_key;AUTO_INCREMENT"`
	PrepareTitle string
	PrepareDesc  string
	CreateTime   time.Time
	UpdateTime   time.Time
}

func GetPrepareList() ([]*DeployPrepare, error) {
	var deployPrepare []*DeployPrepare
	err := db.Find(&deployPrepare).Error
	if err != nil && err != gorm.ErrRecordNotFound {
		return nil, err
	}

	return deployPrepare, nil
}
