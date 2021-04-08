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

from service import login_service
from utils import detect_utils
from utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/activate', methods=['post'])
def activate():
    request_data = request.json
    detect_utils.check_config(config=request_data,
                              required_arguments=['activateUrl', 'appKey', 'appSecret', 'fateManagerId',
                                                  'federatedId',
                                                  'federatedOrganization', 'federatedUrl', 'institution',
                                                  'institutions',
                                                  'passWord', 'userName', 'link'])
    login_service.fate_manager_activate(request_data)
    return get_json_result()


@manager.route('/login', methods=['post'])
def login():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['userName', 'passWord'])
    token = login_service.fate_manager_login(request_data)
    return get_json_result(data={"token": token})


@manager.route('/logout', methods=['post'])
def logout():
    request_data = request.json
    token = request.headers.get("token")
    detect_utils.check_config(config=request_data, required_arguments=['userName'])
    login_service.fate_manager_logout(request_data, token)
    return get_json_result()


@manager.route('/checkjwt', methods=['post'])
def checkjwt():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['token'])
    data = login_service.fate_manager_checkjwt(request_data)
    return get_json_result(data=data)