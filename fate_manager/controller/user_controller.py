import functools
from flask import request

from fate_manager.db.db_models import TokenInfo
from fate_manager.entity.status_code import UserStatusCode
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import EXPIRE_TIME
from fate_manager.utils.base_utils import current_timestamp


def check_token(func):
    @functools.wraps(func)
    def _wrapper(*args, **kwargs):
        token = request.headers.get("token")
        if not token:
            raise Exception(UserStatusCode.TokenExpired, "no found token")
        token_info_list = DBOperator.query_entity(TokenInfo, **{"token": token})
        if token_info_list:
            now_time = current_timestamp()
            if token_info_list[0].expire_time < now_time:
                DBOperator.delete_entity(TokenInfo, **{"token": token})
                raise Exception(UserStatusCode.TokenExpired, "token expired")
            token_info = {
                "user_name": token_info_list[0].user_name,
                "token": token,
                "expire_time": current_timestamp() + EXPIRE_TIME
            }
            DBOperator.update_entity(TokenInfo, token_info)
        else:
            raise Exception(UserStatusCode.TokenExpired, "token expired")
        return func(*args, **kwargs)
    return _wrapper