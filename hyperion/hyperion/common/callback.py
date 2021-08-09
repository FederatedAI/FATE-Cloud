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

from __future__ import (absolute_import, division, print_function)
__metaclass__ = type

import json
import traceback

DOCUMENTATION = '''
    callback: default
    type: stdout
    short_description: default Ansible screen output
    version_added: historical
    description:
        - This is the default output callback for ansible-playbook.
    extends_documentation_fragment:
      - default_callback
    requirements:
      - set as stdout in configuration
    options:
      check_mode_markers:
        name: Show markers when running in check mode
        description:
        - "Toggle to control displaying markers when running in check mode. The markers are C(DRY RUN)
        at the beggining and ending of playbook execution (when calling C(ansible-playbook --check))
        and C(CHECK MODE) as a suffix at every play and task that is run in check mode."
        type: bool
        default: no
        version_added: 2.9
        env:
          - name: ANSIBLE_CHECK_MODE_MARKERS
        ini:
          - key: check_mode_markers
            section: defaults
'''

# NOTE: check_mode_markers functionality is also implemented in the following derived plugins:
#       debug.py, yaml.py, dense.py. Maybe their documentation needs updating, too.
from ansible import constants as C
from ansible import context
from ansible.playbook.task_include import TaskInclude
from ansible.plugins.callback import CallbackBase
from ansible.utils.color import colorize, hostcolor
# from settings import stat_logger
from utils.log_utils import schedule_logger, host_logger
from operations.job_saver import JobSaver
from entity.types import TaskStatus, PlayStatus
from utils.base_utils import current_timestamp

# These values use ansible.constants for historical reasons, mostly to allow
# unmodified derivative plugins to work. However, newer options added to the
# plugin are not also added to ansible.constants, so authors of derivative
# callback plugins will eventually need to add a reference to the common docs
# fragment for the 'default' callback plugin

# these are used to provide backwards compat with old plugins that subclass from default
# but still don't use the new config system and/or fail to document the options
# TODO: Change the default of check_mode_markers to True in a future release (2.13)
# COMPAT_OPTIONS = (('display_skipped_hosts', C.DISPLAY_SKIPPED_HOSTS),
#                   ('display_ok_hosts', True),
#                   ('show_custom_stats', C.SHOW_CUSTOM_STATS),
#                   ('display_failed_stderr', False),
#                   ('check_mode_markers', False),
#                   ('show_per_host_start', False))
COMPAT_OPTIONS = (('display_skipped_hosts', C.DISPLAY_SKIPPED_HOSTS),
                  ('display_ok_hosts', True),
                  ('show_custom_stats', C.SHOW_CUSTOM_STATS),
                  ('display_failed_stderr', True),
                  ('check_mode_markers', False),
                  ('show_per_host_start', True))


class CallbackModule(CallbackBase):

    '''
    This is the default callback interface, which simply prints messages
    to stdout when new callback events are received.
    '''

    CALLBACK_VERSION = 2.0
    CALLBACK_TYPE = 'stdout'
    CALLBACK_NAME = 'default'

    def __init__(self):

        self._play = None
        self._last_task_banner = None
        self._last_task_name = None
        self._task_type_cache = {}
        self._play_id = None
        self._job_id = None
        self._retry = None
        super(CallbackModule, self).__init__()

    def set_play_id(self, value):
        self._play_id = value
        self.set_job_id(value)

    def set_job_id(self, value: str):
        self._job_id = value.split('_')[0]
        schedule_logger(self._job_id).info(f"set job id successfully, job id: {self._job_id}")

    def set_retry(self, value: bool):
        self._retry = value

    def set_options(self, task_keys=None, var_options=None, direct=None):

        super(CallbackModule, self).set_options(task_keys=task_keys, var_options=var_options, direct=direct)

        # for backwards compat with plugins subclassing default, fallback to constants
        for option, constant in COMPAT_OPTIONS:
            try:
                value = self.get_option(option)
            except (AttributeError, KeyError):
                value = constant
            setattr(self, option, value)

    def v2_runner_on_failed(self, result, ignore_errors=False):
        # schedule_logger(self._job_id).error(f"Run on failed, result: {result._task._uuid}")
        # schedule_logger(self._job_id).error(f"Run on failed, details: {result.__dict__}")
        # stat_logger.info(f'>>>>>>>>>run on ok failed: {json.dumps(result._result, indent=4)}')
        schedule_logger(self._job_id).error(f"<TASK FAILED> Host {result._host} executes task {result._task._uuid} {result._task._attributes.get('name')} failed. Details: {json.dumps(result._result, indent=4)}")
        update_info = {
            'job_id': self._job_id,
            'play_id': self._play_id,
            'task_id': result._task._uuid,
            'end_time': current_timestamp(),
            'status': TaskStatus.FAILED,
        }
        JobSaver.update_task(update_info)
        JobSaver.update_task_status(update_info)
        JobSaver.update_play(update_info)
        JobSaver.update_play_status(update_info)
        JobSaver.update_job(update_info)
        JobSaver.update_job_status(update_info)

        delegated_vars = result._result.get('_ansible_delegated_vars', None)
        self._clean_results(result._result, result._task.action)

        if self._last_task_banner != result._task._uuid:
            self._print_task_banner(result._task)

        self._handle_exception(result._result, use_stderr=self.display_failed_stderr)
        self._handle_warnings(result._result)

        if result._task.loop and 'results' in result._result:
            self._process_items(result)

        else:
            if delegated_vars:
                self._display.display("fatal: [%s -> %s]: FAILED! => %s" % (result._host.get_name(), delegated_vars['ansible_host'],
                                                                            self._dump_results(result._result)),
                                      color=C.COLOR_ERROR, stderr=self.display_failed_stderr)
            else:
                self._display.display("fatal: [%s]: FAILED! => %s" % (result._host.get_name(), self._dump_results(result._result)),
                                      color=C.COLOR_ERROR, stderr=self.display_failed_stderr)

        if ignore_errors:
            self._display.display("...ignoring", color=C.COLOR_SKIP)

    def v2_runner_on_ok(self, result):
        try:
            schedule_logger(self._job_id).info(f"<TASK SUCCESS> Host {result._host}, type of host: {type(result._host)} ")

            host_logger(self._job_id, result._host).info(f"<TASK SUCCESS> Task id: {result._task._uuid}, name: {result._task._attributes.get('name')} executed successfully on host {result._host}")
            schedule_logger(self._job_id).info(f"<TASK SUCCESS> Host {result._host} executes task {result._task._uuid} {result._task._attributes.get('name')} successfully. Details: {json.dumps(result._result, indent=4)}")
            # schedule_logger(self._job_id).info(f"Run on ok, details: {json.dumps(result._result, indent=4)}")
            schedule_logger(self._job_id).info(f"<RESULT INFO> f{result.__dict__}")
            update_info = {
                'job_id': self._job_id,
                'play_id': self._play_id,
                'task_id': result._task._uuid,
                'task_name': result._task._attributes.get('name'),
                'status': TaskStatus.SUCCESS,
                'end_time': current_timestamp(),
                'role': result._task._role
            }
            JobSaver.update_task(update_info)
            JobSaver.update_task_status(update_info)

        except Exception:
            schedule_logger(self._job_id).warning(f"In v2_runner_on_ok: details: {traceback.format_exc()}")

        delegated_vars = result._result.get('_ansible_delegated_vars', None)

        if isinstance(result._task, TaskInclude):
            return
        elif result._result.get('changed', False):
            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            if delegated_vars:
                msg = "changed: [%s -> %s]" % (result._host.get_name(), delegated_vars['ansible_host'])
            else:
                msg = "changed: [%s]" % result._host.get_name()
            color = C.COLOR_CHANGED
        else:
            if not self.display_ok_hosts:
                return

            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            if delegated_vars:
                msg = "ok: [%s -> %s]" % (result._host.get_name(), delegated_vars['ansible_host'])
            else:
                msg = "ok: [%s]" % result._host.get_name()
            color = C.COLOR_OK

        self._handle_warnings(result._result)

        if result._task.loop and 'results' in result._result:
            self._process_items(result)
        else:
            self._clean_results(result._result, result._task.action)

            if self._run_is_verbose(result):
                msg += " => %s" % (self._dump_results(result._result),)
            self._display.display(msg, color=color)

    def v2_runner_on_skipped(self, result):
        # stat_logger.info(f'>>>>>>>>>in runner on skipped: {json.dumps(result._result, indent=4)}')
        schedule_logger(self._job_id).info(f"<TASK SKIPPED> Host {result._host} executes task {result._task._uuid} {result._task._attributes.get('name')} skipped. Details: {json.dumps(result._result, indent=4)}")
        update_info = {
            'job_id': self._job_id,
            'play_id': self._play_id,
            'task_id': result._task._uuid,
            'status': TaskStatus.SKIPPED,
            'end_time': current_timestamp(),
            'host': result._host
        }
        JobSaver.update_task(update_info)
        JobSaver.update_task_status(update_info)
        # schedule_logger(self._job_id).info(f'>>>>>>>>>in runner on skipped: {json.dumps(result._result, indent=4)}')

        if self.display_skipped_hosts:
            self._clean_results(result._result, result._task.action)

            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            if result._task.loop and 'results' in result._result:
                self._process_items(result)
            else:
                msg = "skipping: [%s]" % result._host.get_name()
                if self._run_is_verbose(result):
                    msg += " => %s" % self._dump_results(result._result)
                self._display.display(msg, color=C.COLOR_SKIP)

    def v2_runner_on_unreachable(self, result):
        try:
            schedule_logger(self._job_id).error(f"<HOST UNREACHABLE> Host {result._host} executes task {result._task._uuid} {result._task._attributes.get('name')} unreachable. \nDetails: {json.dumps(result._result, indent=4)}")
            # schedule_logger(self._job_id).info(f'>>>>>>>>>in runner on unreachable: {json.dumps(result._result, indent=4)}')
            # stat_logger.info(f'>>>>>>>>>in runner on unreachable: {json.dumps(result._result, indent=4)}')

            update_info = {
                'job_id': self._job_id,
                'play_id': self._play_id,
                'task_id': result._task._uuid,
                'end_time': current_timestamp(),
                'status': TaskStatus.FAILED,
            }
            JobSaver.update_task(update_info)
            JobSaver.update_task_status(update_info)
            JobSaver.update_play(update_info)
            JobSaver.update_play_status(update_info)
        except Exception:
            schedule_logger(self._job_id).warning(f"In v2_runner_on_unreachable, details: {traceback.format_exc()}")

        if self._last_task_banner != result._task._uuid:
            self._print_task_banner(result._task)

        delegated_vars = result._result.get('_ansible_delegated_vars', None)
        if delegated_vars:
            msg = "fatal: [%s -> %s]: UNREACHABLE! => %s" % (result._host.get_name(), delegated_vars['ansible_host'], self._dump_results(result._result))
        else:
            msg = "fatal: [%s]: UNREACHABLE! => %s" % (result._host.get_name(), self._dump_results(result._result))
        self._display.display(msg, color=C.COLOR_UNREACHABLE, stderr=self.display_failed_stderr)

    def v2_playbook_on_no_hosts_matched(self):
        schedule_logger(self._job_id).info("<No host matched> Skipping: no hosts matched.")
        update_info = {
            'job_id': self._job_id,
            'play_id': self._play_id,
            'end_time': current_timestamp(),
            'status': PlayStatus.FAILED
        }
        JobSaver.update_play(update_info)
        JobSaver.update_play_status(update_info)
        JobSaver.update_job(update_info)
        JobSaver.update_job_status(update_info)
        self._display.display("skipping: no hosts matched", color=C.COLOR_SKIP)

    def v2_playbook_on_no_hosts_remaining(self):
        self._display.banner("NO MORE HOSTS LEFT")

    def v2_playbook_on_task_start(self, task, is_conditional):
        self._task_start(task, prefix='TASK')

    def _task_start(self, task, prefix=None):
        # Create task from this function
        # schedule_logger(self._job_id).info(f"<CREATING TASK> Trying to create task. Task id: {task._uuid}, task name: {task._attributes.get('name')}, prefix: {prefix}")
        schedule_logger(self._job_id).info(f"<CREATING TASK> Trying to create task. Task id: {task._uuid}, task name: {task._attributes.get('name')}, module: {task._role}.")
        if self._retry:
            tasks = JobSaver.query_task(play_id=self._play_id)
            if tasks:
                pass
        task_info = {
            'job_id': self._job_id,
            'play_id': self._play_id,
            'task_id': task._uuid,
            'task_name': task._attributes.get('name'),
            'status': TaskStatus.WAITING,
            'create_time': current_timestamp(),
            'role': task._role,
        }
        JobSaver.create_task(task_info)
        schedule_logger(self._job_id).info(f"create task with id {task._uuid} successfully")

        # try:
        #     schedule_logger(self._job_id).info(f"task role: {task._role}")
        # except Exception:
        #     pass
        # stat_logger.info(f"<<<<<>>>>> in _task_start, task name: {task._attributes.get('name')}, task id: {task._uuid}")


        # Cache output prefix for task if provided
        # This is needed to properly display 'RUNNING HANDLER' and similar
        # when hiding skipped/ok task results
        if prefix is not None:
            self._task_type_cache[task._uuid] = prefix

        # Preserve task name, as all vars may not be available for templating
        # when we need it later
        if self._play.strategy == 'free':
            # Explicitly set to None for strategy 'free' to account for any cached
            # task title from a previous non-free play
            self._last_task_name = None
        else:
            self._last_task_name = task.get_name().strip()

            # Display the task banner immediately if we're not doing any filtering based on task result
            if self.display_skipped_hosts and self.display_ok_hosts:
                self._print_task_banner(task)

    def _print_task_banner(self, task):
        # args can be specified as no_log in several places: in the task or in
        # the argument spec.  We can check whether the task is no_log but the
        # argument spec can't be because that is only run on the target
        # machine and we haven't run it thereyet at this time.
        #
        # So we give people a config option to affect display of the args so
        # that they can secure this if they feel that their stdout is insecure
        # (shoulder surfing, logging stdout straight to a file, etc).
        args = ''
        if not task.no_log and C.DISPLAY_ARGS_TO_STDOUT:
            args = u', '.join(u'%s=%s' % a for a in task.args.items())
            args = u' %s' % args

        prefix = self._task_type_cache.get(task._uuid, 'TASK')

        # Use cached task name
        task_name = self._last_task_name
        if task_name is None:
            task_name = task.get_name().strip()

        if task.check_mode and self.check_mode_markers:
            checkmsg = " [CHECK MODE]"
        else:
            checkmsg = ""
        self._display.banner(u"%s [%s%s]%s" % (prefix, task_name, args, checkmsg))
        if self._display.verbosity >= 2:
            path = task.get_path()
            if path:
                self._display.display(u"task path: %s" % path, color=C.COLOR_DEBUG)

        self._last_task_banner = task._uuid

    def v2_playbook_on_cleanup_task_start(self, task):
        self._task_start(task, prefix='CLEANUP TASK')

    def v2_playbook_on_handler_task_start(self, task):
        self._task_start(task, prefix='RUNNING HANDLER')

    def v2_runner_on_start(self, host, task):
        # schedule_logger(self._job_id).info(f"in v2 runner on start, host: {host}, task: {task}, task id: {task._uuid}")
        schedule_logger(self._job_id).info(f"<START TASK> Starting task. Host: {host}. task id: {task._uuid}, task name: {task._attributes.get('name')}")
        update_info = {
            'job_id': self._job_id,
            'play_id': self._play_id,
            'task_id': task._uuid,
            'task_name': task._attributes.get('name'),
            'status': TaskStatus.RUNNING,
            'start_time': current_timestamp(),
            'role': task._role,
            'host': host
        }
        JobSaver.update_task(update_info)
        JobSaver.update_task_status(update_info)
        #
        # schedule_logger(self._job_id).info(f"<TASK ON START> host: {host}, task id: {task._uuid}, task name: {task._attribute.get('name')}")
        # schedule_logger(self._job_id).info(f"<TASK ON START> host: {host}, task id: {task._uuid}, task name: {task._attribute.get('name')}")
        if self.get_option('show_per_host_start'):
            self._display.display(" [started %s on %s]" % (task, host), color=C.COLOR_OK)

    def v2_playbook_on_play_start(self, play):
        # TODO what if there is no play
        name = play.get_name().strip()
        # stat_logger.info(f"<<****<<**>>*****>> in playbook on play start, customise play_id: {self._play_id}")
        schedule_logger(self._job_id).info(f"<PLAY START> Play id: {self._play_id}, play name: {name}")
        try:
            play_info = {
                'job_id': self._job_id,
                'play_id': self._play_id,
                'play_name': name,
                'roles': str(play.get_roles()),
            }
            schedule_logger(self._job_id).info(f"play details: {json.dumps(play_info, indent=4)}")
            JobSaver.update_play(play_info)
        except Exception as e:
            schedule_logger(self._job_id).warning(f"In v2_playbook_on_play_start: {traceback.format_exc()}")
        schedule_logger(self._job_id).info(f"<<****<<**>>*****>> tasks name: uuid  list: {play.get_tasks()}, roles: {play.get_roles()}, ")

        if play.check_mode and self.check_mode_markers:
            checkmsg = " [CHECK MODE]"
        else:
            checkmsg = ""
        if not name:
            msg = u"PLAY%s" % checkmsg
        else:
            msg = u"PLAY [%s]%s" % (name, checkmsg)

        self._play = play
        self._display.banner(msg)

    def v2_on_file_diff(self, result):
        if result._task.loop and 'results' in result._result:
            for res in result._result['results']:
                if 'diff' in res and res['diff'] and res.get('changed', False):
                    diff = self._get_diff(res['diff'])
                    if diff:
                        if self._last_task_banner != result._task._uuid:
                            self._print_task_banner(result._task)
                        self._display.display(diff)
        elif 'diff' in result._result and result._result['diff'] and result._result.get('changed', False):
            diff = self._get_diff(result._result['diff'])
            if diff:
                if self._last_task_banner != result._task._uuid:
                    self._print_task_banner(result._task)
                self._display.display(diff)

    def v2_runner_item_on_ok(self, result):
        schedule_logger(self._job_id).info(f'>>>>>>>>>>>>>>>> in runner item on ok: {json.dumps(result._result, indent=4)}')
        delegated_vars = result._result.get('_ansible_delegated_vars', None)
        if isinstance(result._task, TaskInclude):
            return
        elif result._result.get('changed', False):
            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            msg = 'changed'
            color = C.COLOR_CHANGED
        else:
            if not self.display_ok_hosts:
                return

            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            msg = 'ok'
            color = C.COLOR_OK

        if delegated_vars:
            msg += ": [%s -> %s]" % (result._host.get_name(), delegated_vars['ansible_host'])
        else:
            msg += ": [%s]" % result._host.get_name()

        msg += " => (item=%s)" % (self._get_item_label(result._result),)

        self._clean_results(result._result, result._task.action)
        if self._run_is_verbose(result):
            msg += " => %s" % self._dump_results(result._result)
        self._display.display(msg, color=color)

    def v2_runner_item_on_failed(self, result):
        # stat_logger.info(f'>>>>>>>>>>>>>>>> in runner item on failed: {json.dumps(result._result, indent=4)}')
        schedule_logger(self._job_id).info(f'>>>>>>>>>>>>>>>> in runner item on failed: {json.dumps(result._result, indent=4)}')

        if self._last_task_banner != result._task._uuid:
            self._print_task_banner(result._task)

        delegated_vars = result._result.get('_ansible_delegated_vars', None)
        self._clean_results(result._result, result._task.action)
        self._handle_exception(result._result)

        msg = "failed: "
        if delegated_vars:
            msg += "[%s -> %s]" % (result._host.get_name(), delegated_vars['ansible_host'])
        else:
            msg += "[%s]" % (result._host.get_name())

        self._handle_warnings(result._result)
        self._display.display(msg + " (item=%s) => %s" % (self._get_item_label(result._result), self._dump_results(result._result)), color=C.COLOR_ERROR)

    def v2_runner_item_on_skipped(self, result):
        # stat_logger.info(f'>>>>>>>>>>>>>>>> in runner item on skipped: {json.dumps(result._result, indent=4)}')
        schedule_logger(self._job_id).info(f'>>>>>>>>>>>>>>>> in runner item on skipped: {json.dumps(result._result, indent=4)}')

        if self.display_skipped_hosts:
            if self._last_task_banner != result._task._uuid:
                self._print_task_banner(result._task)

            self._clean_results(result._result, result._task.action)
            msg = "skipping: [%s] => (item=%s) " % (result._host.get_name(), self._get_item_label(result._result))
            if self._run_is_verbose(result):
                msg += " => %s" % self._dump_results(result._result)
            self._display.display(msg, color=C.COLOR_SKIP)

    def v2_playbook_on_include(self, included_file):
        msg = 'included: %s for %s' % (included_file._filename, ", ".join([h.name for h in included_file._hosts]))
        schedule_logger(self._job_id).info(f"in v2 playbook on include, msg: {msg}")
        if 'item' in included_file._args:
            msg += " => (item=%s)" % (self._get_item_label(included_file._args),)
        self._display.display(msg, color=C.COLOR_SKIP)

    def v2_playbook_on_stats(self, stats):

        self._display.banner("PLAY RECAP")

        hosts = sorted(stats.processed.keys())
        failed_count = 0
        for h in hosts:
            t = stats.summarize(h)
            # stat_logger.info(f'>>>>>>>>PLAY RECAP, {t}')
            schedule_logger(self._job_id).info(f"in playbook on stats: RECAP: {t}, host: {h}")
            failed_count += t['failures']
            failed_count += t['unreachable']

            # self._display.display(
            #     u"%s : %s %s %s %s %s %s %s" % (
            #         hostcolor(h, t),
            #         colorize(u'ok', t['ok'], C.COLOR_OK),
            #         colorize(u'changed', t['changed'], C.COLOR_CHANGED),
            #         colorize(u'unreachable', t['unreachable'], C.COLOR_UNREACHABLE),
            #         colorize(u'failed', t['failures'], C.COLOR_ERROR),
            #         colorize(u'skipped', t['skipped'], C.COLOR_SKIP),
            #         colorize(u'rescued', t['rescued'], C.COLOR_OK),
            #         colorize(u'ignored', t['ignored'], C.COLOR_WARN),
            #     ),
            #     screen_only=True
            # )

            self._display.display(
                u"%s : %s %s %s %s %s %s %s" % (
                    hostcolor(h, t, False),
                    colorize(u'ok', t['ok'], None),
                    colorize(u'changed', t['changed'], None),
                    colorize(u'unreachable', t['unreachable'], None),
                    colorize(u'failed', t['failures'], None),
                    colorize(u'skipped', t['skipped'], None),
                    colorize(u'rescued', t['rescued'], None),
                    colorize(u'ignored', t['ignored'], None),
                ),
                log_only=True
            )

        if not failed_count:
            update_info = {
                'job_id': self._job_id,
                'play_id': self._play_id,
                'end_time': current_timestamp(),
                'status': PlayStatus.SUCCESS
            }

            JobSaver.update_play(update_info)
            JobSaver.update_play_status(update_info)

        self._display.display("", screen_only=True)

        # print custom stats if required
        if stats.custom and self.show_custom_stats:
            self._display.banner("CUSTOM STATS: ")
            # per host
            # TODO: come up with 'pretty format'
            for k in sorted(stats.custom.keys()):
                if k == '_run':
                    continue
                self._display.display('\t%s: %s' % (k, self._dump_results(stats.custom[k], indent=1).replace('\n', '')))

            # print per run custom stats
            if '_run' in stats.custom:
                self._display.display("", screen_only=True)
                self._display.display('\tRUN: %s' % self._dump_results(stats.custom['_run'], indent=1).replace('\n', ''))
            self._display.display("", screen_only=True)

        if context.CLIARGS['check'] and self.check_mode_markers:
            self._display.banner("DRY RUN")

    def v2_playbook_on_start(self, playbook):
        schedule_logger(self._job_id).info(f"in playbook on start, playbook: {playbook._file_name}")

        if self._display.verbosity > 1:
            from os.path import basename
            self._display.banner("PLAYBOOK: %s" % basename(playbook._file_name))

        # show CLI arguments
        if self._display.verbosity > 3:
            if context.CLIARGS.get('args'):
                self._display.display('Positional arguments: %s' % ' '.join(context.CLIARGS['args']),
                                      color=C.COLOR_VERBOSE, screen_only=True)

            for argument in (a for a in context.CLIARGS if a != 'args'):
                val = context.CLIARGS[argument]
                if val:
                    self._display.display('%s: %s' % (argument, val), color=C.COLOR_VERBOSE, screen_only=True)

        if context.CLIARGS['check'] and self.check_mode_markers:
            self._display.banner("DRY RUN")

    def v2_runner_retry(self, result):
        task_name = result.task_name or result._task
        msg = "FAILED - RETRYING: %s (%d retries left)." % (task_name, result._result['retries'] - result._result['attempts'])
        if self._run_is_verbose(result, verbosity=2):
            msg += "Result was: %s" % self._dump_results(result._result)
        self._display.display(msg, color=C.COLOR_DEBUG)

    def v2_playbook_on_notify(self, handler, host):
        if self._display.verbosity > 1:
            self._display.display("NOTIFIED HANDLER %s for %s" % (handler.get_name(), host), color=C.COLOR_VERBOSE, screen_only=True)



