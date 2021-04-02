class EnumItem:
    E_DeployStatus = "deployStatus"
    E_EditStatus = "editStatus"
    E_FederatedStatus = "federatedStatus"
    E_IsValid = "isValid"
    E_JobType = "jobType"
    E_LogDeal = "logDeal"
    E_ProductType = "productType"
    E_PullStatus = "pullStatus"
    E_ReadStatus = "readStatus"
    E_RoleType = "roleType"
    E_SiteRunStatus = "siteRunStatus"
    E_SiteStatus = "siteStatus"
    E_TestItemType = "testItemType"
    E_TestStatus = "testStatus"
    E_UpdateStatus = "updateStatus"
    E_PageStatus = "pageStatus"
    E_JobStatus = "jobStatus"
    E_InitStatus = "initStatus"
    E_ActionType = "actionType"
    E_ClickType = "clickType"
    E_ToyTestOnlyType = "toyTestOnlyType"
    E_ToyTestOnlyTypeRead = "toyTestOnlyTypeRead"
    E_UserRole = "userRole"
    E_PermissionType = "permissionType"
    E_ServiceStatus = "serviceStatus"
    E_DeployType = "deployType"
    E_AnsibleClickType = "ansibleClickType"

    @staticmethod
    def to_list():
        d = []
        for k, v in EnumItem.__dict__.items():
            if "E" not in k:
                continue
            d.append(v)
        return d