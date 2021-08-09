from flask import Flask, request
from flask_cors import CORS

from fate_manager.service import login_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result
from fate_manager.utils.base_utils import deserialize_b64,deserialize_b64_demo
from fate_manager.settings import login_service_logger as logger

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/activate', methods=['post'])
def activate_new():
    conf = {}
    request_data = request.json
    logger.info(f'request_data:{request_data}')
    detect_utils.check_config(config=request_data,required_arguments=['link'])
    str_b_data = request_data.get('link')

    b_data = str(str_b_data).replace('\\r', '\r')
    b_data = str(b_data).replace('\\n', '\n')
    conf['link'] = b_data
    str_data = deserialize_b64_demo(b_data)
    lst = str_data.split('?')
    conf['federatedUrl'] = lst[0]
    num_data = lst[-1].split('=')[1].rsplit('_', 1)
    conf['institutions'] = num_data[0]
    # conf['SecretKey'] = num_data[1]
    conf['userName'] = request_data.get('userName')
    conf['fateManagerId'] = num_data[1]
    conf['institutionName'] = num_data[0]
    res_data = login_service.fate_manager_activate_new(conf)

    return get_json_result(data=res_data)


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