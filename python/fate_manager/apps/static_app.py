from flask import Flask, render_template, send_from_directory
from flask_cors import CORS

from utils.api_utils import server_error_response


manager = Flask(__name__, static_url_path='/static', template_folder='../static',
                static_folder="static")
CORS(manager, supports_credentials=True)


@manager.errorhandler(500)
def internal_server_error(e):
    return server_error_response(e)


@manager.route('/static/index', methods=['get'])
def index():
    return render_template('index.html')
