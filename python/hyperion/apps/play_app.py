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
from operations.job_saver import JobSaver
from driver.play_controller import PlayController
from flask import Flask, request
from settings import stat_logger, TEST_MODE
from utils.api_utils import get_json_result
from utils.job_utils import check_config

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/query', methods=['POST'])
def query_play():
    plays = JobSaver.query_play(**request.json)
    if not plays:
        return get_json_result(retcode=101, retmsg='Query play failed, no play found.')
    play_filters = ['job_id', 'play_id', 'create_time', 'start_time', 'end_time', 'status', 'elapsed']
    data = plays[0].to_json(filters=play_filters)
    tasks = JobSaver.query_task(reverse=False, **request.json)
    if tasks:
        task_filters = ['play_id', 'task_id', 'task_name', 'role',
                        'create_time', 'start_time', 'end_time', 'status', 'elapsed']
        data['f_tasks'] = [task.to_json(task_filters) for task in tasks]
    return get_json_result(retmsg="Query play successfully.", data=data)


@manager.route('/query/task', methods=['POST'])
def query_task():
    tasks = JobSaver.query_task(**request.json)
    if not tasks:
        return get_json_result(retcode=101, retmsg='Query task failed, no task found.')
    task_filters = ['play_id', 'task_id', 'task_name', 'role', 'create_time',
                    'start_time', 'end_time', 'status', 'elapsed']
    return get_json_result(retmsg="Query task successfully.", data=tasks[0].to_json(task_filters))


@manager.route('/retry', methods=['POST'])
def retry_play():
    request_data = request.json
    required_parameters = ['job_id', 'play_id']
    check_config(request_data, required_parameters)
    retcode, retmsg = PlayController.retry_play(job_id=request_data['job_id'],
                                                play_id=request_data['play_id'],
                                                test_mode=TEST_MODE)
    return get_json_result(retcode=retcode, retmsg=retmsg, data={'play_id': request_data['play_id']})
