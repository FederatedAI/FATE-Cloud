package entity

import "os"

type AnsibleReq struct {
	PartyId     int    `json:"partyId"`
	ProductType int    `json:"productType"`
	Url         string `json:"ansbileUrl"`
}
type Machine struct {
	ip string `json:"ip"`
}
type PrepareReq struct {
	ControlNode string   `json:"controlNode"`
	ManagerNode []string `json:"managerNode"`
}
type PrepareReqBak struct {
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

type Prepare struct {
	Name    string `json:"name"`
	Details string `json:"details"`
	Status  string `json:"status"`
}
type AnsiblePrepareItem struct {
	Ip   string    `json:"ip"`
	List []Prepare `json:"list"`
}

type AnsiblePrepare struct {
	IpPrepareList []AnsiblePrepareItem
}
