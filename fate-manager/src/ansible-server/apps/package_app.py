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
import os
import shutil
import tarfile

import requests
from subprocess import getoutput
from flask import Flask, request
from settings import stat_logger
from multiprocessing import Process
from utils.api_utils import get_json_result
from utils.base_utils import current_timestamp
from utils.file_utils import get_project_base_directory, get_package_dir_by_version
from utils.job_utils import check_config
from ruamel import yaml
from datetime import datetime
from db.db_models import Package, DB

manager = Flask(__name__)


@manager.errorhandler(500)
def internal_server_error(e):
    stat_logger.exception(e)
    return get_json_result(retcode=100, retmsg=str(e))


@manager.route('/local', methods=['POST'])
def check_local_package():
    request_data = request.json
    required_parameters = ['dir']
    check_config(request_data, required_parameters)
    if os.path.exists(request_data['dir']) and os.path.exists(os.path.join(request_data['dir'], 'roles')):
        # get package module version
        var_fp = os.path.join(request_data['dir'], 'example', 'var_files', 'exp', 'fate_init')
        versions = {}
        if os.path.exists(var_fp):
            with open(var_fp, 'r') as fin:
                versions = yaml.safe_load(fin.read()).get('versions', {})
        output = getoutput(f"du -d 1 -h {os.path.join(request_data['dir'], 'roles')}").split('\n')
        result = []
        for s in output:
            items = s.split()
            module_name = os.path.basename(items[1])
            if module_name != 'roles':
                result.append({
                    'module': module_name,
                    'size': items[0],
                    'time': datetime.fromtimestamp(os.path.getctime(items[1])).strftime('%Y%m%d'),
                    'description': module_name if not versions.get(module_name) else f"{module_name} {versions.get(module_name)}",
                    'version': versions.get(module_name, None)
                })
        return get_json_result(data={'version': versions.get('fate_flow'), 'list': result})
    return get_json_result(retcode=100, retmsg='package dir not exists.')


@manager.route('/download', methods=['POST'])
def download_package():
    request_data = request.json
    required_parameters = ['version', 'url']
    check_config(request_data, required_parameters)
    if not request_data['url']:
        raise Exception(f"illegal url {request_data['url']}")
    if not request_data['version']:
        raise Exception(f"illegal url {request_data['version']}")

    os.makedirs(os.path.join(get_project_base_directory(), 'packages'), exist_ok=True)
    package_dir = get_package_dir_by_version(request_data.get('version'))
    if os.path.exists(package_dir):
        return get_json_result(retcode=100,
                               retmsg=f"Downloading mirror with version {request_data.get('version')} failed, "
                                      f"package dir {package_dir} already exists.")

    package_instance = Package.get_or_none(Package.f_version == request_data['version'],
                                           Package.f_status == 'success')
    if package_instance:
        return get_json_result(retcode=100,
                               retmsg=f"Downloading mirror with version {request_data.get('version')} failed, "
                                      f"version info has been stored in database.")

    request_data['dir'] = package_dir
    p = Process(target=do_download, args=(request_data, ))
    p.start()
    return get_json_result(retmsg=f"Start downloading mirror from url: {request_data.get('url')}.",
                           data= {'version': request_data.get('version')})


@DB.connection_context()
def do_download(data):
    path = os.path.abspath(os.path.join(data.get('dir'), os.pardir, f'temp-{data["version"]}'))
    os.makedirs(path, exist_ok=True)
    fp = os.path.join(path, "package.tar.gz")
    url = data.get('url')

    p = Package()
    p.f_status = 'running'
    p.f_version = data.get('version')
    p.f_start_time = current_timestamp()
    p.save(force_insert=True)

    try:
        stat_logger.info('Start downloading process')
        with requests.get(url, stream=True) as req:
            with open(fp, 'wb') as f:
                for chunk in req.iter_content(chunk_size=1024*5):
                    if chunk:
                        f.write(chunk)
    except Exception as e:
        stat_logger.exception(e)
    else:
        end_time = current_timestamp()
        p.f_end_time = end_time
        p.f_elapsed = p.f_end_time - p.f_start_time
        p.f_status = 'success'

    tar = tarfile.open(fp)
    try:
        dir_name = tar.getmembers()[0].name
        tar.extractall(path=path)
        stat_logger.info(f"rename: src: {os.path.join(path, dir_name)}")
        dst = data.get('dir')

        stat_logger.info(f"rename: dst: {dst}")
        os.rename(src=os.path.join(path, dir_name), dst=dst)
        shutil.rmtree(path=path)
    except Exception as e:
        stat_logger.exception(e)
        p.f_status = 'failed'
    finally:
        tar.close()
        p.save()
        DB.commit()


@manager.route('/remote', methods=['POST'])
def query_package():
    request_data = request.json
    required_paramters = ['version']
    check_config(request_data, required_paramters)
    p = get_package_download_record(request_data['version'])
    if p:
        return get_json_result(data=p.to_json())
    return get_json_result(retcode=100, retmsg=f"can not found version {request_data['version']} record")


@DB.connection_context()
def get_package_download_record(version: str):
    return Package.get_or_none(f_version=version)
