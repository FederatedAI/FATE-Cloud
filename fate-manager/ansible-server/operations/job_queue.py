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
import threading
from time import monotonic as time
from entity.types import JobStatus

from db.db_models import DB, Job
from operations.job_saver import JobSaver
from settings import stat_logger
from entity.runtime_config import RuntimeConfig


class JobQueue:
    def __init__(self):
        self.ready = True
        self.mutex = threading.Lock()
        self.not_empty = threading.Condition(self.mutex)
        self.not_full = threading.Condition(self.mutex)
        self.maxsize = 0
        stat_logger.info('init queue')

    def is_ready(self):
        return self.ready

    @staticmethod
    def lock(db, lock_name, timeout):
        sql = "SELECT GET_LOCK('%s', %s)" % (lock_name, timeout)
        stat_logger.info(f"lock mysql database, name of lock: {lock_name}")
        ret = db.execute_sql(sql).fetchone()
        if ret[0] == 0:
            raise Exception(f"mysql lock {lock_name} is already used")
        elif ret[0] == 1:
            return True
        else:
            raise Exception(f'unknown mysql lock {lock_name} error occurred!')

    @staticmethod
    def unlock(db, lock_name):
        sql = "SELECT RELEASE_LOCK('%s')" % (lock_name)
        stat_logger.info('unlock mysql, lockname {}'.format(lock_name))
        cursor = db.execute_sql(sql)
        ret = cursor.fetchone()
        if ret[0] == 0:
            raise Exception('mysql lock {} is not released'.format(lock_name))
        elif ret[0] == 1:
            return True
        else:
            raise Exception('mysql lock {} did not exist.'.format(lock_name))

    def put_event(self):
        with self.not_full:
            self.not_empty.notify()

    def get(self, block=True, timeout=None):
        with self.not_empty:
            if not block:
                stat_logger.info("in queue get func, in if not block condition")
                if not self.query_waiting_jobs():
                    raise Exception
            elif timeout is None:
                stat_logger.info("in queue get func, in timeout is none condition")
                while not self.query_waiting_jobs():
                    stat_logger.info("in queue get func, in timeout is none condition, in while not loop")
                    self.not_empty.wait()
            elif timeout < 0:
                stat_logger.info("in queue get func, in timeout < 0 condition")
                raise ValueError("'timeout' must be a non-negative number")
            else:
                stat_logger.info("in queue get func, in else condition")
                endtime = time() + timeout
                while not self.query_waiting_jobs():
                    remaining = endtime - time()
                    if remaining <= 0.0:
                        raise Exception
                    self.not_empty.wait(remaining)
            stat_logger.info("in queue get func, ready to get in db")
            with DB.connection_context():
                error = None
                JobQueue.lock(DB, 'deploy_server_job_queue', 10)
                try:
                    item = Job.select().where(Job.f_status == JobStatus.WAITING)[0]
                    if item:
                        update_info = {
                            'job_id': item.f_job_id,
                            'status': JobStatus.READY
                        }
                        JobSaver.update_job_status(update_info)
                except Exception as e:
                    error = e
                JobQueue.unlock(DB, 'deploy_server_job_queue')
                if error:
                    raise Exception(e)
                self.not_full.notify()
                return {
                    'job_id': item.f_job_id,
                }

    def get_event(self):
        try:
            job = self.get(block=True)
            stat_logger.info('get event from queue successfully: {}'.format(job))
            return job
        except Exception as e:
            stat_logger.error('get job from queue failed')
            stat_logger.exception(e)
            return None

    @DB.connection_context()
    def query_waiting_jobs(self):
        jobs = Job.select().where(Job.f_status == JobStatus.WAITING)
        return [job for job in jobs]


def init_job_queue():
    RuntimeConfig.init_config(JOB_QUEUE=JobQueue())
