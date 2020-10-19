#!/bin/sh
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
basepath=$(cd `dirname $0`; pwd)
cd ${basepath}

module=cloud-manager
export JAVA_HOME=
export PATH=${JAVA_HOME}/bin:$PATH

mklogsdir(){
  if [[  ! -d "logs"  ]]; then
    mkdir logs
  fi
}

getpid(){
  pid=$(ps -ef|grep java|grep ${basepath}/${module}.jar|grep -v grep|awk '{print $2}')
  if [[  -n ${pid}  ]]; then
    return 1
  else
    return 0
  fi
}

status(){
  getpid
  if [[  -n ${pid}  ]]; then
    echo "status:
    `ps aux|grep ${pid} |grep -v grep`"
    return 1
  else
    echo "service not running"
    return 0
  fi
}

start(){
  getpid
  if [[  $? -eq 0  ]]; then
    mklogsdir
    nohup java -Dspring.config.location=$basepath/conf/application.properties  -Xmx2048m -Xms2048m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log -XX:+HeapDumpOnOutOfMemoryError  -jar ${basepath}/${module}.jar  >/dev/null 2>&1 &
    if [[  $? -eq 0  ]]; then
      sleep 5
      getpid
      echo "service start successfully. pid: ${pid}"
    else
      echo "service start failed"
    fi
  fi
}

stop(){
  getpid
  if [[  -n ${pid}  ]]; then
    echo "killing:
    `ps aux | grep ${pid} |grep -v grep`"
    kill -9 ${pid}
    if [[  $? -eq 0  ]]; then
      echo "killed"
    else
      echo "kill error"
    fi
  else
    echo "service not running"
  fi
}

usage(){
  echo "usage: sh $0 {start|stop|status|restart}"
}

case "$1" in
  start)
    start
    status
    ;;
  stop)
    stop
    ;;
  status)
    status
    ;;
  restart)
    stop
    start
    status
    ;;
  *)
    usage
    exit -1
esac