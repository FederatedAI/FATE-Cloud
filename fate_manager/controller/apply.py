from fate_manager.db.db_models import AccountInfo, ApplyInstitutionsInfo
from fate_manager.entity import item
from fate_manager.entity.types import IsValidType, UserRole, ApplyReadStatusType
from fate_manager.operation import federated_db_operator, db_operator
from fate_manager.settings import stat_logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager


def apply_result_task(account):
    stat_logger.info("start request cloud ApprovedUri")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    # all
    resp = request_cloud_manager(uri_key="ApprovedUri", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "pageNum": 1,
                                       "pageSize": 100},
                                 url=None
                                 )
    stat_logger.info(f"request cloud success, return {resp}")
    # initiative
    initiative_resp = request_cloud_manager(uri_key="MyApprovedUri", data=institution_signature_item,
                                            body={"institutions": account.institutions},
                                            url=None
                                            )
    initiative_approved_institutions = {}
    for approved_item in initiative_resp:
        initiative_approved_institutions[approved_item.get("institutions")] = approved_item.get("status")
    stat_logger.info(f"request initiative approved, return {initiative_approved_institutions}")

    # get all end status institutions
    update_institutions_models = []
    add_institutions_info = []
    all_institutions = federated_db_operator.get_apply_institutions_info()
    all_institutions_name = [model.institutions for model in all_institutions]
    stat_logger.info(f"all institutions name:{all_institutions_name}")
    for apply_item in resp.get("list"):
        # deal new institutions
        if apply_item["institutions"] not in all_institutions_name:
            institutions_item = {"institutions": apply_item["institutions"], "status": apply_item["status"], "party_status": apply_item["status"]}
            if apply_item["institutions"] in initiative_approved_institutions.keys():
                institutions_item["read_status"] = ApplyReadStatusType.NOT_READ
                institutions_item["party_status"] = initiative_approved_institutions[apply_item["institutions"]]
            add_institutions_info.append(institutions_item)
            continue
        # deal old institutions
        for apply_institutions_info in all_institutions:
            update = False
            if apply_item["institutions"] == apply_institutions_info.institutions:
                if apply_institutions_info.institutions in initiative_approved_institutions:
                    if apply_institutions_info.party_status != initiative_approved_institutions[apply_institutions_info.institutions]:
                        apply_institutions_info.read_status = ApplyReadStatusType.NOT_READ
                        apply_institutions_info.party_status = initiative_approved_institutions[apply_institutions_info.institutions]
                        update = True
                if apply_item["status"] != apply_institutions_info.status or update:
                    apply_institutions_info.status = apply_item["status"]
                    update_institutions_models.append(apply_institutions_info)
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
    return resp if resp else []
