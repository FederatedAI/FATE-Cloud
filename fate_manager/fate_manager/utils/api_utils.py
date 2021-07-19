from flask import jsonify

from fate_manager.settings import stat_logger


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