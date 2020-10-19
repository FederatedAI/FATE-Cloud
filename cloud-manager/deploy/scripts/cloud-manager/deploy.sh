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
cd ${basepath}
pwd

if [  ! -e ${basepath}/../packages ]; then mkdir -p ${basepath}/../packages;fi
packagespath=$(cd ${basepath}/../packages;pwd)
cloudpath=$(cd ${basepath}/../../../../;pwd)
echo ${packagespath}
echo ${cloudpath}

source ${basepath}/configurations.sh

usage(){
  echo "usage: sh $0 {config|packaging|init|install}"
}

config(){
  cp ${basepath}/default_service.sh ${basepath}/service.sh
  sed -i.bak "s#JAVA_HOME=.*#JAVA_HOME=${java_dir}#g" ${basepath}/service.sh
  rm ${basepath}/service.sh.bak

  cp ${cloudpath}/${module}/src/main/resources/application.properties ${basepath}
  sed -i.bak "s#^server.port=.*#server.port=${port}#g" ${basepath}/application.properties
  sed -i.bak "s#^spring.datasource.driver-class-name=.*#spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver#g" ${basepath}/application.properties
  sed -i.bak "s#^spring.datasource.url=.*#spring.datasource.url=jdbc:mysql://${db_ip}:3306/${db_name}?characterEncoding=utf8\&characterSetResults=utf8\&autoReconnect=true\&failOverReadOnly=false\&serverTimezone=GMT%2B8#g" ${basepath}/application.properties
  sed -i.bak "s#^spring.datasource.username=.*#spring.datasource.username=${db_user}#g" ${basepath}/application.properties
  sed -i.bak "s#^spring.datasource.password=.*#spring.datasource.password=${db_password}#g" ${basepath}/application.properties
  sed -i.bak "s#^cloud-manager.ip.*#cloud-manager.ip=${ip}:${port}#g" ${basepath}/application.properties
  rm ${basepath}/application.properties.bak
}

packaging(){
  echo "start packaging"

  if [  ! -e ${packagespath}/${ip} ]; then mkdir -p ${packagespath}/${ip};fi
  cd ${packagespath}/${ip}

  if [ -e "${packagespath}/${ip}/${module}-${version}.tar.gz" ]
	then
		rm ${packagespath}/${ip}/${module}-${version}.tar.gz
	fi

  cd ${cloudpath}/${module}

	echo "start execute mvn build"
  mvn clean package -DskipTests
  echo "mvn  build done"


	if [ ! -f "${cloudpath}/${module}/target/${module}-${version}.jar" ]
	then
		echo "${cloudpath}/${module}/target/${module}-${version}.jar: file doesn't exist."
		exit -1
	fi

  cd ${packagespath}/${ip}
  mkdir -p ${packagespath}/${ip}/${module}/conf

  mv ${basepath}/application.properties ${packagespath}/${ip}/${module}/conf/
  mv ${basepath}/service.sh ${packagespath}/${ip}/${module}/
  cp ${cloudpath}/${module}/target/${module}-${version}.jar ${packagespath}/${ip}/${module}/
  cd ${packagespath}/${ip}/${module}
  ln -s ${module}-${version}.jar ${module}.jar

  cd ${packagespath}/${ip}/
  tar -czf ${packagespath}/${ip}/${module}-${version}.tar.gz ./${module}
  rm -rf  ${packagespath}/${ip}/${module}
}

init(){
  echo "start initing"

  ssh -tt ${system_user}@${db_ip} << eeooff
if [  ! -e ${install_dir} ]; then mkdir -p ${install_dir};fi
if [  -e ${install_dir}/${module}.sql ]; then rm ${install_dir}/${module}.sql;fi
exit
eeooff

  scp ${cloudpath}/${module}/deploy/scripts/mysql/${module}.sql ${system_user}@${db_ip}:${install_dir}

  ssh -tt ${system_user}@${db_ip} << eeooff
${db_dir}/bin/mysql -u${db_user} -p${db_password} -S ${db_sock_path} --connect-expired-password << EOF
CREATE DATABASE if not exists ${db_name};
use ${db_name};
source ${install_dir}/${module}.sql;
INSERT INTO t_cloud_manager_origin_user (name,password) VALUES ("admin","21232f297a57a5a743894a0e4a801fc3");
DELETE FROM mysql.user where user="${db_user}" and host="${ip}";
flush privileges;
create user "${db_user}"@"${ip}" identified by "${db_password}";
grant all on  *.* to ${db_user}@${ip};
flush privileges;
EOF
rm -rf ${install_dir}/${module}.sql
exit
eeooff
}

install(){
  echo "start installing"

  ssh -tt ${system_user}@${ip} << eeooff
if [  ! -e ${install_dir} ]; then mkdir ${install_dir};fi
if [  -e ${install_dir}/${module} ]; then rm -rf ${install_dir}/${module};fi
exit
eeooff
  scp ${packagespath}/${ip}/${module}-${version}.tar.gz ${system_user}@${ip}:${install_dir}
  ssh -tt ${system_user}@${ip} << eeooff
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