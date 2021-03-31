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
import signal
import sys
import time
import traceback

from flask import Flask
from werkzeug.serving import run_simple
from werkzeug.wsgi import DispatcherMiddleware

from apps.check_app import manager as check_app_manager
from apps.job_app import manager as job_app_manager
from apps.package_app import manager as package_app_manager
from apps.play_app import manager as play_app_manager
from apps.server_app import manager as server_app_manager
from apps.status_app import manager as test_app_manager
from db.db_models import init_database_tables
from driver import job_detector
from driver.job_scheduler import JobScheduler
from entity.runtime_config import RuntimeConfig
from operations import job_queue
from settings import stat_logger, IP, PORT, API_VERSION, _ONE_DAY_IN_SECONDS, CONCURRENT_NUM
from utils import job_utils
from utils.api_utils import get_json_result

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


if __name__ == '__main__':
    manager.url_map.strict_slashes = False
    app = DispatcherMiddleware(
        manager,
        {
            '/ansible/{}/play'.format(API_VERSION): play_app_manager,
            '/ansible/{}/job'.format(API_VERSION): job_app_manager,
            '/ansible/{}/package'.format(API_VERSION): package_app_manager,
            '/ansible/{}/check'.format(API_VERSION): check_app_manager,
            '/ansible/{}/test'.format(API_VERSION): test_app_manager,
            '/ansible/{}/server'.format(API_VERSION): server_app_manager,
        }
    )
    signal.signal(signal.SIGTERM, job_utils.cleaning)
    # signal.signal(signal.SIGCHLD, job_utils.wait_child_process)
    init_database_tables()
    job_queue.init_job_queue()
    scheduler = JobScheduler(queue=RuntimeConfig.JOB_QUEUE, concurrent_num=CONCURRENT_NUM)
    scheduler.start()

    # start job detector
    job_detector.JobDetector(interval=60 * 1000).start()

    try:
        run_simple(hostname=IP, port=PORT, application=app, threaded=True)
        stat_logger.info("Ansible deploy server start Successfully")
    except OSError as e:
        traceback.print_exc()
        os.kill(os.getpid(), signal.SIGKILL)
    except Exception as e:
        traceback.print_exc()
        os.kill(os.getpid(), signal.SIGKILL)
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        sys.exit(0)
