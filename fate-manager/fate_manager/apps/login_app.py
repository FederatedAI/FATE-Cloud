from flask import Flask, request
from flask_cors import CORS

from fate_manager.service import login_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result

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
                                                  'passWord', 'userName', 'link', 'createTime'])
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