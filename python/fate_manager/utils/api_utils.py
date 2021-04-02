#
# Copyright 2021 The FATE Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
from flask import jsonify

from settings import stat_logger


def get_json_result(retcode=0, retmsg='success', data=None):
    result_dict = {"code": retcode, "msg": retmsg, "data": data}
    response = {}
    for key, value in result_dict.items():
        if value is None and key != "code":
            continue
        else:
            response[key] = value
    return jsonify(response)


def server_error_response(e):
    stat_logger.exception(e)
    if len(e.args) > 1:
        return get_json_result(retcode=e.args[0], retmsg=str(e.args[1]), data=e.args[1])
    else:
        return get_json_result(retcode=100, retmsg=str(e))