from db.db_models import AccountInfo, ApplyInstitutionsInfo
from entity import item, types
from entity.types import IsValidType, UserRole, ApplyReadStatusType
from operation import federated_db_operator, db_operator
from settings import stat_logger
from utils.request_cloud_utils import request_cloud_manager


def apply_result_task(account):
    stat_logger.info("start request cloud ApprovedUri")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="ApprovedUri", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "pageNum": 1,
                                       "pageSize": 100},
                                 url=None
                                 )
    stat_logger.info(f"request cloud success, return {resp}")
    # get all end status institutions
    update_institutions_models = []
    add_institutions_info = []
    all_institutions = federated_db_operator.get_apply_institutions_info()
    all_institutions_name = [model.institutions for model in all_institutions]
    stat_logger.info(f"all institutions name:{all_institutions_name}")
    for apply_institutions_info in all_institutions:
        for apply_item in resp.get("list"):
            if apply_item["institutions"] == apply_institutions_info.institutions:
                if apply_item["status"] != apply_institutions_info.status:
                    apply_institutions_info.status = apply_item["status"]
                    apply_institutions_info.read_status = ApplyReadStatusType.NOT_READ
                    update_institutions_models.append(apply_institutions_info)
            elif apply_item["institutions"] not in all_institutions_name:
                add_institutions_info.append({"institutions": apply_item["institutions"],
                                              "status": apply_item["status"],
                                              "read_status": ApplyReadStatusType.NOT_READ})
    stat_logger.info(f"update institutions models: {update_institutions_models}")
    stat_logger.info(f"add institutions info: {add_institutions_info}")
    for institutions_model in update_institutions_models:
        model_info = institutions_model.to_json()
        db_operator.DBOperator.update_entity(ApplyInstitutionsInfo, model_info)
    for institutions_info in add_institutions_info:
        db_operator.DBOperator.create_entity(ApplyInstitutionsInfo, institutions_info)


def allow_apply_task(account):
    stat_logger.info("start request cloud AuthorityApplied")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="AuthorityApplied", data=institution_signature_item,
                                 body={"institutions": account.institutions},
                                 url=None
                                 )
    stat_logger.info(f"request cloud success, return {resp}")
    data = {"allow_instituions": resp,
            "user_name": account.user_name,
            "role": UserRole.ADMIN,
            "status": IsValidType.YES,
            "fate_manager_id": account.fate_manager_id}
    db_operator.DBOperator.update_entity(AccountInfo, data)