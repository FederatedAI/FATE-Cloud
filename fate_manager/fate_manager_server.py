#
# Copyright 2021 The FATE Authors. All Rights Reserved.
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
import traceback
from flask import Flask
from werkzeug.middleware.dispatcher import DispatcherMiddleware
from werkzeug.serving import run_simple
# from fate_manager.apps.deploy_app import manager as deploy_mananger

from fate_manager.db.db_models import init_database_tables
from fate_manager.apps.site_app import manager as site_manager
from fate_manager.apps.login_app import manager as login_manager
from fate_manager.apps.user_app import manager as user_manager
from fate_manager.apps.drop_down_app import manager as drop_down_manager
from fate_manager.apps.monitor_app import manager as monitor_manager
from fate_manager.apps.static_app import manager as static_manager
from fate_manager.settings import IP, PORT, stat_logger, JOB_DETECTOR_INTERVAL, TASK_DETECTOR_INTERVAL
from fate_manager.utils.api_utils import get_json_result
from fate_manager.scheduler.detector import TaskDetector, MonitorDetector

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
            # '/fate-manager/{}/deploy'.format(API_VERSION): deploy_mananger,
            '/index': static_manager,
            '/fate-manager/api/site': site_manager,
            '/fate-manager/api/login': login_manager,
            '/fate-manager/api/user': user_manager,
            '/fate-manager/api/dropdown': drop_down_manager,
            '/fate-manager/api/monitor': monitor_manager,
            '/fate-manager': static_manager,
        }
    )
    # init db
    init_database_tables()
    TaskDetector(interval=TASK_DETECTOR_INTERVAL).start()
    MonitorDetector(interval=JOB_DETECTOR_INTERVAL).start()
    try:
        stat_logger.info("FATE Manager http server start...")
        run_simple(hostname=IP, port=PORT, application=app, threaded=True)
    except OSError as e:
        traceback.print_exc()
        os.kill(os.getpid(), signal.SIGKILL)
    except Exception as e:
        traceback.print_exc()
        os.kill(os.getpid(), signal.SIGKILL)