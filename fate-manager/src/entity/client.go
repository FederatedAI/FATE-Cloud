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

import (
	"time"
)

type IdPair struct {
	Code int    `json:"code"`
	Desc string `json:"desc"`
}
type ComponentVersionPair struct {
	ComponentName    string `json:"componentName""`
	ComponentVersion string `json:"componentVersion"`
}
type AuditPair struct {
	Code     int    `json:"code"`
	Desc     string `json:"desc"`
	ReadCode int    `json:"readCode"`
}
type SitePair struct {
	PartyId     int    `json:"partyId"`
	SiteName    string `json:"siteName"`
	Institution string `json:"institution"`
}
type FederatedSite struct {
	PartyId     int `json:"partyId"`
	FederatedId int `json:"federatedId"`
}

//db federation_info left join site_info
type FederatedSiteItem struct {
	SiteId                  int64     `json:"siteId"`
	FederatedId             int       `json:"federatedId"`
	FederatedOrganization   string    `json:"federatedOrganization"`
	FederatedUrl            string    `json:"federatedUrl"`
	Institutions            string    `json:"institutions"`
	FateManagerInstitutions string    `json:"fateManagerInstitutions"`
	Size                    int       `json:"size"`
	CreateTime              time.Time `json:"createTime"`
	PartyId                 int       `json:"partyId"`
	SiteName                string    `json:"siteName"`
	Role                    int       `json:"role"`
	AppKey                  string    `json:"appKey"`
	AppSecret               string    `json:"appSecret"`
	Status                  int       `json:"status"`
	ServiceStatus           int       `json:"serviceStatus"`
	FateVersion             string    `json:"fateVersion"`
	FateServingVersion      string    `json:"fateServingVersion"`
	ComponentVersion        string    `json:"componentVersion"`
	AcativationTime         time.Time `json:"acativationTime"`
}

type FederatedItem struct {
	FederatedId             int        `json:"federatedId"`
	FederatedOrganization   string     `json:"federatedOrganization"`
	Institutions            string     `json:"institutions"`
	FateManagerInstitutions string     `json:"fateManagerInstitutions"`
	Size                    int        `json:"size"`
	SiteItemList            []SiteItem `json:"siteList"`
	CreateTime              int64      `json:"createTime"`
}

type SiteItem struct {
	Id              int64  `json:"siteId"`
	PartyId         int    `json:"partyId"`
	SiteName        string `json:"siteName"`
	Role            IdPair `json:"role"`
	Status          IdPair `json:"status"`
	ServiceStatus   IdPair `json:"serviceStatus"`
	AcativationTime int64  `json:"acativationTime"`
}

//home list response,uri:/api/site
type HomeSiteListResp struct {
	HomeSiteList []*FederatedItem
}

//site info response,uri:/api/site/info
type SiteDetailResp struct {
	Id                    int64  `json:"siteId"`
	FederatedOrganization string `json:"federatedOrganization"`
	FederatedSite
	SiteName               string `json:"siteName"`
	Institutions           string `json:"institutions"`
	Role                   IdPair `json:"role"`
	AppKey                 string `json:"appKey"`
	AppSecret              string `json:"appSecret"`
	RegistrationLink       string `json:"registrationLink"`
	NetworkAccessEntrances string `json:"networkAccessEntrances"`
	NetworkAccessExits     string `json:"networkAccessExits"`
	FateVersion            string `json:"fateVersion"`
	FateServingVersion     string `json:"fateServingVersion"`
	ComponentVersion       string `json:"componentVersion"`
	Status                 IdPair `json:"status"`
	EditStatus             IdPair `json:"editStatus"`
	VersionEditStatus      IdPair `json:"versionEditStatus"`
	CreateTime             int64  `json:"createTime"`
	AcativationTime        int64  `json:"acativationTime"`
}

//update site requqest,uri:/api/site/update
type UpdateSiteReq struct {
	FederatedSite
	FederatedOrganization  string `json:"federatedOrganization"`
	Role                   int    `json:"role"`
	AppKey                 string `json:"appKey"`
	AppSecret              string `json:"appSecret"`
	NetworkAccessEntrances string `json:"networkAccessEntrances"`
	NetworkAccessExits     string `json:"networkAccessExits"`
}

//register requqest,uri:/api/site/register
type RegisterReq struct {
	Id                     int    `json:"id"`
	FederatedOrganization  string `json:"federatedOrganization"`
	PartyId                int    `json:"partyId"`
	SiteName               string `json:"siteName"`
	Institutions           string `json:"institutions"`
	Role                   int    `json:"role"`
	AppKey                 string `json:"appKey"`
	AppSecret              string `json:"appSecret"`
	RegistrationLink       string `json:"registrationLink"`
	NetworkAccessEntrances string `json:"networkAccessEntrances"`
	NetworkAccessExits     string `json:"networkAccessExits"`
	FederatedUrl           string `json:"federatedUrl"`
}

//get site info request:uri:/api/site/info
type SiteDetailReq struct {
	FederatedSite
}

type SiteSecretResp struct {
	AppKey    string `json:"appKey"`
	AppSecret string `json:"appSecret"`
	Role      string `json:"role"`
}

//check site status
type CheckSiteReq struct {
	HostPartyId  int    `json:"dstPartyId"`
	FederatedId  int    `json:"federatedId"`
	GuestPartyId int    `json:"srcPartyId"`
	Role         string `json:"role"`
	AppKey       string `json:"appKey"`
	AppSecret    string `json:"appSecret"`
}

type TelnetReq struct {
	FederatedSite
	Ip   string `json:"ip"`
	Port string `json:"port"`
}

type ReadChangeReq struct {
	FederatedSite
	ReadStatus int `json:"readStatus"`
}
type GetChangeReq struct {
	FederatedSite
}

//get site info request:uri:/api/site/info
type FederationListItem struct {
	FederatedOrganization string `json:"federatedOrganization"`
	FederatedId           int    `json:"federatedId"`
}

type SiteListItem struct {
	PartyId      int    `json:"partyId"`
	SiteName     string `json:"siteName"`
	DeployStatus bool   `json:deployStatus`
}
type LoginSiteItem struct {
	PartyId  int    `json:"partyId"`
	SiteName string `json:"siteName"`
	Role     IdPair `json:"role"`
}
type ServiceInfoReq struct {
	FederatedSite
	ProductType int    `json:"productType"`
	FateVersion string `json:"fateVersion"`
}

type ComponentDeploy struct {
	SiteName         string `json:"siteName"`
	ComponentName    string `json:"componentName"`
	ComponentVersion string `json:"componentVersion"`
	Address          string `json:"address"`
	StartTime        int64  `json:"startTime"`
	FinishTime       int64  `json:"finishTime"`
	Duration         int    `json:"duration"`
	Status           IdPair `json:"status"`
	DeployStatus     IdPair `json:"deployStatus"`
}

type ActionReq struct {
	FederatedSite
	ProductType   int    `json:"productType"`
	ComponentName string `json:"componentName"`
	Action        int    `json:"action"`
}

type LogReq struct {
	FederatedSite
	ProductType int `json:"productType"`
}
type InstallStatus struct {
	FederatedSite
	ProductType int `json:"productType"`
}
type KubeReq struct {
	FederatedSite
	ProductType int    `json:"productType"`
	Url         string `json:"kubenetesUrl"`
}
type OverViewReq struct {
	FederatedId int    `json:"federatedId"`
	Condition   string `json:"condition"`
	Role        int    `json:"role"`
}
type PrepareItem struct {
	ProcName string `json:"procName"`
	ProcDesc string `json:"procDesc"`
}

type PullComponentListReq struct {
	ProductType int    `json:"productType"`
	FateVersion string `json:"fateVersion"`
	FederatedSite
}
type InstallComponentListReq struct {
	FederatedSite
	ProductType int `json:"productType"`
}
type PullComponentListRespItem struct {
	ImageName        string `json:"imageName"`
	ImageId          string `json:"imageId"`
	ImageTag         string `json:"iamgeTag"`
	ImageDescription string `json:"iamgeDescription"`
	ImageVersion     string `json:"imageVersion"`
	ImageSize        string `json:"iamgeSize"`
	ImageCreateTime  int64  `json:"imageCreateTime"`
	PullStatus       IdPair `json:"pullStatus"`
}
type InstallComponentListRespItem struct {
	ComponentName string `json:"componentName"`
	Address       string `json:"address"`
	DeployStatus  IdPair `json:"deployStatus"`
	Duration      int    `json:"duration"`
}

type PullReq struct {
	ProductType       int      `json:"productType"`
	FateVersion       string   `json:"fateVersion"`
	PullComponentList []string `json:"pullComponentList"`
}
type CommitImagePullReq struct {
	FederatedSite
	ProductType int    `json:"productType"`
	FateVersion string `json:"fateVersion"`
	DeployType  int    `json:"deployType"`
}

type InstallReq struct {
	FederatedSite
	ProductType int `json:"productType"`
}
type UpgradeReq struct {
	FederatedSite
	ProductType int    `json:"productType"`
	FateVersion string `json:"fateVersion"`
}
type UpdateReq struct {
	FederatedSite
	ProductType   int    `json:"productType"`
	ComponentName string `json:"componentName"`
	Address       string `json:"address"`
}
type AutoTestListReq struct {
	FederatedSite
	ProductType int `json:"productType"`
}
type AutoTestItem struct {
	TestItem string `json:"testItem"`
	Duration int    `json:"duration"`
	Status   IdPair `json:"status"`
}
type AutoTestListRespItem struct {
	AutoTest map[string][]AutoTestItem
}
type AutoTestReq struct {
	FederatedSite
	ProductType  int                 `json:"productType"`
	TestItemList map[string][]string `json:"testItemList"`
	IfOnly       bool                `json:"ifOnly"`
}
type TestResultReq struct {
	FederatedSite
	ProductType int    `json:"productType"`
	TestItem    string `json:"testItem"`
}
type OverViewRspItem struct {
	FederationListItem
	Institutions string        `json:"institutions"`
	PartyId      int           `json:"partyId"`
	SiteName     string        `json:"siteName"`
	FateVersion  string        `json:"fateVersion"`
	Role         IdPair        `json:"role"`
	InstallInfo  []InstallItem `json:"installInfo"`
	DeployTag    bool          `json:"deployTag"`
	DeployType   IdPair        `json:"deployType"`
}

type Operation struct {
	OperateTime   int64  `json:"operateTime"`
	OperateAction IdPair `json:"operateAction"`
}

type InstallItem struct {
	ComponentName    string      `json:"componentName"`
	ComponentVersion string      `json:"ComponentVersion"`
	InstallTime      int64       `json:"installTime"`
	UpgradeTime      int64       `json:"upgradeTime"`
	UpgradeStatus    bool        `json:"upgradeStatus"`
	OperationList    []Operation `json:"operation"`
	DeployType       IdPair      `json:"deployType"`
	ServiceStatus    IdPair      `json:"serviceStatus"`
}

type UpgradeFateReq struct {
	FederatedSite
	ProductType int `json"productType"`
}
type PageStatusReq struct {
	FederatedSite
	ProductType int `json"productType"`
}
type PageStatusResp struct {
	PageStatus IdPair `json:"pageStatus"`
	DeployType IdPair `json:"deployType"`
}
type UpdateVersionResp struct {
	VersionIndex int
	FateVersion  string
}
type GetChangeResp struct {
	EditStatus IdPair `json:"editStatus"`
	ReadStatus IdPair `json:"readStatus"`
}
type ClickReq struct {
	FederatedSite
	ProductType int `json"productType"`
	ClickType   int `json:"clickType"`
}

type ToyResultReadReq struct {
	FederatedSite
	ProductType     int `json"productType"`
	ToyTestOnlyRead int `json:"toyTestOnlyRead"`
}

type LoginReq struct {
	AccountName string `json:"userName"`
	Password    string `json:"passWord"`
	SubTag      bool   `json:"subTag"`
}
type LogoutReq struct {
	AccountName string `json:"userName"`
	AccountId   string `json:"userId"`
}

type AccountActivateReq struct {
	LoginReq
	FederatedUrl           string `json:"federatedUrl"`
	FederatedId            int    `json:"federatedId"`
	FederatedOrganization  string `json:"federatedOrganization"`
	FateManagerInstitution string `json:"institutions"`
	Institution            string `json:"institution"`
	AppKey                 string `json:"appKey"`
	AppSecret              string `json:"appSecret"`
	ActivateUrl            string `json:"activateUrl"`
	FateManagerId          string `json:"fateManagerId"`
}
type Role struct {
	RoleId   int    `json:"roleId"`
	RoleName string `json:"roleName"`
}

type LoginResp struct {
	Token string `json:"token"`
}
type ApplySiteReq struct {
	ApplyList    []string `json:"authorityInstitutions"`
	Institutions string   `json:"institutions"`
}
type QuerySiteReq struct {
	Institutions string `json:"institutions"`
	PageNum      int    `json:"pageNum"`
	PageSize     int    `json:"pageSize"`
}
type UserListItem struct {
	UserName string `json:"userName"`
	UserId   string `json:"userId"`
}
type UserAccessListReq struct {
	UserName string `json:"userName"`
	PartyId  int    `json:"partyId"`
	RoleId   int    `json:"roleId"`
}
type AddUserReq struct {
	UserListItem
	SitePair
	RoleId         int    `json:"roleId"`
	PermissionList []int  `json:"permissionList"`
	Creator        string `json:"creator"`
}
type Permission struct {
	PermissionId   int    `json:"permissionId"`
	PermissionName string `json:"permissionName"`
}
type UserAccessListItem struct {
	UserId         string       `json:"userId"`
	UserName       string       `json:"userName"`
	Site           SitePair     `json:"site"`
	Role           Role         `json:"role"`
	CloudUser      bool         `json:"cloudUser"`
	PermissionList []Permission `json:"permissionList"`
	Creator        string       `json:"creator"`
	CreateTime     int64        `json:"createTime"`
}

type EditUserReq struct {
	UserListItem
	OldPartyId     int    `json:"oldPartyId"`
	OldRoleId      int    `json:"oldRoleId"`
	PartyId        int    `json:"partyId"`
	SiteName       string `json:"siteName"`
	RoleId         int    `json:"roleId"`
	PermissionList []int  `json:"permissionList"`
}

type DeleteUserReq struct {
	UserListItem
	SitePair
}
type SearchReq struct {
	Context string `json:"context"`
}

type SiteInfoUserListReq struct {
	PartyId int `json:"partyId"`
}
type SiteInfoUserListItem struct {
	UserName   string `json:"userName"`
	Role       string `json:"role"`
	Permission string `json:"permission"`
}

type ApplyResult struct {
	Status       IdPair `json:"status"`
	Institutions string `json:"institutions"`
}
type Audit struct {
	AuditResult []IdPair `json:"auditResult"`
}
type Function struct {
	FunctionId   int    `json:"functionId"`
	FunctionName string `json:"functionName"`
	Status       int    `json:"status"`
}
type FunctionResp struct {
	Function
	ReadStatus int `json:"readStatus"`
}
type InstitutionsReq struct {
	Institutions string `json:"institutions"`
	PageNum      int    `json:"pageNum"`
	PageSize     int    `json:"pageSize"`
}
type InstitutionsList struct {
	Institutions string `json:"institutions"`
	Status       int    `json:"status"`
}
type Institutions struct {
	Start       int                `json:"start"`
	End         int                `json:"end"`
	List        []InstitutionsList `json:"list"`
	PageNum     int                `json:"pageNum"`
	PageSize    int                `json:"pageSize"`
	StartIndex  int                `json:"startIndex"`
	TotalPage   int                `json:"totalPage"`
	TotalRecord int                `json:"totalRecord"`
}
type CheckJwtReq struct {
	Token string `json:"token"`
}
type CheckJwtResp struct {
	UserId       string `json:"userId"`
	UserName     string `json:"userName"`
	Institutions string `json:"institutions"`
}

type UserInfoResp struct {
	Role           Role         `json:"role"`
	PermissionList []Permission `json:"permissionList"`
	UserName       string       `json:"userName"`
	UserId         string       `json:"userId"`
}
type ApplyFateManager struct {
	Institutions []string `json:"institutions"`
	Size         int      `json:"size"`
}
type UpdateComponentVersionReq struct {
	FederatedSite
	ComponentVersion   string `json:"componentVersion"`
	FateVersion        string `json:"fateVersion"`
	FateServingVersion string `json:"fateServingVersion"`
}
type ApplyLog struct {
	ApplyTime int64  `json:"applyTime"`
	Content   string `json:"content"`
}
type PermissionAuthorityReq struct {
	PartyId int `json:"PartyId"`
}
type SubLoginReq struct {
	LoginReq
	PartyId int `json:"PartyId"`
}
type SubLoginResp struct {
	PartyId  int    `json:"partyId"`
	SiteName string `json:"siteName"`
	Role
}
type AllowReq struct {
	PartyId  int    `json:"partyId"`
	RoleName string `json:"roleName"`
}
type ComponentversionReq struct {
	FateVersion string `json:"fateVersion"`
}
type ChangeLoginReq struct {
	AccountName string `json:"userName"`
	SubTag      bool   `json:"subTag"`
	PartyId     int    `json:"PartyId"`
}
type ComponentVersionDetail struct {
	Version string `json:"version"`
	Address string `json:"address"`
}
type MonitorReq struct {
	StartDate string `json:"startDate"`
	EndDate   string `json:"endDate"`
	PageNum   int    `json:"pageNum"`
	PageSize  int    `json:"pageSize"`
}

type ExchangeItem struct {
	ExchangeId   int    `json:"exchangeId"`
	ExchangeName string `json:"exchangeName"`
	Address      string `json:"address"`
	UpdateTime   string `json:"updateTime"`
}
type ExchangeResponse struct {
	ExchangeVip   []ExchangeItem `json:"exchangeVip"`
	ExchangeExits []ExchangeItem `json:"exchangeExits"`
}
