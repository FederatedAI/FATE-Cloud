# Fate Manager部署指南

## 1. 部署组件说明

| 组件         | 是否必须 | 说明                         |
| ------------ | -------- | ---------------------------- |
| fate-manager | 是       | 站点端用于注册和加入联邦组织 |
| mysql        | 否       | 数据库                       |
| python       | 否       | python环境                   |

部署脚本提供全部组件的部署功能，用户可根据需要自行选择;
由于涉及到python版本及依赖问题，建议用户部署我们提供的python组件;

## 2.  项目部署

### 2.1 获取部署包

在目标服务器下执行：

```shell
cd /data/projects
wget https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/fate-cloud/1.3.0/fate_manager_1.4.0_release-1.0.0.tar.gz
tar -xzvf fate_manager_1.4.0_release-1.0.0.tar.gz
```

### 2.2 配置文件修改和示例

修改配置文件fate_manager-install/common-deploy/conf/setup.conf

```shell
vi fate_manager-install/common-deploy/conf/setup.conf
```

配置说明:

| 配置参数          | 配置项值                        | 说明                   |
| --------        | --------------                  |  --------------------|
| pbase           |默认：/data/projects              |部署mysql的安装根目录     |
| pname           |默认：soft-common                  |安装python和mysql文件夹名  |
| lbase           |默认：/data/projects/logs         |日志文件路径             |
| mysql_path      |默认：soft-common/mysql           |mysql安装子目录(用户选择部署本组件提供的mysql，此处填相对路径；)           |
| mysql_admin_user |默认：root                       |mysql登入账号            |
| mysql_admin_pass |默认：fate_dev                   |mysql登入密码             |
| mysql_port      |默认：3308                         |mysql服务监听端口        |
| fate_manager_dbname  |默认：fate_manager           | 数据库名             |
| mysql_ip         |默认：127.0.0.1                  |数据库IP               |
| pyenv            |默认                             |Python运行的环境          |
| http_port         |默认：9080  根据实际情况修改       |fate manager端口       |
| host             |默认：不需要修改                   |fate manager ip  |
| pro_base         |默认：/data/projects            | 项目部署目录              |
| pro_name         |默认：FATE-Cloud 不需要修改         |项目部署名 |

### 2.3 部署

#### 2.3.1 部署全部组件

```
cd fate_manager-install && sh common-deploy/common-deploy.sh 
```

fate-manager部署日志在:/data/projects/FATE-Cloud/logs下


#### 2.3.2 部署单个组件

- **只部署fata-manager**

```
cd fate_manager-install && sh common-deploy/common-deploy.sh fate-manager
```

**注意**: 只部署fate-manager时需要用户添加提供python环境路径及mysql的相关配置，具体如下:

**python：(3.6.x及以上版本)**

| 配置参数          | 配置项值          | 说明                        |
| --------        | --------------   |  -------------------------|
| pyenv           |Python路径         |绝对路径                  |

*python依赖下载：python-deploy/files/requirements.txt*
     
**mysql配置：**

| 配置参数              | 说明                        |
| --------             |  -------------------------|
| mysql_path           |mysql的安装路径(用户选择提供自己的mysql，此处填写提供绝对路径)  |
| mysql_admin_user     |mysql用户名            |
| mysql_admin_pass     |mysql密码              |
| mysql_port           |mysql端口         |
| fate_manager_dbname  |数据库名(需要手动创建),在部署之前用户先在数据库建立此库名 |

## 3.部署验证

浏览器访问: http:/ip:port/index,其中ip, port填部署fate-manager时的配置

## 4.启停服务

```
cd /data/projects/FATE-Cloud/fate_manager/ 
sh service.sh start|stop|restart|status
```

