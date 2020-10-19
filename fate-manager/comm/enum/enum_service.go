package enum

import "fate.manager/entity"

const (
	E_DeployStatus        = "deployStatus"
	E_EditStatus          = "editStatus"
	E_FederatedStatus     = "federatedStatus"
	E_IsValid             = "isValid"
	E_JobType             = "jobType"
	E_LogDeal             = "logDeal"
	E_ProductType         = "productType"
	E_PullStatus          = "pullStatus"
	E_ReadStatus          = "readStatus"
	E_RoleType            = "roleType"
	E_SiteRunStatus       = "siteRunStatus"
	E_SiteStatus          = "siteStatus"
	E_TestItemType        = "testItemType"
	E_TestStatus          = "testStatus"
	E_UpdateStatus        = "updateStatus"
	E_PageStatus          = "pageStatus"
	E_JobStatus           = "jobStatus"
	E_InitStatus          = "initStatus"
	E_ActionType          = "actionType"
	E_ClickType           = "clickType"
	E_ToyTestOnlyType     = "toyTestOnlyType"
	E_ToyTestOnlyTypeRead = "toyTestOnlyTypeRead"
	E_UserRole            = "userRole"
	E_PermissionType      = "permissionType"
)

func GetEnumNameDropDownList() []string {
	var enumNameList []string
	enumNameList = append(enumNameList, E_DeployStatus)
	enumNameList = append(enumNameList, E_EditStatus)
	enumNameList = append(enumNameList, E_FederatedStatus)
	enumNameList = append(enumNameList, E_IsValid)
	enumNameList = append(enumNameList, E_JobType)
	enumNameList = append(enumNameList, E_LogDeal)
	enumNameList = append(enumNameList, E_ProductType)
	enumNameList = append(enumNameList, E_PullStatus)
	enumNameList = append(enumNameList, E_ReadStatus)
	enumNameList = append(enumNameList, E_RoleType)
	enumNameList = append(enumNameList, E_SiteRunStatus)
	enumNameList = append(enumNameList, E_SiteStatus)
	enumNameList = append(enumNameList, E_TestItemType)
	enumNameList = append(enumNameList, E_TestStatus)
	enumNameList = append(enumNameList, E_UpdateStatus)
	enumNameList = append(enumNameList, E_PageStatus)
	enumNameList = append(enumNameList, E_JobStatus)
	enumNameList = append(enumNameList, E_InitStatus)
	enumNameList = append(enumNameList, E_ActionType)
	enumNameList = append(enumNameList, E_ClickType)
	enumNameList = append(enumNameList, E_ToyTestOnlyType)
	enumNameList = append(enumNameList, E_ToyTestOnlyTypeRead)
	enumNameList = append(enumNameList, E_UserRole)
	enumNameList = append(enumNameList, E_PermissionType)

	return enumNameList
}

func GetEnumNameInfo(enumName string) []entity.IdPair {
	var idPair []entity.IdPair
	if enumName == E_DeployStatus {
		idPair = GetDeployStatusList()
	} else if enumName == E_EditStatus {
		idPair = GetEditList()
	} else if enumName == E_FederatedStatus {
		idPair = GetFederatedStatusList()
	} else if enumName == E_IsValid {
		idPair = GetIsValidList()
	} else if enumName == E_JobType {
		idPair = GetJobTypeList()
	} else if enumName == E_LogDeal {
		idPair = GetLogDealList()
	} else if enumName == E_ProductType {
		idPair = GetProductTypeList()
	} else if enumName == E_PullStatus {
		idPair = GetPullStatusList()
	} else if enumName == E_ReadStatus {
		idPair = GetReadStatusList()
	} else if enumName == E_RoleType {
		idPair = GetRoleTypeList()
	} else if enumName == E_SiteRunStatus {
		idPair = GetSiteRunStatusList()
	} else if enumName == E_SiteStatus {
		idPair = GetSiteStatusList()
	} else if enumName == E_TestItemType {
		idPair = GetTestItemTypeList()
	} else if enumName == E_TestStatus {
		idPair = GetTestStatusList()
	} else if enumName == E_UpdateStatus {
		idPair = GetUpdateStatusList()
	} else if enumName == E_PageStatus {
		idPair = GetPageStatusList()
	} else if enumName == E_JobStatus {
		idPair = GetJobStatusList()
	} else if enumName == E_InitStatus {
		idPair = GetInitStatusList()
	} else if enumName == E_ActionType {
		idPair = GetActionTypeList()
	} else if enumName == E_ClickType {
		idPair = GetClickTypeList()
	} else if enumName == E_ToyTestOnlyType {
		idPair = GetToyTestOnlyTypeList()
	} else if enumName == E_ToyTestOnlyTypeRead {
		idPair = GetToyTestOnlyTypeReadList()
	} else if enumName == E_UserRole {
		idPair = GetUserRoleList()
	} else if enumName == E_PermissionType {
		idPair = GetPermissionTypeList()
	} else {
		return nil
	}
	return idPair
}
