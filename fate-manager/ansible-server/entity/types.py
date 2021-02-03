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


class BaseStatus(object):
    @classmethod
    def status_list(cls):
        return [cls.__dict__[k] for k in cls.__dict__.keys() if not callable(getattr(cls, k)) and not k.startswith("__")]

    @classmethod
    def contains(cls, status):
        return status in cls.status_list()


class BaseStateTransitionRule(object):
    RULES = {}

    @classmethod
    def if_pass(cls, src_status, dest_status):
        if src_status not in cls.RULES:
            return False
        if dest_status not in cls.RULES[src_status]:
            return False
        else:
            return True


class StatusSet(BaseStatus):
    WAITING = 'waiting'
    READY = 'ready'
    RUNNING = "running"
    CANCELED = "canceled"
    TIMEOUT = "timeout"
    FAILED = "failed"
    SUCCESS = "success"
    SKIPPED = "skipped"

    @classmethod
    def get_level(cls, status):
        return dict(zip(cls.status_list(), range(len(cls.status_list())))).get(status, None)


class JobStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    READY = StatusSet.READY
    RUNNING = StatusSet.RUNNING
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED
    SUCCESS = StatusSet.SUCCESS

    class StateTransitionRule(BaseStateTransitionRule):
        RULES = {
            StatusSet.WAITING: [StatusSet.READY, StatusSet.RUNNING, StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.READY: [StatusSet.WAITING, StatusSet.RUNNING, StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED],
            StatusSet.RUNNING: [StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.CANCELED: [StatusSet.WAITING],
            StatusSet.TIMEOUT: [StatusSet.FAILED, StatusSet.SUCCESS, StatusSet.WAITING],
            StatusSet.FAILED: [StatusSet.WAITING],
            StatusSet.SUCCESS: [StatusSet.WAITING],
        }


class PlayStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    READY = StatusSet.READY
    RUNNING = StatusSet.RUNNING
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED
    SUCCESS = StatusSet.SUCCESS

    class StateTransitionRule(BaseStateTransitionRule):
        RULES = {
            StatusSet.WAITING: [StatusSet.READY, StatusSet.RUNNING, StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.READY: [StatusSet.RUNNING, StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED],
            StatusSet.RUNNING: [StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.CANCELED: [StatusSet.WAITING],
            StatusSet.TIMEOUT: [StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.FAILED: [StatusSet.WAITING],
            StatusSet.SUCCESS: [],
        }


class TaskStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    RUNNING = StatusSet.RUNNING
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED
    SUCCESS = StatusSet.SUCCESS
    SKIPPED = StatusSet.SKIPPED

    class StateTransitionRule(BaseStateTransitionRule):
        RULES = {
            StatusSet.WAITING: [StatusSet.RUNNING, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS, StatusSet.SKIPPED],
            StatusSet.RUNNING: [StatusSet.CANCELED, StatusSet.TIMEOUT, StatusSet.FAILED, StatusSet.SUCCESS, StatusSet.SKIPPED],
            StatusSet.CANCELED: [StatusSet.WAITING],
            StatusSet.TIMEOUT: [StatusSet.FAILED, StatusSet.SUCCESS],
            StatusSet.FAILED: [],
            StatusSet.SUCCESS: [],
            StatusSet.SKIPPED: []
        }


class EndStatus(BaseStatus):
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED
    COMPLETE = StatusSet.SUCCESS
    SKIPPED = StatusSet.SKIPPED


class StandbyStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    READY = StatusSet.READY


class OngoingStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    READY = StatusSet.READY
    RUNNING = StatusSet.RUNNING


class InterruptStatus(BaseStatus):
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED


class NoneKillStatus(BaseStatus):
    WAITING = StatusSet.WAITING
    READY = StatusSet.READY
    CANCELED = StatusSet.CANCELED
    TIMEOUT = StatusSet.TIMEOUT
    FAILED = StatusSet.FAILED
    SUCCESS = StatusSet.SUCCESS
    SKIPPED = StatusSet.SKIPPED