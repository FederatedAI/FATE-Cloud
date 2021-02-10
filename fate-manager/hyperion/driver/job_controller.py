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
import traceback

from entity.runtime_config import RuntimeConfig
from entity.types import JobStatus, PlayStatus, EndStatus, OngoingStatus, StandbyStatus
from operations.job_saver import JobSaver
from settings import stat_logger
from utils import job_utils, file_utils
from utils.base_utils import current_timestamp
from utils.log_utils import schedule_logger


class JobController:
    task_executor_pool = None

    @classmethod
    def submit_job(cls, job_data, job_id=None):
        try:
            if not job_id:
                job_id = job_utils.generate_job_id()
            stat_logger.info(f'Trying submit job, job_id {job_id}, body {job_data}')
            schedule_logger(job_id).info(f'Trying submit job, job_id {job_id}, body {job_data}')

            job_utils.check_job_conf(job_data)
            # {'job_conf_path': 'xxx', 'job_runtime_conf_path': 'xxx'}
            job_conf_path = file_utils.save_job_conf(job_id=job_id, job_data=job_data)

            job_info = {
                'job_id': job_id,
                'job_conf': job_data,
                'status': JobStatus.WAITING
            }
            JobSaver.create_job(job_info=job_info)
            RuntimeConfig.JOB_QUEUE.put_event()
            schedule_logger(job_id).info(f"submit job successfully, job id is {job_id}")
            stat_logger.info(f"submit job successfully, job id is {job_id}")
        except Exception:
            stat_logger.error(f"Submit job fail, details: {traceback.format_exc()}")
            return {}, {}
        else:
            return job_id, job_conf_path

    @staticmethod
    def stop_job(job_id, status=JobStatus.CANCELED):
        jobs = JobSaver.query_job(job_id=job_id)
        if jobs:
            plays = JobSaver.query_play(job_id=job_id)
            stop_result = {}
            final_status = True
            for play in [item for item in plays if not EndStatus.contains(item.f_status)]:
                stop_status = JobController.stop_play(job_id=job_id, play_id=play.f_play_id, status=status)
                stop_result[play.f_play_id] = 'stopped successfully' if stop_status else 'stopped failed'
                final_status = final_status & stop_status
            if final_status:
                update_info = {
                    'job_id': job_id,
                    'end_time': current_timestamp(),
                    'status': JobStatus.CANCELED
                }
                JobSaver.update_job(update_info)
                JobSaver.update_job_status(update_info)
            return final_status, stop_result
        else:
            return False, {job_id: f"Cannot found job {job_id}"}

    @staticmethod
    def stop_play(job_id, play_id, status=PlayStatus.CANCELED):
        plays = JobSaver.query_play(play_id=play_id)
        if plays:
            play = plays[0]
            kill_status = job_utils.kill_play_process_execution(play)
            if kill_status:
                if OngoingStatus.contains(play.f_status):
                    play_info = {
                        'job_id': job_id,
                        'play_id': play_id,
                        'end_time': current_timestamp(),
                        'status': status,
                    }
                    JobSaver.update_play_status(play_info)
                    if not StandbyStatus.contains(play.f_status):
                        JobSaver.update_play(play_info)
                return True
            else:
                return False
        else:
            schedule_logger(job_id).info(f"cannot find and kill process of play {play_id}")
            return False
