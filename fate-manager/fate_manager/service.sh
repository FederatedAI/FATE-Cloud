#!/bin/bash

workdir=$(cd $(dirname $0); pwd)
. ${workdir}/conf/setup.conf
venv=${pyenv}

PROJECT_BASE=$(cd "$(dirname "$0")";cd ../;pwd)
export PYTHONPATH=$PROJECT_BASE


log_dir=$PROJECT_BASE/logs
module=fate_manager_server.py



getpid() {
    sleep 2
    pid=`ps aux | grep ${module} | grep -v grep | awk '{print $2}'`

    if [[ -n ${pid} ]]; then
        return 1
    else
        return 0
    fi
}

mklogsdir() {
    if [[ ! -d $log_dir ]]; then
        mkdir -p $log_dir
    fi
}

status() {
    getpid
    if [[ -n ${pid} ]]; then
        echo "status:
        `ps aux | grep ${pid} | grep -v grep`"
        exit 0
    else
        echo "service not running"
        exit 0
    fi
}

start() {
    getpid
    if [[ $? -eq 0 ]]; then
        mklogsdir
        source ${venv}/bin/activate
        nohup python ${module}  >>"${log_dir}/console.log" 2>>"${log_dir}/error.log" &
        if [[ $? -eq 0 ]]; then
            getpid
            echo "service start sucessfully. pid: ${pid}"
        else
            echo "service start failed"
        fi
    else
        echo "service already started. pid: ${pid}"
    fi
}

starting() {
    getpid
    if [[ $? -eq 0 ]]; then
        mklogsdir
        source ${venv}/bin/activate
        exec python ${module}  >>"${log_dir}/console.log" 2>>"${log_dir}/error.log"
        if [[ $? -eq 0 ]]; then
            getpid
            echo "service start sucessfully. pid: ${pid}"
        else
            echo "service start failed"
        fi
    else
        echo "service already started. pid: ${pid}"
    fi
}

stop() {
    getpid
    if [[ -n ${pid} ]]; then
        echo "killing:
        `ps aux | grep ${pid} | grep -v grep`"
        kill -9 ${pid}
        if [[ $? -eq 0 ]]; then
            echo "killed"
        else
            echo "kill error"
        fi
    else
        echo "service not running"
    fi
}

case "$1" in
    start)
        start
        status
        ;;
    
    starting)
        starting
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
        echo "usage: $0 {start|stop|status|restart}"
        exit -1
esac
