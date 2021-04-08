#
# Copyright 2021 The FATE Authors. All Rights Reserved.
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
import datetime
import inspect
import os
import sys

import __main__
from peewee import (CharField, IntegerField, BigIntegerField, SmallIntegerField,
                    TextField, CompositeKey, BigAutoField, BooleanField)
from playhouse.pool import PooledMySQLDatabase

from arch.common import log
from fate_manager.db.base_model import JSONField, BaseModel, LongTextField, DateTimeField, ListField
from fate_manager.settings import DATABASE, stat_logger


LOGGER = log.getLogger()


def singleton(cls, *args, **kw):
    instances = {}

    def _singleton():
        key = str(cls) + str(os.getpid())
        if key not in instances:
            instances[key] = cls(*args, **kw)
        return instances[key]

    return _singleton


@singleton
class BaseDataBase(object):
    def __init__(self):
        database_config = DATABASE.copy()
        db_name = database_config.pop("name")
        self.database_connection = PooledMySQLDatabase(db_name, **database_config)
        stat_logger.info('init mysql database successfully')


MAIN_FILE_PATH = os.path.realpath(__main__.__file__)
if MAIN_FILE_PATH.endswith('fate_manager_server.py') or \
        MAIN_FILE_PATH.find("/unittest/__main__.py"):
    DB = BaseDataBase().database_connection
else:
    # Initialize the database only when the server is started.
    DB = None


def close_connection():
    try:
        if DB:
            DB.close()
    except Exception as e:
        LOGGER.exception(e)


class DataBaseModel(BaseModel):
    class Meta:
        database = DB


@DB.connection_context()
def init_database_tables():
    members = inspect.getmembers(sys.modules[__name__], inspect.isclass)
    table_objs = []
    for name, obj in members:
        if obj != DataBaseModel and issubclass(obj, DataBaseModel):
            table_objs.append(obj)
    DB.create_tables(table_objs)


def fill_db_model_object(model_object, human_model_dict):
    for k, v in human_model_dict.items():
        attr_name = 'f_%s' % k
        if hasattr(model_object.__class__, attr_name):
            setattr(model_object, attr_name, v)
    return model_object


class AccountInfo(DataBaseModel):
    fate_manager_id = CharField(max_length=50, null=True, help_text='fate manager id')
    status = SmallIntegerField(default=0, help_text='user status 0 unvalid，1 valid')
    role = IntegerField(null=True, help_text='role')
    user_name = CharField(max_length=50, null=True, help_text='user name')
    institutions = CharField(max_length=50, null=True, help_text='institutions')
    app_key = CharField(max_length=50, null=True, help_text='app key')
    app_secret = CharField(max_length=50, null=True, help_text='app secret')
    active_url = TextField(help_text='cloud manger account active url')
    creator = CharField(max_length=32, null=True, help_text='creator')
    party_id = IntegerField(null=True, help_text='party id', default=0)
    site_name = CharField(max_length=256, null=True, help_text='site name')
    block_msg = ListField(null=True, help_text='function block')
    permission_list = ListField(null=True, help_text='permission list')
    allow_instituions = ListField(null=True, help_text='allow other fate manager to apply')

    class Meta:
        db_table = "t_fate_account_info"
        primary_key = CompositeKey("user_name", "fate_manager_id", "party_id")


class ApplySiteInfo(DataBaseModel):
    institutions = ListField(help_text='institutions dict')
    status = IntegerField(default=0, help_text='user status 0 unvalid，1 valid')

    class Meta:
        db_table = "t_fate_apply_site_info"


class AutoTest(DataBaseModel):
    id = BigAutoField()
    federated_id = IntegerField(null=True, help_text='federated id')
    party_id = IntegerField(null=True, help_text='party id')
    product_type = SmallIntegerField(null=True, help_text="0:fate,1:fate-serving")
    fate_version = CharField(max_length=30, null=True, help_text='fate version')
    test_item = CharField(max_length=32, null=True, help_text='test item')
    start_time = BigIntegerField(null=True, help_text='start time')
    end_time = BigIntegerField(null=True, help_text='end time')
    status = SmallIntegerField(default=0, help_text='user status 0 unvalid，1 valid')

    class Meta:
        db_table = "t_fate_auto_test"


class ChangeLog(DataBaseModel):
    case_id = CharField(max_length=50, null=True)
    federated_id = IntegerField(null=True, help_text='federated id')
    federated_organization = CharField(max_length=128, null=True, help_text='Federated Organization')
    party_id = IntegerField(null=True, help_text='party id')
    network_access_entrances = CharField(max_length=256, null=True, help_text='network access entrances')
    network_access_exits = CharField(max_length=256, null=True, help_text='network access exits')
    status = SmallIntegerField(default=0, help_text='user status 0 unvalid，1 valid')

    class Meta:
        db_table = "t_fate_change_log"
        primary_key = CompositeKey('federated_id', 'party_id', 'case_id')



class ComponentVersion(DataBaseModel):
    id = BigAutoField()
    fate_version = CharField(max_length=30, null=True, help_text='fate version')
    product_type = SmallIntegerField(null=True, help_text="0:fate,1:fate-serving")
    component_name = CharField(max_length=32, null=True, help_text='component name')
    image_id = CharField(max_length=32, null=True, help_text='docker image id')
    image_name = CharField(max_length=32, null=True, help_text='docker image name')
    image_version = CharField(max_length=32, null=True, help_text='docker image version')
    image_tag = CharField(max_length=32, null=True, help_text='docker image tag')
    image_description = CharField(max_length=256, null=True, help_text='docker image description')
    image_size = CharField(max_length=20, null=True, help_text='docker image size')
    image_create_time = BigIntegerField(null=True, help_text='image create time')
    component_version = CharField(max_length=32, null=True, help_text='component version')
    version_index = IntegerField(null=True, help_text='version index from 0')
    pull_status = SmallIntegerField(null=True, help_text='image pull state,0no pull,1pulled')
    package_status = SmallIntegerField(null=True, help_text='download status,0no,1yes')

    class Meta:
        db_table = "t_fate_component_version"


class DeployComponent(DataBaseModel):
    id = BigAutoField()
    federated_id = IntegerField(null=True, help_text='federated id')
    party_id = IntegerField(null=True, index=True, help_text='party id')
    site_name = CharField(max_length=50, null=True, help_text='site name')
    product_type = SmallIntegerField(null=True, help_text="0:fate,1:fate-serving")
    job_id = CharField(max_length=50, null=True, help_text='job id')
    fate_version = CharField(max_length=30, null=True, help_text='fate version')
    component_name = CharField(max_length=32, null=True, help_text='component name')
    component_version = CharField(max_length=32, null=True, help_text='component version')
    address = CharField(max_length=128, null=True, help_text='service ip and port')
    label = CharField(max_length=128, null=True, help_text='node label')
    start_time = BigIntegerField(null=True, help_text='start time')
    end_time = BigIntegerField(null=True, help_text='end time')
    duration = IntegerField(null=True, help_text='duration')
    version_index = IntegerField(null=True, help_text='version index,from 1')
    deploy_status = SmallIntegerField(default=-1, help_text='deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed')
    deploy_type = SmallIntegerField(default=-1, help_text='deploy type ,1k8s,2ansible')
    status = SmallIntegerField(default=-1, help_text='component status,-1 unkonwn,0 running,1stopped')
    is_valid = SmallIntegerField(default=-1, help_text='0 unvalid,1valid')
    finish_time = BigIntegerField(null=True, help_text='finish time')

    class Meta:
        db_table = "t_fate_deploy_component"


class DeployJob(DataBaseModel):
    id = BigAutoField()
    job_id = CharField(max_length=50, null=True, help_text='job id')
    job_type = SmallIntegerField(default=-1, help_text='kube job type,-1 unkonwn,0 isntall,1 update,2delete')
    creator = CharField(max_length=32, null=True, help_text='creator')
    status = SmallIntegerField(default=-1, help_text='job status,-1 unkonwn,0success,1 failed')
    start_time = BigIntegerField(null=True, help_text='start time')
    end_time = BigIntegerField(null=True, help_text='end time')
    cluster_id = CharField(max_length=50, null=True, help_text='kubefate cluster id')
    federated_id = IntegerField(null=True, help_text='federated id')
    deploy_type = IntegerField(null=True, help_text='deploy type ,1k8s,2ansible')
    party_id = IntegerField(null=True, index=True, help_text='party id')
    result = CharField(max_length=128, null=True, help_text='result')
    product_type = SmallIntegerField(null=True, help_text="product_type,1fate,2fate-serving")

    class Meta:
        db_table = "t_fate_deploy_job"


class DeployPrepare(DataBaseModel):
    id = BigAutoField()
    prepare_title = CharField(max_length=50, null=True, help_text='deploy prepare title')
    prepare_desc = CharField(max_length=50, null=True, help_text='deploy prepare description')
    is_valid = SmallIntegerField(null=True, help_text='0 unvalid,1 valid')

    class Meta:
        db_table = "t_fate_deploy_prepare"


class DeploySite(DataBaseModel):
    id = BigAutoField()
    federated_id = IntegerField(null=True, help_text='federated id')
    party_id = IntegerField(null=True, index=True, help_text='party id')
    product_type = SmallIntegerField(null=True, help_text="0:fate,1:fate-serving")
    fate_version = CharField(max_length=30, null=True, help_text='fate version')
    job_id = CharField(max_length=50, null=True, help_text='job id')
    name = CharField(max_length=50, null=True, help_text='kubefate name')
    name_space = CharField(max_length=50, null=True, help_text='kubefate name space')
    revision = IntegerField(null=True, help_text='kubefate revision')
    deploy_status = SmallIntegerField(default=-1, help_text='deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed')
    status = SmallIntegerField(default=-1, help_text='site status after autotest,-1 unkonwn,1 stoped,2runing')
    chart = CharField(max_length=32, null=True, help_text='chart name')
    chart_version = CharField(max_length=32, null=True, help_text='chart version')
    cluster_values = LongTextField(help_text='cluster values')
    cluster_info = LongTextField(help_text='cluster info')
    upgrade_status = SmallIntegerField(default=-1, help_text='site upgrade status,-1 unkonwn,0 no,1yes')
    version_index = IntegerField(default=-1, help_text='fate version index')
    fateboard = CharField(max_length=50, null=True, help_text='kubefate fateboard url')
    cluster_id = CharField(max_length=50, null=True, help_text='kubefate cluster id')
    single_test = SmallIntegerField(null=True, help_text='single test,0 no test,1testing,2test ok,3test failed')
    toy_test = SmallIntegerField(null=True, help_text='toy test,0 no test,1testing,2test ok,3test failed')
    toy_test_only = SmallIntegerField(null=True, help_text='toy test only,0no test,1testing,2success,3failed')
    toy_test_only_read = SmallIntegerField(null=True, help_text='toy test only read ,0read,1unread')
    duration = IntegerField(null=True, help_text='duration')
    kubenetes_id = IntegerField(null=True, help_text='kubenetes id')
    minimize_fast_test = SmallIntegerField(null=True, help_text='minimize fast test,0 no test,1testing,2test ok,3test failed')
    minimize_normal_test = SmallIntegerField(null=True, help_text='minimize normal test,0 no test,1testing,2test ok,3test failed')
    config = JSONField(null=True, help_text='install config')
    rollsite_port = IntegerField(null=True, help_text='yaml proxy port')
    python_port = IntegerField(null=True)
    click_type = IntegerField(null=True, help_text='click type,1 connect success;2page start;3pull;4install;5finish')
    deploy_type = IntegerField(null=True, help_text='deploy type ,1k8s,2ansible')
    is_valid = SmallIntegerField(default=-1, help_text='-1 unkonw,0unvalid,1valid')
    finish_time = BigIntegerField(null=True, help_text='finish time')

    class Meta:
        db_table = "t_fate_deploy_site"


class FateVersion(DataBaseModel):
    id = BigAutoField()
    fate_version = CharField(max_length=32, null=True, help_text='fate version')
    product_type = SmallIntegerField(null=True, help_text='0fate,1fate-serving')
    chart_version = CharField(max_length=32, null=True, help_text='fate chart version')
    version_index = IntegerField(null=True, help_text='fate verson index ,increase from 0')
    pull_status = SmallIntegerField(null=True, help_text='image pull state,0no pull,1pulled')
    package_status = SmallIntegerField(null=True, help_text='package status ,0no,1yes')
    package_url = TextField(help_text='package download url')

    class Meta:
        db_table = "t_fate_fate_version"


class FederatedInfo(DataBaseModel):
    federated_id = IntegerField(default=0, help_text='cloud-manager id')
    federated_organization = CharField(max_length=128, null=True, help_text='Federated Organization')
    institutions = CharField(max_length=50, null=True, help_text='institutions')
    federated_url = CharField(null=True, help_text='federated host')
    status = SmallIntegerField(default=0, help_text='0:unvalid,1valid')
    size = SmallIntegerField(default=0, help_text='federated size')

    class Meta:
        db_table = "t_fate_federated_info"
        primary_key = CompositeKey('federated_id')


class KubenetesConf(DataBaseModel):
    id = BigAutoField()
    kubenetes_url = CharField(max_length=50, null=True, help_text='kubenetes host domain')
    python_port = IntegerField(null=True, help_text='portlist')
    rollsite_port = IntegerField(null=True)
    node_list = TextField(help_text='node list')
    deploy_type = SmallIntegerField(null=True, help_text='deploy type ,1 k8s,2ansible')
    click_type = SmallIntegerField(null=True, help_text='ansible deploy click type')

    class Meta:
        db_table = "t_fate_kubenetes_conf"


class MonitorDetail(DataBaseModel):
    id = BigAutoField()
    ds = IntegerField(null=True, help_text='report date')
    guest_party_id = IntegerField(null=True, help_text='guest partyid')
    guest_site_name = CharField(max_length=50, null=True, help_text='guest site name')
    guest_institution = CharField(max_length=64, null=True, help_text='guest institution')
    host_party_id = IntegerField(null=True, help_text='host partyid')
    host_site_name = CharField(max_length=50, null=True, help_text='host site name')
    host_institution = CharField(max_length=64, null=True, help_text='host institution')
    arbiter_party_id = IntegerField(null=True, help_text='arbiter partyid')
    arbiter_site_name = CharField(max_length=50, null=True, help_text='arbiter site name')
    arbiter_institution = CharField(max_length=64, null=True, help_text='arbiter institution')
    job_id = CharField(max_length=64, null=True, help_text='job id')
    status = CharField(max_length=50, default='0', help_text='model status,0 success,1failed')
    start_time = BigIntegerField(null=True, help_text='start time')
    end_time = BigIntegerField(null=True, help_text='end time')

    class Meta:
        db_table = "t_fate_monitor_detail"


class FateOtherSiteInfo(DataBaseModel):
    party_id = IntegerField(null=True, index=True, help_text='party id')
    site_id = BigIntegerField(null=True, help_text='cloud manger autocremetid')
    site_name = CharField(max_length=128, null=True, help_text='Site Name')
    institutions = CharField(max_length=128, null=True, help_text='site belongs to institutions')
    role = SmallIntegerField(default=0, help_text='role,1:guest,2:host')
    status = SmallIntegerField(default=0, help_text='site status,1 not joined,2 joined,3 removed')
    service_status = SmallIntegerField(default=0, help_text='service status,0 unavailable,1available')
    is_valid = SmallIntegerField(default=0, help_text='is valid')
    activation_time = BigIntegerField(null=True, help_text='activation Time')

    class Meta:
        db_table = "t_fate_other_site_info"
        primary_key = CompositeKey('site_id', 'party_id', 'site_name')



class FateReportInstitution(DataBaseModel):
    id = BigAutoField()
    ds = IntegerField(null=True, help_text='report date')
    institution = CharField(max_length=128, null=True, help_text='institution')
    total = IntegerField(null=True, help_text='total')
    success = IntegerField(null=True, help_text='success')
    running = IntegerField(null=True, help_text='running')
    waiting = IntegerField(null=True, help_text='waiting')
    timeout = IntegerField(null=True, help_text='timeout')
    failed = IntegerField(null=True, help_text='failed')
    canceled = IntegerField(null=True, help_text='failed')

    class Meta:
        db_table = "t_fate_report_institution"


class FateReportSite(DataBaseModel):
    id = BigAutoField()
    ds = IntegerField(null=True, help_text='report date')
    institution = CharField(max_length=128, null=True, help_text='institution')
    institution_site_name = CharField(max_length=64, null=True, help_text='institution site name')
    site_name = CharField(max_length=128, null=True, help_text='Site Name')
    total = IntegerField(null=True, help_text='total')
    success = IntegerField(null=True, help_text='success')
    running = IntegerField(null=True, help_text='running')
    waiting = IntegerField(null=True, help_text='waiting')
    timeout = IntegerField(null=True, help_text='timeout')
    failed = IntegerField(null=True, help_text='failed')
    canceled = IntegerField(null=True, help_text='failed')

    class Meta:
        db_table = "t_fate_report_site"


class FateSiteInfo(DataBaseModel):
    federated_id = IntegerField(null=True, help_text='federated id')
    site_id = BigIntegerField(null=True, help_text='site id')
    site_name = CharField(max_length=128, help_text='site name')
    federated_organization = CharField(max_length=128, null=True, help_text='Federated Organization')
    party_id = BigIntegerField(null=True, help_text='party id')
    institutions = CharField(max_length=128, null=True, help_text='site belongs to institutions')
    role = SmallIntegerField(default=0, help_text='role,1:guest,2:host')
    app_key = CharField(max_length=64, null=True, help_text='federation key')
    app_secret = CharField(max_length=64, null=True, help_text='Federation secret')
    registration_link = TextField(help_text='registration link')
    network_access_entrances = CharField(null=True, help_text='network access entrances')
    network_access_exits = CharField(null=True, help_text='network access exits')
    fate_version = CharField(max_length=10, null=True, help_text='fate version')
    fate_serving_version = CharField(max_length=10, null=True, help_text='fate serving version')
    component_version = CharField(max_length=512, null=True, help_text='fate component version')
    status = SmallIntegerField(default=0, help_text='site status,1 not joined,2 joined,3 removed')
    deploy_type = SmallIntegerField(default=0, help_text='deploy type ,1k8s,2ansible')
    service_status = SmallIntegerField(default=0, help_text='service status,0 unavailable,1available')
    edit_status = SmallIntegerField(default=-1, help_text='edit status,-1 unkonwn,1 unedit,2 edit')
    read_status = SmallIntegerField(default=-1, help_text='read status,-1 unkonwn,3 read,1agreed ,2rejected')
    activation_time = BigIntegerField(null=True, help_text='activation Time')

    class Meta:
        db_table = "t_fate_site_info"
        primary_key = CompositeKey('party_id', 'federated_id')


class TokenInfo(DataBaseModel):
    user_name = CharField(max_length=50, null=True, help_text='user name', primary_key=True)
    token = CharField(null=True, help_text='token')
    expire_time = BigIntegerField(null=True, help_text='expire time')

    class Meta:
        db_table = "t_fate_manager_token_info"


class FateUserInfo(DataBaseModel):
    user_id = CharField(max_length=50, null=True, help_text='user id')
    user_name = CharField(max_length=50, null=True, help_text='user name', primary_key=True)
    password = CharField(max_length=128, null=True, help_text='user password')
    is_delete = IntegerField(default=0, null=True, help_text='0 or 1')

    class Meta:
        db_table = "t_fate_user_info"
