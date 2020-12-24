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
type CheckSystem struct {
	ManagerNode []string `json:"managerNode"`
	CheckName   string   `json:"check_name"`
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
type AnsibleAutoTestItem struct {
	Name    string `json:"name"`
	Status  string `json:"status"`
	Uptime  string `json:"uptime"`
	Version string `json:"version"`
}
type AnsibleAutoTest struct {
	Ip   string                `json:"ip"`
	List []AnsibleAutoTestItem `json:"list"`
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
	Data AnsibleAutoTest `json:"data"`
}
type LocalUploadReq struct {
	PartyId int    `json:"partyId"`
	Ip      string `json:"ip"`
	Path    string `json:"dir"`
}
type AutoAcquireReq struct {
	PartyId     int    `json:"partyId"`
	FateVersion string `json:"fateVersion"`
	DownloadUrl string `json:"url"`
}
type DownloadPackageReq struct {
	FateVersion string `json:"version"`
	DownloadUrl string `json:"url"`
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
	CreateTime int64  `json:"f_create_time"`
	Elapsed    int    `json:"f_elapsed"`
	StartTime  int64  `json:"f_start_time"`
	Status     string `json:"f_status"`
	EndTime    int64  `json:"f_end_time"`
	JobId      string `json:"f_job_id"`
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
type AnsibleLogResp struct {
	Content []string `json:"content"`
	Total   int      `json:"total"`
}
type AnsibleLogResponse struct {
	AnsibleCommResp
	Data []string `json:"data"`
}
type AnsibleAutoTestReq struct {
	PartyId     int `json:"partyId"`
	ProductType int `json:"productType"`
	IfOnly      bool `json:"ifOnly"`
}

type AnsibleSingleTestReq struct {
	PartyId int    `json:"party_id"`
	Ip      string `json:"host"`
}

type AnsibleToyTestReq struct {
	GuestPartyId int    `json:"guest_party_id"`
	HostPartyId  int    `json:"host_party_id"`
	Ip           string `json:"host"`
	WorkMode     int    `json:"work_mode"`
}
type AnsibleMinTestReq struct {
	ArbiterPartyId int `json:"arbiter_party_id"`
	GuestPartyId int    `json:"guest_party_id"`
	HostPartyId  int    `json:"host_party_id"`
	Ip           string `json:"host"`
}
type AnsibleToyTestResultReq struct {
	Limit    int    `json:"limit"`
	Ip       string `json:"host"`
	TestType string `json:"test_type"`
}

type AnsibleTestResultResponse struct {
	AnsibleCommResp
	Data []string `json:"data"`
}
type DataUploadReq struct {
	Ip string `json:"host"`
}
