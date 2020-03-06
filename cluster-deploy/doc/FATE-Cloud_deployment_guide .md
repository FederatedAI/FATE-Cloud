# FATE Cloud Deploy Guide #

1.First to know
============

FATE-Cloud is an Infrastructure for Building and Managing Federated Data Collaboration Network. FATE-Cloud enables FATE to be managed in multi-cloud, forming a secure federated data network. It is deployed on the premise that a complete set of FATE services already exist.The following FATE Cloud deployment configuration requires the FATE deployment information.

2.Module Information
============
A separate Cloud manager service and FATE Board service containing the FATE Manager plug-in for each site.

|               Module name                | Port | Description                                        |
| :--------------------------------------: | ---- | -------------------------------------------------- |
|              Cloud Manager               | 9999 | Only one for a FATE Cloud system.                  |
| FATE Board(integrated with FATE Manager) | 8080 | Each site (which equals the role in FATE) has one. |

3.Server Configurations
============

|    Server     |                                                              |
| :-----------: | ------------------------------------------------------------ |
|    Number     | 1                                                            |
| configuration | 8 core /16GB memory / 500GB disk/10M brandwith               |
|    System     | CentOS linux 7.2 and above / Ubuntu 16.04 and above          |
|     User      | User：app，Group：apps（app should use command "sudo su root" without password） |
|  File system  | 1.  500G disk mount on the directory "/data"   2.  create directory "/data/projects"， it belongs to app:apps |

4.Environments
==============

4.1 Shutdown selinux (optional)
---------------

**Execute on target server（example:192.168.0.1）by root：**

Confirm that selinux exist first:

```
# centos：
rpm -qa | grep selinux

# ubuntu：
apt list --installed | grep selinux
```

If selinux exit, then : 

```
setenforce 0
```



4.2 Change the max file-open number of Linux
---------------------------

**Execute on target server（example:192.168.0.1）by root：**

```
vim /etc/security/limits.conf

* soft nofile 65536

* hard nofile 65536
```



4.3 Shutdown Firewall(optional)
--------------

**Execute on target server（example:192.168.0.1）by root：**

```
Centos：

systemctl disable firewalld.service

systemctl stop firewalld.service

systemctl status firewalld.service


Ubuntu：

ufw disable

ufw status
```



4.4  Software Environments Initialization
------------------

**1）Create User**

**Execute on target server（example:192.168.0.1）by root：**

```
groupadd -g 6000 apps
useradd -s /bin/bash -g apps -d /home/app app
passwd app
```

**1）Configure sudo**

**Execute on target server（example:192.168.0.1）by root：**

```
vim /etc/sudoers.d/app

app ALL=(ALL) ALL

app ALL=(ALL) NOPASSWD: ALL

Defaults !env_reset
```

**2）Configure ssh (login without passwords)**

Target Server should login all the machines which would deploy servers by ssh without passwords.  Please refer steps blow,（192.168.0.1 192.168.0.2）as example.



**a. Execute on target server（example:192.168.0.1）by app：**

```
su app

ssh-keygen -t rsa

cat ~/.ssh/id_rsa.pub >> /home/app/.ssh/authorized_keys

chmod 600 ~/.ssh/authorized_keys
```



**b.Integrate  id_rsa.pub**

**Execute on target server（example:192.168.0.1）by app：**

```
scp ~/.ssh/authorized_keys app\@192.168.0.2:/home/app/.ssh
```

**Execute on target server（example:192.168.0.2）by app：**

```
cat ~/.ssh/id_rsa.pub >> /home/app/.ssh/authorized_keys

scp ~/.ssh/authorized_keys app\@192.168.0.1:/home/app/.ssh
```

**c. Test on （192.168.0.1 192.168.0.2）by app**

```
ssh app\@192.168.0.1

ssh app\@192.168.0.2
```



5.Deploy
==========

Notes: the default install directory is /data/projects/, user is app.   You could change as your own requirements.

5.1 Pull Code
------------

**Execute on target server with Internet（example:192.168.0.1）by app：**:

**Notes： Git and maven 3.5+ is required on the server.**

```
cd /data/projects/
git clone https://github.com/FederatedAI/FATE-Cloud.git
```

5.2 Configurations
----------------

**Execute on target server（example:192.168.0.1）by app：**

Fill the default-configurations.sh in FATE-Cloud/cluster-deploy/scripts/deploy/.

Explains for default-configurations.sh：

| Item                  | Mean                                                         | Default-Value | Explains                                                     |
| :-------------------- | :----------------------------------------------------------- | :------------ | :----------------------------------------------------------- |
| system_user           | user for operation                                           | app           | use default value                                            |
| install_dir           | install directory for Fate Cloud                             | none          | customize                                                    |
| java_dir              | path for java environments                                   | none          | java path used in FATE system                                |
| cloud_ip              | ip for Cloud Manager to deploy                               | none          | customize                                                    |
| cloud_port            | Cloud Manager服务的端口                                      | 9999          | enable customize, but should avoid port conflicts            |
| cloud_db_ip           | ip of database which Cloud Manager would connect             | none          | mysql should work well in this ip                            |
| cloud_db_name         | name of database which Cloud Manager  would create           | cloud_manager | use default value                                            |
| cloud_db_user         | user of database which Cloud Manager  would use to connect   | none          | use root, because it need to create other users and authority |
| cloud_db_password     | password of database which Cloud Manager would use to connect | none          | cloud_db_user and cloud_db_password should match             |
| cloud_db_dir          | install directory of database which Cloud Manager would use to connect | none          | the path is used to execute command to build mysql environments |
| fateboard_branch      | branch of FATE Board                                         | develop-1.2.1 | use default value, other branches may not support            |
| fateboard_db_dir      | install directory of database which Cloud Manager would connect | none          | the path is used to execute command to build mysql environments |
| fateboard_db_ip       | ip of database which FATE Board  would connect               | none          | ip should lie in FATE                                        |
| fateboard_db_name     | name of database which FATE Board  would connect             | fate_flow     | use default value                                            |
| fateboard_db_user     | user of database which FATE Board  would connect             | none          | use root, because it need to create other users and authority |
| fateboard_db_password | password of database which FATE Board  would connect         | none          | fateboard_db_user and fateboard_db_password should match     |
| fateboard_ip          | ip for FATE Board to deploy                                  | none          | ip should lie in FATE, if there are many, the sequence should be same with fate_flow_ip it matches |
| fateboard_port        | port of FATE Board                                           | 8080          | enable customize, but should avoid port conflicts            |
| fate_flow_ip          | ip of FATE Flow which FATE Board would connect               | none          | ip should lie in FATE, if there are many, the sequence should be same with fateboard_ip it matches |
| fate_flow_port        | port of FATE Flow  which FATE Board would connect            | 9380          | use default value                                            |
| fate_path             | install directory of FATE                                    | none          | directory where FATE lies in                                 |

**Configurations Example**

Situation:

​	There are 2 machines（192.168.0.1 192.168.0.2）which are deployed a FATE cluster. And 192.168.0.1 is host, 192.168.0.2 is guest.  Cloud Manager in FATE Cloud is deployed in host.Fate Manager is integrated in FATE Board, and it would be deployed in both guest and host (note: shutdown the FATE board service already exist in machines, or change the FATE Board port to avid conflicts).  You also could deploy Cloud Manager in another seperate machine, just make sure that machine has right java and mysql environment.  

```
system_user=app
install_dir=/data/projects/fate-cloud
java_dir=/data/projects/common/jdk/jdk1.8.0_192
#cloud-manager
cloud_ip=192.168.0.1a
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

5.3 Deploy
--------



After finished the default-configurations.sh, execute follow commands.

```
cd FATE-Cloud/cluster-deploy/scripts/deploy
```

If deploy all modules：

```
sh deploy.sh  all install
```

If deploy single module(optional：cloud-manager,fate-manager)：

```
sh deploy.sh  cloud-manager install
```

6.Configurations Check
==========

Check the configurations of modules. The configuration document lies in directory: install_dir/${module}/conf.

7.Manage Service
================

7.1 Start Service
------------

**Execute on target server（example:192.168.0.1）by app：**

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh start
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh start
```

7.2 Check Service status
----------------

**Execute on target server（example:192.168.0.1）by app：**

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh status
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh status
```

7.3 Stop Service
------------

**Execute on target server（example:192.168.0.1）by app：**

```
cd /data/projects/FATE-Cloud/cloud-manager
sh service.sh stop
cd /data/projects/FATE-Cloud/fate-manager
sh service.sh stop
```



8.Test
======

8.1 Cloud Manager testing
----------------------

Cloud Manager is a web service. If it run successfully , it could be visited by http://192.168.0.1:8080.

Notes: 

The port is able to pass the  firewall.

Replace the 192.168.0.1 with you actually ip.



8.2 FATE Manager testing
----------------------

FATE Manager is integrated in FATE Board which is also a web service. If it runs successfully , it could be visited by http://192.168.0.1:8080,  http://192.168.0.2:8080/fateManagerIndex.

Notes: 

The port is able to pass the  firewall.

Replace the 192.168.0.1,  192.168.0.2 with you actually ip.

