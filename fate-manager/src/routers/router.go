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
		login.POST("/login", SignIn)
		login.POST("/logout", SignOut)
		login.POST("/activate", Activate)
		login.POST("/checkjwt", CheckJwt)
	}

	//Site Manager
	sites := router.Group("/fate-manager/api/site").Use(JWT())
	{
		sites.GET("", GetHomeSiteList)
		sites.GET("/other", GetOtherSiteList)
		sites.GET("/fatemanager", GetFateManagerList)
		sites.POST("/register", Register)
		sites.POST("/info", GetSiteDetail)
		sites.POST("/update", UpdateSite)
		sites.POST("/checkUrl", CheckRegisterUrl)
		sites.POST("/checksite", CheckSite)
		sites.POST("/secretinfo", GetSecretInfo)
		sites.POST("/telnet", TelnetSiteIp)
		sites.POST("/readmsg", ReadChangeMsg)
		sites.POST("/getmsg", GetChangeMsg)
		sites.GET("/institutions", GetApplyInstitutions)
		sites.POST("/applysite", ApplySites)
		sites.GET("/queryapply", QueryApplySites)
		sites.POST("/readapplysite", ReadApplySites)
		sites.POST("/updateVersion", UpdateComponentVersion)
		sites.GET("applylog", GetApplyLog)
	}
	router.POST("/fate-manager/api/site/function", GetFunction)
	router.GET("/fate-manager/api/site/functionread", FunctionRead)

	//All DropDown List
	dropDownList := router.Group("/fate-manager/api/dropdown")//.Use(JWT())
	{
		dropDownList.GET("/federation", GetFederationDropDownList)
		dropDownList.GET("/site", GetSiteDropDownList)
		dropDownList.GET("/version", GetFateVersionDropDownList)
		dropDownList.GET("/enumname", GetEnumNameDropDownList)
		dropDownList.POST("/enuminfo", GetEnumNameInfo)
		dropDownList.GET("/mysql", GetMysqlVersionList)
		dropDownList.GET("/cluster", GetClusterManagerVersionList)
		dropDownList.GET("/node", GetNodeManagerVersionList)
		dropDownList.GET("/rollsite", GetRollSiteVersionList)
		dropDownList.GET("/fateflow", GetFateFlowVersionList)
		dropDownList.GET("/fateboard", GetFateBoardVersionList)
		dropDownList.GET("/fateserving", GetFateServingVersionList)
		dropDownList.POST("/componentversion",GetComponentVersionList)
	}
	//Manager,Service Managment
	services := router.Group("/fate-manager/api/service").Use(JWT())
	{
		services.POST("/info", GetService)
		services.POST("/action", DoAcation)
		services.POST("/log", GetLog)
		services.POST("/installstatus", InstallStatus)
		services.POST("/connectkubefate", ConnectKubeFate)
		services.POST("/overview", GetServiceOverview)
		services.POST("/upgradelist", UpgradeFateList)
		services.POST("/pagestatus", GetPageStatus)
	}
	//FATE Deploy
	deploys := router.Group("/fate-manager/api/deploy").Use(JWT())
	{
		deploys.GET("/prepare", GetPrepare)
		deploys.POST("/pulllist", GetPullComponentList)
		deploys.POST("/installlist", GetInstallComponentList)
		deploys.POST("/pull", Pull)
		deploys.POST("/commit", CommitImagePull)
		deploys.POST("/install", Install)
		deploys.POST("/upgrade", Upgrade)
		deploys.POST("/testlist", GetAutoTestList)
		deploys.POST("/autotest", AutoTest)
		deploys.POST("/update", Update)
		deploys.POST("/testresult", TestResult)
		deploys.POST("/click", Click)
		deploys.POST("/testresultread", ToyResultRead)
		deploys.POST("/testlog", GetTestLog)
		deploys.POST("/fateboard", GetFateBoardUrl)
		deploys.POST("/version", GetInstallVersion)
	}

	//User
	user := router.Group("/fate-manager/api/user").Use(JWT())
	{
		user.GET("/info", GetUserInfo)
		user.POST("/list", GetUserList)
		user.POST("/accesslist", GetUserAccessList)
		user.POST("/add", AddUser)
		user.POST("/delete", DeleteUser)
		user.POST("/edit", EditUser)
		user.POST("/sitelist", UserSiteList)
		user.POST("siteinfouserlist", GetSiteInfoUserList)
		user.POST("/userpartylist",GetLoginUserManagerList)
		user.POST("/allowpartylist",GetAllAllowPartyList)
		user.POST("/permmsionauth", PermissionAuthority)
		user.POST("/sublogin",SubLogin)
	}

	//Web
	router.LoadHTMLGlob("./fate-manager/static/*.html")
	router.LoadHTMLFiles("./fate-manager/static/*/*")
	router.StaticFS("./fate-manager/static", http.Dir("./fate-manager/static"))
	router.StaticFile("/fate-manager/", "./fate-manager/static/index.html")
	return router
}
