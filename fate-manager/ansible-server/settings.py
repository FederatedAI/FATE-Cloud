#
# Copyright 2020 The FATE Authors. All Rights Reserved.
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

from utils import log_utils
from utils.conf_utils import get_base_config, get_project_base_directory


DATABASE = get_base_config("database", {})
ALLOW_VERSION = ['1.4.0', '1.4.3', '1.4.5', '1.5.0']
CONCURRENT_NUM = 5
CONCURRENT_RETRY_NUM = 5
TEST_MODE = True

PRE_CHECK_ITEM = ['check_open_files', 'check_max_user_processes', 'check_mysql', 'check_swap',
                  'check_supervisor', 'check', 'check_supervisor_conf', 'check_mysql_root',
                  'check_fate_process', 'check_port', 'check_fate_root']


# Default Parameter
_ONE_DAY_IN_SECONDS = 60 * 60 * 24


# logger
log_utils.LoggerFactory.LEVEL = 10
# {CRITICAL: 50, FATAL:50, ERROR:40, WARNING:30, WARN:30, INFO:20, DEBUG:10, NOTSET:0}
STAT_LOG_DIR = os.path.join(get_project_base_directory(), 'logs', 'ansible')
STAT_LOGGER_NAME = "ansible_stat"
log_utils.LoggerFactory.set_directory(STAT_LOG_DIR)
stat_logger = log_utils.getLogger(STAT_LOGGER_NAME)
detect_logger = log_utils.getLogger("ansible_detect")


# Constants
ANSIBLE_SERVICE_NAME = 'ansible'
IP = get_base_config(ANSIBLE_SERVICE_NAME, {}).get("host", "127.0.0.1")
PORT = get_base_config(ANSIBLE_SERVICE_NAME, {}).get("http_port")
API_VERSION ='v1'


# HOST_LOG_LIMIT
LOG_LINE_LIMIT = 50
