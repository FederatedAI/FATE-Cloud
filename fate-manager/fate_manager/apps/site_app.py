from flask import Flask, request
from flask_cors import CORS

from fate_manager.controller import site_controller
from fate_manager.controller.site_controller import site_page
from fate_manager.service import site_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/register', methods=['post'])
def register_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['appKey', 'appSecret', 'federatedOrganization',
                                                                       'id', 'institutions','partyId',"siteId",
                                                                       'registrationLink','role','siteName',
                                                                        'networkAccessEntrances',
                                                                       'fmRollSiteNetworkEntrances',
                                                                       'fmRollSiteNetworkAccessExitsList',
                                                                        'pollingStatus', 'secureStatus',
                                                                        'vipEntrance', 'exchangeName',
                                                                       'cmRollSiteNetworkAccessExitsList'])

    data = site_service.register_fate_site(request_data)
    return get_json_result(data=data)


@manager.route('/checkUrl', methods=['post'])
def check_url():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['link'])
    data = site_service.check_register_url(request_data)
    site_service.check_exists_site(data)
    return get_json_result(data=data)


@manager.route('/checkWeb', methods=['post'])
def connect_test():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['ip', 'port'])
    data = site_controller.connect_test(request_data.get('ip'), request_data.get('port'))
    return get_json_result(data=data)


@manager.route('/getsite', methods=['post'])
def get_site():
    request_data = request.json
    site_info = site_service.get_home_site_list()
    data = site_page(site_info, int(request_data.get('pageSize', 10)), int(request_data.get('pageNum', 1)))
    return get_json_result(data=data)


@manager.route('/function', methods=['post'])
def function():
    data = site_service.find_all_fatemanager()
    return get_json_result(data=data)


@manager.route('/fatemanager', methods=['get'])
def fate_manager():
    allow_institutions_list = site_service.get_fate_manager_list()
    return get_json_result(data={"institutions": allow_institutions_list, "size": len(allow_institutions_list)})


@manager.route('/other', methods=['post'])
def other_site():
    request_data = request.json
    data = site_service.get_other_site_list(request_data)
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


@manager.route('/noticeapplysite', methods=['post'])
def notice_apply_site():
    data = site_service.get_institutions_read_status()
    return get_json_result(data=data)


@manager.route('/institutions', methods=['get'])
def apply_institutions():
    data = site_service.get_apply_institutions()
    return get_json_result(data=data)


@manager.route('/info', methods=['post'])
def site_info():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId", "siteId"])
    data = site_service.get_site_detail(request_data)
    return get_json_result(data=data)


@manager.route('/update', methods=['post'])
def update_site():
    request_data = request.json
    UpdateFateFlowInfo = request_data.get('UpdateFateFlowInfo', None)
    if UpdateFateFlowInfo:
        data = site_service.update_version(request_data)
        return get_json_result(retmsg=data.get('msg'))
    else:
        detect_utils.check_config(config=request_data, required_arguments=["federatedOrganization", "role", "appKey",
                                                                           "appSecret", "networkAccessEntrances",
                                                                           'fmRollSiteNetworkAccessExitsList',
                                                                           'fmRollSiteNetworkAccess',
                                                                           "pollingStatus","secureStatus",
                                                                           "federatedId","partyId","siteId",
                                                                           ])
        site_service.update_site(request_data)
        return get_json_result()


@manager.route('/telnet', methods=['post'])
def telnet_site_ip():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["ip", "port"])
    ip = request_data.get('ip')
    port = request_data.get('port')
    res = site_service.connect_test(ip, int(port))
    return get_json_result(retmsg=res)


@manager.route('/readmsg', methods=['post'])
def readmsg():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId", "readStatus"])
    site_service.read_change_msg(request_data)
    return get_json_result()


@manager.route('/getmsg', methods=['post'])
def getmsg():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId", "siteId"])
    ret = site_service.get_change_msg(request_data)
    return get_json_result(data=ret)


@manager.route('/updateVersion', methods=['post'])
def update_version():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=["componentVersion", "fateServingVersion",
                                                                       "fateVersion", "federatedId", "partyId"])
    site_service.update_component_version(request_data)
    return get_json_result()


@manager.route('/applylog', methods=['post'])
def apply_log():
    request_data = request.json
    data = site_service.get_apply_log(request_data)
    return get_json_result(data=data)


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


@manager.route('/testrollsite', methods=['post'])
def connect_rollsite():
    request_data = request.json
    data = site_service.test_rollsite_ip(request_data)
    return get_json_result(data=data)