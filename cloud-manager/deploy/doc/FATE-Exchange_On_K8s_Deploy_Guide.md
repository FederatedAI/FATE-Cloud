# FATE-Exchange基于Kubernetes环境部署指引文档 ##

## 1. 准备 ##
请参考[FATE-Manager Deploy](../../../fate-manager/deploy/FATE-Manager_Deploy_Guide.md)部署Kubernetes和KubeFATE环境

## 2. 部署 ##

`ExChange`是中心端的一个路由模块，为了方便部署验证，保证逻辑上是分开的，物理上可以放在一起，通过kubefate部署方式将它部署在任意一个机器上，方便后面管理的最小化测试等功能的验证。

- **创建命名空间**
```
sudo kubectl create namespace fate-exchange
```
- **准备配置文件**
按当前目录下的配置文件cluster.yaml,,生成西的配置文件：fate-exchange.yaml，
```
[app@kube-m1 kubefate]$ cat fate-exchange.yaml 
name: fate-exchange
namespace: fate-exchange
chartName: fate
chartVersion: v1.4.4
partyId: 9998 
registry: ""
pullPolicy: 
persistence: false
istio:
  enabled: false
modules:
  - rollsite

rollsite: 
  type: NodePort
  nodePort: 30001
  partyList:
  - partyId: 9999
    partyIp: 172.16.153.131
    partyPort: 31001
  - partyId: 10000
    partyIp: 172.16.153.131
    partyPort: 31002
```
主要修改内容有：
删除其他模块，只保留rollsite，因为我们是部署exchange
registry:镜像下载地址，默认从dockerhub下载，如果有私有镜像可以配置在这里
更改rollsite模块的配置，设置监听的端口为30001；
更改partyList部分，这里的每个节点都带不一个路由节点，partyIp和partyPort代表每个站点的rollsite ip和端口

- **修改完毕，执行部署**
```
[app@kube-m1 kubefate]$ kubefate cluster install -f ./fate-exchange.yaml 
create job success, job id=19eaeec8-4e01-4e41-a7d0-9b0d2ac5e6c3
```
这个步骤需要去Docker Hub下载相关镜像，所以具体速度与服务器的网速有很大关系，如果网速快，或者镜像已经准备好在服务器上的话，大概2、3分钟可以部署完成。我们可以使用kubefate job ls命令观察部署情况：
```
                                    
[app@kube-m1 kubefate]$ kubefate job describe 19eaeec8-4e01-4e41-a7d0-9b0d2ac5e6c3
UUID            19eaeec8-4e01-4e41-a7d0-9b0d2ac5e6c3                                                                                                          
StartTime       2020-10-14 09:08:42                                                                                                                           
EndTime         2020-10-14 09:08:44                                                                                                                           
Duration        2s                                                                                                                                            
Status          Success                                                                                                                                       
Creator         admin                                                                                                                                         
ClusterId       027c65c1-8f7c-4ead-8da7-d9827c5bc86c                                                                                                          
Result          Cluster install success                                                                                                                       
SubJobs         rollsite             PodStatus: Running, SubJobStatus: Success, Duration:     2s, StartTime: 2020-10-14 09:08:42, EndTime: 2020-10-14 09:08:44
```
```         
[app@kube-m1 kubefate]$ kubefate cluster ls
UUID                                    NAME            NAMESPACE       REVISION        STATUS  CHART   ChartVERSION    AGE  
027c65c1-8f7c-4ead-8da7-d9827c5bc86c    fate=exchange        fate-exchange        1               Running fate    v1.4.4          2m29s
```
可以看到fate-exchange已经部署完成！
查看路由表配置信息：
```
[app@kube-m1 kubefate]$ sudo kubectl get configmap -n fate-exchange
NAME              DATA   AGE
eggroll-config    1      6m25s
rollsite-config   1      6m25s
```
修改路由表信息：
```
sudo kubectl edit configmap rollsite-config -n fate-exchange
```
至此，ExChange部署结束！