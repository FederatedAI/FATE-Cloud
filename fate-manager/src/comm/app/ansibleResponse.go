package app

import (
	"fate.manager/entity"
)

type CheckSystemResp struct {
	CommResp
	Data []entity.Prepare `json:"data"`
}
