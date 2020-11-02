package entity

import "os"

type Machine struct {
	Ip string `json:"ip"`
	RootName string `json:"rootName"`
	RootPwd  string `json:"rootPwd"`
}
type AnsibleReq struct {
	PartyId     int    `json:"partyId"`
	ProductType int    `json:"productType"`
	Url         string `json:"ansbileUrl"`
}
type PrepareReq struct {
	PartyId      int       `json:"partyId"`
	ControlNode  Machine   `json:"controlNode"`
	ManageNode   []Machine `json:"manageNode"`
}
type CheckSystemReq struct {
	PartyId      int       `json:"partyId"`
}
type CheckItem struct {
	TestItem  string  `json:"testItem"`
	TestDuration int  `json:"testDuration"`
	Status       int  `json:"status"`
}
type CheckSystemResp struct {
	PartyId      int       `json:"partyId"`
	CheckItemList []CheckItem `json:"Checklist"`
}
type DeployAnsibleReq struct {
	PartyId      int       `json:"partyId"`
}
type DeployAnsibleResp struct {
	PartyId      int       `json:"partyId"`
}
type LocalUploadReq struct {
	PartyId      int       `json:"partyId"`
	FileContent  os.File   `json:"fileContent"`
}
type AutoAcquireReq struct {
	PartyId      int       `json:"partyId"`
	FateVersion  string    `json:"fateVersion"`
}