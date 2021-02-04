#
# Copyright 2020 The FATE Authors. All Rights Reserved.
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
import time
import base64
import pickle
import datetime
import json
from enum import Enum, IntEnum


class CustomJSONEncoder(json.JSONEncoder):
    def __init__(self, **kwargs):
        super(CustomJSONEncoder, self).__init__(**kwargs)

    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, datetime.date):
            return obj.strftime('%Y-%m-%d')
        elif isinstance(obj, datetime.timedelta):
            return str(obj)
        elif issubclass(type(obj), Enum) or issubclass(type(obj), IntEnum):
            return obj.value
        else:
            return json.JSONEncoder.default(self, obj)


def current_timestamp():
    return int(time.time() * 1000)


def string_to_bytes(string):
    return string if isinstance(string, bytes) else string.encode(encoding="utf-8")


def bytes_to_string(byte):
    return byte.decode(encoding="utf-8")


def serialize_b64(src, to_str=False):
    dest = base64.b64encode(pickle.dumps(src))
    if not to_str:
        return dest
    else:
        return bytes_to_string(dest)


def deserialize_b64(src):
    return pickle.loads(base64.b64decode(string_to_bytes(src) if isinstance(src, str) else src))


def json_dumps(src, byte=False, indent=None):
    if byte:
        return string_to_bytes(json.dumps(src, indent=indent, cls=CustomJSONEncoder))
    else:
        return json.dumps(src, indent=indent, cls=CustomJSONEncoder)


def json_loads(src):
    if isinstance(src, bytes):
        return json.loads(bytes_to_string(src))
    else:
        return json.loads(src)