from flask import Flask, request
from flask_cors import CORS

from fate_manager.controller.user_controller import check_token
from fate_manager.service import user_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/info', methods=['get'])
@check_token
def user_info():
    token = request.headers.get("token")
    data = user_service.get_user_info(token)
    return get_json_result(data=data)


@manager.route('/list', methods=['post'])
@check_token
def get_user_list():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["context"])
    data = user_service.get_user_list(request_data)
    return get_json_result(data=data)


@manager.route('/accesslist', methods=['post'])
@check_token
def get_user_access_list():
    request_data = request.json
    data = user_service.get_user_access_list(request_data)
    return get_json_result(data=data)


@manager.route('/add', methods=['post'])
@check_token
def add_user():
    request_data = request.json
    token = request.headers.get("token")
    detect_utils.check_config(config=request_data, required_arguments=["userId", "userName", "roleId",
                                                                       "creator", "permissionList"])
    data = user_service.add_user(request_data, token)
    return get_json_result(data=data)


@manager.route('/delete', methods=['post'])
@check_token
def delete_user():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["userName"])
    token = request.headers.get("token")
    user_service.delete_user(token, request_data)
    return get_json_result()


@manager.route('/edit', methods=['post'])
@check_token
def edit_user():
    request_data = request.json
    user_service.edit_user(request_data)
    return get_json_result()


@manager.route('/sitelist', methods=['post'])
@check_token
def user_site_list():
    data = user_service.get_user_site_list()
    return get_json_result(data=data)


@manager.route('/siteinfouserlist', methods=['post'])
@check_token
def get_site_info_user_list():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId"])
    data = user_service.get_site_info_user_list(request_data)
    return get_json_result(data=data)


@manager.route('/userpartylist', methods=['post'])
@check_token
def user_party_list():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["userName"])
    data = user_service.get_login_user_manager_list(request_data)
    return get_json_result(data=data)


@manager.route('/sublogin', methods=['post'])
@check_token
def sublogin():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId", "subTag", "userName"])
    data = user_service.sublogin(request_data)
    return get_json_result(data=data)


@manager.route('/changelogin', methods=['post'])
@check_token
def change_login():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId", "userName", "subTag"])
    data = user_service.change_login(request_data)
    return get_json_result(data=data)


@manager.route('/permmsionauth', methods=['post'])
def permmsionauth():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId"])
    data = user_service.permission_authority(request_data)
    return get_json_result(data=data)


@manager.route('/allowpartylist', methods=['post'])
def get_allow_party_list():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId"])
    data = user_service.get_allow_party_list(request_data)
    return get_json_result(data=data)
