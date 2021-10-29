## Cloud-Manager部署指导文档-COS版



### 1.部署包下载并解压

```
# 下载部署包
wget https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/fate-cloud/1.4.1/cloud_manager_1.4.1_release-1.0.0.tar.gz

# 解压包
tar zxvf cloud_manager_1.4.1_release-1.0.0.tar.gz;
cd cloud-manager/;

# 目录结构如下
drwxr-xr-- 2 app apps     4096 8月  23 12:56 bin						 	# 执行脚本
-rwxr-xr-- 1 app apps 61295626 8月  16 10:57 cloud-manager-1.4.1.jar  	# 服务jar包
drwxr-xr-- 2 app apps     4096 8月  23 12:54 config					 	# 服务配置文件
drwxr-xr-- 2 app apps     4096 8月  23 12:57 deploy						# 服务部署文件
drwxr-xr-- 2 app apps     4096 8月  23 11:16 sql						    # 服务数据库sql脚本
```

#### ps. 该部署包暂未提供部署mysql服务, 需连接已有mysql并创建好数据库, 请使用sql/cloud-manager.sql脚本创建数据库相关表



### 2.修改部署配置文件

```
# 服务部署配置文件
vim deploy/deploy.conf


# 服务基础配置项
server_port=9000									# 服务监听端口
server_ip=127.0.0.1									# 当前服务所在服务器ip或代理ip, 创建机构与站点时激活链接将使用此ip为默认服务ip

# 服务使用db配置
mysql_ip=127.0.0.1									# mysql数据库ip
mysql_port=3306										# mysql数据库端口
mysql_username=      								# mysql数据库用户名
mysql_password= 									# mysql数据库密码
mysql_db=cloud_manager								# mysql数据库名

# 服务路由管理模块连接rollsite服务管理配置, rollsite配置需要与下面配置保持一致, 否则无法连接
exchange_id=exchange								# exchange下管理rollsite管理员partyId
exchange_key=exchange								# exchange下管理rollsite签名key

# 其他服务与环境配置
fate_chain_ca=127.0.0.1:8000						# fate chain ca 服务地址, 证书管理模块生成证书需要配置此服务
supervisord_dir=/data/projects/common/supervisord	# 如果使用supervisord, 则需要配置supervisord服务地址
java_home=/nemo/jdk1.8.0_141						# java环境目录
```



### 3. 初始化并启动服务

```
sh bin/cloud_manager.sh format
```



### 其它相关命令

```
# 启动服务
sh bin/cloud_manager.sh start

# 停止服务
sh bin/cloud_manager.sh stop

# 重启服务
sh bin/cloud_manager.sh restart

# 服务状态
sh bin/cloud_manager.sh status

# 清除服务日志等临时文件
sh bin/cloud_manager.sh clear
```
