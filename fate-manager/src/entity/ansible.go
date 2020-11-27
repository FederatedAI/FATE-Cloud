package entity

type AnsibleCommResp struct {
	Code int    `json:"retcode"`
	Msg  string `json:"retmsg"`
}

type AnsibleCheckResp struct {
	AnsibleCommResp
	Data []AnsiblePrepareItem `json:"data"`
}
type LocalUpload struct {
	FateVersion string `json:"fateVersion"`
}
type AnsibleInstallListResponse struct {
	AnsibleCommResp
	Data LocalUpload `json:"data"`
}
type SubmitResponse struct {
	AnsibleCommResp
	Data AnsibleSubmitData `json:"data"`
}
type VersionDownload struct {
	Elapsed   int    `json:"elapsed"`
	StartTime int64  `json:"f_start_time"`
	EndTime   int64  `json:"f_end_time"`
	Status    string `json:"f_status"`
	Version   string `json:"f_version"`
}
type VersionDownloadResponse struct {
	AnsibleCommResp
	Data VersionDownload `json:"data"`
}
type QueryResponse struct {
	AnsibleCommResp
	Data QueryData `json:"data"`
}
type AnsibleConnectReq struct {
	PartyId int    `json:"partyId"`
	Url     string `json:"ansbileUrl"`
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
type ConnectAnsible struct {
	PartyId int `json:"party_id"`
}
type AnsibleAutoTestRespItem struct {
	Status   bool `json:"status"`
	Duration int  `json:"duration"`
}
type DeployAnsibleStatus struct {
	Status string `json:"status"`
}
type DeployAnsibleResp struct {
	AnsibleCommResp
	Data DeployAnsibleStatus `json:"data"`
}
type AnsibleAutoTestResp struct {
	AnsibleCommResp
	Data map[string]AnsibleAutoTestRespItem
}
type LocalUploadReq struct {
	PartyId int    `json:"partyId"`
	Ip      string `json:"ip"`
	Path    string `json:"path"`
}
type AutoAcquireReq struct {
	PartyId     int    `json:"partyId"`
	FateVersion string `json:"fateVersion"`
	DownloadUrl string `json:"downloadUrl"`
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
type AnsibleListItem struct {
	Ip       string `json:"ip"`
	Duration int    `json:"duration"`
	Status   string `json:"status"`
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
	Item             string `json:"module"`
	Description      string `json:"description"`
	ComponentVersion string `json:"version"`
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
	Data AnsibleConnect `json:"data"`
}
type ConnectItem struct {
	Ips      []string `json:"ips"`
	Port     int      `json:"port"`
	HttpPort int      `json:"httpPort"`
	GrpcPort int      `json:"grpcPort"`
	Module   string   `json:"name"`
	Status   string   `json:"status"`
}
type AnsibleConnect struct {
	PartyId     string        `json:"status"`
	FateVersion string        `json:"version"`
	List        []ConnectItem `json:"list"`
}
type AnsibleLog struct {
	Level string `json:"level"`
}

type AnsibleLogResponse struct {
	AnsibleCommResp
	Data []string `json:"data"`
}
type AnsibleAutoTestReq struct {
	PartyId int  `json:"partyId"`
	IfOnly  bool `json:"ifOnly"`
}

type AnsibleToyTestReq struct {
	GuestPartyId int    `json:"guestPartyId"`
	HostPartyId  int    `json:"hostPartyId"`
	Ip           string `json:"ip"`
	TestType     string `json:"testType"`
}
type AnsibleToyTestResultReq struct {
	PartyId  int    `json:"partyId"`
	Ip       string `json:"ip"`
	TestType string `json:"testType"`
}

type AnsibleTestResultResponse struct {
	AnsibleCommResp
	Data []string  `json:"data"`
}
