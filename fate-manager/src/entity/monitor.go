package entity

type JobBase struct {
	TotalJobs       int     `json:"totalJobs"`
	SuccessJobs     int     `json:"successJobs"`
	SuccessPercent  float64 `json:"successPercent"`
	RunningJobs     int     `json:"runningJobs"`
	RunningPercent  float64 `json:"runningPercent"`
	TimeoutJobs     int     `json:"timeoutJobs"`
	TimeoutPercent  float64 `json:"timeoutPercent"`
	FailedJobs      int     `json:"failedJobs"`
	FailedPercent   float64 `json:"failedPercent"`
}
type SiteModelingItem struct {
	PartyId  int    `json:"partyId"`
	SiteName string `json:"siteName"`
	JobBase
}
type MonitorTotalResp struct {
	ActiveData int `json:"activeData"`
	JobBase
	SiteModeling []SiteModelingItem `json:"data"`
}

type InstitutionModelingItem struct {
	Institution string `json:"institution"`
	JobBase
}

type InstitutionBaseStaticsResp struct {
	JobBase
	InstitutionModeling []InstitutionModelingItem `json:"data"`
}
type MixSiteModeling struct {
	InstitutionSiteName string `json:"institutionSiteName"`
	JobBase
}

type InstitutionSiteModelingItem struct {
	SiteName        string            `json:"siteName"`
	Institution     string            `json:"institution"`
	MixSiteModeling []MixSiteModeling `json:"mixSiteModeling"`
}

