package setting

import (
	"github.com/go-ini/ini"
	"log"
	"time"
)

var SiteQueryUri = "/cloud-manager/api/site/findOneSite/fateManager"
var HeartUri = "/cloud-manager/api/site/heart/fateManager"
var ActivateUri = "/cloud-manager/api/site/activate"
var IpAcceptUri = "/cloud-manager/api/site/ip/accept"
var IpQueryUri = "/cloud-manager/api/site/ip/query"
var CheckUri = "/cloud-manager/api/site/checkUrl"
var FederationUri = "/cloud-manager/api/federation/findOrganization"
var UpdateVersionUri = "/cloud-manager/api/site/fate/version"
var CheckAuthorityUri = "/cloud-manager/api/site/checkAuthority/fateManager"
var CheckWebUri = "/cloud-manager/api/site/checkWeb"
var SystemAddUri = "/cloud-manager/api/system/add"
var UserActivateUri = "/cloud-manager/api/fate/user/activate"
var AuthorityInstitutions = "/cloud-manager/api/authority/institutions"
var AuthorityApply = "/cloud-manager/api/authority/apply"
var AuthorityApplied = "/cloud-manager/api/authority/applied"
var AuthorityResults = "/cloud-manager/api/authority/results"
var FunctionAllUri = "/cloud-manager/api/function/find/all/fateManager"
var OtherSiteUri = "/cloud-manager/api/site/page/fateManager"
var ApprovedUri = "/cloud-manager/api/authority/institutions/approved"

type App struct {
	PageSize        int
	PrefixUrl       string
	RuntimeRootPath string
	LogSavePath     string
	LogSaveName     string
	LogFileExt      string
	TimeFormat      string
	Debug           bool
	WebBug          bool
	ProxyUrl        string
	IfProxy         bool
}

var AppSetting = &App{}

type Server struct {
	RunMode      string
	HttpPort     int
	ReadTimeout  time.Duration
	WriteTimeout time.Duration
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
	ExchangeIp               string
	ExchangePort             int
	FlowPort                 int
	RollsitePort             int
	SudoTag                  bool
	NodeManager              int
	Registry                 string
	SessionProcessorsPerNode int
	TestPartyId              int
}

var KubenetesSetting = &Kubenetes{}

type Schedule struct {
	IpManager int
	Heart     int
	Job       int
	Test      int
}

var ScheduleSetting = &Schedule{}

var cfg *ini.File

func Setup() {
	var err error
	cfg, err = ini.Load("conf/app.ini")
	if err != nil {
		log.Fatalf("setting.Setup, fail to parse 'conf/app.ini': %v", err)
	}

	mapTo("app", AppSetting)
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
