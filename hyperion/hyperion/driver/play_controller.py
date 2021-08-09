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
import json
import os
import shutil
import sys
from collections import OrderedDict
from concurrent.futures.thread import ThreadPoolExecutor

from settings import stat_logger, TEST_MODE, CONCURRENT_RETRY_NUM
from utils.file_utils import get_package_dir_by_version
from utils.log_utils import schedule_logger
from driver.play_executor import PlayExecutor
from entity.types import JobStatus, PlayStatus
from operations.job_saver import JobSaver
from utils import job_utils, file_utils
from db.db_models import DB, PartyInfo
from utils.base_utils import current_timestamp

play_retry_executor_pool = ThreadPoolExecutor(max_workers=CONCURRENT_RETRY_NUM)


class PlayController:
    @staticmethod
    def run_job(job_id):
        job_data = job_utils.get_job_configuration(job_id=job_id)
        stat_logger.info(f"in play controller run job func, get job data: {json.dumps(job_data, indent=4)}")
        schedule_logger(job_id).info(f"in play controller, func run job: {json.dumps(job_data, indent=4)}")

        play_conf_path_dict = PlayController.initialize_plays(job_id=job_id, job_data=job_data)
        stat_logger.info(f"in play controller run job func after initialize play\n get play conf path dict: {play_conf_path_dict}")

        # TODO get package dir by version
        package_dir = get_package_dir_by_version(job_data.get('version'))
        if not os.path.exists(package_dir) and not os.path.isdir(package_dir):
            raise Exception(f'Local package directory {package_dir} not exists.')

        job_info = {
            'job_id': job_id,
            'status': JobStatus.RUNNING,
            'start_time': current_timestamp()
        }
        JobSaver.update_job_status(job_info)
        JobSaver.update_job(job_info)

        for play_id, conf_dict in play_conf_path_dict.items():
            conf_dict['conf_path'] = shutil.copy2(src=conf_dict['conf_path'], dst=package_dir)
            PlayController.run_play(job_id=job_id, play_id=play_id,
                                    play_conf_path=conf_dict.get('conf_path'),
                                    play_hosts_path=conf_dict.get('hosts_path'),
                                    test_mode=TEST_MODE)
            if os.path.exists(conf_dict['conf_path']):
                os.remove(conf_dict['conf_path'])
            plays = JobSaver.query_play(play_id=play_id)
            if plays:
                play = plays[0]
                status = play.f_status
                if status != PlayStatus.SUCCESS:
                    if status in [PlayStatus.CANCELED, PlayStatus.FAILED, PlayStatus.TIMEOUT]:
                        update_info = {
                            'job_id': job_id,
                            'play_id': play_id,
                            'status': status,
                            'end_time': current_timestamp()
                        }
                        JobSaver.update_play_status(update_info)
                        JobSaver.update_play(update_info)
                        JobSaver.update_job_status(update_info)
                        JobSaver.update_job(update_info)
                    else:
                        update_info = {
                            'job_id': job_id,
                            'play_id': play_id,
                            'status': PlayStatus.FAILED,
                            'end_time': current_timestamp()
                        }
                        schedule_logger(job_id).error(f'Unexpected error occured on play {play_id}, job {job_id} failed, previous status of play: {play.f_status}')
                        stat_logger.error(f'Unexpected error occured on play {play_id}, job {job_id} failed, previous status of play: {play.f_status}')

                        JobSaver.update_play_status(update_info)
                        JobSaver.update_play(update_info)
                        JobSaver.update_job_status(update_info)
                        JobSaver.update_job(update_info)

                        schedule_logger(job_id).info(f"job {job_id} finished, status is {update_info.get('status')}")
                    break
                else:
                    update_info = {
                        'job_id': job_id,
                        'play_id': play_id,
                        'status': PlayStatus.SUCCESS,
                        'end_time': current_timestamp()
                    }
                    JobSaver.update_play_status(update_info)
                    JobSaver.update_play(update_info)
            else:
                raise Exception(f'can not find play {play_id}')
        else:
            update_info = {
                'job_id': job_id,
                'status': JobStatus.SUCCESS,
                'end_time': current_timestamp()
            }
            JobSaver.update_job(update_info)
            JobSaver.update_job_status(update_info)
            schedule_logger(job_id).info(f"job {job_id} finished, status is {update_info.get('status')}")

        if not TEST_MODE:
            plays = JobSaver.query_play(job_id=job_id, status=PlayStatus.SUCCESS)
            modules = []
            module_names = []
            for play in plays:
                module_name = play.f_roles.strip('[]').replace('_', '')
                module_names.append(module_name)
                modules.append({'name': module_name,
                                'ips': job_data.get('modules', {}).get(module_name, {}).get('ips', []),
                                'port': job_data.get('modules', {}).get(module_name, {}).get('port', None)
                                })

            # parties = PartyInfo.get_or_none(f_version=job_data.get('version'), f_party_id=job_data.get('party_id'))
            parties = PartyInfo.get_or_none(f_party_id=job_data.get('party_id'))
            if parties:
                module_mapping = dict(zip(module_names, modules))
                stored_modules = parties.f_modules.get("data", [])

                name_map = {}
                for offset, item in enumerate(stored_modules):
                    name_map[item.get('name')] = offset

                for key, value in module_mapping.items():
                    if key in name_map:
                        schedule_logger(job_id).info(f"{key} in name map, in replace process")
                        stored_modules[name_map[key]] = value
                    else:
                        schedule_logger(job_id).info(f"{key} not in name map, in append process ")
                        stored_modules.append(value)

                # update_status = False
                # for offset, module_info in enumerate(stored_modules):
                #     if module_info['name'] in module_mapping:
                #         stored_modules[offset] = module_mapping[module_info['name']]
                #         update_status = True
                for key in ['role', 'version']:
                    # if parties[key] != job_data[key]:
                    #     parties[key] = job_data[key]
                    if getattr(parties, f'f_{key}') != job_data[key]:
                        setattr(parties, f'f_{key}', job_data[key])
                        # update_status = True
                # if update_status:
                parties.f_modules = {'data': stored_modules}
                parties.save()
                DB.commit()
            else:
                party_info = PartyInfo()
                # party_info.f_job_id = job_id
                party_info.f_role = job_data.get('role')
                party_info.f_version = job_data.get('version')
                party_info.f_party_id = job_data.get('party_id')
                party_info.f_modules = {'data': modules}
                party_info.save(force_insert=True)

    @staticmethod
    def retry_play(job_id, play_id, test_mode=False):
        plays = JobSaver.query_play(play_id=play_id)
        if not plays:
            return 100, f"Retry play {play_id} failed, can not find such play in database."

        # copy play conf into package dir
        play_conf_path_dict = file_utils.get_play_conf_path(play_id)
        with open(file_utils.get_job_conf_path(job_id), 'r') as f:
            job_conf = json.loads(f.read())
        package_dir = get_package_dir_by_version(job_conf.get('version'))
        play_conf_path_dict['conf_path'] = shutil.copy2(src=play_conf_path_dict['conf_path'], dst=package_dir)

        update_info = {
            'job_id': job_id,
            'play_id': play_id,
            'status': PlayStatus.WAITING,
        }
        JobSaver.update_play_status(update_info)
        JobSaver.update_job_status(update_info)

        # clean task records
        JobSaver.clean_task(play_id=play_id)
        # execute run_play method
        try:
            play_retry_executor_pool.submit(PlayController.run_play,
                                            job_id=job_id, play_id=play_id,
                                            play_conf_path=play_conf_path_dict['conf_path'],
                                            play_hosts_path=play_conf_path_dict['hosts_path'],
                                            test_mode=test_mode, retry_mode=True)
            return 0, f"Start retrying play {play_id}"
        except Exception as e:
            stat_logger.exception(e)
            return 100, f"Retry play {play_id} failed, details: {str(e)}"

    @staticmethod
    def run_play(job_id, play_id, play_conf_path, play_hosts_path, test_mode=False, retry_mode=False):
        schedule_logger(job_id).info(f'Trying to start to run play with id: {play_id}')
        # task_process_start_status = False
        process_cmd = [
            'python3', sys.modules[PlayExecutor.__module__].__file__,
            '--job_id', job_id,
            '--play_id', play_id,
            '--conf_path', play_conf_path,
            '--hosts_path', play_hosts_path,
        ]
        if test_mode:
            process_cmd.append('--test')
        if retry_mode:
            process_cmd.append('--retry')
        schedule_logger(job_id).info(f"Trying to start job {job_id}, play {play_id} subprocess.")
        try:
            config_dir = file_utils.get_play_directory(play_id)
            std_dir = file_utils.get_job_log_directory(job_id)
            p = job_utils.run_subprocess(config_dir=config_dir,
                                         process_cmd=process_cmd,
                                         log_dir=std_dir)
            if p:
                # task_process_start_status = True
                play_info = {
                    'pid': p.pid,
                    'job_id': job_id,
                    'play_id': play_id,
                    'status': PlayStatus.RUNNING,
                    'start_time': current_timestamp()
                }
                JobSaver.update_play_status(play_info=play_info)
                JobSaver.update_play(play_info=play_info)
                p.wait()
            else:
                raise Exception(f'play {play_id} start subprocess failed')
        except Exception as e:
            play_info = {
                'job_id': job_id,
                'play_id': play_id,
                'status': PlayStatus.FAILED,
                'end_time': current_timestamp()
            }
            JobSaver.update_play_status(play_info)
            JobSaver.update_play(play_info)
            schedule_logger(job_id).exception(e)
        finally:
            if retry_mode:
                job_info = {
                    'job_id': job_id,
                    'play_id': play_id,
                    'end_time': current_timestamp(),
                    'status': JobStatus.SUCCESS if PlayController.check_job_status(job_id=job_id) else JobStatus.FAILED
                }
                JobSaver.update_job(job_info)
                JobSaver.update_job_status(job_info)

    @staticmethod
    def create_play(job_id, play_id, play_conf, play_hosts):
        play_info = {
            "job_id": job_id,
            "play_id": play_id,
            "play_conf": play_conf,
            "play_name": play_id,
            "hosts": play_hosts,
            "status": PlayStatus.WAITING,
        }
        JobSaver.create_play(play_info=play_info)

    @staticmethod
    def initialize_plays(job_id, job_data):
        play_conf_path = OrderedDict()
        adapter = job_utils.get_adapter(job_data.get('version'))
        try:
            schedule_logger(job_id).info('Start initializing plays...')
            stat_logger.info('Start initializing plays...')

            play_conf_dict = adapter.generate_play_conf(job_id=job_id, job_data=job_data)

            for play_id, conf in play_conf_dict.items():
                schedule_logger(job_id).info(f'Start create and save play conf, play id: {play_id}')
                schedule_logger(job_id).info(f'Start create and save play conf, play id: {play_id}')
                path_dict = file_utils.save_play_conf(job_id=job_id, play_id=play_id,
                                                      play_conf=conf['yml'], play_hosts=conf['hosts'])
                play_conf_path.update(path_dict)
                PlayController.create_play(job_id=job_id, play_id=play_id,
                                           play_conf=conf['yml'], play_hosts=conf['hosts'])
                schedule_logger(job_id).info(f'Initializing play successfully, play id: {play_id}')
        except Exception as e:
            stat_logger.exception(e)
            schedule_logger(job_id).exception(e)
            return {}
        else:
            return play_conf_path

    @staticmethod
    def check_job_status(job_id: str):
        plays = JobSaver.query_play(job_id=job_id)
        job_success = True
        if plays:
            for play in plays:
                job_success = job_success and play.f_status == PlayStatus.SUCCESS
        else:
            job_success = False
        return job_success

    @staticmethod
    def clean_queue():
        # TODO clean all events in queue
        pass

    # @staticmethod
    # def update_play_status(play_info: dict):
    #     update_status = JobSaver.update_play_status(play_info)
    #     return update_status
    #
    # @staticmethod
    # def update_task_status(task_info: dict):
    #     update_status = JobSaver.update_task_status(task_info)
    #     return update_status
    #
    # @staticmethod
    # def update_play(play_info: dict):
    #     update_status = JobSaver.update_play(play_info)
    #     return update_status
    #
    # @staticmethod
    # def update_task(task_info: dict):
    #     update_status = JobSaver.update_task(task_info)
    #     return update_status







