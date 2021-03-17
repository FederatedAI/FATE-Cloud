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
from operations.job_saver import JobSaver
from utils.api_utils import get_json_result
from utils.job_utils import get_server_log
from utils.status_utils import distribute_status_check_task

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/status', methods=['POST'])
def check_ansible():
    request_data = request.json
    if request_data.get('party_id'):
        # TODO consider case that there are more than one records of specified party
        parties = JobSaver.query_party(party_id=request_data.get('party_id'))
        if parties:
            party_info = parties[0].to_json(filters=['modules', 'version', 'role'])
            modules = party_info.get('f_modules', {}).get('data', [])
            status = {}
            host = None
            for module in modules:
                if module.get('name') == 'supervisor':
                    host = module.get('ips')[0]
                    break
            if host:
                result = distribute_status_check_task(host=host)
                if result and result.get('list'):
                    for item in result.get('list'):
                        status[item['name']] = item['status']
            if status:
                for module in modules:
                    module['status'] = status.get(module['name'], None)
            return get_json_result(retmsg='Query party info successfully',
                                   data={'party_id': request_data.get('party_id'), 'list': modules, 'fate_version': party_info.get('f_version'), "role": party_info.get('f_role')})
        return get_json_result(retcode=0,
                               retmsg=f"can not found info party {request_data.get('party_id')} in database.")
    return get_json_result(data={'status': 'success'})


@manager.route('/log', methods=['POST'])
def get_log():
    request_data = request.json
    retcode, retmsg, data = get_server_log(level=request_data.get('level'))
    return get_json_result(retcode=retcode, retmsg=retmsg, data=data)
