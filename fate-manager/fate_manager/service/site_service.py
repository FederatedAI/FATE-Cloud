import json
import requests
import socket
from copy import deepcopy

from fate_manager.controller.site_controller import connect_test
from fate_manager.utils.base_utils import current_timestamp, deserialize_b64_decode
from fate_manager.controller import version_controller
from fate_manager.controller.apply import apply_result_task, allow_apply_task
from fate_manager.db.db_models import AccountInfo, FederatedInfo, FateSiteInfo, DeploySite, ChangeLog, TokenInfo, \
    AccountSiteInfo, ApplyInstitutionsInfo, ApplySiteInfo
from fate_manager.entity import item
from fate_manager.entity.status_code import UserStatusCode, SiteStatusCode
from fate_manager.entity.types import ActivateStatus, UserRole, SiteStatusType, EditType, ServiceStatusType, FuncDebug, \
    ApplyReadStatusType, RoleType, IsValidType, AuditStatusType, ReadStatusType, LogDealType, PollingType, SecureType
from fate_manager.operation.db_operator import SingleOperation, JointOperator
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import site_service_logger as logger, CLOUD_URL,FATE_FLOW_SETTINGS,ROLL_SITE_KEY
from fate_manager.utils.request_cloud_utils import request_cloud_manager, SignatureHead
from fate_manager.controller.roll_site import write_site_route


def register_fate_site(request_data):
    registrationLink = request_data.get("registrationLink")
    url, institutions, id = deserialize_b64_decode(registrationLink)
    logger.info(f'site registrationLink decode info:{url}--{institutions}--{id}')
    request_data['federatedUrl'] = url
    body = {
        "pollingStatus": request_data.get("pollingStatus", PollingType.FALSE),
        "secureStatus": request_data.get("secureStatus", SecureType.FALSE),
        "registrationLink": request_data.get("registrationLink"),
        "networkAccessEntrances": request_data.get("networkAccessEntrances"),
        "networkAccessExits": request_data.get("rollSiteNetworkAccessExits")
    }

    logger.info("start save exchange info to rollsite")
    # update_all_rollsite_info(request_data.get("network"), request_data.get("rollSiteNetworkAccess"),
    #                          request_data.get("partyId"), is_secure=request_data.get("secureStatus", SecureType.FALSE),
    #                          is_polling=request_data.get("pollingStatus", SecureType.FALSE))
    logger.info(f"update rollsite info success")
    logger.info(f"start request cloud ActivateUri, data: {request_data}, body:{body}")
    request_cloud_manager(uri_key="ActivateUri", data=request_data, body=body, url=request_data.get("federatedUrl"),
                          active=True, need_body=False)
    logger.info("request cloud success")
    federated_info = {
        "federated_id": request_data.get("id"),
        "federated_organization": request_data.get("federatedOrganization"),
        "institutions": request_data.get("institutions"),
        "federated_url": request_data.get("federatedUrl"),
        "status": 1
    }
    logger.info(f"start save federated info: {federated_info}")
    DBOperator.safe_save(FederatedInfo, federated_info)
    logger.info("save federated info success")
    site_info = {
        "federated_id": request_data.get("id"),
        "federated_organization": request_data.get("federatedOrganization"),
        "party_id": request_data.get("partyId"),
        "site_name": request_data.get("siteName"),
        "institutions": request_data.get("institutions"),
        "role": request_data.get("role"),
        "app_key": request_data.get("appKey"),
        "app_secret": request_data.get("appSecret"),
        "registration_link": request_data.get("registrationLink"),

        "network_access_entrances": request_data.get("networkAccessEntrances"),
        "rollsite_network_access": request_data.get("rollSiteNetworkAccess"),
        "rollsite_network_access_exits": request_data.get("rollSiteNetworkAccessExits"),
        "polling_status": request_data.get("pollingStatus", PollingType.FALSE),
        "secure_status": request_data.get("secureStatus", SecureType.FALSE),

        "exchange_name": request_data.get("exchangeName"),
        "vip_entrances": request_data.get("vipEntrance"),
        "exchange_network_access_exits": request_data.get("rollSiteNetworkAccessExitsList"),
        "exchange_network_access": request_data.get("network"),

        "status": SiteStatusType.JOINED,
        "edit_status": EditType.YES,
        "service_status": ServiceStatusType.UNAVAILABLE,
        "activation_time": current_timestamp()
    }
    logger.info(f"start save site info:{site_info}")
    DBOperator.safe_save(FateSiteInfo, site_info)
    DBOperator.create_entity(ApplySiteInfo, {
        "institutions": request_data.get("institutions"),
        "party_id": request_data.get("partyId"),
        "site_name": request_data.get("siteName")
    })
    logger.info("save site info success")

    logger.info(f"start request cloud SiteQueryUri")
    data = request_cloud_manager(uri_key="SiteQueryUri", data=item.SiteSignatureItem(**request_data).to_dict(), body={},
                                 url=request_data.get("federatedUrl"))
    logger.info(f"cloud return:{data}")
    return {"SiteId": data.get("id"), "FederatedId": request_data.get("id")}


def check_register_url(request_data):
    request_data = {'registrationLink': request_data.get("link")}
    request_data['federatedUrl'], request_data['partyId'], request_data['appKey'] = deserialize_b64_decode(request_data.get("registrationLink"))
    logger.info(f'site checkUrl request info:{request_data}')
    body = {"registrationLink": request_data.get("registrationLink")}
    logger.info(f"start request cloud site info, body:{body}")
    message = request_cloud_manager(uri_key="ActivateUriInfo", data=item.SiteSignatureItem(**request_data).to_dict(),
                                    body=body, url=request_data.get("federatedUrl"), active=True)
    logger.info(f"request cloud ActivateUriInfo success: {message}")
    return message

def find_all_fatemanager():
    logger.info(f"start query account by status:{ActivateStatus.YES} role {UserRole.ADMIN}")
    accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, role=UserRole.ADMIN)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, "no found account")
    account = accounts[0]
    logger.info("query account success")

    logger.info("start request cloud to find all fateManager")
    resp_list = request_cloud_manager(uri_key="FunctionAllUri",
                                      data={"appKey": account.app_key, "fateManagerId": account.fate_manager_id,
                                            "appSecret": account.app_secret}, body={}, url=None)
    logger.info(f"request cloud success, resp list:{resp_list}")
    block_msg = []
    logger.info("check block msg")
    for cloud_temp in resp_list:
        new_dict = deepcopy(cloud_temp)
        for manager_temp in account.block_msg:
            if cloud_temp["functionName"] == manager_temp["functionName"]:
                if cloud_temp["status"] == manager_temp["status"]:
                    new_dict["readStatus"] = manager_temp["status"]
                elif cloud_temp["status"] == FuncDebug.ON:
                    new_dict["readStatus"] = ApplyReadStatusType.READ
                else:
                    new_dict["readStatus"] = ApplyReadStatusType.NOT_READ
        block_msg.append(new_dict)
    logger.info(f"make new block msg:{block_msg}")
    logger.info(f"save account info by fate_manager_id {account.fate_manager_id}, block_msg {block_msg}")
    DBOperator.safe_save(AccountInfo, {"fate_manager_id": account.fate_manager_id,
                                       "user_name": account.user_name,
                                       "block_msg": block_msg})
    logger.info("save  account info success")
    return block_msg


def get_home_site_list():
    logger.info("get all site")
    federated_site_list = JointOperator.get_home_site()
    logger.info(f"federated_site_list:{federated_site_list}")
    account = SingleOperation.get_admin_info()
    federated_item_dict = {}
    if not federated_site_list:
        return []
    if len(federated_site_list) == 1 and not hasattr(federated_site_list[0], "fatesiteinfo"):
        federated_item = item.FederatedItem()
        federated_item.federatedOrganization = federated_site_list[0].federated_organization
        federated_item.institutions = federated_site_list[0].institution
        federated_item.fateManagerInstitutions = account.institutions
        federated_item.federatedId = federated_site_list[0].federated_id
        federated_item.createTime = federated_site_list[0].federated_organization_create_time
        federated_item_dict = {federated_item.federatedId: federated_item.to_dict(need_none=True)}
    else:
        for site in federated_site_list:
            fate_site_info = site.fatesiteinfo
            site_signature_req = item.SiteSignatureItem(partyId=fate_site_info.party_id,
                                                        role=fate_site_info.role,
                                                        appKey=fate_site_info.app_key,
                                                        appSecret=fate_site_info.app_secret).to_dict()
            logger.info(f"start request cloud FederationUri:{site_signature_req}")
            resp = request_cloud_manager(uri_key="FederationUri", data=site_signature_req, body={}, methods="get",
                                         url=site.federated_url)
            logger.info(f"request cloud success, return {resp}")
            federated_item = item.FederatedItem()
            federated_item.federatedId = site.federated_id
            federated_item.size = resp.get("total")
            federated_item.createTime = resp.get("federatedOrganizationDto", {}).get("createTime")
            federated_item.federatedOrganization = resp.get("federatedOrganizationDto", {}).get("name")
            federated_item.institutions = resp.get("federatedOrganizationDto", {}).get("institution")
            federated_item.fateManagerInstitutions = fate_site_info.fate_manager_institution
            site_item = item.SiteItem()
            site_item.siteId = fate_site_info.site_id
            site_item.role = item.IdPair(code=fate_site_info.role, desc=RoleType.to_str(int(fate_site_info.role))).to_dict()
            site_item.status = item.IdPair(code=fate_site_info.status, desc=SiteStatusType.to_str(fate_site_info.status)).to_dict()
            site_item.activationTime = fate_site_info.activation_time
            site_item.partyId = fate_site_info.party_id
            site_item.siteName = fate_site_info.site_name
            site_item.serviceStatus = item.IdPair(code=fate_site_info.service_status,
                                                  desc=ServiceStatusType.to_str(fate_site_info.service_status)).to_dict()
            if fate_site_info.status == SiteStatusType.JOINED:
                logger.info(f"site status is {SiteStatusType.JOINED}, start request cloud SiteQueryUri")
                resp_data = request_cloud_manager(uri_key="SiteQueryUri", data=site_signature_req, body={},
                                                  url=site.federated_url)
                logger.info(f"resp: {resp_data}")
                site_item.status = item.IdPair(code=resp_data.get("status"),
                                               desc=SiteStatusType.to_str(resp_data.get("status"))).to_dict()
                site_info = {
                    "status": site_item.status["code"],
                    "party_id": fate_site_info.party_id,
                    "federated_id": site.federated_id,
                    "create_time": resp_data.get("createTime"),
                    "activation_time": resp_data.get("activationTime"),
                    "site_id": resp_data.get("id")
                }
                logger.info(f"start save site info:{site_info}")
                DBOperator.safe_save(FateSiteInfo, site_info)
                logger.info("save site info success")

                # update fate version
                # if fate_site_info.fate_version or fate_site_info.fate_serving_version:
                #     version_controller.update_version_to_cloud_manager(site)
            if federated_item.federatedId not in federated_item_dict.keys():
                federated_item_dict[federated_item.federatedId] = federated_item.to_dict(need_none=True)
                federated_item_dict[federated_item.federatedId]["siteList"] = [site_item.to_dict(need_none=True)]
            else:
                federated_item_dict[federated_item.federatedId]["siteList"].append(site_item.to_dict())
    logger.info(f"federated item dict {federated_item_dict} to list")
    federated_item_list = [v for _, v in federated_item_dict.items()]
    logger.info(f"federated item list {federated_item_list}")
    return federated_item_list


def get_fate_manager_list():
    account = SingleOperation.get_admin_info()
    allow_institutions_list = allow_apply_task(account)
    return allow_institutions_list


def get_other_site_list(request_data):
    pageNum = request_data.get('pageNum', 1)
    pageSize = request_data.get('pageSize', 5)

    federated_infos = SingleOperation.get_federated_info()
    account = SingleOperation.get_admin_info()
    apply_result_task(account)
    apply_institutions_list = SingleOperation.get_apply_institutions_info(status=AuditStatusType.AGREED)
    logger.info(f"apply institutions list: {apply_institutions_list}")
    if not apply_institutions_list:
        return None
    federated_item_list = []
    logger.info('start get other site list')
    apply_site_list = DBOperator.query_entity(ApplySiteInfo)
    for institutions_item in apply_institutions_list:
        logger.info("start request cloud OtherSiteUri")
        institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                                   appKey=account.app_key,
                                                                   appSecret=account.app_secret).to_dict()
        resp = request_cloud_manager(uri_key="OtherSiteUri", data=institution_signature_item,
                                     body={"institutions": institutions_item.institutions,
                                           "pageNum": pageNum,
                                           "pageSize": pageSize},
                                     url=None
                                     )
        site_item_list = []
        if resp:
            for site in resp.get("list", []):
                site_item = item.SiteItem()
                site_item.siteId = site.get("id")
                site_item.partyId = site.get("partyId")
                site_item.siteName = site.get("siteName")
                site_item.role = item.IdPair(code=site.get("role"), desc=RoleType.to_str(int(site.get("role")))).to_dict()
                site_item.status = item.IdPair(code=site.get("status"), desc=SiteStatusType.to_str(int(site.get("status")))).to_dict()
                site_item.serviceStatus = item.IdPair(code=site.get("detectiveStatus"), desc=ServiceStatusType.to_str(int(site.get("detectiveStatus")))).to_dict()
                site_item.activationTime = site.get("activationTime")
                site_item_list.append(site_item.to_dict())
                if site.get("partyId") not in [apply_site.party_id for apply_site in apply_site_list]:
                    DBOperator.create_entity(ApplySiteInfo, {"institutions": institutions_item.institutions,
                                                             "party_id": site.get("partyId"),
                                                             "site_name": site.get("siteName")})
        federated_item = item.FederatedItem()
        federated_item.federatedId = federated_infos[0].federated_id
        federated_item.fateManagerInstitutions = institutions_item.institutions
        federated_item.size = len(resp.get("list")) if resp else 0
        federated_item.siteList = site_item_list
        federated_item_list.append(federated_item.to_dict(need_none=False))
    return federated_item_list


def query_apply_site():
    account = SingleOperation.get_admin_info()
    logger.info('start request cloud: GetApplyListUri')
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="GetApplyListUri", data=institution_signature_item,
                                 body={"institutions": account.institutions},
                                 url=None
                                 )
    return resp


def get_institutions_read_status():
    wait_read_items = DBOperator.query_entity(ApplyInstitutionsInfo, read_status=ApplyReadStatusType.NOT_READ)
    wait_read_list = []
    for not_read_item in wait_read_items:
        if not_read_item.status in [AuditStatusType.AGREED, AuditStatusType.REJECTED, AuditStatusType.CANCEL]:
            wait_read_list.append({"institutions": not_read_item.institutions,
                                   "status": not_read_item.status})
    return wait_read_list


def get_exchange_info():
    account = SingleOperation.get_admin_info()
    logger.info("start request cloud ExchangeUri")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="ExchangeUri", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "pageNum": 1,
                                       "pageSize": 100},
                                 url=None
                                 )
    vip_list = []
    for exchange_info in resp.get("list"):
        vip_list.append({
            "exchangeName": exchange_info.get("exchangeName"),
            "vip": exchange_info.get("vip"),
            "updateTime": exchange_info.get("updateTime")
        })

    if vip_list:
        return {"exchangeVip": vip_list}
    else:
        return {}


def get_apply_institutions():
    account = SingleOperation.get_admin_info()
    logger.info("start request cloud AuthorityInstitutions")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="AuthorityInstitutions", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "pageNum": 1,
                                       "pageSize": 100},
                                 url=None
                                 )
    institutions_list = []
    resp_institutions_list = resp.get("list") if resp else []
    if not resp_institutions_list:
        resp_institutions_list = []
    for institutions in resp_institutions_list:
        apply_result = {
            "status": {"code": institutions.get("status"), "desc": IsValidType.to_str(institutions.get("status"))},
            "institutions": institutions.get("institutions")

        }
        if institutions.get("status") == AuditStatusType.REJECTED:
            apply_result["status"]["code"] = IsValidType.YES
            apply_result["status"]["desc"] = IsValidType.to_str(IsValidType.YES)
        elif institutions.get("status") == AuditStatusType.AGREED:
            apply_result["status"]["desc"] = IsValidType.to_str(IsValidType.NO)
        institutions_list.append(apply_result)
    return institutions_list


def apply_site(request_data):
    account = SingleOperation.get_admin_info()
    request_data["institutions"] = account.institutions
    logger.info("start request cloud AuthorityApply")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="AuthorityApply", data=institution_signature_item,
                                 body=request_data,  url=None)


def read_apply_site():
    logger.info(f'start update apply institutions read status')
    update_list = SingleOperation.update_apply_institutions_read_status(ApplyReadStatusType.NOT_READ,
                                                                {"read_status": ApplyReadStatusType.READ})
    logger.info(f'update success: {update_list}')


def get_site_detail(request_data):
    site_list = DBOperator.query_entity(FateSiteInfo, party_id=request_data.get("partyId"),
                                        federated_id=request_data.get("federatedId"))
    if not site_list:
        raise Exception(SiteStatusCode.NoFoundSite, f'no found apply site by party id {request_data.get("partyId")}, federated id {request_data.get("federatedId")}')
    site = site_list[0]
    site_detail = {
        "acativationTime": site.activation_time,
        "appKey": site.app_key,
        "appSecret": site.app_secret,
        "componentVersion": site.component_version,
        "createTime": site.create_time,
        "editStatus": item.IdPair(code=1, desc=EditType.to_str(1)).to_dict(),
        "fateServingVersion": site.fate_serving_version,
        "fateFlowIp": site.fate_flow_info,
        "fateVersion": site.fate_version,
        "federatedId": site.federated_id,
        "federatedOrganization": site.federated_organization,
        "institutions": site.institutions,
        "rollSiteNetworkAccess": site.rollsite_network_access,

        "ExchangeInfo": {
            "exchangeName": site.exchange_name,
            "vipEntrances": site.vip_entrances,
            "networkAccessEntrances": site.network_access_entrances,
            "networkAccessExits": site.rollsite_network_access_exits,

            "exchangeNetworkAccessExits": site.exchange_network_access_exits,
            "exchangeNetworkAccesss": site.exchange_network_access,
        },
        "exchangeRreadStatus": site.exchange_read_status,

        "partyId": site.party_id,
        "registrationLink": site.registration_link,
        "siteId": site.site_id,
        "siteName": site.site_name,
        "role": item.IdPair(code=site.role, desc=RoleType.to_str(site.role)).to_dict(),
        "status": item.IdPair(code=site.status, desc=SiteStatusType.to_str(site.status)).to_dict(),
        "versionEditStatus": item.IdPair(code=EditType.YES, desc=EditType.to_str(EditType.YES)).to_dict()
    }

    if request_data.get('updateMark'):
        update_info = {
            "party_id": request_data.get("partyId"),
            "federated_id": request_data.get("federatedId"),

            "network_access_entrances": site.network_access_entrances_new,
            "network_access_exits": site.network_access_exits_new,
            "exchange_name": site.exchange_name_new,
            "vip_entrances": site.vip_entrances_new,
            # "exchange_network_access_exits": site.exchange_network_access_exits_new,
            # "exchange_network_access": site.exchange_exits_new,

            "exchange_name_new": "",
            "vip_entrances_new": "",
            "network_access_entrances_new": "",
            "network_access_exits_new": "",
            "exchange_network_access_exits_new": "",
            "exchange_exits_new": "",

            "exchange_read_status": 0
        }
        DBOperator.update_entity(FateSiteInfo, update_info)

        # update rollsite info：
        if site.vip_entrances_new:
            update_all_rollsite_info(site.vip_entrances_new, site.rollsite_network_access, site.party_id)
    else:
        if site.exchange_read_status == 1:
            add_info = {
                "exchangeNameNew": site.exchange_name_new,
                "vipEntrancesNew": site.vip_entrances_new,
                "NetworkAccessEntrancesNew": site.network_access_entrances_new,
                "NetworkAccessExitsNew": site.network_access_exits_new,
            }
            site_detail["ExchangeInfo"].update(add_info)
        deploy_site_list = DBOperator.query_entity(DeploySite, party_id=request_data.get("partyId"),
                                                   federated_id=request_data.get("federatedId"),
                                                   deploy_type=1,
                                                   is_valid=IsValidType.YES
                                                   )
        if deploy_site_list:
            site_detail["versionEditStatus"] = item.IdPair(code=EditType.NO, desc=EditType.to_str(EditType.NO)).to_dict()

    return site_detail


def update_site(request_data):
    site_list = DBOperator.query_entity(FateSiteInfo, party_id=request_data.get("partyId"),
                                        federated_id=request_data.get("federatedId"))
    if not site_list:
        raise Exception(SiteStatusCode.NoFoundSite, f'no found apply site by party id {request_data.get("partyId")}, federated id {request_data.get("federatedId")}')
    site = site_list[0]
    if site.party_id == 0:
        raise Exception(SiteStatusCode.PARTY_ID_NOT_EXIST, f'no found party id')
    elif site.status == SiteStatusType.NO_JOIN:
        raise Exception(SiteStatusCode.SITE_NOT_JOINED, f'site status is no join')
    elif site.edit_status == EditType.NO:
        raise Exception(SiteStatusCode.SITE_NOT_ALLOW_UPDATE, f'site not allow update')
    federated_info = DBOperator.query_entity(FederatedInfo, federated_id=request_data.get("federatedId"))[0]
    logger.info("start request cloud IpAcceptUri")
    site_signature_req = item.SiteSignatureItem(partyId=request_data.get("partyId"), role=request_data.get("role"),
                                                appKey=request_data.get("appKey"),
                                                appSecret=request_data.get("appSecret")).to_dict()
    resp = request_cloud_manager(uri_key="IpAcceptUri", data=site_signature_req,
                                 body={"partyId": request_data.get("partyId"),
                                       "networkAccessEntrances": request_data.get("networkAccessEntrances"),
                                       "networkAccessExits": request_data.get("networkAccessExits"),
                                       }, url=federated_info.federated_url)
    logger.info(f"request cloud success:{resp}")
    change_log_info = {
        "party_id": request_data.get("partyId"),
        "network_access_entrances": request_data.get("networkAccessEntrances"),
        "rollsite_network_access_exits": request_data.get("networkAccessExits"),
        "status": LogDealType.NO,
        "federated_id": request_data.get("federatedId"),
        "federated_organization": request_data.get("federatedOrganization"),
        "case_id":resp.get("caseId")
    }
    logger.info(f"change log info: {change_log_info}")
    DBOperator.create_entity(ChangeLog, entity_info=change_log_info)
    DBOperator.update_entity(FateSiteInfo, {"site_id": site.site_id, "federated_id": site.federated_id,
                                            "party_id": site.party_id, "edit_status": EditType.NO})


def telnet_ip(request_data):
    federated_info = DBOperator.query_entity(FederatedInfo, federated_id=request_data.get("federatedId"))[0]
    logger.info("start request cloud CheckWebUri")
    site_info = DBOperator.query_entity(FateSiteInfo, **{"federated_id": request_data.get("federatedId"),
                                                         "party_id": request_data.get("partyId")})[0]
    site_signature_req = item.SiteSignatureItem(partyId=site_info.party_id, role=site_info.role,
                                                appKey=site_info.app_key,
                                                appSecret=site_info.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="CheckWebUri", data=site_signature_req,
                                 body={"ip": request_data.get("ip"), "port": request_data.get("port")},
                                 url=federated_info.federated_url)
    logger.info("request cloud success")


def read_change_msg(request_data):
    DBOperator.update_entity(FateSiteInfo, {"federated_id": request_data.get("federatedId"),
                                            "party_id": request_data.get("partyId"),
                                            "read_status": request_data.get("readStatus")})


def get_change_msg(request_data):
    site_list = DBOperator.query_entity(FateSiteInfo, **{"federated_id": request_data.get("federatedId"),
                                                         "party_id": request_data.get("partyId")})
    if not site_list:
        raise Exception(SiteStatusCode.NoFoundSite,
                        f'no found apply site by party id {request_data.get("partyId")}, federated id {request_data.get("federatedId")}')
    site = site_list[0]
    return {
        "editStatus": item.IdPair(code=site.edit_status, desc=EditType.to_str(site.edit_status)).to_dict(),
        "readStatus": item.IdPair(code=site.read_status, desc=ReadStatusType.to_str(site.read_status)).to_dict()
    }


def update_component_version(request_data):
    site_info = {
        "federated_id": request_data.get("federatedId"),
        "party_id": request_data.get("PartyId"),
        "component_version": request_data.get("componentVersion"),
        "fate_version": request_data.get("fateVersion"),
        "fate_serving_version": request_data.get("fateServingVersion")
    }
    DBOperator.update_entity(FateSiteInfo, site_info)
    federated_info_list = JointOperator.get_party_id_info(party_id=request_data.get("PartyId"))
    if federated_info_list:
        federated_item = federated_info_list[0]
        federated_item.component_version = request_data.get("componentVersion")
        version_controller.update_version_to_cloud_manager(federated_item)
    else:
        raise Exception(SiteStatusCode.NoFoundSite, "no found site")


def get_apply_log(request_data):
    pageNum = request_data.get('pageNum', 1)
    pageSize = request_data.get('pageSize', 5)
    account = SingleOperation.get_admin_info()
    logger.info("start request cloud ApplyLog")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="ApplyLog", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "pageNum": pageNum,
                                       "pageSize": pageSize},
                                 url=None)
    logger.info(f'cloud return:{resp}')
    return resp


def function_read():
    account = SingleOperation.get_admin_info()
    resp_list = []
    for block_item in account.block_msg:
        block_item["readStatus"] = ApplyReadStatusType.READ
        resp_list.append(block_item)
    DBOperator.update_entity(AccountInfo, {"fate_manager_id": account.fate_manager_id,
                                           "block_msg": resp_list,
                                           "user_name": account.user_name})


def check_site(request_data):
    federated_info_list = JointOperator.get_party_id_info(party_id=request_data.get("dstPartyId"))
    if federated_info_list:
        federated_item = federated_info_list[0]
        fate_site_item = federated_item.fatesiteinfo
        uri = CLOUD_URL["CheckAuthorityUri"]
        # guest head
        old_signature_item = item.OldSignatureItem(partyId=request_data.get("srcPartyId"),
                                                   role=request_data.get("role"),
                                                   appKey=request_data.get("appKey"),
                                                   appSecret=request_data.get("appSecret"))
        old_head = SignatureHead.old_signature_head(uri, old_signature_item.to_dict(),
                                          body=json.dumps({"partyId": request_data.get("srcPartyId")}, separators=(',', ':')))
        check_site_body = {
            "HTTP_BODY": {"partyId": request_data.get('srcPartyId')},
            "HTTP_URI": uri,
            "HTTP_HEADER": old_head
        }
        logger.info("start request cloud CheckAuthorityUri")
        site_signature_req = item.SiteSignatureItem(partyId=request_data.get("dstPartyId"),
                                                    role=int(fate_site_item.role),
                                                    appKey=fate_site_item.app_key,
                                                    appSecret=fate_site_item.app_secret).to_dict()
        resp = request_cloud_manager(uri_key="CheckAuthorityUri", data=site_signature_req,
                                     body={"site": json.dumps(check_site_body, separators=(',', ':'))},
                                     url=federated_item.federated_url)
        logger.info(f"request cloud success:{resp}")
    else:
        raise Exception(SiteStatusCode.NoFoundSite, "no found site")


def get_secret_info(request_data):
    site_list = DBOperator.query_entity(FateSiteInfo, **{"federated_id": request_data.get("federatedId"),
                                                         "party_id": request_data.get("partyId")})
    if not site_list:
        raise Exception(SiteStatusCode.NoFoundSite,
                        f'no found apply site by party id {request_data.get("partyId")}, federated id {request_data.get("federatedId")}')
    site = site_list[0]
    return {"appKey": site.app_key, "AppSecret": site.app_secret, "role": site.role}


def update_version_or_rollsite(request_data):

    uri = FATE_FLOW_SETTINGS.get('QueryFateVersion')
    federatedId = request_data.get('federatedId')
    partyId = request_data.get('partyId')

    UpdateFateFlowInfo = request_data.get('UpdateFateFlowInfo')
    UpdateRollSiteInfo = request_data.get('UpdateRollSiteInfo')

    site_list = DBOperator.query_entity(FateSiteInfo, party_id=partyId, federated_id=federatedId)
    if not site_list:
        raise Exception(SiteStatusCode.NoFoundSite,
                        f'no found apply site by party id {request_data.get("partyId")}, federated id {request_data.get("federatedId")}')
    site = site_list[0]
    if UpdateFateFlowInfo:
        fate_flow_info = UpdateFateFlowInfo.split(':')
        flow_ip = fate_flow_info[0]
        flow_port = fate_flow_info[1]

        url = 'http://' + flow_ip + ':' + flow_port + uri
        res = requests.post(url, json={"module": "FATE"})
        if res:
            data = res.text
            logger.info(f"get_fate_version {data}")
            info = json.loads(data)
            fateVersion = info.get('data').get('FATE')

            DBOperator.update_entity(FateSiteInfo, {"fate_version": fateVersion, "federated_id": site.federated_id,
                                                    "fate_flow_info": UpdateFateFlowInfo,
                                                "party_id": site.party_id, "edit_status": EditType.NO})

            federated_id = site.federated_id
            Fed_Info= DBOperator.query_entity(FederatedInfo, federated_id=federated_id)
            site_signature_req = item.SiteSignatureItem(partyId=partyId,
                                                        role=site.role,
                                                        appKey=site.app_key,
                                                        appSecret=site.app_secret).to_dict()

            request_cloud_manager(uri_key="UpdateVersionUri", data=site_signature_req,
                                         body={"componentVersion": "",
                                               "fateServingVersion": "",
                                               "fateVersion": fateVersion,
                                               }, url=Fed_Info[0].federated_url)
        else:
            logger.info(f'connect  fate flow {UpdateFateFlowInfo} faild')
            return []


    if UpdateRollSiteInfo:
        res = check_rollsite_url(UpdateRollSiteInfo)
        success = res.get('success')
        rollsite = ";".join(success)
        DBOperator.update_entity(FateSiteInfo, {"rollsite_network_access": rollsite, "federated_id": site.federated_id,
                                                "party_id": site.party_id, "edit_status": EditType.NO})
        return res


def check_rollsite_url(UpdateRollSiteInfo):
    logger.info(f"start test connect rollsite, rollsite address:{UpdateRollSiteInfo}")
    rollsite_list = UpdateRollSiteInfo.split(';')[:-1]
    connect_result = {'success': [], 'failed': []}
    for rollsite in rollsite_list:
        ip_port = rollsite.split(':')
        roll_site_ip = ip_port[0]
        roll_site_port = int(ip_port[1])
        try:
            connect_test(roll_site_ip, roll_site_port)
            connect_result['success'].append(rollsite)
        except ConnectionError as e:
            connect_result['failed'].append(rollsite)
            logger.exception(f'not connect rollsite {e}')
    logger.info("connect rollsite over")
    return connect_result


def update_all_rollsite_info(exchange_address, rollsite_network_access, party_id, is_secure=False, is_polling=False):
    rollsite_network_access = rollsite_network_access.split(';')[:-1]
    exchange_ip = exchange_address.split(':')[0]
    exchange_port = exchange_address.split(':')[1]
    for rollsite in rollsite_network_access:
        rollsite_ip = rollsite.split(':')[0]
        rollsite_port = rollsite.split(':')[1]
        site_route = {'9999': {'default': [{'port': int(exchange_port), 'ip': exchange_ip, 'is_secure': is_secure,
                                            "is_polling": is_polling}]}}
        logger.info(f"write rollsite info:ip {rollsite_ip}, port {rollsite_port},key {ROLL_SITE_KEY}, route: {site_route}")
        try:
            ret = write_site_route(rollsite_ip, rollsite_port, ROLL_SITE_KEY, site_route, party_id)
        except Exception as e:
            logger.exception(e)
            raise Exception(f"failed to add routing table")




