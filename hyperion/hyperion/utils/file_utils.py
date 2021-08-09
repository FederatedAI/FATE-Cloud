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
import json
import os
import traceback

from ruamel import yaml

from settings import ALLOW_VERSION, stat_logger
from utils.log_utils import schedule_logger

PROJECT_BASE = None


def get_project_base_directory():
    global PROJECT_BASE
    if PROJECT_BASE is None:
        PROJECT_BASE = os.path.abspath(
            os.path.join(os.path.dirname(os.path.realpath(__file__)), os.pardir))
    return PROJECT_BASE


def get_package_dir_by_version(version: str):
    # TODO allow version check
    if version in ALLOW_VERSION:
        return os.path.join(get_project_base_directory(), 'packages', f"v{version.replace('.', '_')}")
    else:
        raise Exception(f"version {version} is not supported to download currently, allowed version: {ALLOW_VERSION}")
    # return '/Users/izhfeng/Desktop/ansible/ansible-nfate-1.4.0'


def get_job_directory(job_id):
    job_dir = os.path.join(get_project_base_directory(), 'jobs', job_id)
    os.makedirs(job_dir, exist_ok=True)
    return job_dir


def get_job_log_directory(job_id):
    return os.path.join(get_project_base_directory(), 'logs', job_id)


def get_play_directory(play_id):
    return os.path.join(get_project_base_directory(), 'jobs', play_id.split('_')[0], play_id)


def get_play_log_dirctory(play_id):
    return os.path.join(get_job_log_directory(play_id.split('_')[0]), play_id)


def get_job_conf_path(job_id):
    return os.path.join(get_job_directory(job_id), 'job_conf.json')


def get_play_conf_path(play_id):
    result = {
        'conf_path': os.path.join(get_play_directory(play_id), f'{play_id}_conf.yml'),
        'hosts_path': os.path.join(get_play_directory(play_id), f'{play_id}_hosts')
    }
    stat_logger.info(f"in get play conf path, path dict: {json.dumps(result, indent=4)}")
    return result


def get_template_dir(version: str):
    if version not in ALLOW_VERSION:
        raise ValueError(f'version {version} is not supported currently.')
    base_dir = get_project_base_directory()
    return os.path.join(base_dir, 'templates', f"v{version.replace('.', '_')}")


def save_job_conf(job_id, job_data):
    fp = os.path.join(get_job_directory(job_id), 'job_conf.json')
    stat_logger.info(f"in save job conf, file path: {fp}")
    stat_logger.info(f"in save job conf, job data: {json.dumps(job_data, indent=4)}")
    with open(fp, 'w') as f:
        f.write(json.dumps(job_data, indent=4))
    return fp


def save_play_conf(job_id, play_id, play_conf, play_hosts) -> dict:
    try:
        # return {'play_id': {'conf_path': 'xxx', 'hosts_path': 'xxx'}}
        stat_logger.info(f"in save play conf func, play id: {play_id}")
        stat_logger.info(f"in save play conf func, play conf: {play_conf}")
        stat_logger.info(f"in save play conf func, play hosts: {play_hosts}")
        schedule_logger(job_id).info(f'Saving play {play_id} conf file and hosts file...')
        stat_logger.info(f'Saving play {play_id} conf file and hosts file...')

        play_conf_path = get_play_conf_path(play_id)
        os.makedirs(os.path.dirname(play_conf_path.get('conf_path')), exist_ok=True)
        with open(play_conf_path.get('conf_path'), 'w') as conf_fp:
            yaml.dump(play_conf, conf_fp, Dumper=yaml.RoundTripDumper)
        schedule_logger(job_id).info(f"Saving play {play_id} conf file success, file path {play_conf_path.get('conf_path')}")
        with open(play_conf_path.get('hosts_path'), 'w') as hosts_fp:
            hosts_fp.write(play_hosts)
        schedule_logger(job_id).info(f"Saving play {play_id} hosts file success, file path {play_conf_path.get('hosts_path')}")
        stat_logger.info(f"Saving play {play_id} hosts file success, file path {play_conf_path.get('hosts_path')}")
        return {
            play_id: {
                "conf_path": play_conf_path.get('conf_path'),
                "hosts_path": play_conf_path.get('hosts_path')
            }
        }
    except Exception:
        stat_logger.error(traceback.format_exc())
        raise


