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

set -e
basepath=$(cd `dirname $0`; pwd)
cloudpath=$(cd ${basepath}/../../../;pwd)
cd ${basepath}

source ${basepath}/configurations.sh
modules=(cloud-manager)
cloud_version=$(grep -E -m 1 -o "<version>(.*)</version>" ${cloudpath}/cloud-manager/pom.xml|  awk -F '[<>]' '{print $3}')

usage(){
  echo "usage: sh $0 {all|module_name} {install|package|distribute}"
}

config(){
    cd ${basepath}/$1
    cp ${basepath}/$1/default_configurations.sh ${basepath}/$1/configurations.sh

    sed -i.bak "s#^ip=.*#ip=${cloud_ip}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^system_user=.*#system_user=${system_user}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^install_dir=.*#install_dir=${install_dir}#g" ${basepath}/$1/configurations.sh

    sed -i.bak "s#^module=.*#module=$1#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^version=.*#version=${cloud_version}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^port=.*#port=${cloud_port}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^java_dir=.*#java_dir=${java_dir}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_ip=.*#db_ip=${cloud_db_ip}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_name=.*#db_name=${cloud_db_name}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_user=.*#db_user=${cloud_db_user}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_password=.*#db_password=${cloud_db_password}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_dir=.*#db_dir=${cloud_db_dir}#g" ${basepath}/$1/configurations.sh
    sed -i.bak "s#^db_sock_path=.*#db_sock_path=${cloud_db_sock_path}#g" ${basepath}/$1/configurations.sh

    rm ${basepath}/$1/configurations.sh.bak
}



operation(){
  if [[  "$1" == "install"  ]]; then
    sh deploy.sh all
  else
    if [[  "$1" == "package"  ]]; then
      sh deploy.sh config
      sh deploy.sh packaging
    else
      if [[  "$1" == "distribute"  ]]; then
        sh deploy.sh init
        sh deploy.sh install
      else
        echo "The operation doesn't exist!"
        usage
      fi
    fi
  fi
}


install(){
  echo "[INFO] Installing $1 start-------"

  config $1
  cd ${basepath}/$1
  operation $2

  echo "[INFO] Installing $1 complete-------"

}

all(){
  for((i=0; i<${#modules[*]}; i++))
  do
    install ${modules[i]} $2
  done
}

single(){
  if [[ ${modules[@]/$1} != ${modules[@]} ]];then
    install $1 $2
  else
    echo "The module doesn't exist!"
    usage
  fi
}

case "$1" in
  all)
      all $@
      ;;
  *)
      single $@
esac