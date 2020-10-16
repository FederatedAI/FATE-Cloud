package routers

import (
	. "fate.manager/controllers"
	_ "fate.manager/docs"
	"github.com/gin-gonic/gin"
	ginSwagger "github.com/swaggo/gin-swagger"
	"github.com/swaggo/gin-swagger/swaggerFiles"
	"net/http"
)

func InitRouter() *gin.Engine {
	router := gin.Default()
	router.GET("/fate-manager/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	login := router.Group("/fate-manager/api/login")
	{
		login.POST("/login", SignIn)      //http://localhost:9090/fate-manager/api/login/login
		login.POST("/logout", SignOut)    //http://localhost:9090/fate-manager/api/login/logout
		login.POST("/activate", Activate) //http://localhost:9090/fate-manager/api/login/activate
		login.POST("/checkjwt", CheckJwt) //http://localhost:9090/fate-manager/api/login/checkjwt
	}

	//Site Manager
	sites := router.Group("/fate-manager/api/site").Use(JWT())
	{
		sites.GET("", GetHomeSiteList)                   //http://localhost:9090/fate-manager/api/site
		sites.GET("/other", GetOtherSiteList)            //http://localhost:9090/fate-manager/api/site/other
		sites.GET("/fatemanager", GetFateManagerList)    //http://localhost:9090/fate-manager/api/site/fatemanager
		sites.POST("/register", Register)                //http://localhost:9090/fate-manager/api/site/register
		sites.POST("/info", GetSiteDetail)               //http://localhost:9090/fate-manager/api/site/111
		sites.POST("/update", UpdateSite)                //http://localhost:9090/fate-manager/api/site/update
		sites.POST("/checkUrl", CheckRegisterUrl)        //http://localhost:9090/fate-manager/api/site/checkurl
		sites.POST("/checksite", CheckSite)              //http://localhost:9090/fate-manager/api/site/checksite
		sites.POST("/secretinfo", GetSecretInfo)         //http://localhost:9090/fate-manager/api/site/secretinfo
		sites.POST("/telnet", TelnetSiteIp)              //http://localhost:9090/fate-manager/api/site/telnet
		sites.POST("/readmsg", ReadChangeMsg)            //http://localhost:9090/fate-manager/api/site/readmsg
		sites.POST("/getmsg", GetChangeMsg)              //http://localhost:9090/fate-manager/api/site/getmsg
		sites.GET("/institutions", GetApplyInstitutions) //http://localhost:9090/fate-manager/api/site/institutions
		sites.POST("/applysite", ApplySites)             //http://localhost:9090/fate-manager/api/site/applysite
		sites.GET("/queryapply", QueryApplySites)        //http://localhost:9090/fate-manager/api/site/queryapply
		sites.POST("/readapplysite", ReadApplySites)     //http://localhost:9090/fate-manager/api/site/readapplysite
		//sites.POST("/function", GetFunction)             //http://localhost:9090/fate-manager/api/site/function
		sites.POST("/updateVersion", UpdateComponentVersion) //http://localhost:9090/fate-manager/api/site/updateVersion
		sites.GET("applylog", GetApplyLog)                   //http://localhost:9090/fate-manager/api/site/applylog
	}
	router.POST("/fate-manager/api/site/function", GetFunction)     //http://localhost:9090/fate-manager/api/site/function
	router.GET("/fate-manager/api/site/functionread", FunctionRead) //http://localhost:9090/fate-manager/api/site/functionread

	//All DropDown List
	dropDownList := router.Group("/fate-manager/api/dropdown").Use(JWT())
	{
		dropDownList.GET("/federation", GetFederationDropDownList)  //http://localhost:9090/fate-manager/api/dropdown/federation
		dropDownList.GET("/site", GetSiteDropDownList)              //http://localhost:9090/fate-manager/api/dropdown/site
		dropDownList.GET("/version", GetFateVersionDropDownList)    //http://localhost:9090/fate-manager/api/dropdown/version
		dropDownList.GET("/enumname", GetEnumNameDropDownList)      //http://localhost:9090/fate-manager/api/dropdown/enumname
		dropDownList.POST("/enuminfo", GetEnumNameInfo)             //http://localhost:9090/fate-manager/api/dropdown/enuminfo
		dropDownList.GET("/mysql", GetMysqlVersionList)             //http://localhost:9090/fate-manager/api/dropdown/mysql
		dropDownList.GET("/cluster", GetClusterManagerVersionList)  //http://localhost:9090/fate-manager/api/dropdown/cluster
		dropDownList.GET("/node", GetNodeManagerVersionList)        //http://localhost:9090/fate-manager/api/dropdown/node
		dropDownList.GET("/rollsite", GetRollSiteVersionList)       //http://localhost:9090/fate-manager/api/dropdown/rollsite
		dropDownList.GET("/fateflow", GetFateFlowVersionList)       //http://localhost:9090/fate-manager/api/dropdown/fateflow
		dropDownList.GET("/fateboard", GetFateBoardVersionList)     //http://localhost:9090/fate-manager/api/dropdown/fateboard
		dropDownList.GET("/fateserving", GetFateServingVersionList) //http://localhost:9090/fate-manager/api/dropdown/fateserving
	}
	//Manager,Service Managment
	services := router.Group("/fate-manager/api/service").Use(JWT())
	{
		services.POST("/info", GetService)                 //http://localhost:9090/fate-manager/api/service/info
		services.POST("/action", DoAcation)                //http://localhost:9090/fate-manager/api/service/action
		services.POST("/log", GetLog)                      //http://localhost:9090/fate-manager/api/service/log
		services.POST("/installstatus", InstallStatus)     //http://localhost:9090/fate-manager/api/service/installstatus
		services.POST("/connectkubefate", ConnectKubeFate) //http://localhost:9090/fate-manager/api/service/connectkubefate
		services.POST("/overview", GetServiceOverview)     //http://localhost:9090/fate-manager/api/service/overview
		services.POST("/upgradelist", UpgradeFateList)     //http://localhost:9090/fate-manager/api/service/upgradelist
		services.POST("/pagestatus", GetPageStatus)        //http://localhost:9090/fate-manager/api/service/pagestatus
	}
	//FATE Deploy
	deploys := router.Group("/fate-manager/api/deploy").Use(JWT())
	{
		deploys.GET("/prepare", GetPrepare)                   //http://localhost:9090/fate-manager/api/deploy/prepare
		deploys.POST("/pulllist", GetPullComponentList)       //http://localhost:9090/fate-manager/api/deploy/pulllist
		deploys.POST("/installlist", GetInstallComponentList) //http://localhost:9090/fate-manager/api/deploy/installlist
		deploys.POST("/pull", Pull)                           //http://localhost:9090/fate-manager/api/deploy/pull
		deploys.POST("/commit", CommitImagePull)              //http://localhost:9090/fate-manager/api/deploy/commit
		deploys.POST("/install", Install)                     //http://localhost:9090/fate-manager/api/deploy/install
		deploys.POST("/upgrade", Upgrade)                     //http://localhost:9090/fate-manager/api/deploy/upgrade
		deploys.POST("/testlist", GetAutoTestList)            //http://localhost:9090/fate-manager/api/deploy/testlist
		deploys.POST("/autotest", AutoTest)                   //http://localhost:9090/fate-manager/api/deploy/autotest
		deploys.POST("/update", Update)                       //http://localhost:9090/fate-manager/api/deploy/update
		deploys.POST("/testresult", TestResult)               //http://localhost:9090/fate-manager/api/deploy/testresult
		deploys.POST("/click", Click)                         //http://localhost:9090/fate-manager/api/deploy/clcik
		deploys.POST("/testresultread", ToyResultRead)        //http://localhost:9090/fate-manager/api/deploy/testresultread
		deploys.POST("/testlog", GetTestLog)                  //http://localhost:9090/fate-manager/api/deploy/testlog
		deploys.POST("/fateboard", GetFateBoardUrl)           //http://localhost:9090/fate-manager/api/deploy/fateboard
		deploys.POST("/version", GetInstallVersion)           //http://localhost:9090/fate-manager/api/deploy/version
	}

	//User
	user := router.Group("/fate-manager/api/user").Use(JWT())
	{
		user.GET("/info", GetUserInfo)                     //http://localhost:9090/fate-manager/api/user/info
		user.POST("/list", GetUserList)                    //http://localhost:9090/fate-manager/api/user/list
		user.POST("/accesslist", GetUserAccessList)        //http://localhost:9090/fate-manager/api/user/accesslist
		user.POST("/add", AddUser)                         //http://localhost:9090/fate-manager/api/user/add
		user.POST("/delete", DeleteUser)                   //http://localhost:9090/fate-manager/api/user/delete
		user.POST("/edit", EditUser)                       //http://localhost:9090/fate-manager/api/user/edit
		user.POST("/sitelist", UserSiteList)               //http://localhost:9090/fate-manager/api/user/sitelist
		user.POST("siteinfouserlist", GetSiteInfoUserList) //http://localhost:9090/fate-manager/api/user/siteinfouserlist
	}

	//Web
	router.LoadHTMLGlob("./fate-manager/static/*.html")
	router.LoadHTMLFiles("./fate-manager/static/*/*")
	router.StaticFS("./fate-manager/static", http.Dir("./fate-manager/static"))
	router.StaticFile("/fate-manager/", "./fate-manager/static/index.html")
	return router
}
