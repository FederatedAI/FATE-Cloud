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

system_user=app
install_dir=/data/projects/xym/deploy
java_dir=/data/projects/common/jdk/jdk1.8.0_192
#cloud-manager
cloud_ip=10.107.117.33
cloud_port=9999
cloud_db_ip=10.107.117.33
cloud_db_name=cloud_manager
cloud_db_user=root
cloud_db_password=fate_dev
cloud_db_dir=/data/projects/fate/common/mysql/mysql-8.0.13
#fateboard
fateboard_branch=develop-1.2.1
fateboard_db_dir=/data/projects/fate/common/mysql/mysql-8.0.13
fateboard_db_ip=(10.107.117.33)
fateboard_db_name=fate_flow
fateboard_db_user=root
fateboard_db_password=fate_dev
fateboard_ip=(10.107.117.33)
fateboard_port=10000
fate_flow_ip=(10.107.117.33)
fate_flow_port=9380
fate_path=/data/projects/fate
