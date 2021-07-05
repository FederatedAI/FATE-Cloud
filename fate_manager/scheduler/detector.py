import functools
import traceback

from fate_manager.controller import apply, count
from fate_manager.operation import federated_db_operator
from fate_manager.service import task_service
from fate_manager.service.site_service import get_home_site_list
from fate_manager.settings import stat_logger as logger
from fate_manager.utils import cron


def exception_catch(func):
    @functools.wraps(func)
    def _wrapper(*args, **kwargs):
        try:
            res = func(*args, **kwargs)
        except Exception as e:
            traceback.print_exc()
            logger.exception(e)
            res = None
        return res
    return _wrapper


class TaskDetector(cron.Cron):
    @exception_catch
    def run_do(self):
        admin_info = federated_db_operator.get_admin_info()
        self.site_status_task()
        # self.ip_manager_task()
        self.heart_task()
        self.apply_result_task(admin_info)
        self.allow_apply_task(admin_info)
        self.ip_manager_task()

    @classmethod
    @exception_catch
    def site_status_task(cls):
        get_home_site_list()

    @classmethod
    @exception_catch
    def ip_manager_task(cls):
        task_service.get_change_log_task()

    @classmethod
    @exception_catch
    def heart_task(cls):
        task_service.heart_task()

    @classmethod
    @exception_catch
    def job_task(cls):
        pass

    @classmethod
    @exception_catch
    def test_only_task(cls):
        # task_service.test_only_task()
        pass

    @classmethod
    @exception_catch
    def component_status_task(cls):
        pass

    @classmethod
    @exception_catch
    def apply_result_task(cls, admin_info):
        apply.apply_result_task(admin_info)

    @classmethod
    @exception_catch
    def allow_apply_task(cls, admin_info):
        apply.allow_apply_task(admin_info)

    @classmethod
    @exception_catch
    def auto_test_check(cls):
        pass

    @classmethod
    @exception_catch
    def monitor_task(cls, admin_info):
        pass

    @classmethod
    @exception_catch
    def package_status_task(cls):
        pass

    @classmethod
    @exception_catch
    def auto_test_task(cls):
        pass

    @classmethod
    @exception_catch
    def version_update_task(cls):
        pass


class MonitorDetector(cron.Cron):
    @exception_catch
    def run_do(self):
        account = federated_db_operator.get_admin_info()
        self.log_fate_flow_job(account)

    @classmethod
    @exception_catch
    def log_fate_flow_job(cls, account):
        party_id_flow_url = count.CountJob.count_fate_flow_job(account)
        count.CountJob.detector_no_end_job(account, party_id_flow_url)
        count.CountJob.detector_no_report_job(account)

