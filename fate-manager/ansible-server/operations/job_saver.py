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
import peewee
import operator
from utils.log_utils import schedule_logger
from db.db_models import Task, DB, Job, Play, PartyInfo
from settings import stat_logger
from utils.base_utils import current_timestamp
from entity.types import EndStatus, TaskStatus, JobStatus, PlayStatus


class JobSaver:
    STATUS_FIELDS = ['status']

    @classmethod
    def create_job(cls, job_info):
        schedule_logger(job_info['job_id']).info(f"Trying to create job {job_info['job_id']}...")
        cls.create_job_family_entity(Job, job_info)
        schedule_logger(job_info['job_id']).info(f"Creating job {job_info['job_id']} successfully.")

    @classmethod
    def create_task(cls, task_info):
        schedule_logger(task_info['job_id']).info(f"Trying to create task {task_info['task_id']}...")
        cls.create_job_family_entity(Task, task_info)
        schedule_logger(task_info['job_id']).info(f"Creating task {task_info['task_id']} successfully.")

    @classmethod
    def create_play(cls, play_info):
        schedule_logger(play_info['job_id']).info(f"Trying to create play {play_info['play_id']}...")
        cls.create_job_family_entity(Play, play_info)
        schedule_logger(play_info['job_id']).info(f"Creating play {play_info['play_id']} successfully.")

    @classmethod
    @DB.connection_context()
    def create_job_family_entity(cls, entity_model, entity_info):
        obj = entity_model()
        if 'create_time' not in entity_info:
            obj.f_create_time = current_timestamp()
        for k, v in entity_info.items():
            attr_name = 'f_%s' % k
            if hasattr(entity_model, attr_name):
                setattr(obj, attr_name, v)
        try:
            rows = obj.save(force_insert=True)
            if rows != 1:
                raise Exception("Create {} failed".format(entity_model))
        except peewee.IntegrityError as e:
            if e.args[0] == 1062:
                stat_logger.warning(e)
            else:
                raise Exception("Create {} failed:\n{}".format(entity_model, e))
        except Exception as e:
            raise Exception("Create {} failed:\n{}".format(entity_model, e))

    @classmethod
    def query_play(cls, reverse=None, order_by=None, **kwargs):
        return cls.query_entity(entity_model=Play, reverse=reverse, order_by=order_by, **kwargs)

    @classmethod
    def query_job(cls, reverse=None, order_by=None, **kwargs):
        return cls.query_entity(entity_model=Job, reverse=reverse, order_by=order_by, **kwargs)

    @classmethod
    def query_task(cls, reverse=None, order_by=None, **kwargs):
        return cls.query_entity(entity_model=Task, reverse=reverse, order_by=order_by, **kwargs)

    @classmethod
    def query_party(cls, reverse=None, order_by=None, **kwargs):
        return cls.query_entity(entity_model=PartyInfo, reverse=reverse, order_by=order_by, **kwargs)

    @classmethod
    @DB.connection_context()
    def query_entity(cls, entity_model, reverse=None, order_by=None, **kwargs):
        filters = []
        for f_n, f_v in kwargs.items():
            attr_name = 'f_%s' % f_n
            if hasattr(entity_model, attr_name):
                filters.append(operator.attrgetter('f_%s' % f_n)(entity_model) == f_v)
        if filters:
            entity_instances = entity_model.select().where(*filters)
            if reverse is not None:
                if not order_by or not hasattr(entity_model, f"f_{order_by}"):
                    order_by = "create_time"
                if reverse is True:
                    entity_instances = entity_instances.order_by(getattr(entity_model, f"f_{order_by}").desc())
                elif reverse is False:
                    entity_instances = entity_instances.order_by(getattr(entity_model, f"f_{order_by}").asc())
            return [instance for instance in entity_instances]
        else:
            # not allow query all instances
            return []

    @classmethod
    def clean_task(cls, **kwargs):
        return cls.clean_entity(entity_model=Task, **kwargs)

    @classmethod
    @DB.connection_context()
    def clean_entity(cls, entity_model, **kwargs):
        filters = []
        for f_n, f_v in kwargs.items():
            attr_name = 'f_%s' % f_n
            if hasattr(entity_model, attr_name):
                filters.append(operator.attrgetter('f_%s' % f_n)(entity_model) == f_v)
        if filters:
            entity_instances = entity_model.select().where(*filters)
            if entity_instances:
                for instance in entity_instances:
                    instance.delete_instance()
                return True
            return False
        return False

    @classmethod
    @DB.connection_context()
    def update_entity_table(cls, entity_model, entity_info):
        query_filters = []
        primary_keys = entity_model.get_primary_keys_name()
        for p_k in primary_keys:
            query_filters.append(operator.attrgetter(p_k)(entity_model) == entity_info[p_k.lstrip("f").lstrip("_")])
        objs = entity_model.select().where(*query_filters)
        if objs:
            obj = objs[0]
        else:
            raise Exception("can not found the {}".format(entity_model.__class__.__name__))
        update_filters = query_filters[:]
        update_info = {}
        update_info.update(entity_info)
        for _ in cls.STATUS_FIELDS:
            # not allow update status fields by this function
            update_info.pop(_, None)
        if update_info.get('end_time'):
            if obj.f_start_time:
                update_info['elapsed'] = update_info['end_time'] - obj.f_start_time
            else:
                update_info.pop('end_time')
        return cls.execute_update(old_obj=obj, model=entity_model,
                                  update_info=update_info,
                                  update_filters=update_filters)

    @classmethod
    @DB.connection_context()
    def execute_update(cls, old_obj, model, update_info, update_filters):
        update_fields = {}
        for k, v in update_info.items():
            attr_name = 'f_%s' % k
            if hasattr(model, attr_name) and attr_name not in model.get_primary_keys_name():
                update_fields[operator.attrgetter(attr_name)(model)] = v
        if update_fields:
            update_fields.update({'f_update_time': current_timestamp()})
            if update_filters:
                operate = old_obj.update(update_fields).where(*update_filters)
            else:
                operate = old_obj.update(update_fields)
            return operate.execute() > 0
        else:
            return False

    @classmethod
    def update_job(cls, job_info):
        schedule_logger(job_info["job_id"]).info(f'Start to update info of job {job_info["job_id"]}')
        update_status = cls.update_entity_table(entity_model=Job, entity_info=job_info)
        schedule_logger(job_info["job_id"]).info(f'Update info of job {job_info["job_id"]} {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    def update_play(cls, play_info):
        schedule_logger(play_info["job_id"]).info(f'Start to update info of play {play_info["play_id"]}, play_info: {play_info}')
        update_status = cls.update_entity_table(entity_model=Play, entity_info=play_info)
        schedule_logger(play_info["job_id"]).info(f'Update info of play {play_info["play_id"]} {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    def update_task(cls, task_info):
        schedule_logger(task_info["job_id"]).info(f'Start to update info of task {task_info["task_id"]}')
        update_status = cls.update_entity_table(entity_model=Task, entity_info=task_info)
        schedule_logger(task_info["job_id"]).info(f'Update info of task {task_info["task_id"]} {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    def update_job_status(cls, job_info):
        schedule_logger(job_info["job_id"]).info(f'Start to update status of job {job_info["job_id"]} to <{job_info["status"]}>')
        update_status = cls.update_status(entity_model=Job, entity_info=job_info)
        schedule_logger(job_info["job_id"]).info(f'Update status of job {job_info["job_id"]} to <{job_info["status"]}> {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    def update_play_status(cls, play_info):
        schedule_logger(play_info["job_id"]).info(f'Start to update status of play {play_info["play_id"]} to <{play_info["status"]}>')
        update_status = cls.update_status(entity_model=Play, entity_info=play_info)
        schedule_logger(play_info["job_id"]).info(f'Update status of play {play_info["play_id"]} to <{play_info["status"]}> {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    def update_task_status(cls, task_info):
        schedule_logger(task_info["job_id"]).info(f'Start to update status of task {task_info["task_id"]} to <{task_info["status"]}>')
        update_status = cls.update_status(entity_model=Task, entity_info=task_info)
        schedule_logger(task_info["job_id"]).info(f'Update status of task {task_info["task_id"]} to <{task_info["status"]}> {"success" if update_status else "failed"}')
        return update_status

    @classmethod
    @DB.connection_context()
    def update_status(cls, entity_model, entity_info):
        query_filters = []
        primary_keys = entity_model.get_primary_keys_name()
        for p_k in primary_keys:
            query_filters.append(operator.attrgetter(p_k)(entity_model) == entity_info[p_k.lstrip("f").lstrip("_")])
        objs = entity_model.select().where(*query_filters)
        if objs:
            obj = objs[0]
        else:
            raise Exception("can not found the {}".format(entity_model.__class__.__name__))
        update_filters = query_filters[:]
        update_info = {}
        for status_field in cls.STATUS_FIELDS:
            if entity_info.get(status_field) and hasattr(entity_model, f"f_{status_field}"):
                update_info[status_field] = entity_info[status_field]
                if_pass = False
                if isinstance(obj, Task):
                    if TaskStatus.StateTransitionRule.if_pass(src_status=getattr(obj, f"f_{status_field}"), dest_status=update_info[status_field]):
                        if_pass = True
                elif isinstance(obj, Job):
                    if JobStatus.StateTransitionRule.if_pass(src_status=getattr(obj, f"f_{status_field}"), dest_status=update_info[status_field]):
                        if_pass = True
                elif isinstance(obj, Play):
                    if PlayStatus.StateTransitionRule.if_pass(src_status=getattr(obj, f"f_{status_field}"), dest_status=update_info[status_field]):
                        if_pass = True
                if if_pass:
                    update_filters.append(operator.attrgetter(f"f_{status_field}")(type(obj)) == getattr(obj, f"f_{status_field}"))
                else:
                    # not allow update status
                    update_info.pop(status_field)
        return cls.execute_update(old_obj=obj, model=entity_model, update_info=update_info, update_filters=update_filters)
