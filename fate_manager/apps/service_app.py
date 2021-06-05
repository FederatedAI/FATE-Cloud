from flask import Flask
from flask_cors import CORS

from fate_manager.utils.api_utils import server_error_response, get_json_result

manager = Flask(__name__)
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/overview', methods=['post'])
def overview():
    return get_json_result()