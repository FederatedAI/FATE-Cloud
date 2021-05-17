from flask import Flask, request
from flask_cors import CORS

from fate_manager.service import drop_down_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/federation', methods=['get'])
def federation():
    data = drop_down_service.get_federation_drop_down_list()
    return get_json_result(data)


@manager.route('/site', methods=['get'])
def get_site_drop_down_list():
    federated_id = request.form.get("federatedId")
    data = drop_down_service.get_site_drop_down_list(federated_id)
    return get_json_result(data)


@manager.route('/version', methods=['get'])
def get_version():
    product_type = request.form.get("productType")
    data = drop_down_service.get_fate_version_drop_down_list(product_type)
    return get_json_result(data)


@manager.route('/enumname', methods=['get'])
def get_enum_name():
    data = drop_down_service.get_enum_name_drop_down_list()
    return get_json_result(data)


@manager.route('/enuminfo', methods=['post'])
def get_enum_name_info():
    enum_name = request.form.get("enumName")
    data = drop_down_service.get_enum_name_info(enum_name)
    return get_json_result(data)


@manager.route('/mysql', methods=['get'])
def get_mysql_version():
    data = drop_down_service.get_component_version_list("mysql")
    return get_json_result(data)


@manager.route('/cluster', methods=['get'])
def get_cluster_version():
    data = drop_down_service.get_component_version_list("clustermanager")
    return get_json_result(data)


@manager.route('/node', methods=['get'])
def get_node_manager_version():
    data = drop_down_service.get_component_version_list("nodemanager")
    return get_json_result(data)


@manager.route('/rollsite', methods=['get'])
def get_rollsite_version():
    data = drop_down_service.get_component_version_list("rollsite")
    return get_json_result(data)


@manager.route('/fateflow', methods=['get'])
def get_fate_flow_version():
    data = drop_down_service.get_component_version_list("fateflow")
    return get_json_result(data)


@manager.route('/fateboard', methods=['get'])
def get_fate_board_version():
    data = drop_down_service.get_component_version_list("fateboard")
    return get_json_result(data)


@manager.route('/fateserving', methods=['get'])
def get_fate_serving_version():
    data = drop_down_service.get_component_version_list("fateserving")
    return get_json_result(data)


@manager.route('/componentversion', methods=['post'])
def get_component_version():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['fateVersion'])
    data = drop_down_service.get_component_version_list(request_data.get("fateVersion"), by_fateversion=True)
    return get_json_result(data)


@manager.route('/managernode', methods=['get'])
def get_manager_ip():
    return get_json_result()


@manager.route('/manager', methods=['get'])
def get_manager_ip_port():
    return get_json_result()


@manager.route('/party_id', methods=['get'])
def get_party_id():
    data = drop_down_service.get_party_id()
    return get_json_result(data=data)