import operator

from peewee import JOIN

from fate_manager.db.db_models import DB, FederatedInfo, FateSiteInfo, AccountInfo, ApplyInstitutionsInfo, FateUserInfo, ChangeLog, \
    FateSiteJobInfo
from fate_manager.entity.status_code import UserStatusCode
from fate_manager.entity.types import UserRole, IsValidType, LogDealType
from fate_manager.operation.db_operator import DBOperator


@DB.connection_context()
def get_home_site():
    feature_store_infos = FederatedInfo.select(FederatedInfo.federated_id,
                                               FederatedInfo.federated_organization,
                                               FederatedInfo.institutions,
                                               FederatedInfo.institution,
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
    return [feature_store_info for feature_store_info in feature_store_infos]


@DB.connection_context()
def get_no_deal_list():
    change_log_list = ChangeLog.select().where(ChangeLog.status == LogDealType.NO)
    return [change_log for change_log in change_log_list]


@DB.connection_context()
def get_party_id_info(party_id):
    home_site_items = FederatedInfo.select(FederatedInfo.federated_id,
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
    return [home_site_item for home_site_item in home_site_items]


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


def get_apply_institutions_info(status=None):
    if not status:
        apply_sites = DBOperator.query_entity(ApplyInstitutionsInfo, order_by="update_time", reverse=True)
    else:
        apply_sites = DBOperator.query_entity(ApplyInstitutionsInfo, status=status, order_by="update_time", reverse=True)
    return apply_sites


@DB.connection_context()
def update_apply_institutions_read_status(read_status, info):
    update_list = []
    apply_institutions_list = ApplyInstitutionsInfo.select().where(ApplyInstitutionsInfo.read_status == read_status)
    for institution_item in apply_institutions_list:
        institution_item.read_status = info.get("read_status", institution_item.status)
        institution_item.save(force_insert=False)
        update_list.append(institution_item.institutions)
    return update_list


@DB.connection_context()
def get_user_list(condition):
    user_list = FateUserInfo.select().where(FateUserInfo.user_name % "%{}%".format(condition))
    return [user for user in user_list]


@DB.connection_context()
def check_user(user_name):
    account_info_list = AccountInfo.select().where(AccountInfo.user_name == user_name)
    return [account_info for account_info in account_info_list]


@DB.connection_context()
def query_fate_site_job(**kwargs):
    filters = []
    for k, v in kwargs.items():
        if k in ['job_create_day_date'] and isinstance(v, list):
            b_timestamp = v[0]
            e_timestamp = v[1]
            filters.append(getattr(FateSiteJobInfo, k).between(b_timestamp, e_timestamp))
        # if k in ['job_create_day_date'] and isinstance(v, list):
        #     filters.append(getattr(FateSiteJobInfo, k) > v[0])
        #     filters.append(getattr(FateSiteJobInfo, k) <= v[1])
        elif hasattr(FateSiteJobInfo, k):
            filters.append(operator.attrgetter(k)(FateSiteJobInfo) == v)
    if not filters:
        instances = FateSiteJobInfoSelect()
    else:
        instances = FateSiteJobInfoSelect().where(*filters)
    return [item for item in instances]


def FateSiteJobInfoSelect():
    return FateSiteJobInfo.select(FateSiteJobInfo.institutions, FateSiteJobInfo.party_id, FateSiteJobInfo.site_name,
                                  FateSiteJobInfo.role,
                                  FateSiteJobInfo.job_id, FateSiteJobInfo.job_elapsed, FateSiteJobInfo.roles,
                                  FateSiteJobInfo.other_party_id,
                                  FateSiteJobInfo.other_institutions, FateSiteJobInfo.job_type,
                                  FateSiteJobInfo.job_create_day,
                                  FateSiteJobInfo.job_create_day_date, FateSiteJobInfo.job_start_time,
                                  FateSiteJobInfo.job_end_time,
                                  FateSiteJobInfo.status, FateSiteJobInfo.institutions_party_id, FateSiteJobInfo.is_end)
