/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package entity

type JobBase struct {
	TotalJobs      int     `json:"totalJobs"`
	SuccessJobs    int     `json:"successJobs"`
	SuccessPercent float64 `json:"successPercent"`
	RunningJobs    int     `json:"runningJobs"`
	RunningPercent float64 `json:"runningPercent"`
	WaitingJobs    int     `json:"waitingJobs"`
	WaitingPercent float64 `json:"waitingPercent"`
	TimeoutJobs    int     `json:"timeoutJobs"`
	TimeoutPercent float64 `json:"timeoutPercent"`
	FailedJobs     int     `json:"failedJobs"`
	FailedPercent  float64 `json:"failedPercent"`
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
	Total int `json:"total"`
}
type MixSiteModeling struct {
	SiteName string `json:"siteName"`
	JobBase
}
type InstitutionSite struct {
	InstitutionSiteName string            `json:"institutionSiteName"`
	MixSiteModeling     []MixSiteModeling `json:"mixSiteModeling"`
}
type InstitutionSiteModelingItem struct {
	Institution         string            `json:"institution"`
	InstitutionSiteList []InstitutionSite `json:"institutionSiteList"`
}
type InsitutionSiteModeling struct {
	SiteList []string `json:"siteList"`
	OtherSiteList []InstitutionSiteModelingItem `json:"data"`
	Total int `json:"total"`
}