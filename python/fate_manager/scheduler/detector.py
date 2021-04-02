import functools
import traceback

from controller import apply
from operation import federated_db_operator
from service import task_service
from service.site_service import get_home_site_list
from settings import stat_logger as logger
from utils import cron


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
        self.ip_manager_task()
        self.heart_task()
        self.apply_result_task(admin_info)
        self.allow_apply_task(admin_info)

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

