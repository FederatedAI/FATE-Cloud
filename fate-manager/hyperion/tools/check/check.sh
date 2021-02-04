#!/bin/bash

cwd=$(cd `dirname $0`; pwd)

cd $cwd

source ./check_open_files.sh

source ./check_max_user_processes.sh

source ./check_swap.sh

source ./check_fate_process.sh

source ./check_port.sh

source ./check_mysql.sh

source ./check_fate_root.sh

cd $cwd

source ./check_mysql_root.sh

source ./check_supervisor.sh

source ./check_supervisor_conf.sh

