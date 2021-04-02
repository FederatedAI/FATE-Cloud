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
package e

var MsgFlags = map[int]string{
	UNKONWN:                                  "unkonwn",
	SUCCESS:                                  "success",
	ERROR:                                    "fail",
	INVALID_PARAMS:                           "param error",
	ERROR_GET_SITE_FAIL:                      "get site failed",
	ERROR_GET_SITE_LIST_FAIL:                 "get site list failed",
	ERROR_REGISTER_FEDERATED_FAIL:            "register federated failed",
	ERROR_UPDATE_SITE_FAIL:                   "update site failed",
	ERROR_PARTY_ID_NOT_EXIST:                 "party id is not exists",
	ERROR_SITE_NOT_JOINED:                    "site is not joined",
	ERROR_SITE_NOT_ALLOW_UPDATE:              "site is note allow to update",
	ERROR_FEDERATED_NOT_EXIST:                "federated not exists",
	ERROR_PARSE_JSON_ERROR:                   "parse json error",
	ERROR_GET_FEDERATED_FAIL:                 "get federated failed",
	ERROR_CHECK_REGISTER_URL_FAIL:            "register url is unvalid",
	ERROR_HTTP_FAIL:                          "http post || get failed",
	ERROR_GET_SECRET_INFO_FAIL:               "get site secret info failed",
	ERROR_CHECK_SITE_FAIL:                    "check site failed",
	ERROR_CHECK_WEB_FAIL:                     "There is an error when connect to the ip!",
	ERROR_GET_FEDERATION__DROPLIST_FAIL:      "Get Federation List Failed",
	ERROR_GET_SITE_DROP_LIST_FAIL:            "Get Site Drop List Failed",
	ERROR_GET_VERSION_DROP_LIST_FAIL:         "Get Version Drop List Failed",
	ERROR_GET_SERVICE_INFO_FAIL:              "Get Service Info Failed",
	ERROR_UPGRADE_ONE_FAIL:                   "Upgrade One Component Failed",
	ERROR_UPGRADE_ALL_FAIL:                   "Upgrade All Failed",
	ERROR_ACTION_FAIL:                        "Do Stopped Or Restart Action Failed",
	ERROR_TOY_TEST_FAIL:                      "Toy Test Failed",
	ERROR_GET_LOG_FAIL:                       "Get Log Failed",
	ERROR_CONNECT_KUBE_FATE_FAIL:             "Connect Kube Fate Failed",
	ERROR_GET_PREPARE_FAIL:                   "Get Prapare Failed",
	ERROR_GET_PULL_COMPONENT_LIST_FAIL:       "Get Pull Component List Failed",
	ERROR_PULL_IMAGE_FAIL:                    "Pull Image Failed",
	ERROR_GET_INSTALL_COMPONENT_LIST_FAIL:    "Get Install Site Componet List Failed",
	ERROR_INSTALL_ALL_FAIL:                   "Install All Componet Failed",
	ERROR_UPDATE_COMPONENT_FAIL:              "Update Component Address Failed",
	ERROR_GET_AUTO_TEST_LIST_FAIL:            "Get Auto Test List Failed",
	ERROR_AUTO_TEST_FAIL:                     "Auto Test Failed",
	ERROR_READ_CHANGE_MSG_FAIL:               "Read Change Msg Failed",
	ERROR_READ_STATUS_IS_NOT_EXISTS_FAIL:     "Read Status Is Not Exists",
	ERROR_COMPONENT_NOT_EXISTS_FAIL:          "Component Is Not Exists",
	ERROR_UPDATE_VERSION_LOW_CURRENT_FAIL:    "Update Version Do Not Lower Than Current Version",
	ERROR_COMMIT_PULL_IMAGE_FAIL:             "Commit Pull Image Faild",
	ERROR_SELECT_DB_FAIL:                     "Select Db Failed",
	ERROR_IMAGE_NOT_ALL_PULL_FAIL:            "Image Not All Pull",
	ERROR_UPDATE_DB_FAIL:                     "Update Db Failed",
	ERROR_GET_SERVICE_OVERVIEW_FAIL:          "Get Service OverView Fail",
	ERROR_GET_IP_CHANGE_FAIL:                 "Get Ip Change Fail",
	ERROR_GET_UPGRADE_FATE_LIST_FAIL:         "Get Upgrade Fate List Fail",
	ERROR_GET_PAGE_STATUS_FAIL:               "Get Page Status Fail",
	ERROR_VERSION_NO_LOWER_THAN_CURRENT_FAIL: "Fate Version No Lower Than Current",
	ERROR_GET_TOKEN_FAIL:                     "Get Token Fail",
	ERROR_PARTY_INSTALLED_FAIL:               "PartyId Has Installed",
	ERROR_GET_INSTALL_STATUS_FAIL:            "No Install Fate",
	ERROR_PARTY_IS_INSTALLING_FAIL:           "PartyId Is In Installing,Could Not Restart",
	ERROR_IP_NOT_COURRECT_FAIL:               "Ip Is Not Correct",
	ERROR_CLICK_FAIL:                         "Click Button Fail",
	ERROR_TOY_TEST_ONLY_READ_FAIL:            "Read Toy Test Only Fail",
	ERROR_TOY_TEST_ONLY_FAIL:                 "Toy Test Only Has Read",
	ERROR_SIGN_IN_FAIL:                       "Sign In Fail",
	ERROR_ACCOUNT_ACTIVATE_FAIL:              "Account Activate Fail",
	ERROR_APPLY_SITES_FAIL:                   "Apply Sites Fail",
	ERROR_USER_ACCESS_LIST_FAIL:              "Get User Access List Fail",
	ERROR_EDIT_USER_FAIL:                     "Edit User Fail",
	ERROR_DELETE_USER_FAIL:                   "Delete User Fail",
	ERROR_ADD_USER_FAIL:                      "Add User Fail",
	ERROR_GET_SITE_INFO_USER_LIST_FAIL:       "Get Site Info User List Fail",
	ERROR_SITE_MANAGER_NOT_ACTIVE_FAIL:       "Site Account Not Activate",
	ERROR_CONCACT_SITE_MANAGER_FAIL:          "Please contact the administrator to add permissions for you.",
	ERROR_AUTH_CHECK_TOKEN_FAIL:              "Token Auth Fail",
	ERROR_AUTH_CHECK_TOKEN_TIMEOUT:           "Token Expired",
	ERROR_AUTH_TOKEN:                         "Token Init Fail",
	ERROR_AUTH:                               "Token Fail",
	ERROR_USERNAME_OR_PASSWORD_FAIL:          "Username Or Password Is Fail",
	ERROR_USE_FAIL:                           "User Not Exists!",
	ERROR_QUERY_APPLY_SITES_FAIL:             "Query Apply Site Result Fail",
	ERROR_UPDATE_APPLY_SITES_FAIL:            "Update Apply Site Fail",
	ERROR_GET_FUNCTION_FAIL:                  "Get Function Fail",
	ERROR_CHECK_JWT_FAIL:                     "Check Jwt Fail",
	ERROR_SIGN_OUT_FAIL:                      "Sign Out Fail",
	ERROR_GET_USER_INFO_FAIL:                 "Get User Info Fail",
	ERROR_GET_OTHER_SITE_FAIL:                "Get Other Site Fail",
	ERROR_GET_FATE_MANAGER_LIST_FAIL:         "Get Apply Fate Manager List Fail",
	ERROR_USER_NOT_EXISTS_FAIL:               "User Not Exists",
	ERROR_REFRESH_FATE_MANAGER_LIST_FAIL:     "Refresh Fate Manager List Fail",
	ERROR_ADMIN_USER_NOT_EXISTS:              "Cloud Admin User Not Exists",
	ERROR_UPDATE_COMPONENT_VERSION_FAIL:      "Update Component Version Fail",
	ERROR_GET_APPLY_LOG_FAIL:                 "Get Apply Log Fail",
	ERROR_UPDATE_INSTALL_LIST_FAIL:           "Update Install List Fail",
	ERROR_PERMISSION_AUTH_FAIL:               "Permission Auth Fail",
	ERROR_NO_PARTY_PRIVILEGE_FAIL:            "No Party Privilege Fail",
	ERROR_GET_ALLOW_PARTY_LIST_FAIL:          "Get Allow Party List Fail",
	ERROR_GET_MONITOR_TOTAL_FAIL:            "Get Monitor Total Faild",
	ERROR_GET_INSTITUTION_BASE_STATITCS_FAIL:"Get Institution Based Statistics Fail",
	ERROR_GET_SITE_BASE_STTITCS_FAIL:        "Get Site Base Statistics Fail",
	ERROR_CONNECT_ANSIBLE_FAIL:              "Connect To Ansible Server Fail",
	ERROR_CHECK_SYSTEM_FAIL:                 "Check System Fail",
	GET_CHECK_SYSYTEM_LIST_FAIL:             "Get Chck Sytem List Fail",
	ERROR_GET_DEPLOY_ANSIBLE_LIST_FAIL:      "Get Deploy Ansible List Fail",
	ERROR_ANSIBLE_CONNECT_FIRST:             "Ansible Not Connect Fail",
	ERROR_GET_MANAGER_IP_FAIL:               "Get Manager Ip List Fail",
	ERROR_DEPLOY_ANSIBLE_FAIL:               "Deploy Ansible Fail",
	ERROR_LOACAL_UPLOAD_FAIL:                "Upload Local Fail",
	ERROR_AUTO_ACQUIRE_FAIL:                 "Auto Acquire Fail",
	GET_ANSIBLE_LIST_FAIL:                   "Get Ansible List Fail",
	ERROR_GET_EXCHANGE_INFO_FAIL:            "Get Exchange Info Fail",
}

func GetMsg(code int) string {
	msg, ok := MsgFlags[code]
	if ok {
		return msg
	}

	return MsgFlags[ERROR]
}
