import operator
import peewee

from fate_manager.db.db_models import DB, FateSiteJobInfo, AccountInfo, FateUserInfo, ApplyInstitutionsInfo, \
    FederatedInfo, ChangeLog, FateSiteInfo
from fate_manager.entity.status_code import UserStatusCode
from fate_manager.entity.types import UserRole, LogDealType, IsValidType
from fate_manager.settings import stat_logger
from fate_manager.utils.base_utils import current_timestamp, timestamp_to_date


class DBOperator:
    @classmethod
    @DB.connection_context()
    def create_entity(cls, entity_model, entity_info):
        instance = entity_model()
        if 'create_time' not in entity_info:
            instance.create_time = current_timestamp()
        if 'update_time' not in entity_info:
            instance.update_time = current_timestamp()
        for k, v in entity_info.items():
            if hasattr(entity_model, k):
                setattr(instance, k, v)
        try:
            rows = instance.save(force_insert=True)
            if rows != 1:
                raise Exception("Create {} failed".format(entity_model))
        except peewee.IntegrityError as e:
            if e.args[0] == 1062:
                stat_logger.warning(e)
            else:
                raise Exception("Create {} failed:\n{}".format(entity_model, e))
        except Exception as e:
            raise Exception("Create {} failed:\n{}".format(entity_model, e))

    @classmethod
    @DB.connection_context()
    def delete_entity(cls, entity_model, **kwargs):
        filters = []
        for k, v in kwargs.items():
            if hasattr(entity_model, k):
                filters.append(operator.attrgetter(k)(entity_model) == v)
        if filters:
            instances = entity_model.select().where(*filters)
            if instances:
                for instance in instances:
                    instance.delete_instance()
                return True
            return False
        return False


    @classmethod
    @DB.connection_context()
    def delete_entity_data(cls, entity_model, **kwargs):

        instances = entity_model.select()
        if instances:
            for instance in instances:
                instance.delete_instance()
            return True

    @classmethod
    @DB.connection_context()
    def update_entity(cls, entity_model, entity_info):
        query_filters = []
        primary_keys = entity_model.get_primary_keys_name()
        for p_k in primary_keys:
            if p_k in entity_info:
                query_filters.append(operator.attrgetter(p_k)(entity_model) == entity_info[p_k])
        instances = entity_model.select().where(*query_filters)
        if instances:
            instance = instances[0]
        else:
            # raise Exception("can not found the {}".format(entity_model.__class__.__name__))
            return False
        update_filters = query_filters[:]
        update_info = {}
        update_info.update(entity_info)
        update_info["update_time"] = current_timestamp()
        update_info["update_date"] = timestamp_to_date(update_info["update_time"])
        cls.execute_update(old_obj=instance, model=entity_model,
                           update_info=update_info,
                           update_filters=update_filters)
        return True

    @classmethod
    @DB.connection_context()
    def safe_save(cls, entity_model, entity_info):
        primary_keys = entity_model.get_primary_keys_name()
        filters = []
        for k in primary_keys:
            filters.append(operator.attrgetter(k)(entity_model) == entity_info[k])
        instances = entity_model.select().where(*filters)
        if instances:
            instance = instances[0]
            update_info = {}
            update_info.update(entity_info)
            update_info["update_time"] = current_timestamp()
            for k, v in update_info.items():
                if hasattr(entity_model, k):
                    setattr(instance, k, v)
            instance.save(force_insert=False)
        else:
            instance = entity_model()
            if 'create_time' not in entity_info:
                entity_info["create_time"] = current_timestamp()
            if 'update_time' not in entity_info:
                entity_info["update_time"] = current_timestamp()
            for k, v in entity_info.items():
                if hasattr(entity_model, k):
                    setattr(instance, k, v)
            try:
                rows = instance.save(force_insert=True)
                if rows != 1:
                    raise Exception("Create {} failed".format(entity_model))
            except peewee.IntegrityError as e:
                if e.args[0] == 1062:
                    stat_logger.warning(e)
                else:
                    raise Exception("Create {} failed:\n{}".format(entity_model, e))
            except Exception as e:
                raise Exception("Create {} failed:\n{}".format(entity_model, e))

    @classmethod
    @DB.connection_context()
    def execute_update(cls, old_obj, model, update_info, update_filters):
        update_fields = {}
        for k, v in update_info.items():
            if hasattr(model, k) and k not in model.get_primary_keys_name():
                update_fields[operator.attrgetter(k)(model)] = v
        if update_fields:
            if update_filters:
                stat_logger.info(f'update fields: {update_fields}')
                stat_logger.info(f'update filters: {update_filters}')
                operate = old_obj.update(update_fields).where(*update_filters)
            else:
                operate = old_obj.update(update_fields)
            return operate.execute() > 0
        else:
            return False

    @classmethod
    @DB.connection_context()
    def query_entity(cls, entity_model, reverse=None, order_by=None, **kwargs):
        filters = []
        for k, v in kwargs.items():
            if hasattr(entity_model, k):
                filters.append(operator.attrgetter(k)(entity_model) == v)
        if not filters:
            instances = entity_model.select()
        else:
            instances = entity_model.select().where(*filters)
        if reverse is not None:
            if not order_by or not hasattr(entity_model, f"{order_by}"):
                order_by = "create_time"
            if reverse is True:
                instances = instances.order_by(getattr(entity_model, f"{order_by}").desc())
            elif reverse is False:
                instances = instances.order_by(getattr(entity_model, f"{order_by}").asc())
        return [item for item in instances]


class JointOperator:
    @classmethod
    @DB.connection_context()
    def get_home_site(cls):
        feature_store_infos = FederatedInfo.select(FederatedInfo.federated_id,
                                                   FederatedInfo.federated_organization,
                                                   FederatedInfo.institutions,
                                                   FederatedInfo.institution,
                                                   FederatedInfo.federated_organization_create_time,
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
                                                   ).join(FateSiteInfo, join_type=peewee.JOIN.LEFT_OUTER,
                                                          on=(FederatedInfo.federated_id == FateSiteInfo.federated_id)).where(FederatedInfo.status==1)
        return [feature_store_info for feature_store_info in feature_store_infos]

    @classmethod
    @DB.connection_context()
    def get_party_id_info(cls, party_id):
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
                                               FateSiteInfo.fate_serving_version
                                               ).join(FateSiteInfo, on=(FederatedInfo.federated_id ==
                                                                        FateSiteInfo.federated_id)).where(
            FederatedInfo.status == 1, FateSiteInfo.party_id == party_id)
        return [home_site_item for home_site_item in home_site_items]


class SingleOperation:
    @classmethod
    @DB.connection_context()
    def get_no_deal_list(cls):
        change_log_list = ChangeLog.select().where(ChangeLog.status == LogDealType.NO)
        return [change_log for change_log in change_log_list]

    @classmethod
    def get_admin_info(cls):
        accounts = DBOperator.query_entity(AccountInfo, role=UserRole.ADMIN, status=IsValidType.YES)
        if not accounts:
            raise Exception(UserStatusCode.NoFoundAccount, "no found account")
        return accounts[0]

    @classmethod
    def get_federated_info(cls):
        federated_infos = DBOperator.query_entity(FederatedInfo, status=1)
        if not federated_infos:
            raise Exception(UserStatusCode.NoFoundFederated, "no found federated info by status:1")
        return federated_infos

    @classmethod
    def get_apply_institutions_info(cls, status=None):
        if not status:
            apply_sites = DBOperator.query_entity(ApplyInstitutionsInfo, order_by="update_time", reverse=True)
        else:
            apply_sites = DBOperator.query_entity(ApplyInstitutionsInfo, status=status, order_by="update_time", reverse=True)
        return apply_sites

    @classmethod
    @DB.connection_context()
    def update_apply_institutions_read_status(cls, read_status, info):
        update_list = []
        apply_institutions_list = ApplyInstitutionsInfo.select().where(ApplyInstitutionsInfo.read_status == read_status)
        for institution_item in apply_institutions_list:
            institution_item.read_status = info.get("read_status", institution_item.status)
            institution_item.save(force_insert=False)
            update_list.append(institution_item.institutions)
        return update_list

    @classmethod
    @DB.connection_context()
    def get_user_list(cls, condition):
        user_list = FateUserInfo.select().where(FateUserInfo.user_name % "%{}%".format(condition))
        return [user for user in user_list]

    @classmethod
    @DB.connection_context()
    def check_user(cls, user_name):
        account_info_list = AccountInfo.select().where(AccountInfo.user_name == user_name)
        return [account_info for account_info in account_info_list]

    @classmethod
    @DB.connection_context()
    def query_fate_site_job(cls, **kwargs):
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
            instances = cls.fate_site_job_info_select()
        else:
            instances = cls.fate_site_job_info_select().where(*filters)
        return [item for item in instances]

    @classmethod
    def fate_site_job_info_select(cls):
        return FateSiteJobInfo.select(FateSiteJobInfo.institutions, FateSiteJobInfo.party_id, FateSiteJobInfo.site_name,
                                      FateSiteJobInfo.role,
                                      FateSiteJobInfo.job_id, FateSiteJobInfo.job_elapsed, FateSiteJobInfo.roles,
                                      FateSiteJobInfo.other_party_id,
                                      FateSiteJobInfo.other_institutions, FateSiteJobInfo.job_type,
                                      FateSiteJobInfo.job_create_day,
                                      FateSiteJobInfo.job_create_day_date, FateSiteJobInfo.job_start_time,
                                      FateSiteJobInfo.job_end_time,
                                      FateSiteJobInfo.status, FateSiteJobInfo.institutions_party_id, FateSiteJobInfo.is_end)


