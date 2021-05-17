import threading
import time
import random


class Cron(threading.Thread):
    def __init__(self, interval, run_second=None, rand_size=None, title='', logger=None, lock=None):
        """

        :param interval: interval by millisecond
        :param run_second:
        :param rand_size:
        :param title:
        :param logger:
        :param lock:
        """
        super(Cron, self).__init__()
        self.interval = interval
        self.run_second = run_second
        self.rand_size = rand_size
        self.finished = threading.Event()
        self.title = title
        self.logger = logger
        self.lock = lock

    def cancel(self):
        self.finished.set()

    def run(self):
        def do():
            try:
                if not self.lock or self.lock.acquire(0):
                    self.run_do()
            except Exception as e:
                if self.logger:
                    self.logger.exception(e)
                else:
                    raise e
            finally:
                if self.lock and self.lock.locked():
                    self.lock.release()
        try:
            if self.logger and self.title:
                self.logger.info('%s cron start.' % self.title)

            if self.run_second is None:
                first_interval = self.interval
            else:
                now = int(round(time.time()*1000))
                delta = -now % 60000 + self.run_second*1000
                if delta > 0:
                    first_interval = delta
                else:
                    first_interval = 60*1000 + delta
            self.finished.wait(first_interval/1000)
            if not self.finished.is_set():
                do()

            while True:
                self.finished.wait((self.interval if self.rand_size is None else self.interval - random.randint(0, self.rand_size))/1000)
                if not self.finished.is_set():
                    do()
        except Exception as e:
            if self.logger:
                self.logger.exception(e)
            else:
                raise e

    def run_do(self):
        pass

