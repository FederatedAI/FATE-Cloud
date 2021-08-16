import hashlib
import json
import time

import grpc

from fate_manager.settings import DEFAULT_GRPC_TIMEOUT, site_service_logger
from fate_manager.utils.base_utils import json_loads
from fate_manager.protobuf import proxy_pb2, proxy_pb2_grpc


def write_site_route(roll_site_ip, roll_site_port, roll_site_key, site_route, party_id,
                     overall_timeout=DEFAULT_GRPC_TIMEOUT):
    site_service_logger.info(f"get route table")
    route_table = roll_site_actuator(roll_site_ip, roll_site_port, roll_site_key, "", party_id,
                                     operator="get_route_table", overall_timeout=overall_timeout)
    route_table["route_table"].update(site_route)
    site_service_logger.info(f"set route table")
    roll_site_actuator(roll_site_ip, roll_site_port, roll_site_key, route_table, party_id, "set_route_table",
                       overall_timeout=DEFAULT_GRPC_TIMEOUT)


def roll_site_actuator(roll_site_ip, roll_site_port, roll_site_key, content, party_id, operator,
                       overall_timeout=DEFAULT_GRPC_TIMEOUT):
    _packet = wrap_grpc_packet(content, party_id, roll_site_key, operator, overall_timeout)
    channel, stub = get_command_federation_channel(roll_site_ip, roll_site_port)
    _return, _call = stub.unaryCall.with_call(_packet, timeout=(overall_timeout/1000))
    channel.close()
    site_service_logger.info(_return.body.value)
    if operator == "get_route_table":
        json_body = json_loads(_return.body.value)
        return json_body

def get_command_federation_channel(host, port):
    channel = grpc.insecure_channel(f"{host}:{port}")
    stub = proxy_pb2_grpc.DataTransferServiceStub(channel)
    return channel, stub


def wrap_grpc_packet(content, party_id, key, operator, overall_timeout):
    _time_str = str(int(time.time()*1000))
    content_str = json.dumps(content)
    _dst = proxy_pb2.Topic(partyId="{}".format(party_id))
    _conf = proxy_pb2.Conf(overallTimeout=overall_timeout)
    _meta = proxy_pb2.Metadata(dst=_dst, operator=operator, conf=_conf)
    _data = proxy_pb2.Data(key=hashlib_md5(bytes(_time_str+content_str+key, 'utf-8')),
                           value=bytes(_time_str+content_str, 'utf-8'))
    return proxy_pb2.Packet(header=_meta, body=_data)

def hashlib_md5(src):
    _md5 = hashlib.md5()
    _md5.update(src)
    return _md5.hexdigest()