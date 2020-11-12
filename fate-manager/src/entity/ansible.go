package entity

import "os"

type Machine struct {
	Ip string `json:"ip"`
}
type AnsibleReq struct {
	PartyId     int    `json:"partyId"`
	ProductType int    `json:"productType"`
	Url         string `json:"ansbileUrl"`
}
type PrepareReq struct {
	ControlNode Machine   `json:"controlNode"`
	ManagerNode []Machine `json:"managerNode"`
}
type CheckItem struct {
	TestItem     string `json:"testItem"`
	TestDuration int    `json:"testDuration"`
	Status       int    `json:"status"`
}
type CheckSystemResp struct {
	PartyId       int         `json:"partyId"`
	CheckItemList []CheckItem `json:"Checklist"`
}
type DeployAnsibleReq struct {
	PartyId int `json:"partyId"`
}
type DeployAnsibleResp struct {
	PartyId int `json:"partyId"`
}
type LocalUploadReq struct {
	PartyId     int     `json:"partyId"`
	FileContent os.File `json:"fileContent"`
}
type AutoAcquireReq struct {
	PartyId     int    `json:"partyId"`
	FateVersion string `json:"fateVersion"`
}
