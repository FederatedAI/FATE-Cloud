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
