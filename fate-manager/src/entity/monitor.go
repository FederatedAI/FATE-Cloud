package entity

type SiteModelingItem struct {
	PartyId         int    `json:"partyId"`
	SiteName        string `json:"siteName"`
	TotalJobs       int    `json:"totalJobs"`
	CompleteJobs    int    `json:"completeJobs"`
	FailedJobs      int    `json:"failedJobs"`
}
type MonitorTotalResp struct {
	ActiveData              int `json:"activeData"`
	FederatedModelingJobs   int `json:"federatedModelingJobs"`
	CompleteJobs            int `json:"completeJobs"`
	CompletePercent         float32 `json:"completePercent"`
	FailedJobs              int `json:"failedJobs"`
	FailedPercent           float32 `json:"failedPercent"`
	SiteModeling            []SiteModelingItem `json:"siteModeling"`
}

type InstitutionBaseStaticsResp struct {

}

type SiteBaseStatisticsResp struct {

}