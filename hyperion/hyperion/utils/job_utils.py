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
import importlib
import os
import subprocess
import sys
import errno
import datetime
import threading
import typing
import psutil
from db.db_models import DB, Job, Play
from entity.types import NoneKillStatus
from settings import stat_logger, LOG_LINE_LIMIT, STAT_LOG_DIR
from utils import file_utils
from utils.base_utils import json_loads
from utils.log_utils import schedule_logger
from settings import ALLOW_VERSION


class IdCounter(object):
    _lock = threading.RLock()

    def __init__(self, initial_value=0):
        self._value = initial_value

    def incr(self, delta=1):
        '''
        Increment the counter with locking
        '''
        with IdCounter._lock:
            self._value += delta
            return self._value


id_counter = IdCounter()


def generate_job_id():
    return '{}{}'.format(datetime.datetime.now().strftime("%Y%m%d%H%M%S%f"), str(id_counter.incr()))


def cleaning(signum, frame):
    sys.exit(0)


def wait_child_process(signum, frame):
    try:
        while True:
            child_pid, status = os.waitpid(-1, os.WNOHANG)
            if child_pid == 0:
                stat_logger.info('no child process was immediately available')
                break
            exitcode = status >> 8
            stat_logger.info('child process %s exit with exitcode %s', child_pid, exitcode)
    except OSError as e:
        if e.errno == errno.ECHILD:
            stat_logger.warning('current process has no existing unwaited-for child processes.')
        else:
            raise


def check_config(config: typing.Dict, required_parameters: typing.List):
    for parameter in required_parameters:
        if parameter not in config:
            raise AttributeError('configuration has no {} parameter'.format(parameter))


def get_job_configuration(job_id, plays=None):
    with DB.connection_context():
        if plays:
            jobs_run_conf = {}
            for play in plays:
                jobs = Job.select(Job.f_job_id, Job.f_job_conf).where(Job.f_job_id == play.f_job_id)
                if jobs:
                    job = jobs[0]
                    jobs_run_conf[job.f_job_id] = json_loads(job.f_job_conf)
                else:
                    jobs_run_conf[play.f_job_id] = {}
            return jobs_run_conf
        else:
            jobs = Job.select(Job.f_job_conf).where(Job.f_job_id == job_id)
        if jobs:
            job = jobs[0]
            return job.f_job_conf
        else:
            return {}


# def get_modules_sequence(version: str):
#     if version not in ALLOW_VERSION:
#         raise ValueError(f"version {version} is not supported currently.")
#     with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{version.replace('.', '_')}", 'sequence.json'), 'r') as f:
#         data = json_loads(f.read())
#     return data
#
#
# def get_proj_yml(version: str):
#     if version not in ALLOW_VERSION:
#         raise ValueError(f"version {version} is not supported currently.")
#     with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{version.replace('.', '_')}", 'project.yml'), 'r') as f:
#         data = yaml.safe_load(f.read())
#     return data
#
#
# def get_roles_args(version: str):
#     if version not in ALLOW_VERSION:
#         raise ValueError(f"version {version} is not supported currently.")
#     with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{version.replace('.', '_')}", 'roles_arg.json'), 'r') as f:
#         data = json_loads(f.read())
#     return data
#
#
# def get_hosts_template(version: str) -> str:
#     if version not in ALLOW_VERSION:
#         raise ValueError(f"version {version} is not supported currently.")
#     with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{version.replace('.', '_')}", 'hosts'), 'r') as f:
#         data = f.read()
#     return data
#
#
# def get_var_files_dir(version: str):
#     if version not in ALLOW_VERSION:
#         raise ValueError(f"version {version} is not supported currently.")
#     return os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{version.replace('.', '_')}", 'var_files')


# TODO REMOVE THE BACKUP
# def generate_play_conf(job_id: str, job_data: dict) -> dict:
#     '''
#     seperate job conf into different play confs
#     :param job_id:
#     :param job_data:
#     :return: {"play_id": (play_conf, hosts)}  orderdict
#     '''
#     result = OrderedDict()
#     sequence = get_modules_sequence(job_data.get('version')).get('sequence')
#     roles_args = get_roles_args(job_data.get('version'))
#     play_conf_list = job_data.get('conf_list', [])
#
#     count = 1
#
#     for offset, conf in enumerate(play_conf_list):
#         stat_logger.info(f"in generate play conf:")
#         stat_logger.info(f"offset: {offset}")
#         stat_logger.info(f"No.{offset} conf in cutomize conf list: {json.dumps(conf, indent=4)}")
#
#         var_path_list = generate_variable_files(job_id, str(offset+1), job_data.get('version'), conf)
#         stat_logger.info(f"sequence: {sequence}")
#         for module in sequence:
#             stat_logger.info(f"in loop, module: {module}")
#             if module in conf.get('modules'):
#                 stat_logger.info(f"module {module} is in conf")
#                 stat_logger.info(f"result of conf.get(module).get('enable'): {conf.get('modules').get(module).get('enable')}")
#                 if conf.get('modules').get(module).get('enable'):
#                     # generate play id
#                     play_id = f"{job_id}_{count}"
#                     stat_logger.info(f"play id: {play_id}")
#                     # generate template config yaml
#                     template_yml = get_proj_yml(job_data.get('version'))
#                     template_yml[0]['hosts'] = play_id
#                     template_yml[0]['vars_files'] = var_path_list
#                     template_yml[0]['roles'] = [roles_args.get(module)]
#                     stat_logger.info(f"template yaml content: {json.dumps(template_yml, indent=4)}")
#                     # generate template hosts
#                     hosts_str = get_hosts_template(job_data.get('version'))
#                     hosts_str = hosts_str.replace('init', play_id)
#                     stat_logger.info(f"in generate play conf, conf.get('modules').get(module).get('ips'): {conf.get('modules').get(module).get('ips')}")
#                     hosts_str = hosts_str.replace('127.0.0.1', '\n'.join(conf.get('modules').get(module).get('ips')))
#                     stat_logger.info(f"hosts string: {hosts_str}")
#                     result.update({
#                             play_id: {
#                                 'yml': template_yml,
#                                 'hosts': hosts_str,
#                             }
#                     })
#                     stat_logger.info(f"current result dict: {json.dumps(result, indent=4)}")
#                     count += 1
#
#     stat_logger.info(f'Generating play conf successfully, result is: {json.dumps(result, indent=4)}')
#     return result

# TODO save to v140 adapter and remove
# def generate_play_conf(job_id: str, job_data: dict) -> dict:
#     '''
#     seperate job conf into different play confs
#     :param job_id:
#     :param job_data:
#     :return: {"play_id": (play_conf, hosts)}  orderdict
#     '''
#     result = OrderedDict()
#     sequence = get_modules_sequence(job_data.get('version')).get('sequence')
#     roles_args = get_roles_args(job_data.get('version'))
#     # play_conf_list = job_data.get('conf_list', [])
#
#     count = 1
#
#     for offset, conf in enumerate([job_data]):
#         stat_logger.info(f"in generate play conf:")
#         stat_logger.info(f"offset: {offset}")
#         stat_logger.info(f"No.{offset} conf in cutomize conf list: {json.dumps(conf, indent=4)}")
#
#         var_path_list = generate_variable_files(job_id, str(offset + 1), job_data.get('version'), conf)
#         stat_logger.info(f"sequence: {sequence}")
#         for module in sequence:
#             stat_logger.info(f"in loop, module: {module}")
#             if module in conf.get('modules'):
#                 stat_logger.info(f"module {module} is in conf")
#                 stat_logger.info(
#                     f"result of conf.get(module).get('enable'): {conf.get('modules').get(module).get('enable')}")
#                 # if conf.get('modules').get(module).get('enable'):
#                     # generate play id
#                 play_id = f"{job_id}_{count}"
#                 stat_logger.info(f"play id: {play_id}")
#                 # generate template config yaml
#                 template_yml = get_proj_yml(job_data.get('version'))
#                 template_yml[0]['hosts'] = play_id
#                 template_yml[0]['vars_files'] = var_path_list
#                 template_yml[0]['roles'] = [roles_args.get(module)]
#                 stat_logger.info(f"template yaml content: {json.dumps(template_yml, indent=4)}")
#                 # generate template hosts
#                 hosts_str = get_hosts_template(job_data.get('version'))
#                 hosts_str = hosts_str.replace('init', play_id)
#                 stat_logger.info(f"in generate play conf, conf.get('modules').get(module).get('ips'): {conf.get('modules').get(module).get('ips')}")
#                 hosts_str = hosts_str.replace('127.0.0.1', '\n'.join(conf.get('modules').get(module).get('ips')))
#                 stat_logger.info(f"hosts string: {hosts_str}")
#                 result.update({
#                     play_id: {
#                         'yml': template_yml,
#                         'hosts': hosts_str,
#                     }
#                 })
#                 stat_logger.info(f"current result dict: {json.dumps(result, indent=4)}")
#                 count += 1
#
#     stat_logger.info(f'Generating play conf successfully, result is: {json.dumps(result, indent=4)}')
#     return result
#
#
# def generate_variable_files(job_id: str, count: str, version, conf: dict):
#     stat_logger.info(f"in generate var file func, conf: {json.dumps(conf, indent=4)}")
#
#     result = []
#     job_dir = file_utils.get_job_directory(job_id)
#     var_dir = os.path.join(job_dir, f'var_files_{count}')
#     role = conf.get('role')
#     if role not in ["guest", "host"]:
#         raise ValueError(f"role {role} is not supported currently.")
#     shutil.copytree(src=get_var_files_dir(version), dst=var_dir)
#     with open(os.path.join(var_dir, f"fate_{role}"), "r") as fin:
#         data = yaml.safe_load(fin.read())
#     stat_logger.info(f"in generate var file func, original data: {json.dumps(data, indent=4)}")
#
#     data[role]['partyid'] = conf.get('party_id')
#
#     travel_key = set()
#     for key, value in conf.get('modules').items():
#         if key in data[role]:
#             travel_key.add(key)
#             for item, item_value in value.items():
#                 if item in data[role][key]:
#                     data[role][key][item] = item_value
#
#     modify_key = set(data.get(role).keys()) - travel_key
#     modify_key.remove('partyid')
#     if 'eggroll' in modify_key:
#         modify_key.remove('eggroll')
#     for key in modify_key:
#         data.get(role).get(key)['enable'] = False
#
#     stat_logger.info(f"in generate var file func, edited data: {json.dumps(data, indent=4)}")
#     with open(os.path.join(var_dir, f"fate_{role}"), "w") as fout:
#         yaml.dump(data, fout, Dumper=yaml.RoundTripDumper)
#     for root, dirs, files in os.walk(var_dir):
#         result.extend([os.path.join(os.path.abspath(root), name) for name in files])
#     return result


def check_job_conf(conf):
    # outer_require_parameters = ['version', 'party_id', 'role', 'modules', 'local_package_directory']
    outer_require_parameters = ['version', 'party_id', 'modules', 'role']
    check_parameters = {
        'python': ['enable', 'ips'],
        'java': ['enable', 'ips'],
        'mysql': ['enable', 'ips', 'port', 'dbuser', 'dbpasswd'],
        'rollsite': ['enable', 'ips', 'port', 'default_rules', 'rules'],
        'clustermanager': ['enable', 'ips', 'port'],
        'nodemanger': ['enable', 'ips', 'port'],
        'eggroll': ['dbname', 'egg'],
        'fate_flow': ['enable', 'ips', 'httpPort', 'grpcPort', 'dbname'],
        'supervisor': ['enable', 'ips'],
        'fateboard': ['enable', 'ips', 'port', 'dbname']
    }

    check_config(config=conf, required_parameters=outer_require_parameters)
    if conf.get('version') not in ALLOW_VERSION:
        raise ValueError(f"version {conf.get('version')} is not supported currently.")
    # if 'loacl_package_path' not in conf and 'remote_package_url' not in conf:
    #     raise ValueError("Conf requires one of 'loacl_package_path' or 'remote_package_url' parameter." )


def check_job_process(pid):
    if pid < 0:
        return False
    if pid == 0:
        raise ValueError('invalid PID 0')
    try:
        os.kill(pid, 0)
    except OSError as err:
        if err.errno == errno.ESRCH:
            # ESRCH == No such process
            return False
        elif err.errno == errno.EPERM:
            # EPERM clearly means there's a process to deny access to
            return True
        else:
            # According to "man 2 kill" possible error values are
            # (EINVAL, EPERM, ESRCH)
            raise
    else:
        return True


def is_play_executor_process(play: Play):
    # TODO check process before kill process （NEXT VERSION）
    return True


def kill_play_process_execution(play: Play):
    try:
        if play.f_pid:
            schedule_logger(play.f_job_id).info(f"try to stop play {play.f_play_id} of job {play.f_job_id}, pid: {play.f_pid}")
            pid = int(play.f_pid)
            if not is_play_executor_process(play):
                return False
            if check_job_process(pid):
                p = psutil.Process(pid)
                p.kill()
                kill_status = True
            else:
                schedule_logger(play.f_job_id).info(f'pid {play.f_pid} not exists')
                kill_status = True
        else:
            if NoneKillStatus.contains(play.f_status):
                kill_status = True
            else:
                kill_status = False
        schedule_logger(play.f_job_id).info(f"stop play {play.f_play_id} of job {play.f_job_id} {'success' if kill_status else 'failed'}")
        return kill_status
    except Exception as e:
        raise e


def run_subprocess(config_dir, process_cmd, log_dir=None):
    stat_logger.info(f'Starting process command: {process_cmd}')
    stat_logger.info(' '.join(process_cmd))

    os.makedirs(config_dir, exist_ok=True)
    if log_dir:
        os.makedirs(log_dir, exist_ok=True)
    std_log = open(os.path.join(log_dir if log_dir else config_dir, 'std.log'), 'a')
    std_err_log = open(os.path.join(log_dir if log_dir else config_dir, 'std_err.log'), 'a')
    pid_path = os.path.join(config_dir, 'pid')

    if os.name == 'nt':
        startupinfo = subprocess.STARTUPINFO()
        startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW
        startupinfo.wShowWindow = subprocess.SW_HIDE
    else:
        startupinfo = None
    p = subprocess.Popen(process_cmd,
                         stdout=std_log,
                         stderr=std_err_log,
                         startupinfo=startupinfo
                         )
    with open(pid_path, 'w') as f:
        f.truncate()
        f.write(str(p.pid) + "\n")
        f.flush()
    return p


def get_host_log(job_id: str, host: str, limit=LOG_LINE_LIMIT):
    host_log_path = os.path.join(file_utils.get_job_log_directory(job_id), f"{host}.log")
    if os.path.exists(host_log_path):
        res = subprocess.getoutput(f"tail -{limit} {host_log_path}")
        return 0, res
    else:
        return 100, ''


def get_server_log(level: str, limit=LOG_LINE_LIMIT):
    log_path = os.path.join(STAT_LOG_DIR, f"{level.upper()}.log")
    if os.path.exists(log_path):
        res = subprocess.getoutput(f"tail -{limit} {log_path}")
        return 0, 'success', res.split('\n')
    return 100, f'cannot find {level} log in {log_path}', []


def get_adapter(version: str):
    if version not in ALLOW_VERSION:
        raise ValueError(f"version {version} is not supported currently.")
    module_name = f"v{version.replace('.', '_')}"
    adapter_module = importlib.import_module(f"adapters.{module_name}")
    return adapter_module.Adapter()

