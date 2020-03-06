#!/bin/bash
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
cd ${basepath}
packagespath=$(cd ${basepath}/../../../packages;pwd)
cloudpath=$(cd ${basepath}/../../../../;pwd)
#echo ${packagespath}
#echo ${cloudpath}
pwd

source ./configurations.sh

usage(){
  echo "usage: sh $0 {config|packaging|init|install}"
}

config(){
  cd ${basepath}

  cp ./default-service.sh ./service.sh
  sed -i.bak "s#JAVA_HOME=.*#JAVA_HOME=${java_dir}#g" ./service.sh
  sed -i.bak "s#logspath=.*#logspath=${logspath}#g" ./service.sh
  rm ./service.sh.bak

  cp ${cloudpath}/${module}/src/main/resources/application.properties ./
  sed -i.bak "s#^server.port=.*#server.port=${fateboard_port}#g" ./application.properties
  sed -i.bak "s#^fateflow.url=.*#fateflow.url=http://${fate_flow_ip}:${fate_flow_port}#g" ./application.properties
  sed -i.bak "s#^spring.datasource.driver-Class-Name=.*#spring.datasource.driver-Class-Name=com.mysql.cj.jdbc.Driver#g" ./application.properties
  sed -i.bak "s#^fateboard.datasource.jdbc-url=.*#fateboard.datasource.jdbc-url=jdbc:mysql://${db_ip}:3306/${db_name}?characterEncoding=utf8\&characterSetResults=utf8\&autoReconnect=true\&failOverReadOnly=false\&serverTimezone=GMT%2B8#g" ./application.properties
  sed -i.bak "s/^fateboard.datasource.username=.*/fateboard.datasource.username=${db_user}/g" ./application.properties
  sed -i.bak "s/^fateboard.datasource.password=.*/fateboard.datasource.password=${db_password}/g" ./application.properties

  echo "spring.fatemanager.datasource.url=jdbc:mysql://${db_ip}:3306/${db_name}?characterEncoding=utf8&characterSetResults=utf8&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8" >> ./application.properties
  echo "spring.fatemanager.datasource.username=${db_user}"  >> ./application.properties
  echo "spring.fatemanager.datasource.password=${db_password}" >> ./application.properties
  echo "swagger.enable=true" >> ./application.properties
  rm ./application.properties.bak
}
packaging(){
  echo "start packaging"
  cd ${basepath}

  if [  ! -e ${packagespath}/${fateboard_ip} ]; then mkdir ${packagespath}/${fateboard_ip};fi
  cd ${packagespath}/${fateboard_ip}
  pwd
  if [ -e "${module}-${version}.tar.gz" ]
	then
		rm ${module}-${version}.tar.gz
	fi

  mkdir -p ${module}/conf
  mkdir -p ${module}/ssh

  mv ${basepath}/application.properties ./${module}/conf/
  mv ${basepath}/service.sh ./${module}/
  cp ${cloudpath}/${module}/target/${module}-${version}.jar ./${module}/
  cd ${module}
  ln -s ${module}-${version}.jar ${module}.jar
  cd ./ssh
  touch ssh.properties
  cd ../../
  tar -czf ${module}-${version}.tar.gz ./${module}
  rm -rf ./${module}
}
init(){
  echo "start initing"
  cd ${basepath}

  ssh -tt ${system_user}@${db_ip} << eeooff
if [  ! -e ${install_dir} ]; then mkdir ${install_dir};fi
if [  -e ${install_dir}/$fate-manager.sql ]; then rm ${install_dir}/fate-manager.sql;fi
exit
eeooff
  scp ../mysql/fate-manager.sql ${system_user}@${db_ip}:${install_dir}
  ssh -tt ${system_user}@${db_ip} << eeooff
${db_dir}/bin/mysql -u${db_user} -p${db_password} -S ${db_dir}/mysql.sock --connect-expired-password << EOF
use ${db_name};
source ${install_dir}/fate-manager.sql;
DELETE FROM mysql.user where user="${db_user}" and host="${fateboard_ip}";
flush privileges;
create user "${db_user}"@"${fateboard_ip}" identified by "${db_password}";
grant all on  *.* to ${db_user}@${fateboard_ip};
flush privileges;
EOF
rm -rf ${install_dir}/fate-manager.sql
exit
eeooff
}

install(){
  echo "start installing"
  cd ${basepath}

  ssh -tt ${system_user}@${fateboard_ip} << eeooff
if [  ! -e ${install_dir} ]; then mkdir ${install_dir};fi
if [  -e ${install_dir}/${module} ]; then rm -rf ${install_dir}/${module};fi
exit
eeooff
  scp ${packagespath}/${fateboard_ip}/${module}-${version}.tar.gz ${system_user}@${fateboard_ip}:${install_dir}
  ssh -tt ${system_user}@${fateboard_ip} << eeooff
cd ${install_dir}
tar -xvf ${module}-${version}.tar.gz
rm ${module}-${version}.tar.gz
exit
eeooff
  rm ${basepath}/configurations.sh
}

all(){
  config
  packaging
  init
  install
}

case "$1" in
  config)
      config
      ;;
  packaging)
      packaging
      ;;
  init)
      init
      ;;
  install)
      install
      ;;
  all)
      all
      ;;
  *)
      usage
esac