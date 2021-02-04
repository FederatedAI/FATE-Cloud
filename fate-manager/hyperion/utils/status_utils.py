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
import argparse
import os
import subprocess
from settings import stat_logger, PRE_CHECK_ITEM, LOG_LINE_LIMIT
from utils import job_utils, file_utils

ANSIBLE_SHELL = 'ansible all -i "{}," -m shell -a "{}"'
FATE_ROOT = '/data/projects/fate'


def if_exists(host: str, fp: str):
    cmd = f"ansible all -i '{host},' -m shell -a 'ls {fp}'"
    result = subprocess.getoutput(cmd)
    return 'CHANGED' in result.split('\n')[0]


def distribute_pre_check_script(host: str, dst_dir: str = '/data/projects', mode: str = '0755') -> bool:
    stat_logger.info(f"Trying to distribute pre chcck scripts to host {host}, dst dir is: {dst_dir}")
    tool_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, 'tools'))
    cmd = f"ansible all -i '{host},' -m copy -a 'src={tool_dir}/check dest={dst_dir} mode={mode}'"
    result = subprocess.getoutput(cmd)
    stat_logger.info(f"subprocess result: {result}")
    stat_logger.info(f"Distribute pre chcck scripts to host {host} {'success' if 'CHANGED' in result or 'SUCCESS' in result else 'failed'}")
    return 'CHANGED' in result or 'SUCCESS' in result


def distribute_pre_check_task(host: str, task_name: str, script_dir: str = '/data/projects/check') -> dict:
    stat_logger.info(f"Trying to distribute pre check task to host {host}, task: {task_name}")
    allow_check = PRE_CHECK_ITEM
    if task_name not in allow_check:
        raise Exception(f"check task {task_name} not in allow check list, allow list: {allow_check}")
    cmd = f'ansible all -i "{host}," -m shell -a "{script_dir}/{task_name}.sh"'
    result = subprocess.getoutput(cmd)
    res_list = result.split('\n')
    data = []
    if 'CHANGED' in res_list[0] or 'SUCCESS' in res_list[0]:
        stat_logger.info(f"executed pre check on host {host} successfully.")
        for item in res_list[1:]:
            item_list = item.split(': ')
            data.append(dict(zip(['name', 'status', 'details'], item_list)))
        return {'ip': host, 'list': data}
    stat_logger.info(f"executed pre check on host {host} failed. Details: {result}")
    return {}


def distribute_status_check_task(host: str, module_name: str = None, supervisor_dir='/data/projects/common/supervisord') -> dict:
    if module_name:
        # TODO acquire by fate version
        allow_modules = ['fateflow', 'eggroll', 'clustermanager', 'rollsite', 'nodemanager', 'fateboard', 'mysql']
        if module_name not in allow_modules:
            raise Exception(f"status check module {module_name} not in allow check list, allow list: {allow_modules}")
    stat_logger.info(f"Trying to distribute status check task to host {host}, module: {module_name if module_name else 'all'}")
    if module_name:
        cmd = f'ansible all -i "{host}," -m shell -a "sh {supervisor_dir}/service.sh status fate-{module_name}"'
    else:
        cmd = f'ansible all -i "{host}," -m shell -a "sh {supervisor_dir}/service.sh status all"'
    result = subprocess.getoutput(cmd)
    res_list = result.split('\n')
    data = []
    if 'CHANGED' in res_list[0] or 'SUCCESS' in res_list[0]:
        stat_logger.info(f"executed status check on host {host} successfully.")
        for item in res_list[1:]:
            item_list = item.split()
            data.append(dict(zip(['name', 'status', 'uptime'], [item_list[0].replace('fate-', ''), item_list[1].lower(), ' '.join(item_list[2:])])))
        return {'ip': host, 'list': data}
    stat_logger.info(f"executed status check on host {host} failed. Details: {result}")
    return {}


def toy_test(host, guest_party_id, host_party_id, work_mode):
    shell_cmd = f'nohup python {FATE_ROOT}/python/examples/toy_example/run_toy_example.py {guest_party_id} {host_party_id} {work_mode} > {FATE_ROOT}/toy_test.log &'
    return execute_test(host, shell_cmd, 'toy')


def single_test(host, party_id):
    shell_cmd = f'nohup python {FATE_ROOT}/python/examples/toy_example/run_toy_example.py {party_id} {party_id} 1 > {FATE_ROOT}/single_test.log &'
    return execute_test(host, shell_cmd, 'single')


def upload_data_for_min_test(host):
    shell_cmd = f'nohup python {FATE_ROOT}/python/examples/scripts/upload_default_data.py -m 1 > {FATE_ROOT}/upload_test.log &'
    return execute_test(host, shell_cmd, 'upload')


def min_test(host, guest_party_id, host_party_id, arbiter_party_id, test_type):
    shell_cmd = f'nohup python {FATE_ROOT}/python/examples/min_test_task/run_task.py -m 1 -gid {guest_party_id} -hid {host_party_id} -aid {arbiter_party_id} -f {test_type} > {FATE_ROOT}/{test_type}_test.log '
    return execute_test(host, shell_cmd, test_type)


def execute_test(host, shell_cmd, test_type):
    process_cmd = [
        'python3', __file__,
        '--host', host,
        '--shell_cmd', shell_cmd,
        '--test_type', test_type
    ]
    log_dir = os.path.join(file_utils.get_project_base_directory(), 'logs', host, test_type)
    job_utils.run_subprocess(config_dir=log_dir, process_cmd=process_cmd, log_dir=log_dir)
    return 0, f'submit {test_type} test success'


def run_test():
    parser = argparse.ArgumentParser()
    parser.add_argument('--host', required=True, type=str)
    parser.add_argument('--shell_cmd', required=True, type=str)
    parser.add_argument('--test_type', required=True, type=str)
    args = parser.parse_args()

    if not if_exists(args.host, f'{FATE_ROOT}/python/examples'):
        args.shell_cmd = args.shell_cmd.replace('/python/examples', '/examples')
    if if_exists(args.host, f'{FATE_ROOT}/init_env.sh'):
        shell_cmd = f"source {FATE_ROOT}/init_env.sh && " + args.shell_cmd
    else:
        shell_cmd = f"source {FATE_ROOT}/bin/init_env.sh && " + args.shell_cmd
    cmd = ANSIBLE_SHELL.format(args.host, shell_cmd)
    result = subprocess.getoutput(cmd)
    stat_logger.info(f'{args.test_type}_test response: {result}')



def get_test_log(host, test_type, limit=LOG_LINE_LIMIT):
    shell_cmd = f'tail -{limit} {FATE_ROOT}/{test_type}_test.log'
    cmd = ANSIBLE_SHELL.format(host, shell_cmd)
    result = subprocess.getoutput(cmd)
    res_list = result.split('\n')
    if 'CHANGED' in res_list[0] or 'SUCCESS' in res_list[0]:
        stat_logger.info(f"execute query log of {test_type} test on host {host} successfully.")
        return 0, 'success', res_list[1:]
    stat_logger.error(f"execute query log of {test_type} test on host {host} failed. Details: {result}")
    return 100, 'failed', []


if __name__ == '__main__':
    run_test()