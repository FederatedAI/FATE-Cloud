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
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import as_completed

from utils.log_utils import schedule_logger
from driver.play_controller import PlayController
from operations.job_queue import JobQueue
from settings import stat_logger


class JobScheduler(threading.Thread):
    def __init__(self, queue: JobQueue, concurrent_num: int = 1):
        super(JobScheduler, self).__init__()
        self.concurrent_num = concurrent_num
        self.queue = queue
        self.job_executor_pool = ThreadPoolExecutor(max_workers=concurrent_num)

    def run(self):
        if not self.queue.is_ready():
            schedule_logger().error('queue is not ready')
            return False
        all_jobs = []
        while True:
            try:
                schedule_logger().info("Starting in queue detecting loop...")
                if len(all_jobs) == self.concurrent_num:
                    for future in as_completed(all_jobs):
                        all_jobs.remove(future)
                        break
                stat_logger.info("Trying get event...")
                job_event = self.queue.get_event()
                stat_logger.info("get event success")
                schedule_logger(job_event['job_id']).info('schedule job {}'.format(job_event))
                future = self.job_executor_pool.submit(JobScheduler.handle_event, job_event['job_id'])
                future.add_done_callback(JobScheduler.get_result)
                all_jobs.append(future)
            except Exception as e:
                schedule_logger().exception(e)

    def stop(self):
        self.job_executor_pool.shutdown(True)

    @staticmethod
    def handle_event(job_id):
        try:
            return PlayController.run_job(job_id=job_id)
        except Exception as e:
            schedule_logger(job_id).exception(e)
            return False

    @staticmethod
    def get_result(future):
        future.result()
