#
# Copyright 2021 The FATE Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import os
from fate_manager.utils import log
from fate_manager.utils.conf_utils import get_base_config, get_project_base_directory


# DB
DATABASE = get_base_config('database', {})


# log
log.LoggerFactory.LEVEL = 10
# {CRITICAL: 50, FATAL:50, ERROR:40, WARNING:30, WARN:30, INFO:20, DEBUG:10, NOTSET:0}
STAT_LOG_DIR = os.path.join(get_project_base_directory(), 'logs', 'fate_manager')
STAT_LOGGER_NAME = "fate_manager_stat"
log.LoggerFactory.set_directory(STAT_LOG_DIR)
stat_logger = log.getLogger(STAT_LOGGER_NAME)
request_cloud_logger = log.getLogger('request_cloud')
request_flow_logger = log.getLogger('request_fate_flow')
site_service_logger = log.getLogger('site_service')
login_service_logger = log.getLogger('login_service')
user_logger = log.getLogger('user_service')
monitor_logger = log.getLogger('monitor_service')

ROLL_SITE_KEY = "rollsite"
ROLL_PART_Id = "100"

IP = get_base_config('fate_manager', {}).get('host', '127.0.0.1')
PORT = get_base_config('fate_manager', {}).get('http_port', 9080)
API_VERSION = 'v1'

FATE_FLOW_SETTINGS = {
    "QueryJob": "/v1/job/query",
    "QueryFateVersion": "/v1/version/get"
}

HYPERION_SETTINGS = {
    "ConnectUri": "/ansible/v1/server/status",
    "JobSubmitUri": "/ansible/v1/job/submit",
    "JobQueryUri": "/ansible/v1/job/query",
    "PackageQueryUri": "/ansible/v1/package/remote",
    "PackageDownUri": "/ansible/v1/package/download",
    "LocalUploadUri": "/ansible/v1/package/local",
    "LogUri": "/ansible/v1/server/log",
    "AutoTestUri": "/ansible/v1/test",
}


CLOUD_URL= {
    "SiteQueryUri": "/cloud-manager/api/site/findOneSite/fateManager",
    # "ActivateUri": "/cloud-manager/api/site/activate",
    "IpAcceptUri": "/cloud-manager/api/site/ip/accept",
    "IpQueryUri": "/cloud-manager/api/site/ip/query",
    "CheckUri": "/cloud-manager/api/site/checkUrl",
    "FederationUri": "/cloud-manager/api/federation/findOrganization",
    "UpdateVersionUri": "/cloud-manager/api/site/fate/version",
    "CheckAuthorityUri": "/cloud-manager/api/site/checkAuthority/fateManager/v3",
    "CheckWebUri": "/cloud-manager/api/site/checkWeb",
    "SystemAddUri": "/cloud-manager/api/system/add",
    # "UserActivateUri": "/cloud-manager/api/fate/user/activate",
    "AuthorityInstitutions": "/cloud-manager/api/authority/institutions",
    "AuthorityApply": "/cloud-manager/api/authority/apply",
    "AuthorityApplied": "/cloud-manager/api/authority/applied",
    "FunctionAllUri": "/cloud-manager/api/function/find/all/fateManager",
    "OtherSiteUri": "/cloud-manager/api/site/page/fateManager",
    "ApprovedUri": "/cloud-manager/api/authority/institutions/approved",
    "MyApprovedUri": "/cloud-manager/api/authority/institutions/self/approved",
    "CheckPartyUri": "/cloud-manager/api/authority/check/partyId",
    "SystemHeartUri": "/cloud-manager/api/system/heart",
    "MonitorPushUri": "/cloud-manager/api/job/v3/push",
    "ProductVersionUri": "/cloud-manager/api/product/page/fatemanager",
    "ExchangeUri": "/cloud-manager/api/exchange/exchange/page/fatemanager",
    "GetApplyListUri": "/cloud-manager/api/authority/findPendingApply",
    "ApplyLog": "/cloud-manager/api/authority/history/fateManager",
    "OrganizationQueryUri": "/cloud-manager/api/fate/user/find/page",

    "ActivateUri": "/cloud-manager/api/site/activate/v2",
    "ActivateUriInfo": "/cloud-manager/api/site/activate/query/details",
    "UserActivate": "/cloud-manager/api/fate/user/activate/v2",
    "UpdateIpQueryUri": "/cloud-manager/api/site/ip/update/query",
}

CLOUD_SITE_SIGNATURE = ["CheckUri", "ActivateUri", "SiteQueryUri", "FederationUri", "UpdateVersionUri", "IpAcceptUri",
                        "CheckAuthorityUri", "IpQueryUri", "SystemHeartUri","ActivateUriNew","ActivateUriInfo",]
CLOUD_INSTITUTION_SIGNATURE = ["UserActivateUri", "FunctionAllUri", "ApprovedUri", "MyApprovedUri", "OtherSiteUri",
                               "ExchangeUri", "AuthorityInstitutions", "AuthorityApply", "CheckPartyUri",
                               "AuthorityApplied", "GetApplyListUri", "ApplyLog", "MonitorPushUri",
                               "OrganizationQueryUri","UpdateIpQueryUri","UserActivate",
                               ]

# user token
EXPIRE_TIME = 30 * 60 * 1000  # ms

TASK_DETECTOR_INTERVAL = 30 * 1000  # ms
JOB_DETECTOR_INTERVAL = 30 * 60 * 1000  # ms
DEFAULT_GRPC_TIMEOUT = 30 * 1000  # ms