package entity

type AnsibleCommResp struct {
	Code int    `json:"retcode"`
	Msg  string `json:"retmsg"`
}

type AnsibleCheckResp struct {
	AnsibleCommResp
	Data []AnsiblePrepareItem `json:"data"`
}

type AnsibleInstallListResponse struct {
	AnsibleCommResp
	Data AcquireResp `json:"data"`
}
type SubmitResponse struct {
	AnsibleCommResp
	Data AnsibleSubmitData `json:"data"`
}
type QueryResponse struct {
	AnsibleCommResp
	Data QueryData `json:"data"`
}
type AnsibleConnectReq struct {
	PartyId     int    `json:"partyId"`
	Url         string `json:"ansbileUrl"`
}

type PrepareReq struct {
	ControlNode string   `json:"controlNode"`
	ManagerNode []string `json:"managerNode"`
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
	ControlNode string `json:"controlNode"`
}
type DeployAnsibleResp struct {
	PartyId int `json:"partyId"`
}
type LocalUploadReq struct {
	PartyId  int    `json:"partyId"`
	SiteName string `json:"siteName"`
	Ip       string `json:"ip"`
	Path     string `json:"path"`
}
type AutoAcquireReq struct {
	PartyId     int    `json:"partyId"`
	SiteName    string `json:"siteName"`
	FateVersion string `json:"fateVersion"`
}

type Prepare struct {
	Name     string `json:"name"`
	Details  string `json:"details"`
	Status   string `json:"status"`
	Duration int64  `json:"duration"`
}
type AnsiblePrepareItem struct {
	Ip   string    `json:"ip"`
	List []Prepare `json:"list"`
}

type AnsiblePrepare struct {
	IpPrepareList []AnsiblePrepareItem
}
type CheckSystemReq struct {
	Ip string `json:"ip"`
}
type IpStatus struct {
	Ip     string `json:"ip"`
	Status string `json:"status"`
}
type AcquireRespItem struct {
	Item             string `json:"item"`
	Description      string `json:"description"`
	ComponentVersion string `json:"componentVersion"`
	Size             string `json:"size"`
	Time             int64  `json:"time"`
	Status           IdPair `json:"status"`
}

type AcquireResp struct {
	FateVersion         string            `json:"fateVersion"`
	AcquireRespItemList []AcquireRespItem `json:"list"`
}
type AnsibleSubmitData struct {
	JobId string `json:"job_id"`
}
type Play struct {
	CreateTime int64  `json:"create_time"`
	Elapsed    int    `json:"elapsed"`
	StartTime  int64  `json:"start_time"`
	Status     string `json:"status"`
	EndTime    int64  `json:"end_time"`
	JobId      string `json:"job_id"`
}
type PlayItem struct {
	Play
	Module string `json:"module"`
}
type QueryData struct {
	Play
	Plays []PlayItem `json:"plays"`
}
type AnsibleConnectResp struct {
	AnsibleCommResp
	Data map[string]ConnectItem `json:"data"`
}
type ConnectItem struct {
	Status string `json:"status"`
	UpdateTime string `json:"uptime"`
}

