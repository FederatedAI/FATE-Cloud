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

type CloudCommResp struct {
	Code    int    `json:"code"`
	Message string `json:"msg"`
}
type CloudManagerResp struct {
	CloudCommResp
	Count int               `json:"count"`
	Data  map[string]string `json:"data"`
}
type CloudFunctionResp struct {
	CloudCommResp
	Count int        `json:"count"`
	Data  []Function `json:"data"`
}
type CloudInstitutionResp struct {
	CloudCommResp
	Data Institutions `json:"data"`
}
type ApplySiteResult struct {
	AuthorityId           int    `json:"authorityId"`
	Institutions          string `json:"institutions"`
	AuthorityInstitutions string `json:"authorityInstitutions"`
	CreateTime            string `json:"createTime"`
	UpdateTime            string `json:"updateTime"`
	Status                int    `json:"status"`
	Sequence              int    `json:"sequence"`
}
type ApplySiteResultResp struct {
	CloudCommResp
	Data []ApplySiteResult `json:"data"`
}
type AppliedResultResp struct {
	CloudCommResp
	Data []string `json:"data"`
}

type IpQueryItem struct {
	CaseId  string `json:"caseId"`
	PartyId int    `json:"partyId"`
}

type IpAcceptReq struct {
	NetworkAccessEntrances string `json:"networkAccessEntrances"`
	NetworkAccessExits     string `json:"networkAccessExits"`
	PartyId                int    `json:"partyId"`
}

type SecretInfo struct {
	AppKey    string `json:"appKey"`
	AppSecret string `json:"appSecret"`
}

type UpdateVersionReq struct {
	ComponentVersion   string `json:"componentVersion"`
	FateServingVersion string `json:"fateServingVersion"`
	FateVersion        string `json:"fateVersion"`
}
type FindOneSiteData struct {
	Id                     int64      `json:"id"`
	SiteName               string     `json:"siteName"`
	PartyId                int        `json:"partyId"`
	SecretInfo             SecretInfo `json:"secretInfo"`
	RegistrationLink       string     `json:"registrationLink"`
	NetworkAccessEntrances string     `json:"networkAccessEntrances"`
	NetworkAccessExits     string     `json:"networkAccessExits"`
	Institutions           string     `json:"institutions"`
	FateVersion            string     `json:"fateVersion"`
	FateServingVersion     string     `json:"fateServingVersion"`
	ComponentVersion       string     `json:"componentVersion"`
	CreateTime             int64      `json:"createTime"`
	Status                 int        `json:"status"`
	ServiceStatus          int        `json:"serviceStatus"`
	ActivationTime         int64      `json:"activationTime"`
	UpdateTime             int64      `json:"updateTime"`
	GroupId                int        `json:"groupId"`
	Role                   int        `json:"role"`
	GroupName              string     `json:"groupName"`
}
type FindOneSiteResp struct {
	CloudCommResp
	Data FindOneSiteData `json:"data"`
}
type Federation struct {
	Name        string `json:"name"`
	Institution string `json:institution`
	CreateTime  int64  `json:"createTime"`
}
type FederationData struct {
	Federation Federation `json:"federatedOrganizationDto"`
	Total      int        `json:"total"`
}
type FederationResp struct {
	CloudCommResp
	Data FederationData `json:"data"`
}

type IpQueryResp struct {
	CloudCommResp
	Data map[string]int `json:"data"`
}

type ActivateReq struct {
	RegistrationLink string `json:"registrationLink"`
}

type ApplyData struct {
	End         int               `json:"end"`
	List        []FindOneSiteData `json:"list"`
	PageNum     int               `json:"pageNum"`
	PageSize    int               `json:"pageSize"`
	Start       int               `json:"start"`
	StartIndex  int               `json:"startIndex"`
	TotalPage   int               `json:"totalPage"`
	TotalRecord int               `json:"totalRecord"`
}
type ApplyResp struct {
	CloudCommResp
	Data ApplyData `json:"data"`
}
type ApproveItem struct {
	Institutions string `json:"institutions"`
	Status       int    `json:"status"`
}
type ApproveData struct {
	End         int           `json:"end"`
	List        []ApproveItem `json:"list"`
	PageNum     int           `json:"pageNum"`
	PageSize    int           `json:"pageSize"`
	Start       int           `json:"start"`
	StartIndex  int           `json:"startIndex"`
	TotalPage   int           `json:"totalPage"`
	TotalRecord int           `json:"totalRecord"`
}
type ApproveResp struct {
	CloudCommResp
	Data ApproveData `json:"data"`
}
type CheckPartyId struct {
	Institutions string `json:"institutions"`
	PartyId      int    `json:"partyId"`
}
type CheckPartyIdResp struct {
	CloudCommResp
	Data bool `json:"data"`
}