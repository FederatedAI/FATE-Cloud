from peewee import JOIN

from db.db_models import DB, FederatedInfo, FateSiteInfo, AccountInfo, ApplySiteInfo, FateUserInfo, ChangeLog
from entity.status_code import UserStatusCode
from entity.types import UserRole, IsValidType
from operation.db_operator import DBOperator


def get_home_site():
    with DB.connection_context():
        feature_store_infos = FederatedInfo.select(FederatedInfo.federated_id,
                                                   FederatedInfo.federated_organization,
                                                   FederatedInfo.institutions,
                                                   FateSiteInfo.institutions.alias('fate_manager_institution'),
                                                   FederatedInfo.federated_url,
                                                   FederatedInfo.create_time,
                                                   FateSiteInfo.service_status,
                                                   FateSiteInfo.site_id,
                                                   FateSiteInfo.party_id,
                                                   FateSiteInfo.site_name,
                                                   FateSiteInfo.role,
                                                   FateSiteInfo.status,
                                                   FateSiteInfo.activation_time,
                                                   FateSiteInfo.app_key,
                                                   FateSiteInfo.app_secret,
                                                   FateSiteInfo.fate_version,
                                                   FateSiteInfo.fate_serving_version,
                                                   FateSiteInfo.component_version
                                                   ).join(FateSiteInfo, join_type=JOIN.LEFT_OUTER,
                                                          on=(FederatedInfo.federated_id == FateSiteInfo.federated_id)).where(FederatedInfo.status==1)
        return feature_store_infos


def get_no_deal_list():
    with DB.connection_context():
        change_log_list = ChangeLog.select(ChangeLog.federated_id, ChangeLog.party_id, FateSiteInfo.app_key,
                                           ChangeLog.case_id, FateSiteInfo.app_secret, ChangeLog.status,
                                           FateSiteInfo.role, ChangeLog.network_access_entrances,
                                           ChangeLog.network_access_exits).join(FateSiteInfo, join_type=JOIN.LEFT_OUTER,
                                                                                on=(
                                                                                ChangeLog.party_id == FateSiteInfo.party_id,
                                                                                ChangeLog.federated_id == FateSiteInfo.federated_id)).where(
            ChangeLog.status == 0)
        return change_log_list


def get_party_id_info(party_id):
    with DB.connection_context():
        home_site_item = FederatedInfo.select(FederatedInfo.federated_id,
                                              FederatedInfo.federated_organization,
                                              FederatedInfo.institutions,
                                              FederatedInfo.federated_url,
                                              FederatedInfo.create_time,
                                              FateSiteInfo.party_id,
                                              FateSiteInfo.site_name,
                                              FateSiteInfo.role,
                                              FateSiteInfo.status,
                                              FateSiteInfo.activation_time,
                                              FateSiteInfo.app_key,
                                              FateSiteInfo.app_secret,
                                              FateSiteInfo.fate_version,
                                              FateSiteInfo.fate_serving_version).join(FateSiteInfo,
                                              on=(FederatedInfo.federated_id == FateSiteInfo.federated_id)).where(
                                                  FederatedInfo.status == 1, FateSiteInfo.party_id == party_id)
        return home_site_item


def get_admin_info():
    accounts = DBOperator.query_entity(AccountInfo, role=UserRole.ADMIN, status=IsValidType.YES)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, "no found account")
    return accounts[0]


def get_federated_info():
    federated_infos = DBOperator.query_entity(FederatedInfo, status=1)
    if not federated_infos:
        raise Exception(UserStatusCode.NoFoundFederated, "no found federated info by status:1")
    return federated_infos


def get_apply_site_info(status):
    apply_sites = DBOperator.query_entity(ApplySiteInfo, status=status, order_by="update_time", reverse=True)
    return apply_sites


def update_apply_site_info(status, info):
    with DB.connection_context():
        apply_site_info = ApplySiteInfo.select().where(ApplySiteInfo.status==status)
        apply_site = apply_site_info[0]
        apply_site.status = info.get("status", apply_site.status)
        apply_site.update_time = info.get("institutions", apply_site.institutions)
        apply_site.save(force_insert=False)


def get_user_list(condition):
    with DB.connection_context():
        user_list = FateUserInfo.select().where(FateUserInfo.id % "%{}%".format(condition))
        return [user.to_json() for user in user_list]


def check_user(user_name):
    with DB.connection_context():
        account_info_list = AccountInfo.select().where(AccountInfo.user_name == user_name, AccountInfo.status == 1,
                                                       AccountInfo.role.in_(1, 2))
        return account_info_list
