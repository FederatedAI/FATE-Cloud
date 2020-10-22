CREATE TABLE `t_fate_site_info` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT '联邦组织ID',
	`federated_organization` VARCHAR(128) NULL DEFAULT NULL COMMENT 'Federated Organization' COLLATE 'utf8_general_ci',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`site_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'cloud manger autocremetid',
	`site_name` VARCHAR(128) NULL DEFAULT NULL COMMENT 'Site Name' COLLATE 'utf8_general_ci',
	`institutions` VARCHAR(128) NULL DEFAULT NULL COMMENT 'site belongs to institutions' COLLATE 'utf8_general_ci',
	`role` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'role,1:guest,2:host',
	`app_key` VARCHAR(64) NULL DEFAULT NULL COMMENT 'Federation  Key' COLLATE 'utf8_general_ci',
	`app_secret` VARCHAR(64) NULL DEFAULT NULL COMMENT 'Federation  Secret' COLLATE 'utf8_general_ci',
	`registration_link` TEXT(65535) NULL DEFAULT NULL COMMENT 'registration_link' COLLATE 'utf8_general_ci',
	`network_access_entrances` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access entrances' COLLATE 'utf8_general_ci',
	`network_access_exits` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access exits' COLLATE 'utf8_general_ci',
	`fate_version` VARCHAR(10) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`fate_serving_version` VARCHAR(10) NULL DEFAULT NULL COMMENT 'fate serving version' COLLATE 'utf8_general_ci',
	`component_version` VARCHAR(256) NULL DEFAULT NULL COMMENT 'fate component version' COLLATE 'utf8_general_ci',
	`status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'site status,1 not joined,2 joined,3 removed',
	`edit_status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'edit status,-1 unkonwn,1 unedit,2 edit',
	`read_status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'read status,-1 unkonwn,3 read,1agreed ,2rejected',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`acativation_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'activation Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated Site Info'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_kubenetes_conf` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`kubenetes_url` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubenetes host domain' COLLATE 'utf8_general_ci',
	`current_port` TEXT(65535) NULL DEFAULT NULL COMMENT 'portlist' COLLATE 'utf8_general_ci',
	`node_list` TEXT(65535) NULL DEFAULT NULL COMMENT 'node list' COLLATE 'utf8_general_ci',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated kubenetes conf'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_federated_info` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`federation_id` BIGINT(20) NULL DEFAULT '0' COMMENT 'cloud-manager id',
	`federated_organization` VARCHAR(128) NULL DEFAULT NULL COMMENT 'Federated Organization' COLLATE 'utf8_general_ci',
	`institutions` VARCHAR(128) NULL DEFAULT NULL COMMENT 'site belongs to institutions' COLLATE 'utf8_general_ci',
	`federated_url` VARCHAR(256) NULL DEFAULT NULL COMMENT 'federated host' COLLATE 'utf8_general_ci',
	`status` TINYINT(4) NULL DEFAULT '0' COMMENT '0:unvalid,1valid',
	`size` INT(10) NULL DEFAULT '0' COMMENT 'federated size',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated Info'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_fate_version` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`fate_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`product_type` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT '0fate,1fate-serving',
	`chart_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'fate chart version' COLLATE 'utf8_general_ci',
	`version_index` INT(10) NULL DEFAULT NULL COMMENT 'fate verson index ,increase from 0',
	`pull_status` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT 'image pull state,0no pull,1pulled',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated fate version'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_deploy_site` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT 'federated id',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`product_type` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '0:fate,1fate-serving',
	`fate_version` VARCHAR(30) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`job_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate uuid' COLLATE 'utf8_general_ci',
	`name` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate name' COLLATE 'utf8_general_ci',
	`name_space` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate name space' COLLATE 'utf8_general_ci',
	`revision` INT(10) NULL DEFAULT NULL COMMENT 'kubefate revision',
	`deploy_status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed',
	`status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'site status after autotest,-1 unkonwn,1 stoped,2runing',
	`chart` VARCHAR(32) NULL DEFAULT NULL COMMENT 'chart name' COLLATE 'utf8_general_ci',
	`chart_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'chart version' COLLATE 'utf8_general_ci',
	`cluster_values` TEXT(65535) NULL DEFAULT NULL COMMENT 'cluster values' COLLATE 'utf8_general_ci',
	`cluster_info` TEXT(65535) NULL DEFAULT NULL COMMENT 'cluster info' COLLATE 'utf8_general_ci',
	`upgrade_status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'site upgrade status,-1 unkonwn,0 no,1yes',
	`version_index` INT(11) NOT NULL DEFAULT '-1' COMMENT 'fate version index',
	`fateboard` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate fateboard url' COLLATE 'utf8_general_ci',
	`cluster_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate cluster id' COLLATE 'utf8_general_ci',
	`single_test` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'single test,0 no test,1testing,2test ok,3test failed',
	`toy_test` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'toy test,0 no test,1testing,2test ok,3test failed',
	`toy_test_only` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'toy test only,0no test,1testing,2success,3failed',
	`toy_test_only_read` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'toy test only read ,0read,1unread',
	`duration` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT 'duration',
	`kubenetes_id` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT 'kubenetes id',
	`minimize_fast_test` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'minimize fast test,0 no test,1testing,2test ok,3test failed',
	`minimize_normal_test` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT 'minimize normal test,0 no test,1testing,2test ok,3test failed',
	`config` TEXT(65535) NULL DEFAULT NULL COMMENT 'install cofig' COLLATE 'utf8_general_ci',
	`proxy_port` INT(10) NULL DEFAULT NULL COMMENT 'yaml proxy port',
	`click_type` INT(10) NULL DEFAULT NULL COMMENT 'click type,1 connect success;2page start;3pull;4install;5finish',
	`is_valid` TINYINT(4) NULL DEFAULT '-1' COMMENT '-1 unkonw,0unvalid,1valid',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated site  deploy'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_deploy_prepare` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`prepare_title` VARCHAR(50) NULL DEFAULT NULL COMMENT 'deploy prepare title' COLLATE 'utf8_general_ci',
	`prepare_desc` VARCHAR(256) NULL DEFAULT NULL COMMENT 'deploy prepare description' COLLATE 'utf8_general_ci',
	`is_valid` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '0 unvalid,1valid',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated deploy prepare'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_deploy_job` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`job_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate uuid' COLLATE 'utf8_general_ci',
	`job_type` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'kube job type,-1 unkonwn,0 isntall,1 update,2delete',
	`creator` VARCHAR(30) NULL DEFAULT NULL COMMENT 'kube job creator' COLLATE 'utf8_general_ci',
	`status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'job status,-1 unkonwn,0success,1 failed',
	`start_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'Start Time',
	`end_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'End Time',
	`cluster_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'kubefate cluster id' COLLATE 'utf8_general_ci',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT 'federated id',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`result` VARCHAR(128) NULL DEFAULT NULL COMMENT 'result' COLLATE 'utf8_general_ci',
	`product_type` TINYINT(4) NULL DEFAULT NULL COMMENT 'product_type,1fate,2fate-serving',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated fate deploy job'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_deploy_component` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT 'federated id',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`site_name` VARCHAR(50) NULL DEFAULT NULL COMMENT 'site name' COLLATE 'utf8_general_ci',
	`product_type` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '0fate,1fate-serving',
	`job_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'jobid' COLLATE 'utf8_general_ci',
	`fate_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`component_name` VARCHAR(32) NULL DEFAULT NULL COMMENT 'component name' COLLATE 'utf8_general_ci',
	`component_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'component version' COLLATE 'utf8_general_ci',
	`address` VARCHAR(128) NULL DEFAULT NULL COMMENT 'service ip and port' COLLATE 'utf8_general_ci',
	`start_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'Start Time',
	`end_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'End Time',
	`duration` INT(10) NULL DEFAULT NULL COMMENT 'Duration',
	`version_index` INT(10) NULL DEFAULT NULL COMMENT 'version index,from 1',
	`deploy_status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed',
	`status` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT 'component status,-1 unkonwn,0 running,1stopped',
	`is_valid` TINYINT(4) NOT NULL DEFAULT '-1' COMMENT '0 unvalid,1valid',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated component deploy'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_component_version` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`fate_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`product_type` TINYINT(3) UNSIGNED NULL DEFAULT NULL COMMENT '0fate,1fate-serving',
	`component_name` VARCHAR(32) NULL DEFAULT NULL COMMENT 'component name' COLLATE 'utf8_general_ci',
	`image_id` VARCHAR(32) NULL DEFAULT NULL COMMENT 'docker image id' COLLATE 'utf8_general_ci',
	`image_name` VARCHAR(32) NULL DEFAULT NULL COMMENT 'docker image name' COLLATE 'utf8_general_ci',
	`image_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'docker image version' COLLATE 'utf8_general_ci',
	`image_tag` VARCHAR(32) NULL DEFAULT NULL COMMENT 'docker image tag' COLLATE 'utf8_general_ci',
	`image_description` VARCHAR(32) NULL DEFAULT NULL COMMENT 'docker image description' COLLATE 'utf8_general_ci',
	`image_size` VARCHAR(20) NULL DEFAULT NULL COMMENT 'image size' COLLATE 'utf8_general_ci',
	`image_create_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'image create Time',
	`component_version` VARCHAR(32) NULL DEFAULT NULL COMMENT 'component version' COLLATE 'utf8_general_ci',
	`version_index` INT(10) UNSIGNED NULL DEFAULT NULL COMMENT 'version index from 0',
	`pull_status` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT 'image pull state,0no pull,1pulled',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated component version'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_change_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`case_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT 'federated id',
	`federated_organization` VARCHAR(128) NULL DEFAULT NULL COMMENT 'Federated Organization' COLLATE 'utf8_general_ci',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`network_access_entrances` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access entrances' COLLATE 'utf8_general_ci',
	`network_access_exits` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access exits' COLLATE 'utf8_general_ci',
	`status` TINYINT(4) NULL DEFAULT '0' COMMENT '0:no deal ,1deal',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated Change Log'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;

CREATE TABLE `t_fate_auto_test` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`federated_id` INT(10) NULL DEFAULT NULL COMMENT 'federated id',
	`party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
	`product_type` TINYINT(4) UNSIGNED NULL DEFAULT NULL COMMENT '0:fate,1fate-serving',
	`fate_version` VARCHAR(30) NULL DEFAULT NULL COMMENT 'fate version' COLLATE 'utf8_general_ci',
	`test_item` VARCHAR(32) NULL DEFAULT NULL COMMENT 'Test Item' COLLATE 'utf8_general_ci',
	`start_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'Start Time',
	`end_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'End Time',
	`status` TINYINT(4) NULL DEFAULT NULL COMMENT '0 not test,1testing,2 test yes,3test failed',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Federated Auto Test'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
----------0.2 end ----------
----------0.3 start ---------