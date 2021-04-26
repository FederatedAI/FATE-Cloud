#
#  Copyright 2019 The FATE Authors. All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
from flask import Flask, request, send_file
from flask_cors import CORS

from service import monitor_service
from utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/total', methods=['post'])
def get_monitor_total():
    request_data = request.json
    data = monitor_service.get_total(request_data)
    return get_json_result(data=data)


@manager.route('/institution', methods=['post'])
def get_institution_base_statics():
    request_data = request.json
    data = monitor_service.get_institutions_total(request_data)
    return get_json_result(data=data)


@manager.route('/site', methods=['post'])
def get_site_base_statistics():
    request_data = request.json
    data = monitor_service.get_site_total(request_data)
    return get_json_result(data=data)

