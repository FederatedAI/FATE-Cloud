ls /usr/bin/supervisord >/dev/null 2>&1
if [ $? -ne 0 ]
then
  echo "SUPERVISOR CHECK: warning: supervisor not install, please install by rpm local or yum remote"
else
  echo "SUPERVISOR CHECK: success"
fi