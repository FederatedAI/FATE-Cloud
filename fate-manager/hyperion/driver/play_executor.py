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
import argparse
from utils.log_utils import schedule_logger
from common.playbook import PlayBook


class PlayExecutor:
    @staticmethod
    def run_play():
        parser = argparse.ArgumentParser()
        parser.add_argument('--job_id', required=True, type=str, help='job id')
        parser.add_argument('--play_id', required=True, type=str, help='play id')
        parser.add_argument('--conf_path', required=True, type=str, help='play conf path')
        parser.add_argument('--hosts_path', required=True, type=str, help='play hosts file path')
        parser.add_argument('--test', required=False, action='store_true', help='test mode')
        parser.add_argument('--retry', required=False, action='store_true', help='retry mode')
        args = parser.parse_args()
        schedule_logger(args.job_id).info('enter play executor process')
        schedule_logger(args.job_id).info(args)

        play_args = ['ansible-playbook', '-i', args.hosts_path, args.conf_path]
        if args.test:
            play_args.append('-C')

        try:
            play = PlayBook(args=play_args)
            play.run_play(play_id=args.play_id, retry=args.retry)
        except Exception as e:
            schedule_logger().exception(e)
            raise


if __name__ == '__main__':
    PlayExecutor.run_play()