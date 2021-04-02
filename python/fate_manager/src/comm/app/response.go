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
package app

import (
	"fate.manager/comm/e"
	"fate.manager/entity"
	"github.com/gin-gonic/gin"
)

type Gin struct {
	C *gin.Context
}

type CommResp struct {
	Code int    `json:"code"`
	Msg  string `json:"msg"`
}
type BoolResp struct {
	CommResp
	Data bool `json:"data"`
}
type PairResp struct {
	CommResp
	Data entity.IdPair `json:"data"`
}

type Response struct {
	CommResp
	Data interface{} `json:"data"`
}

func (g *Gin) Response(httpCode, errCode int, data interface{}) {
	g.C.JSON(httpCode, Response{
		CommResp: CommResp{errCode, e.GetMsg(errCode)},
		Data:     data,
	})
	return
}
