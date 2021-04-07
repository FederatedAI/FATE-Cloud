from arch.common.base_utils import current_timestamp
from db.db_models import TokenInfo, AccountInfo, FateSiteInfo, FateUserInfo
from entity import item
from entity.status_code import UserStatusCode
from entity.types import ActivateStatus, UserRole, PermissionType, IsValidType, RoleType, SiteStatusType
from operation import federated_db_operator
from operation.db_operator import DBOperator
from service.site_service import get_other_site_list
from settings import user_logger as logger
from utils.request_cloud_utils import request_cloud_manager


def get_user_info(token):
    token_infos = DBOperator.query_entity(TokenInfo, token=token)
    if not token_infos:
        raise Exception(UserStatusCode.NoFoundToken, "token does not exist, please login again")
    token_info = token_infos[0]
    accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, user_name=token_info.user_name)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, "no found account")
    account = accounts[0]
    role = {
        "roleId": account.role,
        "roleName": UserRole.to_str(account.role)
    }

    permission_list = []
    for permission_id in account.permission_list:
        permission_list.append({
            "PermissionId": int(permission_id),
            "PermissionName": PermissionType.to_str(int(permission_id))
        })
    return {"permissionList": permission_list, "role": role, "userName": account.user_name, "userId":""}


def get_user_list(request_data):
    user_list = federated_db_operator.get_user_list(request_data.get("context"))
    return [{"userId": user.user_id, "userName": user.user_name} for user in user_list]


def get_user_access_list(request_data):
    logger.info(f"request data: {request_data}")
    query_info = {"status": IsValidType.YES}
    if request_data.get("userName"):
        query_info["user_name"] = request_data.get("userName")
    if request_data.get("partyId"):
        query_info["party_id"] = request_data.get("partyId")
    if request_data.get("userName"):
        query_info["role"] = request_data.get("roleId")
    account_list = DBOperator.query_entity(AccountInfo, **query_info)
    logger.info(f"account list: {account_list}")
    data = []
    for account in account_list:
        permission_pair_list = []
        for permission_id in account.permission_list:
            permission_pair_list.append({
                "permissionId": int(permission_id),
                "permissionName": PermissionType.to_str(int(permission_id))
            })
        user_access_list_item = {
            "userId": "",
            "userName": account.user_name,
            "site": item.SitePair(partyId=account.party_id, siteName=account.site_name).to_dict(),
            "role": item.Role(roleId=account.role, roleName=UserRole.to_str(account.role)).to_dict(),
            "cloudUser": True if account.fate_manager_id else False,
            "permissionList": permission_pair_list,
            "creator": account.creator,
            "createTime": account.create_time
        }
        data.append(user_access_list_item)
    return data


def add_user(request_data, token):
    token_info_list = DBOperator.query_entity(TokenInfo, **{"token": token})
    user_name = token_info_list[0].user_name
    request_account = DBOperator.query_entity(AccountInfo, **{"user_name": user_name, "status": IsValidType.YES})[0]
    logger.info(f"request data: {request_data}")
    if request_data.get("roleId") == UserRole.BUSINESS and request_data.get("partyId", 0) == 0:
        raise Exception(UserStatusCode.AddUserFailed, f"add user failed:role id {request_data.get('roleId')} party id"
                                                      f"{request_data.get('partyId')}")
    logger.info("start check user")
    account_info_list = federated_db_operator.check_user(user_name=request_data.get("userName"))
    if account_info_list:
        raise Exception(UserStatusCode.AddUserFailed, f'check user failed: user {request_data.get("userName")} '
                                                      f'already exists')
    logger.info("check user success")
    account_info = {
        "user_id": request_data.get("userId"),
        "user_name": request_data.get("userName", ""),
        "role": request_data.get('roleId'),
        "party_id": request_data.get("partyId", 0),
        "site_name": request_data.get("siteName"),
        "creator": request_data.get("creator"),
        "status": IsValidType.YES,
        "permission_list": request_data.get("permissionList"),
        "institutions": request_data.get("institution", request_account.institutions),
        "fate_manager_id": request_account.fate_manager_id,
        "active_url": request_account.active_url
    }
    DBOperator.create_entity(AccountInfo, account_info)


def delete_user(token, request_data):
    token_info_list = DBOperator.query_entity(TokenInfo, **{"token": token})
    if not token_info_list:
        raise Exception(UserStatusCode.NoFoundToken, f"no found token: {token}")
    token_info = token_info_list[0]
    account_info = {"user_id": request_data.get("userId"),
                    "user_name": request_data.get("UserName"),
                    "party_id": request_data.get("partyId"),
                    "site_name": request_data.get("siteName"),
                    "status": IsValidType.YES}
    account_info_list = DBOperator.query_entity(AccountInfo, **account_info)
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundUser, "no found user")
    account = account_info_list[0]
    if account.role == UserRole.ADMIN and account.fate_manager_id:
        raise Exception(UserStatusCode.DeleteUserFailed, f"Admin {request_data.get('UserName')} Could Not Be Delete")
    if account.user_name == token_info.user_name:
        raise Exception(UserStatusCode.DeleteUserFailed, "Could Not Be Delete Self")
    account_info["status"] = IsValidType.NO
    DBOperator.update_entity(AccountInfo, account_info)
    token_info = {
        "user_name": request_data.get("userName"),
        "expire_time": current_timestamp()
    }
    DBOperator.update_entity(TokenInfo, token_info)


def edit_user(request_data):
    a = ["oldPartyId", "oldRoleId", "partyId", "partyId", "permissionList", "siteName", "userId", "userName"]
    account_info_list = federated_db_operator.check_user(user_name=request_data.get("userName"))
    if not account_info_list:
        raise Exception(UserStatusCode.CheckUserFailed, f'check user failed: request_data.get("userName")')
    is_admin = False
    for account in account_info_list:
        if account.role == UserRole.ADMIN and account.fate_manager_id:
            is_admin = True
            break
    if is_admin:
        raise Exception(UserStatusCode.EditUserFailed, f"Admin User Could Not Edite")
    account_info = {
        "user_name": request_data.get("userName"),
        "role": request_data.get("roleId"),
        "party_id": request_data.get("partyId"),
        "site_name": request_data.get("siteName"),
        "permission_list": request_data.get("permissionList"),
        "update_time": current_timestamp()
    }
    if request_data.get("roleId") == UserRole.ADMIN or request_data.get("roleId") == UserRole.DEVELOPER:
        account_info["party_id"] = 0
        account_info["site_name"] = ""
    DBOperator.update_entity(AccountInfo, account_info)


def get_user_site_list():
    federated_info_list = federated_db_operator.get_federated_info()
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"federated_id": federated_info_list[0].federated_id})
    data = []
    for site in site_info_list:
        if site.status != SiteStatusType.JOINED:
            continue
        data.append(item.SitePair(partyId=site.party_id, siteName=site.site_name).to_dict())
    return data


def get_site_info_user_list(request_data):
    account_info_list = DBOperator.query_entity(AccountInfo, **{"party_id": request_data.get("partyId"),
                                                                "status": IsValidType.YES})
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by party id {request_data.get('partyId')}, "
                                                       f"status {IsValidType.YES}")
    data = []
    for account in account_info_list:
        permission_list = account.permission_list
        permission_str = ""
        for permission in permission_list:
            permission_name = PermissionType.to_str(int(permission))
            permission_str += permission_name + ";"
        data.append({
            "userName": account.user_name,
            "role": UserRole.to_str(account.role),
            "permission": permission_str
        })
    return data


def get_login_user_manager_list(request_data):
    account_info_list = DBOperator.query_entity(AccountInfo, **{"user_name": request_data.get("userName"),
                                                                "status": IsValidType.YES})
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by user name {request_data.get('userName')}, "
                                                       f"status {IsValidType.YES}")
    data = []
    for account in account_info_list:
        site_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": account.party_id, "status": SiteStatusType.JOINED})
        data.append({
            "partyId": account.party_id,
            "siteName": account.site_name,
            "role": {"code": site_list[0].role, "desc": RoleType.to_str(site_list[0].role)} if site_list else {}
        })
    return data


def sublogin(request_data):
    # ["PartyId", "passWord", "subTag", "userName"]
    user_info_list = DBOperator.query_entity(FateUserInfo, **{"user_name": request_data.get("userName"),
                                                              "status": IsValidType.YES})
    if not user_info_list:
        raise Exception(UserStatusCode.NoFoundUser, f"no found account by user name {request_data.get('userName')}, "
                                                    f"status {IsValidType.YES}")
    account_info_list = DBOperator.query_entity(AccountInfo, **{
        "user_name": request_data.get("userName"),
        "party_id": request_data.get("PartyId"),
        "status": IsValidType.YES
    })
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by user name {request_data.get('userName')}, "
                                                       f"status {IsValidType.YES}, party id {request_data.get('PartyId')}")
    account_info = account_info_list[0]
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": account_info.party_id,
                                                              "status": SiteStatusType.JOINED})
    resp = {
        "partyId": account_info.party_id,
        "siteName": account_info.site_name
    }
    if site_info_list:
        resp["roleId"] = site_info_list[0].role
        resp["roleName"] = RoleType.to_str(site_info_list[0].role)
    return resp


def change_login(request_data):
    account_info_list = DBOperator.query_entity(AccountInfo, **{"user_name": request_data.get("userName"),
                                                                "party_id": request_data.get("PartyId"),
                                                                "status": IsValidType.YES})
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundUser, f"no found account by user name {request_data.get('userName')}, "
                                                    f"status {IsValidType.YES}, party id {request_data.get('PartyId')}")
    account_info = account_info_list[0]
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": account_info.party_id,
                                                              "status": SiteStatusType.JOINED})
    resp = {
        "partyId": account_info.party_id,
        "siteName": account_info.site_name
    }
    if site_info_list:
        resp["roleId"] = site_info_list[0].role
        resp["roleName"] = RoleType.to_str(site_info_list[0].role)
    return resp


def permission_authority(request_data):
    account = federated_db_operator.get_admin_info()
    logger.info("start request cloud CheckPartyUri")
    institution_signature_item = item.InstitutionSignatureItem(fateManagerId=account.fate_manager_id,
                                                       appKey=account.app_key,
                                                       appSecret=account.app_secret).to_dict()

    resp = request_cloud_manager(uri_key="CheckPartyUri", data=institution_signature_item,
                                 body={"institutions": account.institutions,
                                       "partyId": request_data.get("PartyId")},
                                 url=None)
    logger.info(f"request cloud success:{resp}")


def get_allow_party_list(request_data):
    federated_item_list = get_other_site_list()
    if not request_data.get("roleName"):
        return federated_item_list
    data = []
    for federated_item in federated_item_list:
        tag = True
        for site_item in federated_item.get("siteList"):
            if request_data.get("roleName") == site_item.get("role").get("desc"):
                tag = False
                break
        if tag:
            data.append(federated_item)