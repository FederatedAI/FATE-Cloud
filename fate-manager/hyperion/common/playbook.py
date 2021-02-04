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

import os
import stat
from ansible import context
from ansible.cli import CLI
from ansible.cli.playbook import PlaybookCLI
from ansible.errors import AnsibleError
from common.executor import PlayBookExecutorEdit
from ansible.module_utils._text import to_bytes
from ansible.playbook.block import Block
from ansible.utils.display import Display
from ansible.utils.collection_loader import AnsibleCollectionLoader, get_collection_name_from_path, set_collection_playbook_paths
from ansible.plugins.loader import add_all_plugin_dirs
from settings import stat_logger

display = Display()


class PlayBook(PlaybookCLI):
    def run_play(self, play_id, retry=False):
        super(PlayBook, self).run()

        # Note: slightly wrong, this is written so that implicit localhost
        # manages passwords
        sshpass = None
        becomepass = None
        passwords = {}

        # initial error check, to make sure all specified playbooks are accessible
        # before we start running anything through the playbook executor

        b_playbook_dirs = []
        # import json
        stat_logger.info(f'context CLIARGS: {context.CLIARGS}')
        # try:
        #     stat_logger.info(f'context cliargs: {json.dumps(context.CLIARGS, indent=4)}')
        # except Exception:
        #     pass
        for playbook in context.CLIARGS['args']:
            stat_logger.info(f'in for loop, playbook: {playbook}')
            if not os.path.exists(playbook):
                raise AnsibleError("the playbook: %s could not be found" % playbook)
            if not (os.path.isfile(playbook) or stat.S_ISFIFO(os.stat(playbook).st_mode)):
                raise AnsibleError("the playbook: %s does not appear to be a file" % playbook)

            b_playbook_dir = os.path.dirname(os.path.abspath(to_bytes(playbook, errors='surrogate_or_strict')))
            stat_logger.info(f'b_playbook_dir: {b_playbook_dir}')
            # load plugins from all playbooks in case they add callbacks/inventory/etc
            add_all_plugin_dirs(b_playbook_dir)

            b_playbook_dirs.append(b_playbook_dir)
        stat_logger.info(f'playbook_dirs: {b_playbook_dirs}')

        set_collection_playbook_paths(b_playbook_dirs)

        playbook_collection = get_collection_name_from_path(b_playbook_dirs[0])
        stat_logger.info(f'playbook_collection: {playbook_collection}')

        if playbook_collection:
            display.warning("running playbook inside collection {0}".format(playbook_collection))
            AnsibleCollectionLoader().set_default_collection(playbook_collection)

        # don't deal with privilege escalation or passwords when we don't need to
        if not (context.CLIARGS['listhosts'] or context.CLIARGS['listtasks'] or
                context.CLIARGS['listtags'] or context.CLIARGS['syntax']):
            (sshpass, becomepass) = self.ask_passwords()
            passwords = {'conn_pass': sshpass, 'become_pass': becomepass}

        # create base objects
        loader, inventory, variable_manager = self._play_prereqs()

        # (which is not returned in list_hosts()) is taken into account for
        # warning if inventory is empty.  But it can't be taken into account for
        # checking if limit doesn't match any hosts.  Instead we don't worry about
        # limit if only implicit localhost was in inventory to start with.
        #
        # Fix this when we rewrite inventory by making localhost a real host (and thus show up in list_hosts())
        CLI.get_host_list(inventory, context.CLIARGS['subset'])

        # flush fact cache if requested
        if context.CLIARGS['flush_cache']:
            self._flush_cache(inventory, variable_manager)

        # create the playbook executor, which manages running the plays via a task queue manager
        pbex = PlayBookExecutorEdit(playbooks=context.CLIARGS['args'], inventory=inventory,
                                    variable_manager=variable_manager, loader=loader,
                                    passwords=passwords, play_id=play_id, retry=retry)

        results = pbex.run()

        if isinstance(results, list):
            for p in results:

                display.display('\nplaybook: %s' % p['playbook'])
                for idx, play in enumerate(p['plays']):
                    if play._included_path is not None:
                        loader.set_basedir(play._included_path)
                    else:
                        pb_dir = os.path.realpath(os.path.dirname(p['playbook']))
                        loader.set_basedir(pb_dir)

                    msg = "\n  play #%d (%s): %s" % (idx + 1, ','.join(play.hosts), play.name)
                    mytags = set(play.tags)
                    msg += '\tTAGS: [%s]' % (','.join(mytags))

                    if context.CLIARGS['listhosts']:
                        playhosts = set(inventory.get_hosts(play.hosts))
                        msg += "\n    pattern: %s\n    hosts (%d):" % (play.hosts, len(playhosts))
                        for host in playhosts:
                            msg += "\n      %s" % host

                    display.display(msg)

                    all_tags = set()
                    if context.CLIARGS['listtags'] or context.CLIARGS['listtasks']:
                        taskmsg = ''
                        if context.CLIARGS['listtasks']:
                            taskmsg = '    tasks:\n'

                        def _process_block(b):
                            taskmsg = ''
                            for task in b.block:
                                if isinstance(task, Block):
                                    taskmsg += _process_block(task)
                                else:
                                    if task.action == 'meta':
                                        continue

                                    all_tags.update(task.tags)
                                    if context.CLIARGS['listtasks']:
                                        cur_tags = list(mytags.union(set(task.tags)))
                                        cur_tags.sort()
                                        if task.name:
                                            taskmsg += "      %s" % task.get_name()
                                        else:
                                            taskmsg += "      %s" % task.action
                                        taskmsg += "\tTAGS: [%s]\n" % ', '.join(cur_tags)

                            return taskmsg

                        all_vars = variable_manager.get_vars(play=play)
                        for block in play.compile():
                            block = block.filter_tagged_tasks(all_vars)
                            if not block.has_tasks():
                                continue
                            taskmsg += _process_block(block)

                        if context.CLIARGS['listtags']:
                            cur_tags = list(mytags.union(all_tags))
                            cur_tags.sort()
                            taskmsg += "      TASK TAGS: [%s]\n" % ', '.join(cur_tags)

                        display.display(taskmsg)

            return 0
        else:
            return results

    def run(self, play_id):
        super(PlayBook, self).run()

        # Note: slightly wrong, this is written so that implicit localhost
        # manages passwords
        sshpass = None
        becomepass = None
        passwords = {}

        # initial error check, to make sure all specified playbooks are accessible
        # before we start running anything through the playbook executor

        b_playbook_dirs = []
        # import json
        stat_logger.info(f'context CLIARGS: {context.CLIARGS}')
        # try:
        #     stat_logger.info(f'context cliargs: {json.dumps(context.CLIARGS, indent=4)}')
        # except Exception:
        #     pass
        for playbook in context.CLIARGS['args']:
            stat_logger.info(f'in for loop, playbook: {playbook}')
            if not os.path.exists(playbook):
                raise AnsibleError("the playbook: %s could not be found" % playbook)
            if not (os.path.isfile(playbook) or stat.S_ISFIFO(os.stat(playbook).st_mode)):
                raise AnsibleError("the playbook: %s does not appear to be a file" % playbook)

            b_playbook_dir = os.path.dirname(os.path.abspath(to_bytes(playbook, errors='surrogate_or_strict')))
            stat_logger.info(f'b_playbook_dir: {b_playbook_dir}')
            # load plugins from all playbooks in case they add callbacks/inventory/etc
            add_all_plugin_dirs(b_playbook_dir)

            b_playbook_dirs.append(b_playbook_dir)
        stat_logger.info(f'playbook_dirs: {b_playbook_dirs}')

        set_collection_playbook_paths(b_playbook_dirs)

        playbook_collection = get_collection_name_from_path(b_playbook_dirs[0])
        stat_logger.info(f'playbook_collection: {playbook_collection}')

        if playbook_collection:
            display.warning("running playbook inside collection {0}".format(playbook_collection))
            AnsibleCollectionLoader().set_default_collection(playbook_collection)

        # don't deal with privilege escalation or passwords when we don't need to
        if not (context.CLIARGS['listhosts'] or context.CLIARGS['listtasks'] or
                context.CLIARGS['listtags'] or context.CLIARGS['syntax']):
            (sshpass, becomepass) = self.ask_passwords()
            passwords = {'conn_pass': sshpass, 'become_pass': becomepass}

        # create base objects
        loader, inventory, variable_manager = self._play_prereqs()

        # (which is not returned in list_hosts()) is taken into account for
        # warning if inventory is empty.  But it can't be taken into account for
        # checking if limit doesn't match any hosts.  Instead we don't worry about
        # limit if only implicit localhost was in inventory to start with.
        #
        # Fix this when we rewrite inventory by making localhost a real host (and thus show up in list_hosts())
        CLI.get_host_list(inventory, context.CLIARGS['subset'])

        # flush fact cache if requested
        if context.CLIARGS['flush_cache']:
            self._flush_cache(inventory, variable_manager)

        # create the playbook executor, which manages running the plays via a task queue manager
        pbex = PlayBookExecutorEdit(playbooks=context.CLIARGS['args'], inventory=inventory,
                                    variable_manager=variable_manager, loader=loader,
                                    passwords=passwords, play_id=play_id)

        results = pbex.run()

        if isinstance(results, list):
            for p in results:

                display.display('\nplaybook: %s' % p['playbook'])
                for idx, play in enumerate(p['plays']):
                    if play._included_path is not None:
                        loader.set_basedir(play._included_path)
                    else:
                        pb_dir = os.path.realpath(os.path.dirname(p['playbook']))
                        loader.set_basedir(pb_dir)

                    msg = "\n  play #%d (%s): %s" % (idx + 1, ','.join(play.hosts), play.name)
                    mytags = set(play.tags)
                    msg += '\tTAGS: [%s]' % (','.join(mytags))

                    if context.CLIARGS['listhosts']:
                        playhosts = set(inventory.get_hosts(play.hosts))
                        msg += "\n    pattern: %s\n    hosts (%d):" % (play.hosts, len(playhosts))
                        for host in playhosts:
                            msg += "\n      %s" % host

                    display.display(msg)

                    all_tags = set()
                    if context.CLIARGS['listtags'] or context.CLIARGS['listtasks']:
                        taskmsg = ''
                        if context.CLIARGS['listtasks']:
                            taskmsg = '    tasks:\n'

                        def _process_block(b):
                            taskmsg = ''
                            for task in b.block:
                                if isinstance(task, Block):
                                    taskmsg += _process_block(task)
                                else:
                                    if task.action == 'meta':
                                        continue

                                    all_tags.update(task.tags)
                                    if context.CLIARGS['listtasks']:
                                        cur_tags = list(mytags.union(set(task.tags)))
                                        cur_tags.sort()
                                        if task.name:
                                            taskmsg += "      %s" % task.get_name()
                                        else:
                                            taskmsg += "      %s" % task.action
                                        taskmsg += "\tTAGS: [%s]\n" % ', '.join(cur_tags)

                            return taskmsg

                        all_vars = variable_manager.get_vars(play=play)
                        for block in play.compile():
                            block = block.filter_tagged_tasks(all_vars)
                            if not block.has_tasks():
                                continue
                            taskmsg += _process_block(block)

                        if context.CLIARGS['listtags']:
                            cur_tags = list(mytags.union(all_tags))
                            cur_tags.sort()
                            taskmsg += "      TASK TAGS: [%s]\n" % ', '.join(cur_tags)

                        display.display(taskmsg)

            return 0
        else:
            return results


if __name__ == '__main__':
    args = ['/Users/izhfeng/pyvenv/ansible-deploy-3.7.3/bin/ansible-playbook', '-i',
            '/Users/izhfeng/Desktop/ansible/ansible-nfate-1.4.0/environments/test/hosts',
            '/Users/izhfeng/Desktop/ansible/ansible-nfate-1.4.0/project_test.yml', '-C']
    import traceback
    import json
    # args = ['/Users/izhfeng/pyvenv/ansible-deploy-3.7.3/bin/ansible-playbook', '-i',
    #         '/Users/izhfeng/Desktop/ansible/ansible-nfate-1.4.0/environments/test/hosts',
    #         '/Users/izhfeng/Desktop/ansible/ansible-nfate-1.4.0/project_test.yml']
    # for offset, arg in enumerate([args, args, args, args]):
    #     # try:
    #     #     a = PlayBook(args=args)
    #     #     a.run()
    #     # except Exception:
    #     #     print('******* in main exception catcher')
    #     #     print(traceback.format_exc())
    #     print(f'************* run {offset+1} play')
    #     a = PlayBook(args=args)
    #     result = a.run()
    #     print('*'*25, 'below is result:')
    #     if isinstance(result, dict):
    #         print(json.dumps(result, indent=4))
    #     else:
    #         print(result)
    a = PlayBook(args=args)
    a.run_play(play_id="202011140021_1")