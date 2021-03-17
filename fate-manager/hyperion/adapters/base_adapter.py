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
import abc
from abc import ABCMeta
from ruamel import yaml
from utils import file_utils
from utils.base_utils import json_loads


class BaseAdapter(metaclass=ABCMeta):
    def get_modules_sequence(self):
        with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{self.version.replace('.', '_')}",
                               'sequence.json'), 'r') as f:
            data = json_loads(f.read())
        return data

    def get_proj_yml(self):
        with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{self.version.replace('.', '_')}",
                               'project.yml'), 'r') as f:
            data = yaml.safe_load(f.read())
        return data

    def get_roles_args(self):
        with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{self.version.replace('.', '_')}",
                               'roles_arg.json'), 'r') as f:
            data = json_loads(f.read())
        return data

    def get_hosts_template(self):
        with open(os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{self.version.replace('.', '_')}",
                               'hosts'), 'r') as f:
            data = f.read()
        return data

    def get_var_files_dir(self):
        return os.path.join(file_utils.get_project_base_directory(), 'templates', f"v{self.version.replace('.', '_')}",
                            'var_files')

    @abc.abstractmethod
    def generate_play_conf(self, job_id: str, job_data: dict) -> dict:
        ...

    @abc.abstractmethod
    def generate_variable_files(self, job_id: str, count: str, version, conf: dict):
        ...



