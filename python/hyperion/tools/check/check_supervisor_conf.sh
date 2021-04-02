if [ ! -d "/data/projects/common/supervisord/supervisord.d/" ];then
  echo "SUPERVISOR CONF CHECK: failed: supervisor.d directory not exists."
else
  num=$( ls /data/projects/common/supervisord/supervisord.d/fate-*.conf | wc -l )
  if [ $num -gt 0 ]
  then
    echo "SUPERVISOR CONF CHECK: warning: supervisor_fate_conf exists, please remove /data/projects/common/supervisord/supervisord.d/fate-*.conf and update all"
  else
    echo "SUPERVISOR CONF CHECK: success"
  fi
fi