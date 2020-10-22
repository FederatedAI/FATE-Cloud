# Cloud-Manager部署指导文档 #



1.服务基本信息
============
|   服务名称    | 端口 | 描述                                              |
| :-----------: | ---- | ------------------------------------------------- |
| Cloud-Manager | 9999 | cloud-Manager用来生成与管理每个加入的联邦站点信息 |

2.服务器配置
============

| 配置 | 描述                                           |
| :--: | ---------------------------------------------- |
| 数量 | 1                                              |
| 配置 | 内存 : 8 core /16GB   硬盘 : 500GB  带宽 : 10M |
| 系统 | CentOS linux 7.2及以上,   Ubuntu 16.04 及以上. |
| 用户 | app                                            |
| 依赖 | jdk8                                           |

3.服务器环境
==============

3.1 关闭selinux (可选)
---------------

**注意 : 以下操作需root权限**

1. 确认selinux是否存在 :

```
# centos
rpm -qa | grep selinux

# ubuntu
apt list --installed | grep selinux
```

2. 如果存在 : 

```
setenforce 0
```



3.2 关闭防火墙(可选)
--------------

**注意 : 以下操作需root权限**

```
Centos：

systemctl disable firewalld.service

systemctl stop firewalld.service

systemctl status firewalld.service


Ubuntu：

ufw disable

ufw status
```



3.3 用户设置
------------------

**1）创建app用户和apps组**

注意 : 以下操作需root权限

```
groupadd -g 6000 apps
useradd -s /bin/bash -g apps -d /home/app app
passwd app
```

**2）配置sudo**

注意 : 以下操作需root权限

```
vim /etc/sudoers.d/app

app ALL=(ALL) ALL

app ALL=(ALL) NOPASSWD: ALL

Defaults !env_reset
```

**3）配置ssh免密连接**

例：192.168.0.1免密登入192.168.0.2

1.192.168.0.1和192.168.0.2分别生成密钥

```
su app

ssh-keygen -t rsa

```

2.拷贝192.168.0.1的公钥信息（在id_rsa.pub文件中）到192.168.0.1的authorized_keys文件中


3.测试

```
ssh app\@192.168.0.2
```

注意：

a. ssh目录的权限必须是700

```
chmod 700 .ssh
```

b. 授权列表authorized_keys的权限必须是600

```
chmod 600 authorized_keys
```

5.部署 
==========

部署服务器(部署Cloud-Manager服务)192.168.0.1, 源码下载目录 : /data/projects/, 用户 : app (可自定义).

目标服务器(运行Cloud-Manager服务):192.168.0.2 .



5.1 源码拉取
------------

部署服务器:192.168.0.1

依赖:网络, git, maven 3.5+, jdk8

```
cd /data/projects/
git clone https://github.com/FederatedAI/FATE-Cloud.git
```

5.2 配置
----------------

配置文件路径:  /data/projects/FATE-Cloud/cloud-manager/deploy/scripts/configurations.sh

配置文件说明:

| 配置项             | 说明                   | 默认值          | 备注                      |
| :----------------- | :--------------------- | :-------------- | :------------------------ |
| system_user        | 目标服务器用户         | app             | -                         |
| install_dir        | 服务安装目录           | /data/projects/ | 可自定义                  |
| cloud_ip           | 目标服务器IP           | 无              | 自定义                    |
| cloud_port         | 服务端口               | 9999            | 可自定义                  |
| java_dir           | 目标服务器java环境路径 | 无              | -                         |
| cloud_db_ip        | 服务所连mysql的ip      | 无              | 自定义                    |
| cloud_db_name      | 服务所用库名           | cloud_manager   | 部署时自动创建            |
| cloud_db_user      | mysql用户名            | root            | root权限,需创建用户并授权 |
| cloud_db_password  | mysql密码              | 无              | -                         |
| cloud_db_dir       | mysql安装目录          | 无              | -                         |
| cloud_db_sock_path | mysql.sock文件路径     | 无              | -                         |

**例:**

```
system_user=app
install_dir=/data/projects/

#cloud-manager
cloud_ip=192.168.0.2
cloud_port=9999
java_dir=/data/projects/common/jdk/jdk1.8.0_192
cloud_db_ip=192.168.0.2
cloud_db_name=cloud_manager
cloud_db_user=root
cloud_db_password=fate_dev
cloud_db_dir=/data/projects/fate/common/mysql/mysql-8.0.13
cloud_db_sock_path=/data/projects/fate/common/mysql/mysql-8.0.13/mysql.sock
```

5.3 部署
--------

改完配置文件后,执行如下命令:

```
cd /data/projects/FATE-Cloud/cloud-manager/deploy/scripts/
sh deploy.sh  all install
```



6.配置检查
==========

进入目标机器的安装目录,检查配置文件

例:

```
cd /data/projects/cloud-manager
cat ./conf/application.properties
```



7.服务管理
================

7.1 启动服务
------------

例:

```
cd /data/projects/cloud-manager
sh service.sh start
```

7.2 检查状态
----------------

例:

```
cd /data/projects/cloud-manager
sh service.sh status
```

7.3 关闭服务
------------

**例:**

```
cd /data/projects/cloud-manager
sh service.sh stop
```



8.测试
======

Cloud-Manager 是一个web服务.如果部署成功,可以通过网址(例:http://192.168.0.2:9999)访问.

注意: 请用真实配置的IP和端口访问,上文只是示例!


