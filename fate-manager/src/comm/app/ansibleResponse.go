package app

import "fate.manager/entity"

type AnsibleCommResp struct {
	Code int    `json:"retcode"`
	Msg  string `json:"retmsg"`
}

type AnsibleCheckResp struct {
	AnsibleCommResp
	Data []entity.AnsiblePrepareItem `json:"data"`
}