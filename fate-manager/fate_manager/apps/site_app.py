from flask import Flask, request
from flask_cors import CORS

from fate_manager.service import site_service
from fate_manager.utils import detect_utils
from fate_manager.utils.api_utils import server_error_response, get_json_result
from fate_manager.utils.base_utils import deserialize_b64,deserialize_b64_demo
from fate_manager.settings import login_service_logger as logger

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/register', methods=['post'])
def register_site():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['appKey', 'appSecret', 'federatedOrganization',
                                                                        'id', 'institutions','networkAccessEntrances',
                                                                       'networkAccessExits','partyId',
                                                                       'registrationLink', 'role','siteName',
                                                                       'rollSiteNetworkAccess'])

    token = request.headers.get("token")
    b_data = request_data.get("registrationLink")
    conf = com_deserialize_b64(b_data)
    request_data['federatedUrl'] = conf.get("federatedUrl")
    data = site_service.register_fate_site(request_data, token)
    return get_json_result(data=data)


def com_deserialize_b64(data):
    conf = {}
    str_data = deserialize_b64_demo(data)
    lst = str_data.split('?')
    conf['federatedUrl'] = lst[0]
    num_data = lst[-1].split('=')[1].rsplit('_', 1)
    conf['partyId'] = num_data[0]
    conf['appKey'] = num_data[1]
    return conf


@manager.route('/checkUrl', methods=['post'])
def check_url():
    request_data = request.json
    detect_utils.check_config(config=request_data, required_arguments=['link'])
    b_data = request_data.get("link")
    conf = com_deserialize_b64(b_data)
    conf['registrationLink'] = b_data
    data = site_service.get_site_part_info(conf)
    return get_json_result(data=data)


@manager.route('/', methods=['post'])
def get_site():
    request_data = request.json
    pageNum = int(request_data.get('pageNum', 1))
    pageSize = int(request_data.get('pageSize', 10))

    data = site_service.get_home_site_list()
    total = len(data[0]['siteList'])
    take_int = total // pageSize  # 取整
    remainder = total % pageSize  # 取余
    if pageNum <= take_int:
        start_num = (pageNum-1) * pageSize
        info = data[0]['siteList']
        data_split = info[start_num:(start_num + pageSize)]

    else:
        info = data[0]['siteList']
        # start_num = (pageNum - 1) * pageSize
        data_split = info[-remainder:]
    data[0]['siteList'] = data_split
    res_data = {'data': data, 'total': total}
    logger.info(f'get_site:{res_data}')
    return get_json_result(data=res_data)


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
    detect_utils.check_config(config=request_data, required_arguments=["federatedId", "partyId"])
    data = site_service.get_site_detail(request_data)
    return get_json_result(data=data)


@manager.route('/update', methods=['post'])
def update_site():
    request_data = request.json
    UpdateFateFlowInfo = request_data.get('UpdateFateFlowInfo', None)
    UpdateRollSiteInfo = request_data.get('UpdateRollSiteInfo', None)
    if UpdateFateFlowInfo or UpdateRollSiteInfo: # 版本信息&Rollsite网络配置信息相关
        data = site_service.update_version_or_rollsite(request_data)
        return get_json_result(data=data)
    else:
        detect_utils.check_config(config=request_data, required_arguments=["federatedOrganization", "role", "appKey",
                                                                           "appSecret", "networkAccessEntrances",
                                                                           "networkAccessExits", "federatedId",
                                                                           "partyId"])

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
