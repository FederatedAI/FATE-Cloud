from fate_manager.utils.base_utils import current_timestamp
from fate_manager.db.db_models import FederatedInfo, ChangeLog, FateSiteInfo, DeploySite, DeployComponent
from fate_manager.entity import item
from fate_manager.entity.types import LogDealType, EditType, SiteStatusType, SiteRunStatusType, DeployStatus, IsValidType, \
    ToyTestOnlyType, DeployType, ProductType
from fate_manager.operation import federated_db_operator
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import stat_logger as logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager


def get_change_log_task():
    change_log_list = federated_db_operator.get_no_deal_list()
    for change_log in change_log_list:
        federated_info_list = DBOperator.query_entity(FederatedInfo, **{"federated_id": change_log.federated_id})
        body = {"caseId": change_log.case_id, "partyId": change_log.party_id}
        logger.info(f"start request cloud IpQueryUri, body:{body}")
        site_signature_req = item.SiteSignatureItem(partyId=change_log.party_id, role=change_log.role,
                                                    appKey=change_log.app_key,
                                                    appSecret=change_log.app_secret).to_dict()

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
            if change_log.network_access_exits:
                site_info["network_access_exits"] = change_log.network_access_exits
            if change_log.network_access_entrances:
                site_info["network_access_entrances"] = change_log.network_access_entrances
            logger.info(f"start update site info: {site_info}")
            DBOperator.update_entity(FateSiteInfo, site_info)
            logger.info("update site info success")


def heart_task():
    federated_site_list = federated_db_operator.get_home_site()
    for federated_site in federated_site_list:
        fate_site_info = federated_site.fatesiteinfo
        if fate_site_info.status == SiteStatusType.JOINED:
            deploy_site_list = DBOperator.query_entity(DeploySite, **{"party_id": fate_site_info.party_id,
                                                                      "is_valid": IsValidType.YES})
            if not deploy_site_list:
                continue
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
                if deploy_component.status == SiteRunStatusType.RUNNING and deploy_site_list[0].deploy_status == DeployStatus.TEST_PASSED:
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

