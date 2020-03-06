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
cloudpath=$(cd ${basepath}/../../../;pwd)

fateboard_url=https://github.com/FederatedAI/FATE-Board.git
modules=(cloud-manager fate-manager)
source ./default-configurations.sh
cloud_version=$(grep -E -m 1 -o "<version>(.*)</version>" ${cloudpath}/cloud-manager/pom.xml|  awk -F '[<>]' '{print $3}')
fate_manager_version=$(grep -E -m 1 -o "<version>(.*)</version>" ${cloudpath}/fate-manager/pom.xml|  awk -F '[<>]' '{print $3}')
fateboard_version=

usage(){
  echo "usage: sh $0 {all|module_name} {install|package|distribute}"
}

config(){
  if [[  "$1" == "cloud-manager"  ]]; then
    cd ${basepath}/$1
    cp ./default-configurations.sh ./configurations.sh
    sed -i.bak "s#^java_dir=.*#java_dir=${java_dir}#g" ./configurations.sh
    sed -i.bak "s#^module=.*#module=$1#g" ./configurations.sh
    sed -i.bak "s#^version=.*#version=${cloud_version}#g" ./configurations.sh
    sed -i.bak "s#^port=.*#port=${cloud_port}#g" ./configurations.sh
    sed -i.bak "s#^db_ip=.*#db_ip=${cloud_db_ip}#g" ./configurations.sh
    sed -i.bak "s#^db_name=.*#db_name=${cloud_db_name}#g" ./configurations.sh
    sed -i.bak "s#^db_user=.*#db_user=${cloud_db_user}#g" ./configurations.sh
    sed -i.bak "s#^db_password=.*#db_password=${cloud_db_password}#g" ./configurations.sh
    sed -i.bak "s#^db_dir=.*#db_dir=${cloud_db_dir}#g" ./configurations.sh
    sed -i.bak "s#^system_user=.*#system_user=${system_user}#g" ./configurations.sh
    sed -i.bak "s#^ip=.*#ip=${cloud_ip}#g" ./configurations.sh
    sed -i.bak "s#^install_dir=.*#install_dir=${install_dir}#g" ./configurations.sh
    rm ./configurations.sh.bak
  else
    echo "board-----"
    if [[  "$1" == "fate-manager" ]]; then
      cd ${basepath}/fateboard
      cp ./default-configurations.sh ./configurations.sh
      eval db_ip=\${fateboard_db_ip[$2]}
      eval fb_ip=\${fateboard_ip[$2]}
      eval ff_ip=\${fate_flow_ip[$2]}

      sed -i.bak "s#^system_user=.*#system_user=${system_user}#g" ./configurations.sh
      sed -i.bak "s#^java_dir=.*#java_dir=${java_dir}#g" ./configurations.sh
      sed -i.bak "s#^install_dir=.*#install_dir=${install_dir}#g" ./configurations.sh
      sed -i.bak "s#^module=.*#module=fateboard#g" ./configurations.sh
      fateboard_version=$(grep -E -m 1 -o "<version>(.*)</version>" ${cloudpath}/fateboard/pom.xml|  awk -F '[<>]' '{print $3}')
      sed -i.bak "s#^version=.*#version=${fateboard_version}#g" ./configurations.sh
      sed -i.bak "s#^db_dir=.*#db_dir=${fateboard_db_dir}#g" ./configurations.sh
      sed -i.bak "s#^db_ip=.*#db_ip=${db_ip}#g" ./configurations.sh
      sed -i.bak "s#^db_name=.*#db_name=${fateboard_db_name}#g" ./configurations.sh
      sed -i.bak "s#^db_user=.*#db_user=${fateboard_db_user}#g" ./configurations.sh
      sed -i.bak "s#^db_password=.*#db_password=${fateboard_db_password}#g" ./configurations.sh
      sed -i.bak "s#^fateboard_ip=.*#fateboard_ip=${fb_ip}#g" ./configurations.sh
      sed -i.bak "s#^fateboard_port=.*#fateboard_port=${fateboard_port}#g" ./configurations.sh
      sed -i.bak "s#^fate_flow_ip=.*#fate_flow_ip=${ff_ip}#g" ./configurations.sh
      sed -i.bak "s#^fate_flow_port=.*#fate_flow_port=${fate_flow_port}#g" ./configurations.sh
      sed -i.bak "s#^logspath=.*#logspath=${fate_path}/python/logs/#g" ./configurations.sh
      rm ./configurations.sh.bak
    else
      echo "$1 doesn't exist!"
      exit
    fi
  fi
}

fateboard_packaging(){
  ping -c 4 www.baidu.com >>/dev/null 2>&1
  if [ $? -eq 0 ]
  then
   echo "Netword is ok."
  else
     echo "Sorry,the host cannot access the public network."
     exit -1
  fi

  if [[ -e "${cloudpath}/fateboard"  ]];then
    while [[ true ]];do
      read -p "The fateboard directory already exists, delete and re-download? [y/n] " input
      case ${input} in
      [yY]*)
        echo "[INFO] Delete the original fateboard"
        rm -rf ${cloudpath}/fateboard
        git clone ${fateboard_url} -b ${fateboard_branch} ${cloudpath}/fateboard
        break
        ;;
      [nN]*)
        echo "[INFO] Use the original fateboard"
        break
        ;;
      *)
        echo "Just enter y or n, please!"
        ;;
      esac
    done
  else
    git clone ${fateboard_url} -b ${fateboard_branch} ${cloudpath}/fateboard
  fi
  if [[  -e "${cloudpath}/fateboard/src/main/lib"  ]]; then
    rm -rf ${cloudpath}/fateboard/src/main/lib
  fi
  mkdir ${cloudpath}/fateboard/src/main/lib

  cd ${cloudpath}/fate-manager/
  mvn clean package -DskipTests
  cp ./target/fate-manager-${fate_manager_version}.jar ${cloudpath}/fateboard/src/main/lib

  cd ${cloudpath}/fateboard

  if [  -e ./pom.xml.back ]
  then
    rm pom.xml
    mv pom.xml.back pom.xml
  fi

  cp pom.xml pom.xml.back
  sed -i.bak '/<\/dependencies>/i\        <dependency>'  ./pom.xml
  sed -i.bak '/<\/dependencies>/i\            <groupId>com.webank.ai<\/groupId>'  ./pom.xml
  sed -i.bak '/<\/dependencies>/i\            <artifactId>fate-manager<\/artifactId>'  ./pom.xml
  sed -i.bak "/<\/dependencies>/i\            <version>${fate_manager_version}<\/version>"  ./pom.xml
  sed -i.bak '/<\/dependencies>/i\            <scope>system</scope>'  ./pom.xml
  sed -i.bak "/<\/dependencies>/i\            <systemPath>\${project.basedir}/src/main/lib/fate-manager-${fate_manager_version}.jar</systemPath>"  ./pom.xml
  sed -i.bak '/<\/dependencies>/i\        <\/dependency>'  ./pom.xml
  rm ./pom.xml.bak
  mvn clean package -DskipTests


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

  if [[  "$1" == "cloud-manager"  ]]; then

    config $1
    cd ${basepath}/$1
    #sh deploy.sh all
    operation $2
  else
     if [[  "$1" == "fate-manager"  ]]; then
        if [[ "$2" != "distribute" ]]; then
        fateboard_packaging
        fi
        for((i=0; i<${#fateboard_ip[*]}; i++))
        do
          config $1 i
          cd ${basepath}/fateboard
          #sh deploy.sh all
          operation $2
        done
     else
        echo "The module $1 doesn't exist!"
        exit

     fi

  fi

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