
PROJECT_BASE=$(cd "$(dirname "$0")";pwd)
echo "PROJECT_BASE: "${PROJECT_BASE}

# source init_env.sh
INI_ENV_SCRIPT=${PROJECT_BASE}/bin/init_env.sh
if test -f "${INI_ENV_SCRIPT}"; then
  source ${PROJECT_BASE}/bin/init_env.sh
  echo "PYTHONPATH: "${PROJECT_BASE}
else
  echo "file not found: ${INI_ENV_SCRIPT}"
  exit
fi

log_dir=${PROJECT_BASE}/logs

module=ansible_server.py

parse_yaml() {
   local prefix=$2
   local s='[[:space:]]*' w='[a-zA-Z0-9_]*' fs=$(echo @|tr @ '\034')
   sed -ne "s|^\($s\)\($w\)$s:$s\"\(.*\)\"$s\$|\1$fs\2$fs\3|p" \
        -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p"  $1 |
   awk -F$fs '{
      indent = length($1)/2;
      vname[indent] = $2;
      for (i in vname) {if (i > indent) {delete vname[i]}}
      if (length($3) > 0) {
         vn=""; for (i=0; i<indent; i++) {vn=(vn)(vname[i])("_")}
         printf("%s%s%s=\"%s\"\n", "'$prefix'",vn, $2, $3);
      }
   }'
}

getport() {
    service_conf_path=${PROJECT_BASE}/conf/service_conf.yaml
    if test -f "${service_conf_path}"; then
      echo "found service conf: ${service_conf_path}"
      eval $(parse_yaml ${service_conf_path} "service_config_")
      echo "ansible http port: ${service_config_ansible_http_port}"
      echo
    else
      echo "service conf not found: ${service_conf_path}"
      exit
    fi
}

getport

getpid() {
    pid1=`lsof -i:${service_config_ansible_http_port} | grep 'LISTEN' | awk '{print $2}'`
    if [[ -n ${pid1} ]];then
        pid=$pid1
    else
        pid=
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
        echo "status:`ps aux | grep ${pid} | grep -v grep`"
        lsof -i:${service_config_ansible_http_port} | grep 'LISTEN'
    else
        echo "service not running"
    fi
}

start() {
    getpid
    if [[ ${pid} == "" ]]; then
        mklogsdir
        nohup python ${PROJECT_BASE}/ansible_server.py >> "${log_dir}/console.log" 2>>"${log_dir}/error.log" &
        for((i=1;i<=100;i++));
        do
            sleep 0.1
            getpid
            if [[ -n ${pid} ]]; then
                echo "service start sucessfully. pid: ${pid}"
                return
            fi
        done
        if [[ -n ${pid} ]]; then
           echo "service start sucessfully. pid: ${pid}"
        else
           echo "service start failed, please check ${log_dir}/error.log and ${log_dir}/console.log"
        fi
    else
        echo "service already started. pid: ${pid}"
    fi
}

stop() {
    getpid
    if [[ -n ${pid} ]]; then
        echo "killing: `ps aux | grep ${pid} | grep -v grep`"
        for((i=1;i<=100;i++));
        do
            sleep 0.1
            kill ${pid}
            getpid
            if [[ ! -n ${pid} ]]; then
                echo "killed by SIGTERM"
                return
            fi
        done
        kill -9 ${pid}
        if [[ $? -eq 0 ]]; then
            echo "killed by SIGKILL"
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