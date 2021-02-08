# Hyperion部署指引文档



Hyperion 使用pip作为python第三方库管理工具，在安装部署之前需要检查以下条件是否满足：

- 安装python3.6.8+
- 有良好的网络



## Hyperion的部署

1. 执行如下命令，从github上克隆FATE-Cloud最新代码：

```shell
git clone https://github.com/FederatedAI/FATE-Cloud.git
```

2. 执行如下命令，进入源代码根目录：

```shell
cd FATE-Cloud/fate-manager/hyperion
```

3. 修改源代码根目录下的`bin/init_env.sh` 文件，修改内容修改内容如下：

```shell
# 将pythonpath设置为源码根目录路径，例如：
export PYTHONPATH=/data/projects/FATE-Cloud/fate-manager/hyperion
# 将venv设置为python环境所在目录，例如：
venv=/data/projects/python/venv
```

4. 激活python虚拟环境，安装hyperion依赖的第三方库：

```shell
source hyperion/bin/init_env.sh
pip install -r hyperion/requirements.txt
```

5. 根据实际情况修改服务配置，路径为hyperion/conf/service_conf.yaml。如下为服务配置的含义，请根据实际情况对其进行修改及保存。

| 配置项                   | 配置项含义             | 默认值       |
| ------------------------ | ---------------------- | ------------ |
| database.name            | 数据库名               | hyperion |
| database.user            | 数据库用户名           | user         |
| database.passwd          | 数据库密码             | pass         |
| database.host            | 数据库地址             | 127.0.0.1    |
| database.port            | 数据库端口             | 3306         |
| database.max_connections | 数据库最大连接数       | 100          |
| database.stale_timeout   | 数据库连接超时时间     | 30           |
| hyperion.host             | hyperion服务地址 | 0.0.0.0      |
| hyperion.http_port        | hyperion服务端口 | 9996         |

6. 使用在配置文件中填入的数据库账号及密码登入数据库，并根据配置中设置的database.name创建新的数据库：

```mysql
CREATE DATABASE `hyperion` default character set utf8 collate utf8_general_ci;
```

7. 启动服务：

```shell
sh hyperion/service.sh start
```



## 服务管理

关闭服务

```shell
sh hyperion/service.sh stop
```

重启服务

```shell
sh hyperion/service.sh restart
```

服务状态查询

```shell
sh hyperion/service.sh status
```

