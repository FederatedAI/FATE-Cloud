import base64
import datetime
import io
import json
import pickle
import time
import uuid
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


def fate_uuid():
    return uuid.uuid1().hex


def string_to_bytes(string):
    return string if isinstance(string, bytes) else string.encode(encoding="utf-8")


def bytes_to_string(byte):
    return byte.decode(encoding="utf-8")


def json_dumps(src, byte=False, indent=None):
    if byte:
        return string_to_bytes(json.dumps(src, indent=indent, cls=CustomJSONEncoder))
    else:
        return json.dumps(src, indent=indent, cls=CustomJSONEncoder)


def json_loads(src, object_pairs_hook=None):
    if isinstance(src, bytes):
        return json.loads(bytes_to_string(src), object_pairs_hook=object_pairs_hook)
    else:
        return json.loads(src, object_pairs_hook=object_pairs_hook)


def current_timestamp():
    return int(time.time() * 1000)


def timestamp_to_date(timestamp, format_string="%Y-%m-%d %H:%M:%S"):
    timestamp = int(timestamp) / 1000
    time_array = time.localtime(timestamp)
    str_date = time.strftime(format_string, time_array)
    return str_date


def serialize_b64(src, to_str=False):
    dest = base64.b64encode(pickle.dumps(src))
    if not to_str:
        return dest
    else:
        return bytes_to_string(dest)


def deserialize_b64(src):
    src = base64.b64decode(string_to_bytes(src) if isinstance(src, str) else src)
    return restricted_loads(src)


def deserialize_b64_demo(src):
    src = base64.b64decode(string_to_bytes(src) if isinstance(src, str) else src)
    return src.decode('utf-8')

safe_module = {
    'federatedml',
    'numpy',
    'fate_flow'
}


class RestrictedUnpickler(pickle.Unpickler):
    def find_class(self, module, name):
        import importlib
        if module.split('.')[0] in safe_module:
            _module = importlib.import_module(module)
            return getattr(_module, name)
        # Forbid everything else.
        raise pickle.UnpicklingError("global '%s.%s' is forbidden" %
                                     (module, name))


def restricted_loads(src):
    """Helper function analogous to pickle.loads()."""
    return RestrictedUnpickler(io.BytesIO(src)).load()