import functools
from flask import request

from arch.common.base_utils import current_timestamp
from db.db_models import TokenInfo
from entity.status_code import UserStatusCode
from operation.db_operator import DBOperator
from settings import EXPIRE_TIME


def check_token(func):
    @functools.wraps(func)
    def _wrapper(*args, **kwargs):
        token = request.headers.get("token")
        if not token:
            raise Exception(UserStatusCode.NoFoundToken, "no found token")
        token_info_list = DBOperator.query_entity(TokenInfo, {"token": token})
        if token_info_list:
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