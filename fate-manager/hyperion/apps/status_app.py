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
from utils import status_utils
from utils.job_utils import check_config
from operations.job_saver import JobSaver
from utils.api_utils import get_json_result
from utils.status_utils import distribute_status_check_task

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/status', methods=['POST'])
def status_check():
    request_data = request.json
    required_parameters = ['party_id']
    check_config(request_data, required_parameters)
    if request_data.get('module_name'):
        retcode, retmsg, data = do_status_check(party_id=request_data['party_id'], module_name=request_data['module_name'])
    else:
        retcode, retmsg, data = do_status_check(party_id=request_data['party_id'])
    return get_json_result(retcode=retcode, retmsg=retmsg, data=data)


@manager.route('/toy', methods=['POST'])
def toy_test():
    request_data = request.json
    required_parameters = ['host', 'guest_party_id', 'host_party_id', 'work_mode']
    check_config(request_data, required_parameters)
    retcode, retmsg = status_utils.toy_test(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg)


@manager.route('/upload', methods=['POST'])
def upload_data():
    request_data = request.json
    required_parameters = ['host']
    check_config(request_data, required_parameters)
    retcode, retmsg = status_utils.upload_data_for_min_test(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg)


@manager.route('/normal', methods=['POST'])
def normal_test():
    request_data = request.json
    required_parameters = ['host', 'guest_party_id', 'host_party_id', 'arbiter_party_id']
    check_config(request_data, required_parameters)
    request_data['test_type'] = 'normal'
    retcode, retmsg = status_utils.min_test(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg)


@manager.route('/fast', methods=['POST'])
def fast_test():
    request_data = request.json
    required_parameters = ['host', 'guest_party_id', 'host_party_id', 'arbiter_party_id']
    check_config(request_data, required_parameters)
    request_data['test_type'] = 'fast'
    retcode, retmsg = status_utils.min_test(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg)


@manager.route('/single', methods=['POST'])
def single_test():
    request_data = request.json
    required_parameters = ['host', 'party_id']
    check_config(request_data, required_parameters)
    retcode, retmsg = status_utils.single_test(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg)


@manager.route('/log', methods=['POST'])
def query_log():
    request_data = request.json
    required_parameters = ['host', 'test_type']
    check_config(request_data, required_parameters)
    retcode, retmsg, data = status_utils.get_test_log(**request_data)
    return get_json_result(retcode=retcode, retmsg=retmsg, data=data)


def do_status_check(party_id, module_name=None):
    parties = JobSaver.query_party(party_id=party_id, reverse=True)
    if not parties:
        return 100, f"can not find info of party with party id {party_id}", {}
    else:
        party_info = parties[0].to_json(filters=['modules', 'version'])
        modules = party_info.get('f_modules', {}).get('data', [])
        for module in modules:
            if module.get('name') == 'supervisor':
                host = module.get('ips')[0]
                break
        else:
            return 100, f"can not found module supervisor on party {party_id}", {}
        if module_name:
            result = distribute_status_check_task(host=host, module_name=module_name)
        else:
            result = distribute_status_check_task(host=host)
        if result and result.get('list'):
            for item in result.get('list'):
                item.update({'version': party_info['f_version']})
            result['version'] = party_info['f_version']
            return 0, "check module status successfully", result
        return 100, "check module status failed, please check logs/ansible/ansible_stat.log", {}
