

# ansible 部署FATE-Cloud-1.4.1指引



### 1、概述

#### 1.1 FATE Cloud简介

作为构建和管理联邦数据协作网络的基础设施，FATE Cloud 是第一个工业级的联邦学习云服务。

FATE Cloud使FATE能够在多云中进行管理，形成一个安全的联邦数据网络，旨在提供跨组织或组织内部的安全合规的数据协作解决方案，并提供企业级的联合学习生产力应用解决方案。

FATE-Cloud提供标准的联邦基础设施实施能力、技术支持能力、统一的联邦站点管理模式和全流程服务，解决管理协同、数据处理和认证、协作效率低、不同组织间交互不畅等问题。

FATE-Cloud官方网站：https://github.com/FederatedAI/FATE-Cloud



本文将介绍使用ansible部署脚本进行FATE-Cloud部署。




### 2、部署手册
#### 2.1 环境依赖

| 名称             | 说明                                                |
| ---------------- | --------------------------------------------------- |
| 系统             | Centos 7.6                                          |
| 开发语言         | Python 3.6.5、Java 1.8                              |
| 软件组件及其版本 | fate-manager-1.4.1 cloud-manager-1.4.1 mysql-8.0.13 |



#### 2.2 依赖条件

假定是以普通账号（app账号）进行部署，部署Base模块则需要root权限(假定app账号具有免输入密码的sudo权限）。部署的目标为: $pbase/fate目录下。这里假定：

- pbase： /data/projects
- tbase:  /data/temp
- lbase: /data/logs




#### 2.3 组件信息

| 角色          | 端口 | 日志目录                             | 介绍                                      |
| ------------- | ---- | ------------------------------------ | ----------------------------------------- |
| Cloud Manager | 8998 | /data/logs/fate-cloud/cloud_manager/ | 联邦网络的管理中心                        |
| FATE Manager  | 9080 | /data/logs/fate-cloud/fate_manager/  | 联邦站点的管理和维护                      |
| mysql         | 3306 | /data/logs/fate-cloud/mysql/         | 数据存储，fate_manager和cloud_manager依赖 |



#### 2.4 基本概念与原理

##### 2.4.1 部署包形态

- 离线包： 可以直接进行部署的包。

- 在线包： 部署的包，但不包括模块的资源包，不能直接进行部署。

  

##### 2.4.2 部署流程

- 构建

- 部署

​         

##### 2.4.3 部署形态

- 安装： 只安装软件

- 更新配置： 只更新配置

- 部署： 安装软件和更新配置

- 删除： 删除软件和配置




##### 2.4.4 部署模块（fate-cloud）

​       部署一个或者多个模块：mysql、fate_manager、cloud_manager

​       配置组件：

​		- mysql，如已存在数据库，可以使用其外部数据库，具体参考4.1

​		- cloud_manager

​		- fate_manager



#### 2.5 辅助脚本和配置文件

##### 2.5.1 构建脚本和构建配置文件

脚本：**build/build.sh**

文件：**build/conf/setup.conf**

###### 2.5.1.1 构建脚本的使用

- 初始化构建配置文件

  ```
  命令格式：bash build/build.sh init pname version
  参数说明：
          pname： 项目名称 fate-cloud
          version： 版本号
  使用示例：	bash build/build.sh init fate-cloud 1.4.1
  ```

​        

- 按需编辑构建配置文件

  ```
  vim build/conf/setup.conf
  ```

  

- 执行构建

  ```
  bash build/build.sh do
  ```

  

###### 2.5.1.2 构建配置文件

- 部署fate-cloud的构建配置文件

  ```
  project: fate-cloud
  products:
  - fate_manager
  - cloud_manager
  product_fate_cloud_version: 1.4.1
  product_fate_cloud_versions:
    fate_manager: 1.4.1
    cloud_manager: 1.4.1
  ```
  
  配置说明：
          products： 产品
  
  ​        product_fate_cloud_version： 项目版本号
  
  ​		product_fate_cloud_versions：各组件的版本号



##### 2.5.2 部署辅助脚本和部署配置文件

部署辅助脚本：   deploy/deploy.sh

部署配置文件：   deploy/conf/setup.conf

ansible配置文件： var_files/prod/*

###### 2.5.2.1 部署辅助脚本使用指引



```
sh deploy/deploy.sh --help
Usage: deploy/deploy.sh init|render|deploy|install|config|uninstall|help args

sh deploy/deploy.sh render --help
Usage: deploy/deploy.sh render
```



###### 2.5.2.2 使用部署辅助脚本进行初始化

- 生成部署配置文件

```
sh deploy/deploy.sh  init [-c|-f|-d|-m]
```

参数说明： 

​        -c：表示部署cloud_manager的服务IP，需要搭配-d同时使用；使用示例： -c="192.168.0.1" -d 或  -c -d

​		-f：表示部署fate_manager的服务IP，需要搭配-d同时使用；使用示例： -f="192.168.0.1" -d 或  -f -d

​		-d：表示部署mysql的服务IP；使用示例： -d="192.168.0.1" 或  -d

​        -m：部署模式，-m=deploy|install|config|uninstall，默认deploy（安装+配置）

​        

- 使用示例

  - 初始化无需参数值

    ```
    bash deploy/deploy.sh init -c -f -d
    ```

    

  - 初始化使用实际参数值

    ```
    sh deploy/deploy.sh init -c="192.168.0.1" -f="192.168.0.2" -d="192.168.0.3"
    ```



- 特别说明：执行指令生成部署配置文件后，可按需手工修改配置文件调整配置

  ```
  vim deploy/conf/setup.conf
  ```

  

###### 2.5.2.3 使用部署辅助脚本生成ansible配置文件

```
sh deploy/deploy.sh render 
```




###### 2.5.2.4 使用部署辅助脚本进行部署或卸载

     /bin/bash deploy/deploy.sh deploy|install|config|uninstall

参数说明：

​                deploy:   安装软件和更新配置

​                install：  安装软件

​                config：  更新配置

​                uninstall： 卸载

单服务或多服务的部署、卸载：

```
1）vim deploy/conf/setup.conf		//按需增加或删减模块，编辑完成后执行render生成配置

deploy_mode: deploy		//deploy、install、config表示部署、uninstall表示卸载
modules:				//调整需要部署或卸载的模块
  - mysql
  - fate_manager
  - cloud_manager
  
2）/bin/bash deploy/deploy.sh render		//生成配置
3）/bin/bash deploy/deploy.sh deploy|uninstall	//执行部署或卸载
```



- 查看部署、卸载日志

```
tailf logs/deploy-??.log				---部署服务的日志，执行部署命令会提示查看
tailf logs/uninstall-??.log				---卸载服务的日志，执行卸载命令会提示查看
```



###### 2.5.2.5 部署配置文件说明

- **部署配置文件讲解**

  文件：`deploy/conf/setup.conf`

  ```
  env: prod
  pname: fate-cloud
  ssh_port: 22
  deploy_user: app
  deploy_group: apps
  
  deploy_mode: deploy
  
  modules:
    - fate_manager
    - cloud_manager
    - mysql
  
  mysql_node: 192.168.0.1:3306
  
  cloud_manager_node: 192.168.0.1:8998
  
  fate_manager_node: 192.168.0.1:9080
  
  ```

​      参数说明：

	1）deploy_mode： 部署模式。 取值有： deploy、install、config、uninstall，设置方式： 默认deploy表示安装软件并配置服务，install只安装软件，config只更新配置服务，uninstall表示卸载。
	 
	2）modules：需要的部署的模块。取值有：mysql、fate_manager、cloud_manager，设置方式： 单独一个，多个或者全部。例：modules: ['mysql','fate_manager']; 默认初始化会自动生成
	
	3）mysql_node：部署mysql组件。取值为：ip:端口
	
	4）cloud_manager_node： 部署cloud_manager组件。取值为：ip:端口
	
	5）fate_manager_node： 部署fate_manager组件。取值为：ip:端口



##### 2.5.3 ansible配置文件

###### 2.5.3.1 配置base信息

- 涉及建立基础目录和安装基础依赖包等。（涉及sudo/root权限操作）

```
vi var_files/prod/base_init
```

- 内容如下：	默认无需修改配置

```

//目录可以根据实际情况调整			
pbase: "/data/projects"				---项目根目录
dbase: "/data/projects/data"		---数据目录
cbase: "/data/projects/common"		---工具类部署目录（包含supervisor和miniconda的路径）
lbase: "/data/logs"					---日志目录
tbase: "/data/temp"					---临时目录

envCheck: True		---设置为False会跳过环境检查

supervisord:
  version: 1.1.4
  account:
    name: "root"				---supervisor登陆账号
    password: "fate"		---supervisor登陆密码
  service:
    owner: "app"	---supervisor启动用户，也是服务启动用户，按情况调整
    group: "apps"   ---用户的用户组，按情况调整
    ip: "127.0.0.1"	---supervisor启动IP
    port: 9001		---supervisor启动端口

```



###### 2.5.3.2 配置fate基础信息

涉及各个组件的版本。

```
vi var_files/prod/fate_init
```

内容如下：	（默认无需修改配置，外部数据库可调整mysql信息）

```
deploy_mode: deploy	---部署模式，安装install 、配置config、卸载uninstall，脚本自动替换
deploy_modules:		---部署模块
  - mysql
  - fate_manager
  - cloud_manager

pname: "fate-cloud"			---项目名称
versions:				---各服务版本号
  cloud_manager: 1.4.1
  fate_manager: 1.4.1

python:					---python部署信息
  version: 4.5.4
  dest: "miniconda3"
  venv: "common/python/venv"
  pip: fm-site-packages
  must:
  - setuptools-42.0.2-py2.py3-none-any.whl

java:					---java部署信息
  name: "jdk"
  version: "8u192"
  path: "common/jdk"

mysql:					---mysql部署信息
  version: "8.0.13"
  path: "common/mysql"
  user: "root"			---mysql数据库管理账号，使用外部mysql需要修改此参数为实际使用账号
  passwd: "fatE168dev"	---mysql数据库管理密码，使用外部mysql需要修改此参数为实际使用密码
```



###### 2.5.3.3 配置fate_cloud信息

修改文件（可按需修改）

```
vi var_files/prod/fate_cloud
```

内容如下：

```
fate_cloud:
  cloud_manager:
    enable: false		---true为需要部署此模块，False则否
    ip: 192.168.0.1		---cloud_manager部署机器的IP
    port: 8998			---cloud_manager服务端口  
    dbname: cloud_manager	---cloud_manager数据库db名称
    fate_chain: "127.0.0.1:8033"		---cloud_manager依赖的fate_chain服务的url
    exchange:			---服务路由管理模块连接rollsite的管理配置
      party_id: exchange	---exchange下管理rollsite管理员partyId
      key: exchange			---exchange下管理rollsite签名key

  fate_manager:
    enable: false		---true为需要部署此模块，False则否
    ip: 192.168.0.1		---fate_manager部署机器的IP
    http_port: 9080		---fate_manager服务端口
    dbname: fate_manager	---fate_manager数据库db名称
    proxy: false			---是否使用代理,true|True表示开启,默认关闭；代理软件(如：squid)需要提前部署好
    proxy_ip: 192.168.0.1	---代理ip
    proxy_port: 3128		---代理端口

  mysql:
    enable: false			---true为需要部署此模块，False则否
    type: inside			---inside表示内部数据库，自动部署；outside表示外部数据库，不提供部署
    ip: 192.168.0.1			---mysql机器IP
    port: 3306				---mysql服务端口
    dbuser: fatecloud		---数据库业务账号，使用外部mysql可修改此参数
    dbpasswd: fatecloud		---数据库业务账号密码，使用外部mysql可修改此参数
```



###### 2.5.3.4 配置任务列表

修改文件：(无需修改)

```
vi project_prod.yaml
```

project_prod.yaml内容如下：

```
- hosts: fate-cloud
  any_errors_fatal: True
  vars:
    jbase: "{{pbase}}/{{pname}}/{{java['path']}}/{{java['name']}}-{{java['version']}}"
    pybase: "{{pbase}}/{{pname}}/{{python['venv']}}"
  vars_files:
  - var_files/prod/base_init
  - var_files/prod/fate_init
  - var_files/prod/fate_cloud
  roles:
  - supervisor
  - { role: "mysql", when: "( ansible_ssh_host == fate_cloud['mysql']['ip'] and fate_cloud['mysql']['enable'] == True and deploy_mode in [ 'deploy', 'install', 'config' ] )" }
  - { role: "fate_manager", when: "( ansible_ssh_host == fate_cloud['fate_manager']['ip'] and fate_cloud['fate_manager']['enable'] == True and deploy_mode in [ 'deploy', 'install', 'config' ] )" }
  - { role: "cloud_manager", when: "( ansible_ssh_host == fate_cloud['cloud_manager']['ip'] and fate_cloud['cloud_manager']['enable'] == True and deploy_mode in [ 'deploy', 'install', 'config' ] )" }
```



###### 2.5.3.5 配置主机列表

修改文件：(无需修改)

```
vi environments/prod/hosts
```

内如如下：

```
[all:vars]
ansible_connection=ssh
ansible_ssh_port=22			---远程连接端口
ansible_ssh_user=app		---远程执行用户，同时也是部署用户
#ansible_ssh_pass=
##method: sudo or su
ansible_become_method=sudo
ansible_become_user=root
ansible_become_pass=
[deploy_check]

[fate-cloud]
192.168.0.1
```



#### 2.6 部署流程

##### 2.6.1 构建

###### 2.6.1.1 构建离线包

```
wget https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/fate-cloud/1.4.1/fate_cloud_ansible_1.4.1-release.tar.gz
tar xzf fate_cloud_ansible_1.4.1-release.tar.gz
cd fate-cloud-ansible-1.4.1
```

###### 2.6.1.2构建非离线包

- 方法1
  
```
git clone 
```

- 方法2

```
wget https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/fate-cloud/1.4.1/AnsibleFATE_Cloud_1.4.1-release.tar.gz
tar xzf AnsibleFATE_Cloud_1.4.1-release.tar.gz
cd AnsibleFATE-Cloud-1.4.1-release
```

###### 2.6.1.3 按需在线构建资源包

初始化配置（使用最新版本的包）

```
sh build/build.sh init fate-cloud [version] 
// version表示项目的版本号,不填表示使用最新版本的包
```

按需调整配置（已初始化版本默认无需修改）

vim build/conf/setup.conf

```
project: fate-cloud
products:
- fate_manager
- cloud_manager
product_fate_cloud_version: 1.4.1
product_fate_cloud_versions:
  fate_manager: 1.4.1
  cloud_manager: 1.4.1
```

执行构建

```
sh build/build.sh do
```



##### 2.6.2 生成部署配置文件

执行指令生成部署配置文件，指令的使用参考2.5.2



##### 2.6.3 调整参数并生成ansible配置文件

按需手工修改配置文件

```
vi deploy/conf/setup.conf
```

生成ansible配置文件: var_files/prod/***

```
 /bin/bash deploy/deploy.sh render
```



##### 2.6.4 执行ping测试(可选)

```
 /bin/bash deploy/deploy.sh ping
```



##### 2.6.5 执行部署（按需）

```
 /bin/bash deploy/deploy.sh deploy|install|config|uninstall
```



##### 2.6.6 检查服务

​        详看2.7.1一节。



#### 2.7 服务验证

##### 2.7.1 服务进程验证

**访问cloud-manager**

浏览器访问http://192.168.0.1:8998/cloud-manager

**访问fate-manager**

浏览器访问http://192.168.0.1:9080/fate-manager

**查看进程和端口**

使用ps、losf、ss等命令查看已经部署的服务的进程和端口。

- 查看进程

```
ps aux|grep fate-cloud

/bin/bash  /data/projects/common/supervisord/service.sh  status all
```

- 查看在监听的所有tcp端口

```
ss -lnt
```

- 查看指定端口是否监听

```
lsof -i :8998
```



#### 2.8 服务运维

**服务管理**

进入supervisor目录

```
cd /data/projects/common/supervisord
```

启动/关闭/查看所有：

```
sh service.sh start/stop/status all 
```

启动/关闭/查看单个模块(可选：cloud_manager，fate_manager，mysql)：

```
sh service.sh start/stop/status fate-cloud-manager
sh service.sh start/stop/status fate-cloud-fatemanager
sh service.sh start/stop/status fate-cloud-mysql
```

**服务日志**

| 服务          | 进程关键字    | 日志路径                              |
| ------------- | ------------- | ------------------------------------- |
| cloud_manager | cloud_manager | /data/logs/fate-cloud//cloud_manager/ |
| fate_manager  | fate_manager  | /data/logs/fate-cloud//fate_manager/  |
| mysql         | mysql         | /data/logs/fate-cloud/mysql/          |



### 3 部署示例

- ##### 部署目标介绍

  | 角色          | IP          | 端口 | 介绍                                     |
  | ------------- | ----------- | ---- | ---------------------------------------- |
  | cloud-manager | 192.168.0.1 | 8998 | 联邦网络的管理中心                       |
  | fate-manger   | 192.168.0.2 | 9080 | 联邦站点的管理和维护                     |
  | mysql         | 192.168.0.3 | 3306 | 数据存储，cloud-manager和fate-manger依赖 |

- 构建部署包

```
wget https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/fate-cloud/1.4.1/fate_cloud_ansible_1.4.1-release.tar.gz
tar xzf fate_cloud_ansible_1.4.1-release.tar.gz
cd fate-cloud-ansible-1.4.1
```

- 初始化生成部署配置文件

```
/bin/bash deploy/deploy.sh init -c="192.168.0.1" -f="192.168.0.2" -d="192.168.0.3"
```

- 调整部署配置文件

`vim deploy/conf/setup.conf`

```
env: prod
pname: fate-cloud
ssh_port: 22
deploy_user: app
deploy_group: apps

deploy_mode: deploy

modules:
  - fate_manager
  - cloud_manager
  - mysql

mysql_node: 192.168.0.3:3306
cloud_manager_node: 192.168.0.1:8998
fate_manager_node: 192.168.0.2:9080
```

- 执行指令生成ansible配置文件

```
/bin/bash deploy/deploy.sh render
```

- 按需修改ansible配置文件

```
fate_cloud:
  cloud_manager:
    enable: false
    ip: 192.168.0.1
    port: 8998
    dbname: cloud_manager
    fate_chain: "127.0.0.1:8033"
    exchange:
      party_id: exchange
      key: exchange

  fate_manager:
    enable: false
    ip: 192.168.0.2
    http_port: 9080
    dbname: fate_manager
    proxy: false			---是否使用代理,true|True表示开启,默认关闭；代理软件(如：squid)需要提前部署好
    proxy_ip: 192.168.0.1	---代理ip
    proxy_port: 3128		---代理端口

  mysql:
    enable: false
    type: inside		---outside表示不部署数据库，使用外部数据库
    ip: 192.168.0.3
    port: 3306
    dbuser: fatecloud
    dbpasswd: fatecloud
```

- 执行部署

```
/bin/bash deploy/deploy.sh deploy
```

- 查看部署日志

```
tailf logs/deploy-??.log				---部署服务的日志，执行部署命令会提示查看
```



### 4 特定操作指引

#### 4.1 mysql使用外部数据库

1）使用root登录数据库授权内网root所有人访问（没有root用户，使用有管理权限的账号登陆）

登陆mysql（需要cd到mysql的目录下）

```
./bin/mysql -uroot -p
```

执行如下sql

```
alter user 'root'@'localhost' identified with mysql_native_password by 'root管理密码';
CREATE USER if not exists root@'%' IDENTIFIED BY "root管理密码";
GRANT ALL ON *.* TO root@'%' WITH GRANT OPTION;
alter user 'root'@'%' identified with mysql_native_password by 'root管理密码';
flush privileges;
```

2）修改var_files/prod/fate_cloud

```
mysql:
    enable: True
    type: outside			--修改为outside，表示外部
    ip: 192.168.0.1			--填写外部mysql的实际IP
    port: 3306				--填写外部mysql的实际端口
    dbuser: fatecloud	
    dbpasswd: "fatecloud
```

3）修改var_files/prod/fate_init

```
mysql:				
  version: "8.0.13"
  path: "common/mysql"
  user: "root"			---mysql数据库管理账号，修改为实际使用的管理账号
  passwd: "fatE168dev"	---mysql数据库管理密码，修改为实际使用的管理密码
```



### 5 Howto

- 特殊说明

​    1）如何绕过环境检查？

​		为了方便测试，系统参数可能不会与生产同步，需要跳过环境检查的报错

```
vi var_files/prod/base_init
envCheck: False		--设置为False
```
