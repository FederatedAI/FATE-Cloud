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
from flask import Flask, request
from settings import stat_logger
from utils.api_utils import get_json_result
from utils import status_utils

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/pre', methods=['POST'])
def pre_check():
    data = request.json
    result = []
    for ip_addr in data.get('managerNode', []):
        if data.get('check_name'):
            check_name = data['check_name']
        else:
            check_name = 'check'
        if not status_utils.if_exists(host=ip_addr, fp=f'/data/projects/check/{check_name}.sh'):
            distribute_res = status_utils.distribute_pre_check_script(host=ip_addr)
            if not distribute_res:
                result.append({'ip': ip_addr, 'list': []})
                continue
        result.append(status_utils.distribute_pre_check_task(host=ip_addr, task_name=check_name))
    return get_json_result(retmsg='pre check success', data=result)
