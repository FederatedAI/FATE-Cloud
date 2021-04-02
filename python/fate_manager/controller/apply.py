from arch.common.base_utils import current_timestamp
from db.db_models import ApplySiteInfo, FateOtherSiteInfo, AccountInfo
from entity import item, types
from entity.types import IsValidType, UserRole
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
    wait_audit_model_list = federated_db_operator.get_apply_site_info(status=IsValidType.ING)
    wait_audit_list = wait_audit_model_list[0].institutions if wait_audit_model_list else []
    stat_logger.info(f"wait audit list:{wait_audit_list}")
    valid_audit_model_list = federated_db_operator.get_apply_site_info(status=IsValidType.YES)
    valid_audit_list = valid_audit_model_list[0].institutions if valid_audit_model_list else []
    update_wait = False
    cancel_audit = []
    for apply_item in resp.get("list"):
        for wait_audit in wait_audit_list:
            if apply_item["institutions"] == wait_audit.get("desc"):
                if apply_item["status"] != wait_audit.get("code"):
                    update_wait = True
                wait_audit["code"] = apply_item["status"]
                break

        hitvalid = False
        for valid_audit in valid_audit_list:
            if apply_item["institutions"] == valid_audit.get("desc"):
                if apply_item["status"] == int(types.AuditStatusType.AGREED):
                    flush_other_site_info(account, apply_item["institutions"], valid=True)
                if apply_item["status"] != valid_audit.get("code"):
                    valid_audit["code"] = apply_item["status"]
                    valid_audit["readCode"] = types.ApplyReadStatusType.NOT_READ
                    if apply_item["status"] == types.AuditStatusType.CANCEL:
                        cancel_audit.append(item.IdPair(code=valid_audit["code"], desc=apply_item["institutions"]).to_dict())
                        flush_other_site_info(account, apply_item["institutions"], valid=False)
                hitvalid = True
                break
        if not hitvalid:
            valid_audit_item = item.AuditPair(code=apply_item["status"], desc=apply_item["institutions"],
                                              readCode=types.ApplyReadStatusType.NOT_READ)
            if apply_item["status"] == types.AuditStatusType.AGREED:
                flush_other_site_info(account, apply_item["institutions"], valid=True)
            valid_audit_list.append(valid_audit_item.to_dict())
    if update_wait:
        federated_db_operator.update_apply_site_info(status=IsValidType.ING, info={"status": IsValidType.NO,
                                                                                   "institutions": wait_audit_list})
    if cancel_audit:
        db_operator.DBOperator.create_entity(ApplySiteInfo, entity_info={"institutions": cancel_audit, "status": IsValidType.NO})
    if valid_audit_model_list:
        federated_db_operator.update_apply_site_info(status=IsValidType.YES, info={"institutions": valid_audit_list,
                                                                                   "update_time": current_timestamp()})


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
    db_operator.DBOperator.execute_update(AccountInfo, data)


def flush_other_site_info(account, institutions, valid):
    stat_logger.info("start request cloud OtherSiteUri")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                               appKey=account.app_key,
                                                               appSecret=account.app_secret).to_dict()
    resp = request_cloud_manager(uri_key="OtherSiteUri", data=institution_signature_item,
                                 body={"institutions": institutions,
                                       "pageNum": 1,
                                       "pageSize": 100},
                                 url=None
                                 )
    stat_logger.info(f"request cloud success, return {resp}")
    for site in resp.get("list"):
        is_valid = IsValidType.NO
        if valid:
            is_valid = IsValidType.YES
        update_status = db_operator.DBOperator.update_entity(FateOtherSiteInfo, {"party_id": site["partyId"],
                                                                                 "site_id": site["id"],
                                                                                 "site_name": site["siteName"],
                                                                                 "is_valid": is_valid
                                                                                 })

        if not update_status:
            other_site_info = {
                "party_id": site["partyId"],
                "site_id": site["id"],
                "site_name": site["siteName"],
                "institutions": site["institutions"],
                "role": site["role"],
                "service_status": site["detectiveStatus"],
                "status": site["status"],
                "is_valid": int(is_valid),
                "activation_time": site["activationTime"],
                "create_time": site["createTime"],
                "update_time": site["updateTime"]
            }
            db_operator.DBOperator.create_entity(FateOtherSiteInfo, other_site_info)

