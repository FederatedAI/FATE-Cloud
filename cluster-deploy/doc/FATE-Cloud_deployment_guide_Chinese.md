#                     FATE-Cloud部署指南

1.部署需知
============

​	FATE-Cloud 是用于构建和管理联邦数据协作网络的一套基础服务。它能够在云端管理FATE系统，组建一个安全的联邦数据协作网络. 部署它的前提是已经存在一套完整的FATE服务.以下FATE-Cloud的部署配置会需要FATE的部署信息.

# 2.模块信息

|                 模块名称                 | 端口 | 描述                        |
| :--------------------------------------: | ---- | --------------------------- |
|              Cloud Manager               | 9999 | 一套FATE Cloud 系统部署一个 |
| FATE Board(integrated with FATE Manager) | 8080 | 每个站点部署一个            |

3.服务器配置
============

|  服务器  |                                                              |
| :------: | ------------------------------------------------------------ |
|   数量   | 1                                                            |
|   配置   | 8 core /16GB memory / 500GB硬盘/10M带宽                      |
| 操作系统 | CentOS linux 7.2及以上 / Ubuntu 16.04以上                    |
|   用户   | 用户：app，属主：apps（app用户需可以sudo su root而无需密码） |
| 文件系统 | 1.  500G硬盘挂载在/ data目录下； 2.创建/ data / projects目录，目录属主为：app:apps |

4.基础环境配置
==============

4.1 关闭selinux(可选)
---------------

**在目标服务器（例:192.168.0.1）root用户下执行：**

确认是否已安装selinux

centos系统执行：rpm -qa | grep selinux

ubuntu系统执行：apt list --installed | grep selinux

如果已安装了selinux就执行：setenforce 0

4.2 修改Linux最大打开文件数
---------------------------

**在目标服务器（例:192.168.0.1）root用户下执行：**

vim /etc/security/limits.conf

\* soft nofile 65536

\* hard nofile 65536

4.3关闭防火墙(可选)
--------------

**在目标服务器（例:192.168.0.1）root用户下执行**

如果是Centos系统：

systemctl disable firewalld.service

systemctl stop firewalld.service

systemctl status firewalld.service

如果是Ubuntu系统：

ufw disable

ufw status

4.4 软件环境初始化
------------------

**1）创建用户**

**在目标服务器（例:192.168.0.1）root用户下执行**

```
groupadd -g 6000 apps
useradd -s /bin/bash -g apps -d /home/app app
passwd app
```

**1）配置sudo**

**在目标服务器（例:192.168.0.1 ）root用户下执行**

vim /etc/sudoers.d/app

app ALL=(ALL) ALL

app ALL=(ALL) NOPASSWD: ALL

Defaults !env_reset

**2）配置ssh无密登录**

目标服务器需要配置到FATE集群各个机器之前的ssh免密登入,以下用（192.168.0.1 192.168.0.2）2台机器举例.

**a. 在目标服务器（192.168.0.1 192.168.0.2）app用户下执行**

su app

ssh-keygen -t rsa

cat \~/.ssh/id_rsa.pub \>\> /home/app/.ssh/authorized_keys

chmod 600 \~/.ssh/authorized_keys

**b.合并id_rsa_pub文件**

拷贝192.168.0.1的authorized_keys 到192.168.0.2
\~/.ssh目录下,追加到192.168.0.2的id_rsa.pub到authorized_keys，然后再拷贝到192.168.0.1

**在192.168.0.1 app用户下执行**

scp \~/.ssh/authorized_keys app\@192.168.0.2:/home/app/.ssh

输入密码

**在192.168.0.2 app用户下执行**

cat \~/.ssh/id_rsa.pub \>\> /home/app/.ssh/authorized_keys

scp \~/.ssh/authorized_keys app\@192.168.0.1:/home/app/.ssh

覆盖之前的文件

**c. 在目标服务器（192.168.0.1 192.168.0.2）app用户下执行ssh 测试**

ssh app\@192.168.0.1

ssh app\@192.168.0.2

5.项目部署
==========

注：此指导安装目录默认为/data/projects/，执行用户为app，安装时根据具体实际情况修改。

5.1 代码获取
------------

**在目标服务器（例:192.168.0.1 具备外网环境）app用户下执行**:

**注意：服务器需已安装好git和maven 3.5+**

进入执行节点的/data/projects/目录，执行：

```
cd /data/projects/
git clone https://github.com/FederatedAI/FATE-Cloud.git
```

5.2 配置文件修改和示例
----------------

**在目标服务器（例:192.168.0.1）app用户下执行**

进入 FATE-Cloud/cluster-deploy/scripts/deploy目录，修改配置文件default-configurations.sh.

配置文件default-configurations.sh.说明如下：

| 配置项                | 配置项意义                               | 配置项默认值  | 说明                                                         |
| --------------------- | ---------------------------------------- | ------------- | ------------------------------------------------------------ |
| system_user           | 操作用户                                 | app           | 使用默认值                                                   |
| install_dir           | Fate Cloud安装路径                       | 无            | 自定义                                                       |
| java_dir              | java环境的路径                           | 无            | 需指定为FATE集群所用的java环境路径                           |
| cloud_ip              | Cloud Manager服务所需部署的ip            | 无            | 自定义                                                       |
| cloud_port            | Cloud Manager服务的端口                  | 9999          | 可自定义,避免端口冲突即可                                    |
| cloud_db_ip           | Cloud Manager服务所要连的mysql的ip       | 无            | 该ip上需有运行正常数据库                                     |
| cloud_db_name         | Cloud Manager服务所要创建的数据库名      | cloud_manager | 推荐使用默认值                                               |
| cloud_db_user         | Cloud Manager服务所连mysql的用户名       | 无            | 该用户需要创建其他用户并授权,建议使用root                    |
| cloud_db_password     | Cloud Manager服务所连mysql的密码         | 无            | 用户名和密码需匹配                                           |
| cloud_db_dir          | Cloud Manager服务所要连的mysql的安装路径 | 无            | 用来执行mysql命令,配置mysql环境                              |
| fateboard_branch      | FATE Board的分支                         | develop-1.2.1 | 使用默认值                                                   |
| fateboard_db_dir      | FATE Board服务所要连的mysql的安装路径    | 无            | 用来执行mysql命令,配置mysql环境                              |
| fateboard_db_ip       | FATE Board服务所要连的mysql的ip          | 无            | fateboard的基础配置                                          |
| fateboard_db_name     | FATE Board服务所要连的mysql的数据库名    | 无            | fateboard的基础配置                                          |
| fateboard_db_user     | FATE Board服务所要连的mysql的用户名      | 无            | 该用户需要创建其他用户并授权,建议使用root                    |
| fateboard_db_password | FATE Board服务所要连的mysql的密码        | 无            | 用户名和密码需匹配                                           |
| fateboard_ip          | FATE Board服务所需部署的ip               | 无            | 根据实际所部署的FATE中的FATE Board的服务参数填写.存在多个时,顺序与下方的fate-_flow_ip顺序一致 |
| fateboard_port        | FATE Board服务的端口                     | 8080          | 可自定义,避免端口冲突即可                                    |
| fate_flow_ip          | FATE Flow服务所在ip                      | 无            | 根据实际所部署的FATE中的FATE Flow服务参数填写.存在多个时,顺序与上方的fateboard_ip顺序一致 |
| fate_flow_port        | FATE Flow服务所在端口                    | 9380          | 推荐使用默认值                                               |
| fate_path             | FATE集群所在路径                         | 无            | 根据实际所部署的FATE参数填写                                 |

**配置示例**

场景:

​	现有2台机器（192.168.0.1 192.168.0.2）,已经部署好了FATE机器.192.168.0.1作为host,

192.168.0.2作为guest.  其中FATE Cloud中的Cloud Manager服务部署在host(也可单独部署到另一台机器,但该机器需要java环境和mysql服务),FATE Manager作为插件集成到FATE board服务中,部署到guest和host(注意关闭之前机器上的board,或者改变端口,避免冲突).

```
system_user=app
install_dir=/data/projects/fate-cloud
java_dir=/data/projects/common/jdk/jdk1.8.0_192
#cloud-manager
cloud_ip=192.168.0.1
cloud_port=9999
cloud_db_ip=192.168.0.1
cloud_db_name=cloud_manager
cloud_db_user=root
cloud_db_password=***REMOVED***
cloud_db_dir=/data/projects/fate/common/mysql/mysql-8.0.13
#fateboard
fateboard_branch=develop-1.2.1
fateboard_db_dir=/data/projects/fate/common/mysql/mysql-8.0.13
fateboard_db_ip=(192.168.0.1 192.168.0.2)
fateboard_db_name=fate_flow
fateboard_db_user=root
fateboard_db_password=***REMOVED***
fateboard_ip=(192.168.0.1 192.168.0.2)
fateboard_port=8080
fate_flow_ip=(1192.168.0.1 192.168.0.2)
fate_flow_port=9380
fate_path=/data/projects/fate

```

5.3 部署
--------

按照上述配置含义修改default-configurations.sh文件对应的配置项后，然后在FATE-Cloud/cluster-deploy/scripts/deploy目录下执行部署脚本：

```
cd FATE-Cloud/cluster-deploy/scripts/deploy
```

如果需要部署所有组件，执行：

```
sh deploy.sh  all install
```

如果只部署部分组件(可选：cloud-manager,fate-manager)：

```
sh deploy.sh  cloud-manager install
```

6.配置检查
==========

执行后可到各个目标服务器上进行检查对应模块的配置是否准确，每个模块的对应配置文在install_dir路径/模块名/conf/application.properties.

7.启动和停止服务
================

7.1 启动服务
------------

**在目标服务器（192.168.0.1 192.168.0.2）app用户下执行**

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh start
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh start
```

7.2 检查服务状态
----------------

**在目标服务器（192.168.0.1 192.168.0.2）app用户下执行**

查看各个服务进程是否启动成功：

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh status
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh status
```

7.3 关机服务
------------

**在目标服务器（192.168.0.1 192.168.0.2）app用户下执行**

若要关闭服务则使用：

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh stop
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh stop
```



8.测试
======

8.1 Cloud Manager testing
----------------------

Cloud Manager是一项Web服务.如果成功启动了服务，则可以通过访问 http://192.168.0.1:8080 来查看任务信息.

注意事项:

防护墙需要开放该端口

用真实ip替换192.168.0.1

## 8.2 FATE Manager testing

FATE Manager集成在fateboard服务中.如果成功启动了服务，则可以通过访问 http://192.168.0.1:8080/fateManagerIndex, http://192.168.0.2:8080/fateManagerIndex来查看任务信息.

注意事项:

防护墙需要开放该端口

用真实ip替换192.168.0.1, 192.168.0.2

