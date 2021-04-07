from flask import Flask, request
from flask_cors import CORS

from service import site_service
from utils import detect_utils
from utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/register', methods=['post'])
def register_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['appKey', 'appSecret', 'federatedOrganization',
                                                                       'federatedUrl', 'id', 'institutions',
                                                                       'networkAccessEntrances', 'networkAccessExits',
                                                                       'partyId', 'registrationLink', 'role', 'siteName'])
    data = site_service.register_fate_site(request_data)
    return get_json_result(data)


@manager.route('/checkUrl', methods=['post'])
def check_url():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['registrationLink', 'partyId', 'role', 'appKey'])
    request_data = request.json
    site_service.check_register_url(request_data)
    return get_json_result()


@manager.route('/', methods=['get'])
def get_site():
    data = site_service.get_home_site_list()
    return get_json_result(data=data)


@manager.route('/function', methods=['post'])
def function():
    data = site_service.find_all_fatemanager()
    return get_json_result(data=data)


@manager.route('/fatemanager', methods=['get'])
def fate_manager():
    allow_institutions_list = site_service.get_fate_manager_list()
    return get_json_result(data={"institutions": allow_institutions_list, "size": len(allow_institutions_list)})


@manager.route('/other', methods=['get'])
def other_site():
    data = site_service.get_other_site_list()
    return get_json_result(data=data)


@manager.route('/queryapply', methods=['get'])
def query_apply():
    data = site_service.query_apply_site()
    return get_json_result(data=data)


@manager.route('/exchange', methods=['post'])
def exchange():
    data = site_service.get_exchange_info()
    return get_json_result(data=data)


@manager.route('/applysite', methods=['post'])
def apply_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["authorityInstitutions"])
    site_service.apply_site(request_data)
    return get_json_result()


@manager.route('/readapplysite', methods=['post'])
def read_apply_site():
    site_service.read_apply_site()
    return get_json_result()


@manager.route('/institutions', methods=['get'])
def apply_institutions():
    data = site_service.get_apply_institutions()
    return get_json_result(data=data)


@manager.route('/info', methods=['post'])
def site_info():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId"])
    data = site_service.get_site_detail(request_data)
    return get_json_result(data=data)


@manager.route('/update', methods=['post'])
def update_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedOrganization", "role", "appKey",
                                                                       "appSecret", "networkAccessEntrances",
                                                                       "networkAccessExits", "federatedId", "partyId"])
    site_service.update_site(request_data)
    return get_json_result()


@manager.route('/telnet', methods=['post'])
def telnet_site_ip():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["partyId", "ip", "port"])
    site_service.telnet_ip(request_data)
    return get_json_result()


@manager.route('/readmsg', methods=['post'])
def readmsg():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId", "readStatus"])
    site_service.read_change_msg(request_data)
    return get_json_result()


@manager.route('/getmsg', methods=['post'])
def getmsg():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId"])
    site_service.get_change_msg(request_data)
    return get_json_result()


@manager.route('/updateVersion', methods=['post'])
def update_version():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["componentVersion", "fateServingVersion",
                                                                       "fateVersion", "federatedId", "partyId"])
    site_service.update_component_version(request_data)
    return get_json_result()


@manager.route('/applylog', methods=['get'])
def apply_log():
    site_service.get_apply_log()
    return get_json_result()


@manager.route('/functionread', methods=['get'])
def function_read():
    site_service.function_read()
    return get_json_result()


@manager.route('/checksite', methods=['post'])
def check_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["appKey", "appSecret", "dstPartyId",
                                                                       "srcPartyId", "federatedId", "role"])
    site_service.check_site(request_data)
    return get_json_result()


@manager.route('/secretinfo', methods=['post'])
def secret_info():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId"])
    data = site_service.get_secret_info(request_data)
    return get_json_result(data=data)
