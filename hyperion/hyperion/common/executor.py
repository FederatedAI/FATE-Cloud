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
import os

from ansible import context
from ansible.executor.task_queue_manager import TaskQueueManager
from ansible.utils.ssh_functions import check_for_controlpersist
from ansible import constants as C
from ansible.executor.playbook_executor import PlaybookExecutor
from common.callback import CallbackModule
from ansible.plugins.loader import become_loader, connection_loader, shell_loader
from ansible.playbook import Playbook
from ansible.module_utils._text import to_text
from ansible.utils.display import Display
from ansible.template import Templar
from ansible.module_utils.parsing.convert_bool import boolean
from settings import stat_logger

display = Display()


class PlayBookExecutorEdit(PlaybookExecutor):
    def __init__(self, playbooks, inventory, variable_manager, loader, passwords, play_id, retry=False):
        super(PlayBookExecutorEdit, self).__init__(playbooks, inventory, variable_manager, loader, passwords)
        # self.play_id = play_id
        self.callback = CallbackModule()
        self.callback.set_play_id(play_id)
        self.callback.set_retry(retry)
        if context.CLIARGS.get('listhosts') or context.CLIARGS.get('listtasks') or \
                context.CLIARGS.get('listtags') or context.CLIARGS.get('syntax'):
            self._tqm = None
        else:
            self._tqm = TaskQueueManager(
                inventory=self._inventory,
                variable_manager=self._variable_manager,
                loader=self._loader,
                passwords=self.passwords,
                forks=context.CLIARGS.get('forks'),
                stdout_callback=self.callback
            )
        check_for_controlpersist(C.ANSIBLE_SSH_EXECUTABLE)

    def run(self):
        '''
        Run the given playbook, based on the settings in the play which
        may limit the runs to serialized groups, etc.
        '''

        result = 0
        entrylist = []
        entry = {}
        try:
            # preload become/connection/shell to set config defs cached
            list(connection_loader.all(class_only=True))
            list(shell_loader.all(class_only=True))
            list(become_loader.all(class_only=True))

            for playbook_path in self._playbooks:
                pb = Playbook.load(playbook_path, variable_manager=self._variable_manager, loader=self._loader)
                # FIXME: move out of inventory self._inventory.set_playbook_basedir(os.path.realpath(os.path.dirname(playbook_path)))

                if self._tqm is None:  # we are doing a listing
                    entry = {'playbook': playbook_path}
                    entry['plays'] = []
                else:
                    # make sure the tqm has callbacks loaded
                    self._tqm.load_callbacks()
                    self._tqm.send_callback('v2_playbook_on_start', pb)

                i = 1
                plays = pb.get_plays()
                stat_logger.info(f">>>>>>>> in executor edited, all plays: {plays}")
                display.vv(u'%d plays in %s' % (len(plays), to_text(playbook_path)))

                for play in plays:
                    # stat_logger.info(f">>>>>>>> in executor for loop, play uuid: {play._uuid}, play name: {play.name}")
                    # stat_logger.info(f"tasks in play: {play._uuid}")
                    # stat_logger.info(f"type of play.get_tasks(): {type(play.get_tasks())}")
                    # stat_logger.info(f"content of get tasks: {play.get_tasks()}")
                    # for task in play.get_tasks():
                    #     stat_logger.info(f"task name: {task._attributes.get('name')}, task uuid: {task._uuid}")
                    if play._included_path is not None:
                        self._loader.set_basedir(play._included_path)
                    else:
                        self._loader.set_basedir(pb._basedir)

                    # clear any filters which may have been applied to the inventory
                    self._inventory.remove_restriction()

                    # Allow variables to be used in vars_prompt fields.
                    all_vars = self._variable_manager.get_vars(play=play)
                    templar = Templar(loader=self._loader, variables=all_vars)
                    setattr(play, 'vars_prompt', templar.template(play.vars_prompt))

                    # FIXME: this should be a play 'sub object' like loop_control
                    if play.vars_prompt:
                        for var in play.vars_prompt:
                            vname = var['name']
                            prompt = var.get("prompt", vname)
                            default = var.get("default", None)
                            private = boolean(var.get("private", True))
                            confirm = boolean(var.get("confirm", False))
                            encrypt = var.get("encrypt", None)
                            salt_size = var.get("salt_size", None)
                            salt = var.get("salt", None)
                            unsafe = var.get("unsafe", None)

                            if vname not in self._variable_manager.extra_vars:
                                if self._tqm:
                                    self._tqm.send_callback('v2_playbook_on_vars_prompt', vname, private, prompt, encrypt, confirm, salt_size, salt,
                                                            default, unsafe)
                                    play.vars[vname] = display.do_var_prompt(vname, private, prompt, encrypt, confirm, salt_size, salt, default, unsafe)
                                else:  # we are either in --list-<option> or syntax check
                                    play.vars[vname] = default

                    # Post validate so any play level variables are templated
                    all_vars = self._variable_manager.get_vars(play=play)
                    templar = Templar(loader=self._loader, variables=all_vars)
                    play.post_validate(templar)

                    stat_logger.info(f">>>>>>>> in executor for loop, play uuid: {play._uuid}, play name: {play.name}")
                    stat_logger.info(f"tasks in play: {play._uuid}")
                    stat_logger.info(f"type of play.get_tasks(): {type(play.get_tasks())}")
                    stat_logger.info(f"content of get tasks: {play.get_tasks()}")
                    for task in play.get_tasks():
                        stat_logger.info(f"task name: {task._attributes.get('name')}, task uuid: {task._uuid}")

                    if context.CLIARGS['syntax']:
                        continue

                    if self._tqm is None:
                        # we are just doing a listing
                        entry['plays'].append(play)

                    else:
                        self._tqm._unreachable_hosts.update(self._unreachable_hosts)

                        previously_failed = len(self._tqm._failed_hosts)
                        previously_unreachable = len(self._tqm._unreachable_hosts)

                        break_play = False
                        # we are actually running plays
                        batches = self._get_serialized_batches(play)
                        if len(batches) == 0:
                            self._tqm.send_callback('v2_playbook_on_play_start', play)
                            self._tqm.send_callback('v2_playbook_on_no_hosts_matched')
                        for batch in batches:
                            # restrict the inventory to the hosts in the serialized batch
                            self._inventory.restrict_to_hosts(batch)
                            # and run it...
                            result = self._tqm.run(play=play)

                            # break the play if the result equals the special return code
                            if result & self._tqm.RUN_FAILED_BREAK_PLAY != 0:
                                result = self._tqm.RUN_FAILED_HOSTS
                                break_play = True

                            # check the number of failures here, to see if they're above the maximum
                            # failure percentage allowed, or if any errors are fatal. If either of those
                            # conditions are met, we break out, otherwise we only break out if the entire
                            # batch failed
                            failed_hosts_count = len(self._tqm._failed_hosts) + len(self._tqm._unreachable_hosts) - \
                                (previously_failed + previously_unreachable)

                            if len(batch) == failed_hosts_count:
                                break_play = True
                                break

                            # update the previous counts so they don't accumulate incorrectly
                            # over multiple serial batches
                            previously_failed += len(self._tqm._failed_hosts) - previously_failed
                            previously_unreachable += len(self._tqm._unreachable_hosts) - previously_unreachable

                            # save the unreachable hosts from this batch
                            self._unreachable_hosts.update(self._tqm._unreachable_hosts)

                        if break_play:
                            break

                    i = i + 1  # per play

                if entry:
                    entrylist.append(entry)  # per playbook

                # send the stats callback for this playbook
                if self._tqm is not None:
                    if C.RETRY_FILES_ENABLED:
                        retries = set(self._tqm._failed_hosts.keys())
                        retries.update(self._tqm._unreachable_hosts.keys())
                        retries = sorted(retries)
                        if len(retries) > 0:
                            if C.RETRY_FILES_SAVE_PATH:
                                basedir = C.RETRY_FILES_SAVE_PATH
                            elif playbook_path:
                                basedir = os.path.dirname(os.path.abspath(playbook_path))
                            else:
                                basedir = '~/'

                            (retry_name, _) = os.path.splitext(os.path.basename(playbook_path))
                            filename = os.path.join(basedir, "%s.retry" % retry_name)
                            if self._generate_retry_inventory(filename, retries):
                                display.display("\tto retry, use: --limit @%s\n" % filename)

                    self._tqm.send_callback('v2_playbook_on_stats', self._tqm._stats)

                # if the last result wasn't zero, break out of the playbook file name loop
                if result != 0:
                    break

            if entrylist:
                return entrylist

        finally:
            if self._tqm is not None:
                self._tqm.cleanup()
            if self._loader:
                self._loader.cleanup_all_tmp_files()

        if context.CLIARGS['syntax']:
            display.display("No issues encountered")
            return result

        if context.CLIARGS['start_at_task'] and not self._tqm._start_at_done:
            display.error(
                "No matching task \"%s\" found."
                " Note: --start-at-task can only follow static includes."
                % context.CLIARGS['start_at_task']
            )

        return result