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

const (
	UNKONWN        = -1
	SUCCESS        = 0
	ERROR          = 500
	INVALID_PARAMS = 400

	ERROR_GET_SITE_FAIL                      = 10001
	ERROR_GET_SITE_LIST_FAIL                 = 10002
	ERROR_REGISTER_FEDERATED_FAIL            = 10003
	ERROR_UPDATE_SITE_FAIL                   = 10004
	ERROR_PARTY_ID_NOT_EXIST                 = 10005
	ERROR_SITE_NOT_JOINED                    = 10006
	ERROR_SITE_NOT_ALLOW_UPDATE              = 10007
	ERROR_FEDERATED_NOT_EXIST                = 10008
	ERROR_PARSE_JSON_ERROR                   = 10009
	ERROR_GET_FEDERATED_FAIL                 = 10010
	ERROR_CHECK_REGISTER_URL_FAIL            = 10011
	ERROR_HTTP_FAIL                          = 10012
	ERROR_GET_SECRET_INFO_FAIL               = 10013
	ERROR_CHECK_SITE_FAIL                    = 10014
	ERROR_CHECK_WEB_FAIL                     = 10015
	ERROR_GET_FEDERATION__DROPLIST_FAIL      = 10016
	ERROR_GET_SITE_DROP_LIST_FAIL            = 10017
	ERROR_GET_VERSION_DROP_LIST_FAIL         = 10018
	ERROR_GET_SERVICE_INFO_FAIL              = 10019
	ERROR_UPGRADE_ONE_FAIL                   = 10020
	ERROR_UPGRADE_ALL_FAIL                   = 10021
	ERROR_ACTION_FAIL                        = 10022
	ERROR_TOY_TEST_FAIL                      = 10023
	ERROR_GET_LOG_FAIL                       = 10024
	ERROR_CONNECT_KUBE_FATE_FAIL             = 10025
	ERROR_GET_PREPARE_FAIL                   = 10026
	ERROR_GET_PULL_COMPONENT_LIST_FAIL       = 10027
	ERROR_PULL_IMAGE_FAIL                    = 10028
	ERROR_GET_INSTALL_COMPONENT_LIST_FAIL    = 10029
	ERROR_INSTALL_ALL_FAIL                   = 10030
	ERROR_UPDATE_COMPONENT_FAIL              = 10031
	ERROR_GET_AUTO_TEST_LIST_FAIL            = 10032
	ERROR_AUTO_TEST_FAIL                     = 10033
	ERROR_READ_CHANGE_MSG_FAIL               = 10034
	ERROR_READ_STATUS_IS_NOT_EXISTS_FAIL     = 10035
	ERROR_COMPONENT_NOT_EXISTS_FAIL          = 10036
	ERROR_UPDATE_VERSION_LOW_CURRENT_FAIL    = 10037
	ERROR_COMMIT_PULL_IMAGE_FAIL             = 10038
	ERROR_SELECT_DB_FAIL                     = 10039
	ERROR_IMAGE_NOT_ALL_PULL_FAIL            = 10040
	ERROR_UPDATE_DB_FAIL                     = 10041
	ERROR_GET_SERVICE_OVERVIEW_FAIL          = 10042
	ERROR_GET_IP_CHANGE_FAIL                 = 10043
	ERROR_GET_UPGRADE_FATE_LIST_FAIL         = 10044
	ERROR_GET_PAGE_STATUS_FAIL               = 10045
	ERROR_VERSION_NO_LOWER_THAN_CURRENT_FAIL = 10046
	ERROR_GET_TOKEN_FAIL                     = 10047
	ERROR_PARTY_INSTALLED_FAIL               = 10048
	ERROR_GET_INSTALL_STATUS_FAIL            = 10049
	ERROR_PARTY_IS_INSTALLING_FAIL           = 10050
	ERROR_IP_NOT_COURRECT_FAIL               = 10051
	ERROR_CLICK_FAIL                         = 10052
	ERROR_TOY_TEST_ONLY_READ_FAIL            = 10053
	ERROR_TOY_TEST_ONLY_FAIL                 = 10054

	ERROR_SIGN_IN_FAIL                       = 10055
	ERROR_ACCOUNT_ACTIVATE_FAIL              = 10056
	ERROR_APPLY_SITES_FAIL                   = 10057
	ERROR_USER_ACCESS_LIST_FAIL              = 10058
	ERROR_EDIT_USER_FAIL                     = 10059
	ERROR_DELETE_USER_FAIL                   = 10060
	ERROR_ADD_USER_FAIL                      = 10061
	ERROR_GET_SITE_INFO_USER_LIST_FAIL       = 10062
	ERROR_SITE_MANAGER_NOT_ACTIVE_FAIL       = 10063
	ERROR_CONCACT_SITE_MANAGER_FAIL          = 10064

	ERROR_AUTH_CHECK_TOKEN_FAIL              = 10065
	ERROR_AUTH_CHECK_TOKEN_TIMEOUT           = 10066
	ERROR_AUTH_TOKEN                         = 10067
	ERROR_AUTH                               = 10068

	ERROR_USERNAME_OR_PASSWORD_FAIL          = 10069
	ERROR_USE_FAIL                           = 10070
	ERROR_QUERY_APPLY_SITES_FAIL             = 10071
	ERROR_UPDATE_APPLY_SITES_FAIL            = 10072
	ERROR_GET_FUNCTION_FAIL                  = 10073

	ERROR_CHECK_JWT_FAIL                     = 10074
	ERROR_SIGN_OUT_FAIL                      = 10075
	ERROR_GET_USER_INFO_FAIL                 = 10076
	ERROR_GET_OTHER_SITE_FAIL                = 10077
	ERROR_GET_FATE_MANAGER_LIST_FAIL         = 10078
	ERROR_USER_NOT_EXISTS_FAIL               = 10079
	ERROR_REFRESH_FATE_MANAGER_LIST_FAIL     = 10080
	ERROR_ADMIN_USER_NOT_EXISTS              = 10081
	ERROR_UPDATE_COMPONENT_VERSION_FAIL      = 10082
	ERROR_GET_APPLY_LOG_FAIL                 = 10083
	ERROR_UPDATE_INSTALL_LIST_FAIL           = 10084
	ERROR_PERMISSION_AUTH_FAIL               = 10085
	ERROR_NO_PARTY_PRIVILEGE_FAIL            = 10086
	ERROR_GET_ALLOW_PARTY_LIST_FAIL          = 10087
	ERROR_GET_MONITOR_TOTAL_FAIL             = 10088
	ERROR_GET_INSTITUTION_BASE_STATITCS_FAIL = 10089
	ERROR_GET_SITE_BASE_STTITCS_FAIL         = 10090

	ERROR_CONNECT_ANSIBLE_FAIL               = 10091
	ERROR_CHECK_SYSTEM_FAIL                  = 10092
	GET_CHECK_SYSYTEM_LIST_FAIL              = 10093
	ERROR_GET_DEPLOY_ANSIBLE_LIST_FAIL       = 10094
	ERROR_ANSIBLE_CONNECT_FIRST              = 10095
	ERROR_GET_MANAGER_IP_FAIL                = 10096
	ERROR_DEPLOY_ANSIBLE_FAIL                = 10097
	ERROR_LOACAL_UPLOAD_FAIL                 = 10098
	ERROR_AUTO_ACQUIRE_FAIL                  = 10099
)
