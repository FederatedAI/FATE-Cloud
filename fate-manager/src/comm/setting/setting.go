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
package setting

import (
	"github.com/go-ini/ini"
	"log"
	"time"
)

const (
	SiteQueryUri          = "/cloud-manager/api/site/findOneSite/fateManager"
	HeartUri              = "/cloud-manager/api/site/heart/fateManager"
	ActivateUri           = "/cloud-manager/api/site/activate"
	IpAcceptUri           = "/cloud-manager/api/site/ip/accept"
	IpQueryUri            = "/cloud-manager/api/site/ip/query"
	CheckUri              = "/cloud-manager/api/site/checkUrl"
	FederationUri         = "/cloud-manager/api/federation/findOrganization"
	UpdateVersionUri      = "/cloud-manager/api/site/fate/version"
	CheckAuthorityUri     = "/cloud-manager/api/site/checkAuthority/fateManager"
	CheckWebUri           = "/cloud-manager/api/site/checkWeb"
	SystemAddUri          = "/cloud-manager/api/system/add"
	UserActivateUri       = "/cloud-manager/api/fate/user/activate"
	AuthorityInstitutions = "/cloud-manager/api/authority/institutions"
	AuthorityApply        = "/cloud-manager/api/authority/apply"
	AuthorityApplied      = "/cloud-manager/api/authority/applied"
	FunctionAllUri        = "/cloud-manager/api/function/find/all/fateManager"
	OtherSiteUri          = "/cloud-manager/api/site/page/fateManager"
	ApprovedUri           = "/cloud-manager/api/authority/institutions/approved"
	CheckPartyUri         = "/cloud-manager/api/authority/check/partyId"
)
const (
	FlowJobQuery = "/v1/job/query"
)

const (
	AnsibleConnectUri    = "/ansible/v1/server/status"
	AnsiblePrepareUri    = "/ansible/v1/check/pre"
	AnsibleInstallUri    = "/ansible/v1/check/available"
	AnsibleJobSubmitUri  = "/ansible/v1/job/submit"
	AnsibleJobQueryUri   = "/ansible/v1/job/query"
	AnsibleTestStatusUri = "/ansible/v1/test/status"
)

type Server struct {
	RunMode      string
	HttpPort     int
	ReadTimeout  time.Duration
	WriteTimeout time.Duration
	LogSavePath  string
	TimeFormat   string
	ProxyUrl     string
	IfProxy      bool
}

var ServerSetting = &Server{}

type Database struct {
	Type        string
	UserName    string
	Password    string
	HostAddr    string
	DataBase    string
	TablePrefix string
	SqlMode     bool
}

var DatabaseSetting = &Database{}

type Kubenetes struct {
	KubeFateUrl              string
	ExchangeIp               string
	ExchangePort             int
	SudoTag                  bool
	NodeManager              int
	Registry                 string
	SessionProcessorsPerNode int
	TestPartyId              int
	ModeAlone                bool
}

var KubenetesSetting = &Kubenetes{}

type Schedule struct {
	IpManager int
	Heart     int
	Job       int
	Test      int
	Apply     int
	Monitor   int
}

var ScheduleSetting = &Schedule{}

var cfg *ini.File

func Setup() {
	var err error
	cfg, err = ini.Load("conf/app.ini")
	if err != nil {
		log.Fatalf("setting.Setup, fail to parse 'conf/app.ini': %v", err)
	}

	mapTo("server", ServerSetting)
	mapTo("database", DatabaseSetting)
	mapTo("schedule", ScheduleSetting)
	mapTo("kubenetes", KubenetesSetting)

	ServerSetting.ReadTimeout = ServerSetting.ReadTimeout * time.Second
	ServerSetting.WriteTimeout = ServerSetting.WriteTimeout * time.Second
}

func mapTo(section string, v interface{}) {
	err := cfg.Section(section).MapTo(v)
	if err != nil {
		log.Fatalf("Cfg.MapTo %s err: %v", section, err)
	}
}
