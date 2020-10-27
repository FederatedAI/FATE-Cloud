package entity

type JobBase struct {
	TotalJobs               int     `json:"totalJobs"`
	CompleteJobs            int     `json:"completeJobs"`
	CompletePercent         float32 `json:"completePercent"`
	FailedJobs              int     `json:"failedJobs"`
	FailedPercent           float32 `json:"failedPercent"`
}
type SiteModelingItem struct {
	PartyId         int             `json:"partyId"`
	SiteName        string          `json:"siteName"`
	JobBase
}
type MonitorTotalResp struct {
	ActiveData      int                `json:"activeData"`
	JobBase
	SiteModeling    []SiteModelingItem `json:"data"`
}

type InstitutionModelingItem struct {
	Institution   string   `json:"institution"`
	JobBase
}

type InstitutionBaseStaticsResp struct {
	JobBase
	InstitutionModeling []InstitutionModelingItem `json:"data"`
}
type MixSiteModeling struct {
	SiteName string `json:"siteName"`
	InstitutionSiteName string   `json:"institutionSiteName"`
	JobBase
}

type InstitutionSiteModelingItem struct {
   Institution   string `json:"institution"`
   MixSiteModeling []MixSiteModeling `json:"mixSiteModeling"`
}

type SiteBaseStatisticsResp struct {
	InstitutionSiteModeling []InstitutionSiteModelingItem `json:"data"`
}