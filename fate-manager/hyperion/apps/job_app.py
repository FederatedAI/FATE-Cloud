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

import utils.file_utils
from driver.job_controller import JobController
from operations.job_saver import JobSaver
from flask import Flask, request
from settings import stat_logger
from utils import job_utils
from utils.api_utils import get_json_result

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/submit', methods=['POST'])
def submit_job():
    config = request.json
    job_id, job_conf_path = JobController.submit_job(job_data=config)
    if not job_id and not job_conf_path:
        return get_json_result(retcode=100, retmsg=f"Submit job failed.")
    else:
        data = {
            'job_id': job_id,
            'conf_path': job_conf_path
        }
        return get_json_result(retcode=0, retmsg=f"Submit job success", data=data)


@manager.route('/query', methods=['POST'])
def query_job():
    jobs = JobSaver.query_job(**request.json)
    if not jobs:
        return get_json_result(retcode=101, retmsg='Query job failed, no job found.')
    data = jobs[0].to_json(filters=['job_id', 'create_time', 'start_time', 'end_time', 'status', 'elapsed'])
    plays = JobSaver.query_play(reverse=False, order_by='play_id', **request.json)
    if plays:
        play_filters = ['job_id', 'play_id', 'create_time', 'start_time', 'end_time', 'status', 'elapsed']
        data['f_plays'] = [play.to_json(filters=play_filters) for play in plays]
    return get_json_result(retmsg="Query job successfully.", data=data)


@manager.route('/stop', methods=['POST'])
def stop_job():
    request_data = request.json
    final_status, stop_result = JobController.stop_job(request_data.get('job_id'))
    if final_status:
        return get_json_result(retmsg=f"Stop job {request_data.get('job_id')} successfully.")
    return get_json_result(retcode=100, retmsg=f"Stop job {request_data.get('job_id')} failed.", data=stop_result)


@manager.route('/host/log', methods=['POST'])
def get_host_log():
    '''
    :arg host
    :arg job_id
    :arg limit [OPTIONAL]
    :return:
    '''
    data = request.json
    if data.get('limit'):
        retcode, log_data = job_utils.get_host_log(job_id=data.get('job_id'),
                                                   host=data.get('host'),
                                                   limit=data.get('limit'))
    else:
        retcode, log_data = job_utils.get_host_log(job_id=data.get('job_id'),
                                                   host=data.get('host'))
    if retcode == 0:
        return get_json_result(retcode=retcode, retmsg='Query host log successfully', data=log_data)
    return get_json_result(retcode=retcode, retmsg='Query host log failed, no log files found.')


@manager.route('/log', methods=['POST'])
def get_job_log():
    data = request.json
    if not data.get('job_id'):
        return get_json_result(retcode=100, retmsg='job id is required')
    log_dir = utils.file_utils.get_job_log_directory(job_id=data.get('job_id'))
    if os.path.exists(log_dir):
        with open(os.path.join(log_dir, 'schedule.log')) as f:
            log_data = f.read()
    return get_json_result(retcode=0, retmsg=f"Query log of job {data.get('job_id')} successfully", data=log_data)
