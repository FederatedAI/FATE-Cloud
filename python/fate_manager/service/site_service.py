from copy import deepcopy

from arch.common.base_utils import current_timestamp
from controller import version_controller
from controller.apply import apply_result_task, allow_apply_task
from db.db_models import AccountInfo, FederatedInfo, FateSiteInfo, ApplySiteInfo, DeploySite, ChangeLog
from entity import item
from entity.status_code import UserStatusCode, SiteStatusCode
from entity.types import ActivateStatus, UserRole, SiteStatusType, EditType, ServiceStatusType, FuncDebug, \
    ApplyReadStatusType, RoleType, IsValidType, AuditStatusType, ReadStatusType
from operation import federated_db_operator
from operation.db_operator import DBOperator
from settings import site_service_logger as logger, CLOUD_URL
from utils.request_cloud_utils import request_cloud_manager, get_old_signature_head


def register_fate_site(request_data):
    body = {"registrationLink": request_data.get("registrationLink")}
    logger.info(f"start request cloud ActivateUri, data: {request_data}, body:{body}")
    request_cloud_manager(uri_key="ActivateUri", data=request_data, body=body, url=request_data.get("federatedUrl"))
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
        "network_access_exits": request_data.get("network_access_exits"),
        "status": SiteStatusType.JOINED,
        "edit_status": EditType.YES,
        "service_status": ServiceStatusType.UNAVAILABLE,
        "activation_time": current_timestamp()
    }
    logger.info(f"start save site info:{site_info}")
    DBOperator.safe_save(FateSiteInfo, site_info)
    logger.info("save site info success")

    logger.info(f"start request cloud SiteQueryUri")
    data = request_cloud_manager(uri_key="SiteQueryUri", data=item.SiteSignatureItem(**request_data).to_dict(), body={},
                                 url=request_data.get("federatedUrl"))
    logger.info(f"cloud return:{data}")
    return {"SiteId": data.get("id"), "FederatedId": request_data.get("id")}


def check_register_url(request_data):
    body = {"registrationLink": request_data.get("registrationLink")}
    logger.info(f"start request cloud CheckUri, body:{body}")
    request_cloud_manager(uri_key="CheckUri", data=item.SiteSignatureItem(**request_data).to_dict(), body=body,
                          url=request_data.get("federatedUrl"))
    logger.info("request cloud success")


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
    DBOperator.safe_save(AccountInfo, {"fate_manager_id": account.fate_manager_id, "party_id": account.party_id,
                                       "user_name": account.user_name,
                                       "block_msg": block_msg})
    logger.info("save  account info success")
    return block_msg


def get_home_site_list():
    logger.info("get all site")
    federated_site_list = federated_db_operator.get_home_site()
    logger.info(f"federated_site_list:{federated_site_list}")
    account = federated_db_operator.get_admin_info()
    federated_item_dict = {}
    if not federated_site_list:
        return []
    if len(federated_site_list) == 1 and not hasattr(federated_site_list[0], "fatesiteinfo"):
        federated_item = item.FederatedItem()
        federated_item.federatedOrganization = federated_site_list[0].federated_organization
        federated_item.institutions = federated_site_list[0].institutions
        federated_item.fateManagerInstitutions = account.institutions
        federated_item.federatedId = federated_site_list[0].federated_id
        federated_item.fateManagerInstitutions = account.institutions
        federated_item.createTime = federated_site_list[0].create_time
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
            site_item.acativationTime = fate_site_info.activation_time
            site_item.partyId = fate_site_info.party_id
            site_item.siteName = fate_site_info.site_name
            site_item.serviceStatus = item.IdPair(code=fate_site_info.service_status,
                                                  desc=ServiceStatusType.to_str(fate_site_info.service_status)).to_dict()
            if site.status == SiteStatusType.JOINED:
                logger.info(f"site status is {SiteStatusType.JOINED}, start request cloud SiteQueryUri")
                resp_data = request_cloud_manager(uri_key="SiteQueryUri", data=site_signature_req, body={},
                                                  url=site.federated_url)
                logger.info(f"resp: {resp_data}")
                site_item.status = item.IdPair(code=resp_data.get("status"),
                                               desc=SiteStatusType.to_str(resp_data.get("status"))).to_dict()
                site_info = {
                    "status": site_item.status,
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
                if fate_site_info.fate_version or fate_site_info.fate_serving_version:
                    version_controller.update_version_to_cloud_manager(site)
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
    account = federated_db_operator.get_admin_info()
    allow_apply_task(account)
    allow_institutions_list = account.allow_instituions
    return allow_institutions_list


def get_other_site_list():
    federated_infos = federated_db_operator.get_federated_info()
    account = federated_db_operator.get_admin_info()
    apply_result_task(account)
    apply_site_info_list = federated_db_operator.get_apply_site_info(status=IsValidType.YES)
    if not apply_site_info_list:
        return None
    audit_result_list = apply_site_info_list[0].institutions
    federated_item_list = []
    for audit_result in audit_result_list:

        if audit_result["code"] != AuditStatusType.AGREED:
            continue
        logger.info("start request cloud OtherSiteUri")
        institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                                   appKey=account.app_key,
                                                                   appSecret=account.app_secret).to_dict()
        resp = request_cloud_manager(uri_key="OtherSiteUri", data=institution_signature_item,
                                     body={"institutions": audit_result["desc"],
                                           "pageNum": 1,
                                           "pageSize": 100},
                                     url=None
                                     )
        site_item_list = []
        for site in resp.get("list"):
            site_item = item.SiteItem()
            site_item.siteId = site.get("id")
            site_item.partyId = site.get("partyId")
            site_item.siteName = site.get("siteName")
            site_item.role = item.IdPair(code=site.get("role"), desc=RoleType.to_str(int(site.get("role")))).to_dict()
            site_item.status = item.IdPair(code=site.get("status"), desc=SiteStatusType.to_str(int(site.get("status")))).to_dict()
            site_item.serviceStatus = item.IdPair(code=site.get("detectiveStatus"), desc=ServiceStatusType.to_str(int(site.get("status")))).to_dict()
            site_item.activationTime = site.get("activationTime")
            site_item_list.append(site_item.to_dict())

        federated_item = item.FederatedItem()
        federated_item.federatedId = federated_infos[0].federated_id
        federated_item.fateManagerInstitutions = audit_result["desc"]
        federated_item.size = len(resp.get("list"))
        federated_item.siteList = site_item_list
        federated_item_list.append(federated_item.to_dict(need_none=False))
    return federated_item_list


def query_apply_site():
    apply_site_info_list = federated_db_operator.get_apply_site_info(status=IsValidType.YES)
    audit_result_list = []
    if apply_site_info_list:
        audit_list = apply_site_info_list[0].institutions
        for audit in audit_list:
            if int(audit.get("Readcode")) == ApplyReadStatusType.READ:
                continue
            idpair = item.IdPair(code=audit["code"], desc=audit["desc"])
            audit_result_list.append(idpair.to_dict())
    return audit_result_list


def get_exchange_info():
    account = federated_db_operator.get_admin_info()
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
    account = federated_db_operator.get_admin_info()
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
    for institutions in resp.get("list"):
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
    account = federated_db_operator.get_admin_info()
    request_data["institutions"] = account.institutions
    logger.info("start request cloud AuthorityApply")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="AuthorityApply", data=institution_signature_item,
                                 body=request_data,  url=None)
    audit_result_list = []
    for site_info in request_data["authorityInstitutions"]:
        audit = item.IdPair(code=AuditStatusType.AUDITING, desc=site_info).to_dict()
        audit_result_list.append(audit)
    DBOperator.create_entity(ApplySiteInfo, entity_info={"institutions": audit_result_list, "status": IsValidType.ING})
    apply_site_info_list = federated_db_operator.get_apply_site_info(status=IsValidType.YES)
    audit_pair_list = []
    if not apply_site_info_list:
        for audit in audit_result_list:
            audit_item = item.AuditPair(code=audit["code"], desc=audit["desc"], readCode=AuditStatusType.AUDITING)
            audit_pair_list.append(audit_item.to_dict())
    else:
        audit_pair_list = apply_site_info_list[0]
        for audit_result in audit_result_list:
            hittag = False
            for audit_pair in audit_pair_list:
                if audit_result["desc"] == audit_pair["desc"]:
                    audit_pair["code"] = AuditStatusType.AUDITING
                    audit_pair["readCode"] = ApplyReadStatusType.NOT_READ
                    hittag = True
                    break
            if not hittag:
                audit_item = item.AuditPair(code=audit_result["code"], desc=audit_result["desc"], readCode=ApplyReadStatusType.NOT_READ)
                audit_pair_list.append(audit_item.to_dict())
    if not apply_site_info_list:
        DBOperator.create_entity(ApplySiteInfo, entity_info={"institutions": audit_pair_list, "status": IsValidType.YES})
    else:
        federated_db_operator.update_apply_site_info(status=IsValidType.YES, info={"institutions": audit_pair_list})


def read_apply_site():
    apply_sites = federated_db_operator.get_apply_site_info(status=IsValidType.YES)
    if apply_sites:
        audit_pair_list = apply_sites[0].institutions
        for audit_pair in audit_pair_list:
            if audit_pair["code"] == AuditStatusType.AUDITING:
                continue
            audit_pair["readCode"] = ApplyReadStatusType.READ
        federated_db_operator.update_apply_site_info(status=IsValidType.YES, info={"institutions": audit_pair_list})
    else:
        raise Exception(SiteStatusCode.NoFoundSite, f"no found apply site by status {IsValidType.YES}")


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
        "editStatus": item.IdPair(code=site.edit_status, desc=EditType.to_str(site.edit_status)).to_dict(),
        "fateServingVersion": site.fate_serving_version,
        "fateVersion": site.fate_version,
        "federatedId": site.federated_id,
        "federatedOrganization": site.federated_organization,
        "institutions": site.institutions,
        "networkAccessEntrances": site.network_access_entrances,
        "networkAccessExits": site.network_access_exits,
        "partyId": site.party_id,
        "registrationLink": site.registration_link,
        "siteId": site.site_id,
        "siteName": site.site_name,
        "role": item.IdPair(code=site.role, desc=RoleType.to_str(site.role)).to_dict(),
        "status": item.IdPair(code=site.status, desc=SiteStatusType.to_str(site.status)).to_dict(),
        "versionEditStatus": item.IdPair(code=EditType.YES, desc=EditType.to_str(EditType.YES)).to_dict()
    }
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
                                       "networkAccessExits": request_data.get("networkAccessExits")
                                       }, url=federated_info.federated_url)
    logger.info(f"request cloud success:{resp}")
    change_log_info = {
        "partyId": request_data.get("partyId"),
        "networkAccessEntrances": request_data.get("networkAccessEntrances"),
        "networkAccessExits": request_data.get("networkAccessExits"),
        "status": site.status,
        "federated_id": request_data.get("federatedId"),
        "federated_organization": request_data.get("federatedOrganization"),
        "case_id":resp.get("caseId")
    }
    DBOperator.create_entity(ChangeLog, entity_info=change_log_info)
    DBOperator.update_entity(FateSiteInfo, {"site_id": site.site_id, "federated_id": site.federated_id,
                                            "party_id": site.party_id, "edit_status": EditType.NO})


def telnet_ip(request_data):
    federated_info = federated_db_operator.get_party_id_info(request_data.get("partyId"))
    logger.info("start request cloud CheckWebUri")
    request_cloud_manager(uri_key="CheckWebUri", data=None,
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
    federated_info_list = federated_db_operator.get_party_id_info(party_id=request_data.get("PartyId"))
    if federated_info_list:
        federated_item = federated_info_list[0]
        federated_item.component_version = request_data.get("componentVersion")
        version_controller.update_version_to_cloud_manager(federated_item)
    else:
        raise Exception(SiteStatusCode.NoFoundSite, "no found site")


def get_apply_log():
    apply_site_list = DBOperator.query_entity(ApplySiteInfo, **{"status": IsValidType.NO})
    apply_log_list = []
    for apply_site in apply_site_list:
        apply_site_list.append({"applyTime": apply_site.create_time,"content": apply_site.institutions})
    return apply_log_list


def function_read():
    account = federated_db_operator.get_admin_info()
    resp_list = []
    for block_item in account.block_msg:
        block_item["readStatus"] = ApplyReadStatusType.READ
        resp_list.append(block_item)
    DBOperator.update_entity(AccountInfo, {"fate_manager_id": account.fate_manager_id,
                                           "party_id": account.party_id,
                                           "block_msg": resp_list,
                                           "user_name": account.user_name})


def check_site(request_data):
    federated_info_list = federated_db_operator.get_party_id_info(party_id=request_data.get("PartyId"))
    if federated_info_list:
        federated_item = federated_info_list[0]
        uri = CLOUD_URL["CheckAuthorityUri"]
        # guest head
        old_signature_item = item.OldSignatureItem(partyId=request_data.get("srcPartyId"),
                                                   role=request_data.get("role"),
                                                   appKey=request_data.get("appKey"),
                                                   appSecret=request_data.get("appSecret"))
        old_head = get_old_signature_head(uri, old_signature_item.to_dict(),
                                          body={"partyId": request_data.get("srcPartyId")})
        check_site_body = {
            "HTTP_BODY": {"partyId": request_data.get('srcPartyId')},
            "HTTP_URI": uri,
            "HTTP_HEADER": old_head
        }
        logger.info("start request cloud CheckAuthorityUri")
        site_signature_req = item.SiteSignatureItem(partyId=request_data.get("dstPartyId"),
                                                    role=int(federated_item.role),
                                                    appKey=federated_item.app_key,
                                                    appSecret=federated_item.app_secret).to_dict()
        resp = request_cloud_manager(uri_key="CheckAuthorityUri", data=site_signature_req,
                                     body=check_site_body, url=federated_item.federated_url)
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



