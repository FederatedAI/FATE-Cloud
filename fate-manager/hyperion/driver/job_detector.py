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
from entity.types import EndStatus
from operations.job_saver import JobSaver
from utils import job_utils, cron
from driver.job_controller import JobController
from settings import detect_logger
from utils.log_utils import schedule_logger


class JobDetector(cron.Cron):
    def run_do(self):
        try:
            running_plays = JobSaver.query_play(status='running')
            stop_job_ids = set()
            for play in running_plays:
                try:
                    process_exist = job_utils.check_job_process(int(play.f_pid))
                    if not process_exist:
                        detect_logger.info('job {} play {} process does not exist'.format(play.f_job_id, play.f_pid))
                        stop_job_ids.add(play.f_job_id)
                        detect_logger.info(f'start to stop play {play.f_play_id}')
                        JobController.stop_play(job_id=play.f_job_id, play_id=play.f_play_id)
                except Exception as e:
                    detect_logger.exception(e)

            # ready_plays = JobSaver.query_play(status='ready')
            # for play in ready_plays:
            #     try:


            if stop_job_ids:
                schedule_logger().info('start to stop jobs: {}'.format(stop_job_ids))
            for job_id in stop_job_ids:
                jobs = JobSaver.query_job(job_id=job_id)
                if jobs:
                    if not EndStatus.contains(jobs[0].f_status):
                        JobController.stop_job(job_id=job_id)
        except Exception as e:
            detect_logger.exception(e)
        finally:
            detect_logger.info('finish detect running job')