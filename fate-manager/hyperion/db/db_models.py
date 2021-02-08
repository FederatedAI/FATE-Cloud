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
import datetime
import inspect
import os
import sys

import __main__

from utils import log_utils
from settings import DATABASE, stat_logger
from playhouse.pool import PooledMySQLDatabase
from utils.base_utils import current_timestamp
from db.fields import JSONField, LongTextField
from peewee import CharField, IntegerField, BigIntegerField, CompositeKey, Model, AutoField
from collections import OrderedDict

LOGGER = log_utils.getLogger()


class BaseModel(Model):
    def to_json(self, filters=None):
        if filters is None:
            return self.__dict__['__data__']
        else:
            result = OrderedDict()
            data = self.__dict__['__data__']
            for k in filters:
                attr = 'f_%s' % k
                if attr in data:
                    result[attr] = data[attr]
            return result

    def to_human_model_dict(self, only_primary_with: list = None):
        model_dict = self.__dict__["__data__"]
        human_model_dict = {}
        if not only_primary_with:
            for k, v in model_dict.items():
                human_model_dict[k.lstrip("f").lstrip("_")] = v
        else:
            for k in self._meta.primary_key.field_names:
                human_model_dict[k.lstrip("f").lstrip("_")] = model_dict[k]
            for k in only_primary_with:
                human_model_dict[k] = model_dict["f_%s" % k]
        return human_model_dict

    @classmethod
    def get_primary_keys_name(cls):
        return cls._meta.primary_key.field_names if isinstance(cls._meta.primary_key, CompositeKey) else [cls._meta.primary_key.name]

    def save(self, *args, **kwargs):
        if hasattr(self, "f_update_date"):
            self.f_update_date = datetime.datetime.now()
        if hasattr(self, "f_update_time"):
            self.f_update_time = current_timestamp()
        return super(BaseModel, self).save(*args, **kwargs)


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
        stat_logger.info('init mysql database on cluster mode successfully')


# MAIN_FILE_PATH = os.path.realpath(__main__.__file__)
# # if MAIN_FILE_PATH.endswith('ansible_server.py') or \
# #         MAIN_FILE_PATH.endswith('task_executor.py') or \
# #         MAIN_FILE_PATH.find("/unittest/__main__.py"):
# if MAIN_FILE_PATH.endswith('ansible_server.py') or \
#         MAIN_FILE_PATH.endswith('job_saver.py'):
#     DB = BaseDataBase().database_connection
# else:
#     # Initialize the database only when the server is started.
#     DB = None
DB = BaseDataBase().database_connection


def close_connection():
    try:
        if DB:
            DB.close()
    except Exception as e:
        LOGGER.exception(e)


class DataBaseModel(BaseModel):
    class Meta:
        database = DB


def init_database_tables():
    members = inspect.getmembers(sys.modules[__name__], inspect.isclass)
    table_objs = []
    for name, obj in members:
        if obj != DataBaseModel and issubclass(obj, DataBaseModel):
            table_objs.append(obj)
    DB.create_tables(table_objs)


class Job(DataBaseModel):
    f_job_id = CharField(max_length=100, primary_key=True)
    f_job_conf = JSONField()
    f_status = CharField(max_length=20)
    f_create_time = BigIntegerField()
    f_update_time = BigIntegerField(null=True)
    f_start_time = BigIntegerField(null=True)
    f_end_time = BigIntegerField(null=True)
    f_elapsed = IntegerField(null=True)

    class Meta:
        db_table = "t_job"


class Play(DataBaseModel):
    f_job_id = CharField(max_length=25)
    f_play_id = CharField(max_length=100)
    f_pid = IntegerField(null=True)
    f_play_name = CharField(max_length=500, null=True, default='')
    f_play_conf = LongTextField()
    f_status = CharField(max_length=50)
    f_create_time = BigIntegerField()
    f_update_time = BigIntegerField(null=True)
    f_start_time = BigIntegerField(null=True)
    f_end_time = BigIntegerField(null=True)
    f_hosts = LongTextField(null=True)
    f_roles = LongTextField(null=True)
    f_elapsed = IntegerField(null=True)

    class Meta:
        db_table = "t_play"
        primary_key = CompositeKey('f_job_id', 'f_play_id')


class Task(DataBaseModel):
    f_job_id = CharField(max_length=100)
    f_play_id = CharField(max_length=100)
    f_task_id = CharField(max_length=100)        # NEW
    f_role = CharField(max_length=200, null=True)
    f_task_name = CharField(max_length=200, null=True)     # NEW
    f_status = CharField(max_length=50)
    f_host = CharField(max_length=100, null=True)
    f_create_time = BigIntegerField()
    f_update_time = BigIntegerField(null=True)
    f_start_time = BigIntegerField(null=True)
    f_end_time = BigIntegerField(null=True)
    f_elapsed = IntegerField(null=True)

    class Meta:
        db_table = "t_task"
        primary_key = CompositeKey('f_play_id', 'f_task_id')


class PartyInfo(DataBaseModel):
    f_party_id = CharField(max_length=100, primary_key=True)
    # f_job_id = CharField(max_length=100)
    f_role = CharField(max_length=20)
    f_modules = JSONField()
    f_version = CharField(max_length=20)
    f_create_time = BigIntegerField(null=True)
    f_update_time = BigIntegerField(null=True)

    class Meta:
        db_table = "t_party_info"
        # primary_key = CompositeKey('f_version', 'f_party_id')


class Package(DataBaseModel):
    f_id = AutoField(primary_key=True)
    f_version = CharField(max_length=20)
    f_start_time = BigIntegerField(null=True)
    f_end_time = BigIntegerField(null=True)
    f_elapsed = IntegerField(null=True)
    f_status = CharField(max_length=10)

    class Meta:
        db_table = "t_package"


