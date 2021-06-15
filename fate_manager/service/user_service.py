from fate_manager.utils.base_utils import current_timestamp
from fate_manager.db.db_models import TokenInfo, AccountInfo, FateSiteInfo, FateUserInfo, AccountSiteInfo
from fate_manager.entity import item
from fate_manager.entity.status_code import UserStatusCode
from fate_manager.entity.types import ActivateStatus, UserRole, PermissionType, IsValidType, RoleType, SiteStatusType
from fate_manager.operation import federated_db_operator
from fate_manager.operation.db_operator import DBOperator
from fate_manager.service.site_service import get_other_site_list
from fate_manager.settings import user_logger as logger
from fate_manager.utils.request_cloud_utils import request_cloud_manager


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
    if request_data.get("roleId"):
        query_info["role"] = request_data.get("roleId")
    account_list = DBOperator.query_entity(AccountInfo, **query_info)
    logger.info(f"account list: {account_list}")
    if request_data.get("partyId"):
        account_site_list = DBOperator.query_entity(AccountSiteInfo, **{"party_id": request_data.get("partyId")})
        account_list_temp = []
        for account_site in account_site_list:
            for account in account_list:
                if account.user_name == account_site.user_name and account.fate_manager_id == account_site.fate_manager_id:
                    account_list_temp.append(account)
        account_list = account_list_temp
    data = []
    for account in account_list:
        permission_pair_list = []
        for permission_id in account.permission_list:
            permission_pair_list.append({
                "permissionId": int(permission_id),
                "permissionName": PermissionType.to_str(int(permission_id))
            })
        account_site_list = DBOperator.query_entity(AccountSiteInfo, **{"user_name": account.user_name})
        account_site = account_site_list[0] if account_site_list else None
        user_access_list_item = {
            "userId": "",
            "userName": account.user_name,
            "site": item.SitePair(partyId=account_site.party_id, siteName=account_site.site_name).to_dict() if account_site else {},
            "role": item.Role(roleId=account.role, roleName=UserRole.to_str(account.role)).to_dict(),
            "cloudUser": True if account.cloud_user else False,
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
    if account_info_list and account_info_list[0].status == IsValidType.YES:
        raise Exception(UserStatusCode.AddUserFailed, f'check user failed: user {request_data.get("userName")} '
                                                      f'already exists')
    logger.info("check user success")
    account_info = {
        "user_id": request_data.get("userId"),
        "user_name": request_data.get("userName", ""),
        "role": request_data.get('roleId'),
        "creator": request_data.get("creator"),
        "status": IsValidType.YES,
        "permission_list": request_data.get("permissionList"),
        "institutions": request_data.get("institution", request_account.institutions),
        "fate_manager_id": request_account.fate_manager_id,
        "active_url": request_account.active_url,
        "app_key": request_account.app_key,
        "app_secret": request_account.app_secret
    }
    DBOperator.safe_save(AccountInfo, account_info)
    if request_data.get("partyId", 0):
        account_site_info = {
            "party_id": request_data.get("partyId", 0),
            "user_name": request_data.get("userName", ""),
            "fate_manager_id": request_account.fate_manager_id,
            "site_name": request_data.get("siteName")
        }
        DBOperator.create_entity(AccountSiteInfo, account_site_info)


def delete_user(token, request_data):
    token_info_list = DBOperator.query_entity(TokenInfo, **{"token": token})
    if not token_info_list:
        raise Exception(UserStatusCode.NoFoundToken, f"no found token: {token}")
    token_info = token_info_list[0]
    account_info = {"user_name": request_data.get("userName")}
    account_info_list = DBOperator.query_entity(AccountInfo, **account_info)
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundUser, "no found user")
    account = account_info_list[0]
    if account.role == UserRole.ADMIN and account.fate_manager_id:
        raise Exception(UserStatusCode.DeleteUserFailed, f"Admin {request_data.get('userName')} Could Not Be Delete")
    if account.user_name == token_info.user_name:
        raise Exception(UserStatusCode.DeleteUserFailed, "Could Not Be Delete Self")
    account_info["status"] = IsValidType.NO
    DBOperator.update_entity(AccountInfo, account_info)
    token_info = {
        "user_name": request_data.get("userName"),
        "expire_time": current_timestamp()
    }
    DBOperator.update_entity(TokenInfo, token_info)
    try:
        DBOperator.delete_entity(AccountSiteInfo, **{"user_name": request_data.get("userName")})
    except:
        logger.info("account no found site")


def edit_user(request_data):
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
        "site_name": request_data.get("siteName"),
        "permission_list": request_data.get("permissionList"),
        "update_time": current_timestamp()
    }
    logger.info(f"update accont info: {account_info}")
    DBOperator.update_entity(AccountInfo, account_info)
    if request_data.get("roleId") == UserRole.BUSINESS and request_data.get("partyId"):
        account_site_info = {"user_name": request_data.get("userName"),
                             "fate_manager_id": account.fate_manager_id,
                             "party_id": request_data.get("partyId"),
                             "site_name": request_data.get("siteName")}
        logger.info(f"save account info: {account_site_info}")
        DBOperator.safe_save(AccountSiteInfo, account_site_info)
    else:
        try:
            DBOperator.delete_entity(AccountSiteInfo, **{"user_name": request_data.get("userName")})
        except:
            logger.info("account no found site")


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
    user_info_list = DBOperator.query_entity(AccountSiteInfo, **{"party_id": request_data["partyId"]})
    data = []
    for account in user_info_list:
        account_info = DBOperator.query_entity(AccountInfo, **{"user_name": account.user_name})[0]
        permission_list = account_info.permission_list
        permission_str = ""
        for permission in permission_list:
            permission_name = PermissionType.to_str(int(permission))
            permission_str += permission_name + ";"
        data.append({
            "userName": account_info.user_name,
            "role": UserRole.to_str(account_info.role),
            "permission": permission_str
        })
    return data


def get_login_user_manager_list(request_data):
    account_info_list = DBOperator.query_entity(AccountInfo, **{"user_name": request_data.get("userName"),
                                                                "status": IsValidType.YES})
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by user name {request_data.get('userName')}, "
                                                       f"status {IsValidType.YES}")
    account_site_info_list = DBOperator.query_entity(AccountSiteInfo, **{"user_name": request_data.get("userName")})
    data = []
    for account_site in account_site_info_list:
        site_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": account_site.party_id,
                                                             "status": SiteStatusType.JOINED})
        if site_list:
            data.append({
                "partyId": account_site.party_id,
                "siteName": site_list[0].site_name,
                "role": {"code": site_list[0].role, "desc": RoleType.to_str(site_list[0].role)} if site_list else {}
            })
    return data


def sublogin(request_data):
    user_info_list = DBOperator.query_entity(FateUserInfo, **{"user_name": request_data.get("userName"),
                                                              "status": IsValidType.YES})
    if not user_info_list:
        raise Exception(UserStatusCode.NoFoundUser, f"no found account by user name {request_data.get('userName')}, "
                                                    f"status {IsValidType.YES}")
    account_info_list = DBOperator.query_entity(AccountInfo, **{
        "user_name": request_data.get("userName"),
        "status": IsValidType.YES
    })
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by user name {request_data.get('userName')}, "
                                                       f"status {IsValidType.YES}")
    account_info = account_info_list[0]
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": request_data.get("partyId"),
                                                              "status": SiteStatusType.JOINED})
    resp = {
        "partyId": request_data.get("partyId"),
        "siteName": site_info_list[0].site_name if site_info_list else ""
    }
    if site_info_list:
        resp["roleId"] = site_info_list[0].role
        resp["roleName"] = RoleType.to_str(site_info_list[0].role)
    return resp


def change_login(request_data):
    account_info_list = DBOperator.query_entity(AccountInfo, **{"user_name": request_data.get("userName"),
                                                                "status": IsValidType.YES})
    if not account_info_list:
        raise Exception(UserStatusCode.NoFoundUser, f"no found account by user name {request_data.get('userName')}, "
                                                    f"status {IsValidType.YES}, party id {request_data.get('partyId')}")
    account_info = account_info_list[0]
    site_info_list = DBOperator.query_entity(FateSiteInfo, **{"party_id": request_data.get('partyId'),
                                                              "status": SiteStatusType.JOINED})
    resp = {
        "partyId": request_data.get("partyId"),
        "siteName": site_info_list[0].site_name if site_info_list else ""
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
                                       "partyId": request_data.get("partyId")},
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