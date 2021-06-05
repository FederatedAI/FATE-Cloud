import base64
import hmac
import json
import time

import requests
import uuid

from hashlib import sha1

from fate_manager.db.db_models import AccountInfo
from fate_manager.entity.status_code import RequestCloudCode, UserStatusCode
from fate_manager.entity.types import ActivateStatus, UserRole
from fate_manager.operation import federated_db_operator
from fate_manager.operation.db_operator import DBOperator
from fate_manager.settings import request_cloud_logger, CLOUD_URL, CLOUD_SITE_SIGNATURE, CLOUD_INSTITUTION_SIGNATURE


def hash_hmac(key, code):
    hmac_code = hmac.new(key.encode("utf-8"), code.encode("utf-8"), sha1).digest()
    return base64.b64encode(hmac_code).decode()


def get_old_signature_head(uri, data, body):
    head = {}
    head["TIMESTAMP"] = str(int(time.time()*1000))
    head["PARTY_ID"] = str(data.get("partyId"))
    head["NONCE"] = str(uuid.uuid1().hex)
    head["ROLE"] = str(data.get("role"))
    head["APP_KEY"] = data.get("appKey")
    sign_str = '{}\n{}\n{}\n{}\n{}\n{}\n{}'.format(head["PARTY_ID"], head["ROLE"], data.get("appKey"),
                                               head["TIMESTAMP"], head["NONCE"], uri, body)
    key = data.get("appSecret")
    sign = hash_hmac(key, sign_str)
    head["SIGNATURE"] = sign
    return head


def get_institution_signature_head(uri, data, body):
    head = {}
    head["VERSION"] = "v2"
    head["TIMESTAMP"] = str(int(time.time() * 1000))
    head["FATE_MANAGER_USER_ID"] = str(data.get("fateManagerId"))
    head["NONCE"] = uuid.uuid1().hex
    head["FATE_MANAGER_APP_KEY"] = data.get("appKey")
    # FATE_MANAGER_USER_ID, FATE_MANAGER_APP_KEY, TIMESTAMP, NONCE, HTTP_URI, HTTP_BODY
    if body == '{}':
        body = ""
    sign_str = '{}\n{}\n{}\n{}\n{}\n{}'.format(head["FATE_MANAGER_USER_ID"], data.get("appKey"), head["TIMESTAMP"],
                                               head["NONCE"], uri, body)
    key = data.get("appSecret")
    sign = hash_hmac(key, sign_str)
    head["SIGNATURE"] = sign
    return head


def get_site_signature_head(uri, data, body):
    head = {}
    head["VERSION"] = "v2"
    head["TIMESTAMP"] = str(int(time.time()*1000))
    head["PARTY_ID"] = str(data.get("partyId"))
    head["ROLE"] = str(data.get("role"))
    head["APP_KEY"] = data.get("appKey")
    head["FATE_MANAGER_USER_ID"] = data["account"].fate_manager_id
    head["FATE_MANAGER_APP_KEY"] = data["account"].app_key
    head["NONCE"] = uuid.uuid1().hex
    appSecret = data.get("appSecret")
    if body == '{}':
        body = ""
    sign_str = '{}\n{}\n{}\n{}\n{}\n{}\n{}\n{}\n{}'.format(data["account"].fate_manager_id, data["account"].app_key,
                                                           head["PARTY_ID"], head["ROLE"], head["APP_KEY"],
                                                           head["TIMESTAMP"], head["NONCE"], uri, body)
    key = data["account"].app_secret + appSecret
    sign = hash_hmac(key, sign_str)
    head["SIGNATURE"] = sign
    return head


def request_cloud_manager(uri_key, data, body, methods="post", url=None):
    uri = CLOUD_URL[uri_key]
    body_json = json.dumps(body, separators=(',', ':'), sort_keys=True)
    if uri_key in CLOUD_INSTITUTION_SIGNATURE:
        head = get_institution_signature_head(uri, data, body_json)
    elif uri_key in CLOUD_SITE_SIGNATURE:
        if "account" not in data.keys():
            accounts = DBOperator.query_entity(AccountInfo, status=ActivateStatus.YES, role=UserRole.ADMIN)
            if not accounts:
                raise Exception(UserStatusCode.NoFoundAccount, "no found account")
            data["account"] = accounts[0]
        head = get_site_signature_head(uri, data, body_json)
    else:
        head = {}
    if not url:
        federated_info = federated_db_operator.get_federated_info()[0]
        url = federated_info.federated_url
    url = url + uri
    request_cloud_logger.info(f'start request uri:{url}, body:{body}, head:{head}')
    if methods == "get":
        response = requests.get(url, json=body, headers=head)
    else:
        response = requests.post(url, json=body, headers=head)
    request_cloud_logger.info(f'response:{response.text}')
    if response.status_code == 200:
        if not response.json().get('code'):
            return response.json().get('data')
        else:
            raise Exception(RequestCloudCode.SignatureFailed, f'request url {url} failed:code {response.json().get("code")}, msg {response.json().get("msg")}')
    else:
        raise Exception(RequestCloudCode.HttpRequestFailed, f'request url {url} failed,http status code:{response.status_code}')