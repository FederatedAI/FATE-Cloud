import json
import requests
from fate_manager.entity.status_code import SiteStatusCode
from fate_manager.utils.base_utils import current_timestamp
from fate_manager.db.db_models import FederatedInfo, ChangeLog, FateSiteInfo, DeploySite, DeployComponent,FateUserInfo
from fate_manager.entity import item
from fate_manager.entity.types import LogDealType, EditType, SiteStatusType, SiteRunStatusType, DeployStatus, \
    IsValidType, ToyTestOnlyType, DeployType, ProductType
from fate_manager.operation.db_operator import JointOperator, SingleOperation
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import stat_logger as logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager
from fate_manager.settings import site_service_logger as logger
from fate_manager.utils.clear_data import clear_table_data


def get_change_log_task():
    change_log_list = SingleOperation.get_no_deal_list()
    for change_log in change_log_list:
        federated_info_list = DBOperator.query_entity(FederatedInfo, **{"federated_id": change_log.federated_id})
        body = {"caseId": change_log.case_id, "partyId": change_log.party_id}
        logger.info(f"start request cloud IpQueryUri, body:{body}")
        site_info = DBOperator.query_entity(FateSiteInfo, **{"federated_id": change_log.federated_id,
                                                             "party_id": change_log.party_id})
        if not site_info:
            DBOperator.update_entity(ChangeLog, {"federated_id": change_log.federated_id,
                                                 "party_id": change_log.party_id,
                                                 "case_id": change_log.case_id,
                                                 "status": LogDealType.UNKNOWN})
            logger.error('no found site')
            return
        site_info = site_info[0]
        site_signature_req = item.SiteSignatureItem(partyId=site_info.party_id, role=site_info.role,
                                                    appKey=site_info.app_key,
                                                    appSecret=site_info.app_secret).to_dict()

        resp = request_cloud_manager(uri_key="IpQueryUri", data=site_signature_req,
                                     body=body,
                                     url=federated_info_list[0].federated_url)
        logger.info(f"request cloud success:{resp}")
        if resp.get("status") > LogDealType.NO:
            change_log_info = {"federated_id": change_log.federated_id,
                               "party_id": change_log.party_id,
                               "case_id": change_log.case_id,
                               "status": resp.get("status")}
            logger.info(f"start update change log: {change_log_info}")
            DBOperator.update_entity(ChangeLog, change_log_info)

            site_info = {
                "party_id": change_log.party_id,
                "federated_id": change_log.federated_id,
                "edit_status": EditType.YES,
                "read_status": resp.get("status"),
                "update_time": current_timestamp()
            }
            if resp.get("status") == LogDealType.AGREED:
                if change_log.network_access_exits:
                    site_info["network_access_exits"] = change_log.network_access_exits
                if change_log.network_access_entrances:
                    site_info["network_access_entrances"] = change_log.network_access_entrances
            logger.info(f"start update site info: {site_info}")
            DBOperator.update_entity(FateSiteInfo, site_info)
            logger.info("update site info success")


def heart_task():
    federated_site_list = JointOperator.get_home_site()
    for federated_site in federated_site_list:
        if not hasattr(federated_site, "fatesiteinfo"):
            continue
        fate_site_info = federated_site.fatesiteinfo
        if fate_site_info.status == SiteStatusType.JOINED:
            deploy_component_list = DBOperator.query_entity(DeployComponent, **{"party_id": fate_site_info.party_id,
                                                                                "is_valid": IsValidType.YES})
            cloud_system_heart_list = []
            for deploy_component in deploy_component_list:
                cloud_system_heart = {
                    "detectiveStatus": 1,
                    "id": fate_site_info.site_id,
                    "installItems": deploy_component.component_name,
                    "version": deploy_component.component_version,
                }
                if deploy_component.status == SiteRunStatusType.RUNNING:
                    cloud_system_heart["detectiveStatus"] = 2
                cloud_system_heart_list.append(cloud_system_heart)
            if cloud_system_heart_list:
                logger.info(f"start request cloud SystemHeartUri, body:{cloud_system_heart_list}")
                site_signature_req = item.SiteSignatureItem(partyId=fate_site_info.party_id, role=fate_site_info.role,
                                                            appKey=fate_site_info.app_key,
                                                            appSecret=fate_site_info.app_secret).to_dict()

                resp = request_cloud_manager(uri_key="SystemHeartUri", data=site_signature_req,
                                             body=cloud_system_heart_list,
                                             url=federated_site.federated_url)
                logger.info(f"request cloud success:{resp}")


def test_only_task():
    deploy_site_list = DBOperator.query_entity(DeploySite, **{"product_type": ProductType.FATE,
                                                              "toy_test_only": ToyTestOnlyType.TESTING,
                                                              "is_valid": IsValidType.YES})
    for deploy_site in deploy_site_list:
        if deploy_site.deploy_type == DeployType.HYPERION:
            pass


def apply_exchange_task():
    logger.info("get all institution")
    institution = DBOperator.query_entity(FederatedInfo)[0]
    logger.info(f"start request cloud OrganizationQueryUri:{institution.institutions}")
    account = SingleOperation.get_admin_info()
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret,
                                                               ).to_dict()
    institution_signature_item.update({'institutions':institution.institutions})
    try:
        resp = request_cloud_manager(uri_key="UpdateIpQueryUri",
                                     data=institution_signature_item,
                                     body={},
                                     url=None
                                     )
        logger.info(f"request cloud success, return {resp}")
    except Exception as e:
        if "200" in str(e):
            clear_table_data()
        raise Exception(e)
    logger.info(f"request cloud success, return {resp}")
    if resp:
        federated_id = institution.federated_id
        for site in resp:
            partyId = site.get("partyId")
            exchange_name_new = site.get("exchangeName")
            vip_entrances_new = site.get("vipEntrance")
            network_access_entrances_new = site.get("networkAccessEntrances")
            network_access_exits_new = site.get("networkAccessExits")
            site_info = {
                "party_id": partyId,
                "federated_id": federated_id,
                "exchange_read_status": 1,
            }
            if exchange_name_new:
                site_info.update({"exchange_name_new": exchange_name_new})
            if vip_entrances_new:
                site_info.update({"vip_entrances_new": vip_entrances_new})
            if network_access_entrances_new:
                site_info.update({"network_access_entrances_new": network_access_entrances_new})
            if network_access_exits_new:
                site_info.update({"network_access_exits_new": network_access_exits_new})

            DBOperator.safe_save(FateSiteInfo, site_info)
            logger.info(f"update site exchange info {partyId} success")

    logger.info(f"update table {FateSiteInfo}  exchange info success")


