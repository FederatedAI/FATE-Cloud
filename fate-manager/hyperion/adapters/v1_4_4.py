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
from collections import OrderedDict
from ruamel import yaml
from adapters.base_adapter import BaseAdapter
from settings import stat_logger
from utils import file_utils
from utils.base_utils import json_dumps


class Adapter(BaseAdapter):
    def __init__(self):
        self.version = "1.4.4"

    def generate_play_conf(self, job_id: str, job_data: dict) -> dict:
        '''
        seperate job conf into different play confs
        :param job_id:
        :param job_data:
        :return: {"play_id": (play_conf, hosts)}  orderdict
        '''
        result = OrderedDict()
        sequence = self.get_modules_sequence().get('sequence')
        roles_args = self.get_roles_args()

        count = 1

        for offset, conf in enumerate([job_data]):
            stat_logger.info(f"in generate play conf:")
            stat_logger.info(f"offset: {offset}")
            stat_logger.info(f"No.{offset} conf in cutomize conf list: {json_dumps(conf, indent=4)}")

            var_path_list = self.generate_variable_files(job_id, str(offset + 1), job_data.get('version'), conf)
            stat_logger.info(f"sequence: {sequence}")
            for module in sequence:
                stat_logger.info(f"in loop, module: {module}")
                if module in conf.get('modules'):
                    stat_logger.info(f"module {module} is in conf")
                    stat_logger.info(
                        f"result of conf.get(module).get('enable'): {conf.get('modules').get(module).get('enable')}")
                    # if conf.get('modules').get(module).get('enable'):
                    # generate play id
                    play_id = f"{job_id}_{count}"
                    stat_logger.info(f"play id: {play_id}")
                    # generate template config yaml
                    template_yml = self.get_proj_yml()
                    template_yml[0]['hosts'] = play_id
                    template_yml[0]['vars_files'] = var_path_list
                    template_yml[0]['roles'] = [roles_args.get(module)]
                    stat_logger.info(f"template yaml content: {json_dumps(template_yml, indent=4)}")
                    # generate template hosts
                    hosts_str = self.get_hosts_template()
                    hosts_str = hosts_str.replace('init', play_id)
                    stat_logger.info(
                        f"in generate play conf, conf.get('modules').get(module).get('ips'): {conf.get('modules').get(module).get('ips')}")
                    hosts_str = hosts_str.replace('127.0.0.1', '\n'.join(conf.get('modules').get(module).get('ips')))
                    stat_logger.info(f"hosts string: {hosts_str}")
                    result.update({
                        play_id: {
                            'yml': template_yml,
                            'hosts': hosts_str,
                        }
                    })
                    stat_logger.info(f"current result dict: {json_dumps(result, indent=4)}")
                    count += 1

        stat_logger.info(f'Generating play conf successfully, result is: {json_dumps(result, indent=4)}')
        return result

    def generate_variable_files(self, job_id: str, count: str, version, conf: dict):
        stat_logger.info(f"in generate var file func, conf: {json_dumps(conf, indent=4)}")

        result = []
        job_dir = file_utils.get_job_directory(job_id)
        var_dir = os.path.join(job_dir, f'var_files_{count}')
        role = conf.get('role')
        if role not in ["guest", "host"]:
            raise ValueError(f"role {role} is not supported currently.")
        shutil.copytree(src=self.get_var_files_dir(), dst=var_dir)
        with open(os.path.join(var_dir, f"fate_{role}"), "r") as fin:
            data = yaml.safe_load(fin.read())
        stat_logger.info(f"in generate var file func, original data: {json_dumps(data, indent=4)}")

        data[role]['partyid'] = conf.get('party_id')

        travel_key = set()
        for key, value in conf.get('modules').items():
            if key in data[role]:
                travel_key.add(key)
                for item, item_value in value.items():
                    if item in data[role][key]:
                        data[role][key][item] = item_value

        modify_key = set(data.get(role).keys()) - travel_key
        modify_key.remove('partyid')
        if 'eggroll' in modify_key:
            modify_key.remove('eggroll')
        for key in modify_key:
            data.get(role).get(key)['enable'] = False

        stat_logger.info(f"in generate var file func, edited data: {json_dumps(data, indent=4)}")
        with open(os.path.join(var_dir, f"fate_{role}"), "w") as fout:
            yaml.dump(data, fout, Dumper=yaml.RoundTripDumper)
        for root, dirs, files in os.walk(var_dir):
            result.extend([os.path.join(os.path.abspath(root), name) for name in files])
        return result





