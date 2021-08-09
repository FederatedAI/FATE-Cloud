import base64
import hmac
import json

from fate_manager.utils.base_utils import current_timestamp
from fate_manager.db.db_models import AccountInfo, FateUserInfo, FederatedInfo, TokenInfo,FateSiteInfo
from fate_manager.entity.status_code import UserStatusCode,InstitutionStatusCode
from fate_manager.entity.types import UserRole, ActivateStatus, PermissionType, IsValidType
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import EXPIRE_TIME
from fate_manager.settings import login_service_logger as logger
from fate_manager.utils.model_utils import transform_dict_key
from fate_manager.utils.request_cloud_utils import request_cloud_manager,request_cloud_manager_new


def fate_manager_activate(request_data):
    logger.info(f'request data: {request_data}')
    user_name = request_data.get("userName")
    accounts = DBOperator.query_entity(AccountInfo, role=UserRole.ADMIN)
    if accounts:
        raise Exception(UserStatusCode.NoFoundAccount, f"activate failed: fate manager has been activated")
    users = DBOperator.query_entity(FateUserInfo, user_name=user_name)
    if not users:
        raise Exception(UserStatusCode.NoFoundUser, f"user {user_name} no found ")

    # request cloud manager
    body = {"registrationLink": request_data.get("link")}
    logger.info(f'start request cloud: body {body}')
    request_cloud_manager(uri_key="UserActivateUri", data=request_data, body=body, url=request_data.get("federatedUrl"))

    # save account info
    request_info = transform_dict_key(request_data)
    request_info["creator"] = user_name
    request_info["permission_list"] = [PermissionType.BASIC, PermissionType.DEPLOY,
                                       PermissionType.FATEBOARD, PermissionType.FATESTUDIO]
    request_info["role"] = UserRole.ADMIN
    request_info["active_url"] = request_info["activate_url"]
    request_info["status"] = IsValidType.YES
    request_info["cloud_user"] = 1
    logger.info(f'create account info: {request_info}')
    DBOperator.create_entity(AccountInfo, request_info)

    # save federated info
    request_info["status"] = 1
    logger.info(f'save federated info: {request_info}')
    request_info["federated_organization_create_time"] = request_info["create_time"]
    DBOperator.safe_save(FederatedInfo, request_info)


def fate_manager_login(request_data):
    user_name = request_data.get("userName")
    password = request_data.get("passWord")
    accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, user_name=user_name)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by username:{user_name}")
    account = accounts[0]
    if account.role not in [UserRole.ADMIN, UserRole.DEVELOPER] :
        raise Exception(UserStatusCode.AccountRoleLow, f"user role {account.role} not in [{UserRole.ADMIN}, {UserRole.DEVELOPER}]")
    users = DBOperator.query_entity(FateUserInfo, user_name=user_name, password=password)
    if not users:
        raise Exception(UserStatusCode.LoginFailed, f"login failed:user name or password error")
    user = users[0]
    token = generate_token(user.user_name, user.password)
    token_info = {
        "user_name": user.user_name,
        "token": token,
        "expire_time": current_timestamp() + EXPIRE_TIME
    }
    DBOperator.safe_save(TokenInfo, token_info)
    return token


def fate_manager_logout(request_data, token):
    user_name = request_data.get("userName")
    accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, user_name=user_name)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, f"no found account by username:{user_name}")
    logger.info(f'login out, user name: {user_name}, token :{token}')
    token_info = DBOperator.query_entity(TokenInfo, user_name=user_name, token=token)
    if not token_info:
        return False
    token_info = {
        "user_name": user_name,
        "token": token
    }
    DBOperator.delete_entity(TokenInfo, **token_info)


def fate_manager_checkjwt(request_data):
    token = request_data.get("token")
    token_info_list = DBOperator.query_entity(TokenInfo, token=token)
    if not token_info_list:
        raise Exception(UserStatusCode.NoFoundToken, "login failed: token no found")
    token_info = token_info_list[0]
    if current_timestamp() > token_info.expire_time:
        raise Exception(UserStatusCode.TokenExpired, "token expired")
    accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, user_name=token_info.user_name)
    if not accounts:
        raise Exception(UserStatusCode.NoFoundAccount, "no found account")
    account = accounts[0]
    token_info = {
        "user_name": account.user_name,
        "token": token,
        "expire_time": current_timestamp() + EXPIRE_TIME
    }
    DBOperator.safe_save(TokenInfo, token_info)
    return {"userName": account.user_name, "Institutions": account.institutions}


def generate_token(user_name, password):
    value = '\n'.join([password, str(current_timestamp())])
    sha1_key = hmac.new(user_name.encode("utf-8"), value.encode("utf-8"), 'sha1').hexdigest()
    b64_token = base64.urlsafe_b64encode(sha1_key.encode("utf-8"))
    return b64_token.decode("utf-8")


def fate_manager_activate_new(request_data):
    logger.info(f'request data: {request_data}')

    # st = request_data.get("SecretKey")
    user_name = request_data.get("userName")
    accounts = DBOperator.query_entity(AccountInfo, role=UserRole.ADMIN)
    # if accounts:
    #     raise Exception(UserStatusCode.NoFoundAccount, f"activate failed: fate manager has been activated")
    users = DBOperator.query_entity(FateUserInfo, user_name=user_name)
    if not users:
        raise Exception(UserStatusCode.NoFoundUser, f"user {user_name} no found ")

    # 查看此机构下是否有存在站点的记录：
    institutions = request_data.get('institutions')
    institution = DBOperator.query_entity(FateSiteInfo, institutions=institutions)
    if institution:
        raise Exception(InstitutionStatusCode.Institution_Not_Allow_Activate, f"Institution has exist {institutions} actived record,not allow activate ")

    body = {"registrationLink": request_data.get("link")}
    logger.info(f'start request cloud: body {body}')
    res_data = request_cloud_manager_new(uri_key="UserActivateNew", data=request_data, body=body, url=request_data.get("federatedUrl"))
    logger.info(f'response from cloud for institution info: {res_data}')

    # save account info
    request_info = transform_dict_key(res_data)
    logger.info(f'fate_manager_activate_new---11-->:{request_info}')
    save_accountInfo = {}
    save_accountInfo["creator"] = user_name
    save_accountInfo["permission_list"] = [PermissionType.BASIC, PermissionType.DEPLOY,
                                       PermissionType.FATEBOARD, PermissionType.FATESTUDIO]
    save_accountInfo["role"] = UserRole.ADMIN
    save_accountInfo["status"] = IsValidType.YES
    save_accountInfo["cloud_user"] = 1

    save_accountInfo["active_url"] = request_data.get("federatedUrl")
    save_accountInfo["institutions"] = request_data.get("institutions")
    save_accountInfo["fate_manager_id"] = request_data.get("fateManagerId")
    save_accountInfo["user_name"] = user_name

    data = request_info.get('fate_manager_user').get('secretInfo')
    data_dct = json.loads(data)
    save_accountInfo["app_secret"] = data_dct.get("secret")
    save_accountInfo["app_key"] = data_dct.get("key")

    logger.info(f'create account info: {save_accountInfo}')
    DBOperator.safe_save(AccountInfo, save_accountInfo)

    # save federated info
    save_federatedInfo = {}
    federated_info = request_info.get('federated_organization')
    federated_data = transform_dict_key(federated_info)
    logger.info(f'fate_manager_activate_new---222-->:{federated_info}')
    save_federatedInfo["institutions"] = request_data.get('institutions')
    save_federatedInfo["federated_url"] = request_data.get("federatedUrl")
    save_federatedInfo["status"] = 1
    save_federatedInfo["federated_id"] = federated_data.get('id')
    save_federatedInfo["institution"] = federated_data.get('institution')
    save_federatedInfo["federated_organization"] = federated_data.get('name')
    save_federatedInfo["federated_organization_create_time"] = federated_data["create_time"]
    logger.info(f'save federated info {save_federatedInfo}')
    DBOperator.safe_save(FederatedInfo, save_federatedInfo)
    return res_data