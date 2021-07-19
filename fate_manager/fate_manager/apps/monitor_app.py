from flask import Flask, request, send_file
from flask_cors import CORS

from fate_manager.service import monitor_service
from fate_manager.utils.api_utils import server_error_response, get_json_result

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


@manager.route('/detail', methods=['post'])
def get_detail_base_statistics():
    request_data = request.json
    data = monitor_service.get_detail_total(request_data)
    return get_json_result(data=data)
