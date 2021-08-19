
create TABLE IF NOT EXISTS `t_federated_site_manager` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `site_name` varchar(128) DEFAULT NULL COMMENT 'site Name',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `secret_info` varchar(256) NOT NULL DEFAULT '' COMMENT 'site appkey,secret',
  `registration_link` text COMMENT 'federated registration link',
  `network_access_entrances` varchar(512) DEFAULT NULL COMMENT 'network access entrances',
  `network_access_exits` varchar(512) DEFAULT NULL COMMENT 'network access exits',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'site belongs to institutions',
  `component_version` text DEFAULT NULL COMMENT 'fate component version',
  `fate_version` varchar(32) DEFAULT NULL COMMENT 'fate version',
  `fate_serving_version` text DEFAULT NULL COMMENT 'fate serving version',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'site status,1 not joined,2 joined,3 removed',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  `activation_time` timestamp NULL DEFAULT NULL COMMENT 'activation_time',
  `detective_status` tinyint(4) DEFAULT '1' COMMENT 'site status when detect：1：not survie，2: survive',
  `last_detective_time` timestamp NULL DEFAULT NULL COMMENT 'detective time',
  `protocol` varchar(128) COMMENT 'https:// or http://',
  `network` varchar(256) not null COMMENT 'network of cloud manager',
  `encrypt_type` tinyint(4) not null COMMENT 'encrypt type of cloud manager: 1 yes, 2 no',
  `group_id` bigint(20) NOT NULL COMMENT 'group id',
  `exchange_id` BIGINT(20) NOT NULL DEFAULT '0' COMMENT 'exchange id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated SITE Manager';

create TABLE IF NOT EXISTS `t_federated_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `name` varchar(128) DEFAULT NULL COMMENT 'Organization Name',
  `institution` varchar(128) DEFAULT NULL COMMENT 'Institution Name',
  `status` tinyint(4) DEFAULT '1' COMMENT 'Federated Organization Status 1,valid 2,delete',
  `descriptions` varchar(256) DEFAULT NULL COMMENT 'Federated Organization Descriptions',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated Organization Table';

create TABLE IF NOT EXISTS `t_federated_ip_manager` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `case_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'case id ，每次改动唯一' COLLATE 'utf8_general_ci',
  `site_name` VARCHAR(128) NULL DEFAULT NULL COMMENT 'Organization Name' COLLATE 'utf8_general_ci',
  `group_id` INT(20) NULL DEFAULT NULL,
  `institutions` VARCHAR(128) NULL DEFAULT NULL COMMENT 'site belongs to institutions' COLLATE 'utf8_general_ci',
  `party_id` BIGINT(12) NULL DEFAULT NULL COMMENT 'party_id',
  `role` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'role,1:guest,2:host',
  `network_access_entrances` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access entrances' COLLATE 'utf8_general_ci',
  `network_access_entrances_old` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access entrances' COLLATE 'utf8_general_ci',
  `network_access_exits` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access exits' COLLATE 'utf8_general_ci',
  `network_access_exits_old` VARCHAR(256) NULL DEFAULT NULL COMMENT 'network access exits' COLLATE 'utf8_general_ci',
  `exchange_name` VARCHAR(256) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
  `exchange_name_old` VARCHAR(256) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
  `vip_entrance` VARCHAR(256) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
  `vip_entrance_old` VARCHAR(256) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
  `secure_status` TINYINT(4) NOT NULL DEFAULT '0',
  `secure_status_old` TINYINT(4) NOT NULL DEFAULT '0',
  `polling_status` TINYINT(4) NOT NULL DEFAULT '0',
  `polling_status_old` TINYINT(4) NOT NULL DEFAULT '0',
  `status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'log deal status,0:no deal,1:agreed,2:rejected,3:fm noSynced update,4:fm synced update',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated IP Manager';

create TABLE IF NOT EXISTS `t_federated_group_set` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'group id',
  `group_name` varchar(128) DEFAULT NULL COMMENT 'group name',
  `role` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'role, 1:guest, 2:host',
  `range_info` text COMMENT 'party id range',
  `total` bigint(20) DEFAULT NULL COMMENT 'total party id num',
  `used` bigint(20) DEFAULT '0' COMMENT 'used party id num',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'group status, 1:valid, 2:delete',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated Group Set';

create TABLE IF NOT EXISTS `t_federated_group_range_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'group detail id',
  `left_region` bigint(20) NOT NULL DEFAULT '0' COMMENT 'start index',
  `right_region` bigint(20) NOT NULL DEFAULT '0' COMMENT 'end index',
  `size` bigint(20) DEFAULT NULL COMMENT 'total party id num',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'group detail status, 1:valid, 2:delete',
  `use_tag` bigint(20) NOT NULL DEFAULT '0' COMMENT 'use_num,0:not use,use_tag use num',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  `group_id` bigint(20) NOT NULL COMMENT 'group id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated Group Detail';

create TABLE IF NOT EXISTS `t_federated_site_model` (
  `model_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'model id',
  `install_items` varchar(128) DEFAULT NULL COMMENT 'model name',
  `version` varchar(128) DEFAULT NULL COMMENT 'model version',
  `install_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'install time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Update Time',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'version status 1:current, 2:old',
  `update_status` tinyint(4) NOT NULL  COMMENT 'version update status 1:success, 2:failed',
  `detective_status` tinyint(4) DEFAULT '1' COMMENT 'model status when detect：1：not survive，2：survive',
  `last_detective_time` timestamp NULL DEFAULT NULL COMMENT 'detective time',
  `type` varchar(128) DEFAULT NULL COMMENT 'system type',
  `id` bigint(20) NOT NULL COMMENT 'site id',
   PRIMARY KEY (`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated Site model';


create table IF NOT EXISTS `t_federated_site_authority`(
  `authority_id` bigint(20) NOT NULL auto_increment COMMENT 'authority id',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'institution name',
  `authority_institutions` varchar(128) DEFAULT NULL COMMENT 'accessible institutions',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'authority status 1:pending, 2:approve, 3:reject, 4:cancel',
  `generation` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'the generation to work 1:current, 2:old',
  `sequence` bigint(20) NOT NULL  COMMENT 'authority apply sequence',
   PRIMARY KEY (`authority_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='authority information for fate site';


create table IF NOT EXISTS `t_federated_function`(
    `function_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'function id',
    `function_name` varchar(128) UNIQUE NOT NULL COMMENT 'function name',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'authority status 1:available, 2:unavailable',
    `descriptions` text  COMMENT 'descriptions for each function',
    `create_time` timestamp NOT NULL  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL  COMMENT 'Update Time',
     PRIMARY KEY (`function_id`)

)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='function information';


create table IF NOT EXISTS `t_cloud_manager_user`(
    `cloud_manager_id` bigint(20) not null auto_increment COMMENT 'primary key',
    `name` varchar(128) NOT NULL UNIQUE COMMENT 'user name for cloud manager',
    `admin_level` tinyint(4) NOT NULL DEFAULT '1' comment 'the level for user of cloud manager',
    `creator` varchar(128) NOT NULL COMMENT 'creator for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 joined, 2 deleted',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(`cloud_manager_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='cloud manager user information';


create table IF NOT EXISTS `t_cloud_manager_origin_user`(
    `cloud_manager_origin_id` bigint(20) not null auto_increment COMMENT 'primary key',
    `name` varchar(128) NOT NULL UNIQUE COMMENT 'user name for cloud manager',
    `password` varchar(256) NOT NULL COMMENT 'password for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 joined, 2 deleted',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(`cloud_manager_origin_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='cloud manager origin user information';


create table IF NOT EXISTS `t_fate_manager_user`(
    `fate_manager_id` varchar(50) NOT NULL UNIQUE COMMENT 'uuid key',
    `institutions` varchar(128) NOT NULL UNIQUE COMMENT 'institution name for fate manager',
    `secret_info` varchar(256) NOT NULL DEFAULT '' COMMENT 'site appkey,secret',
    `registration_link` text COMMENT 'federated registration link',
    `creator` varchar(128) NOT NULL COMMENT 'creator for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 not activated, 2 activated, 3 deleted',
    `protocol` varchar(128) NOT NULL  COMMENT 'https:// or http://',
    `network` varchar(256) not null COMMENT 'network of cloud manager',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='fate manager user information';

create table IF NOT EXISTS `t_job_statistics`(
    `site_guest_institutions` varchar(128) DEFAULT NULL COMMENT 'guest site belongs to institutions',
    `site_guest_name` varchar(128) DEFAULT NULL COMMENT 'guest site Name',
    `site_guest_id` bigint(20) not null COMMENT 'site id to launch the job',
    `site_host_institutions` varchar(128) DEFAULT NULL COMMENT 'host site belongs to institutions',
    `site_host_name` varchar(128) DEFAULT NULL COMMENT 'host site Name',
    `site_host_id` bigint(20) NOT NULL  COMMENT 'site id to cooperation',
    `job_success_count` bigint(20) not null COMMENT 'count of successful jobs',
    `job_failed_count` bigint(20) NOT NULL COMMENT 'count of failed jobs',
    `job_running_count` bigint(20) NOT NULL COMMENT 'count of running jobs',
    `job_waiting_count` bigint(20) NOT NULL COMMENT 'count of waiting jobs',
    `job_finish_date` date NOT NULL COMMENT 'type: day',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(site_guest_id,site_host_id,job_finish_date)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='jobs statistics for sites';


create TABLE IF NOT EXISTS `t_federated_exchange` (
  	`exchange_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  	`exchange_name` VARCHAR(128) NULL DEFAULT NULL COMMENT 'exchagne Name' COLLATE 'utf8_general_ci',
  	`vip_entrance` VARCHAR(512) NULL DEFAULT NULL COMMENT 'exchange vip entrance address' COLLATE 'utf8_general_ci',
  	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  	PRIMARY KEY (`exchange_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='Federated Exchange Manager';

create TABLE IF NOT EXISTS `t_roll_site` (
	`roll_site_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
	`network_access` VARCHAR(512) NULL DEFAULT NULL COMMENT 'network address' COLLATE 'utf8_general_ci',
	`network_access_exit` VARCHAR(512) NULL DEFAULT NULL COMMENT 'network address exit' COLLATE 'utf8_general_ci',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
	`exchange_id` BIGINT(20) NOT NULL COMMENT 'exchange id',
	PRIMARY KEY (`roll_site_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='roll site details';


create TABLE IF NOT EXISTS `t_party` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'party id',
	`party_id` VARCHAR(128) NULL DEFAULT NULL COMMENT 'party id' COLLATE 'utf8_general_ci',
	`network_access` VARCHAR(512) NULL DEFAULT NULL COMMENT 'party rollsite network address' COLLATE 'utf8_general_ci',
	`secure_status` TINYINT(4) NOT NULL DEFAULT '2' COMMENT 'secure status, 1 true, 2 false',
	`polling_status` TINYINT(4) NOT NULL DEFAULT '2' COMMENT 'polling status, 1 true, 2 false',
	`status` TINYINT(4) NOT NULL DEFAULT '1' COMMENT '1 publised,2 modified, 3 to be deleted',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
	`valid_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'the valid time of the real network property of rollsite',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `party_id` (`party_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='party id details';


CREATE TABLE IF NOT EXISTS `t_roll_site_party` (
	`roll_site_id` BIGINT(20) NOT NULL,
	`party_id` VARCHAR(128) NOT NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (`roll_site_id`, `party_id`) USING BTREE
) COLLATE='utf8_general_ci' ENGINE=InnoDB COMMENT='roll site id and party id associate';


create TABLE IF NOT EXISTS `t_product_version` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `product_name` varchar(128)  not NULL COMMENT 'procuct name',
  `product_version` varchar(128)  not NULL COMMENT 'product version',
  `image_name` varchar(128) DEFAULT NULL COMMENT 'image name',
  `image_download_url` varchar(512) DEFAULT NULL COMMENT 'url for download image',
  `package_name` varchar(128) DEFAULT NULL COMMENT 'package name',
  `package_download_url` varchar(512) DEFAULT NULL COMMENT 'url for download package',
  `kubernetes_chart` varchar(512) DEFAULT NULL COMMENT 'version for kubernetes',
  `public_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 public, 2 private',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  unique key `name_version` (`product_name`, `product_version`),
  PRIMARY KEY (product_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='product version management';


create TABLE IF NOT EXISTS `t_component_version` (
  `component_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `component_name` varchar(128)  not NULL COMMENT 'component name',
  `component_version` varchar(128)  not NULL COMMENT 'component version',
  `image_repository` varchar(128) DEFAULT NULL COMMENT 'image repository',
  `image_tag` varchar(128) DEFAULT NULL COMMENT 'image tag',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
  `product_id` bigint(20) NOT NULL COMMENT ' Product Primary Key',
  PRIMARY KEY (component_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='component version management';


CREATE TABLE IF NOT EXISTS `t_cloud_certificate_manager` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`type_id` BIGINT(20) NOT NULL COMMENT 'certificate type id',
	`validity` VARCHAR(50) NULL DEFAULT NULL COMMENT 'eg: 2021-01-01~2021-02-02' COLLATE 'utf8_general_ci',
	`institution` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`organization` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`site_authority` VARCHAR(50) NULL DEFAULT NULL COMMENT 'eg: 6666,7777,8888' COLLATE 'utf8_general_ci',
	`notes` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci',
	`serial_number` VARCHAR(100) NULL DEFAULT NULL COMMENT 'certificate unique id' COLLATE 'utf8_general_ci',
	`dns_name` VARCHAR(255) NULL DEFAULT NULL COMMENT 'domain name, eg: could.com,manager.net' COLLATE 'utf8_general_ci',
	`status` VARCHAR(50) NULL DEFAULT 'Unpublished' COMMENT '[Unpublished, Valid, Invalid, Revoked]' COLLATE 'utf8_general_ci',
	`create_date` DATETIME NULL DEFAULT NULL,
	`update_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='cloud manager certificate info';


CREATE TABLE IF NOT EXISTS `t_cloud_certificate_type` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`type_name` VARCHAR(50) NOT NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='cloud manager certificate type';

CREATE TABLE IF NOT EXISTS SPRING_SESSION (
	PRIMARY_ID CHAR(36) NOT NULL,
	SESSION_ID CHAR(36) NOT NULL,
	CREATION_TIME BIGINT NOT NULL,
	LAST_ACCESS_TIME BIGINT NOT NULL,
	MAX_INACTIVE_INTERVAL INT NOT NULL,
	EXPIRY_TIME BIGINT NOT NULL,
	PRINCIPAL_NAME VARCHAR(100),
	CONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (
	SESSION_PRIMARY_ID CHAR(36) NOT NULL,
	ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
	ATTRIBUTE_BYTES BLOB NOT NULL,
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
	CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
) ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

CREATE TABLE IF NOT EXISTS `t_fate_site_job` (
  `create_time` bigint(20) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_time` bigint(20) DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `institutions` varchar(128)   DEFAULT NULL,
  `party_id` int(11) NOT NULL DEFAULT '0',
  `site_name` varchar(50)   DEFAULT NULL,
  `role` varchar(50)   NOT NULL,
  `job_id` varchar(64)   NOT NULL DEFAULT '',
  `job_elapsed` bigint(20) DEFAULT NULL,
  `roles` longtext   NOT NULL,
  `other_party_id` longtext   NOT NULL,
  `other_institutions` longtext   NOT NULL,
  `job_type` varchar(50)   NOT NULL,
  `job_create_day` varchar(10)   DEFAULT NULL,
  `job_create_day_date` date DEFAULT NULL,
  `job_create_time` bigint(20) DEFAULT NULL,
  `job_start_time` bigint(20) DEFAULT NULL,
  `job_end_time` bigint(20) DEFAULT NULL,
  `status` varchar(50)   NOT NULL,
  `job_info` longtext ,
  PRIMARY KEY (`party_id`,`role`,`job_id`),
  KEY `fatesitejobinfo_party_id` (`party_id`),
  KEY `fatesitejobinfo_role` (`role`),
  KEY `fatesitejobinfo_job_type` (`job_type`),
  KEY `fatesitejobinfo_job_create_day` (`job_create_day`),
  KEY `fatesitejobinfo_job_create_day_date` (`job_create_day_date`),
  KEY `fatesitejobinfo_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8  COLLATE='utf8_general_ci' COMMENT='fate job statistics';

CREATE TABLE IF NOT EXISTS `t_fate_site_job_detail` (
	`detail_job_id` VARCHAR(64) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_institutions` VARCHAR(128) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_site_name` VARCHAR(128) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_party_id` INT(11) NOT NULL DEFAULT '0',
	`detail_role` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_status` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_job_type` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8_general_ci',
	`detail_job_create_day_date` DATE NOT NULL,
	`detail_create_time` TIMESTAMP NULL DEFAULT NULL,
	`detail_update_time` TIMESTAMP NULL DEFAULT NULL,
	PRIMARY KEY (`detail_job_id`, `detail_party_id`, `detail_role`) USING BTREE,
	INDEX `detail_job_id` (`detail_job_id`) USING BTREE,
	INDEX `detail_institutions` (`detail_institutions`) USING BTREE,
	INDEX `detail_site_name` (`detail_site_name`) USING BTREE,
	INDEX `detail_party_id` (`detail_party_id`) USING BTREE,
	INDEX `detail_role` (`detail_role`) USING BTREE,
	INDEX `detail_status` (`detail_status`) USING BTREE,
	INDEX `detail_job_create_day_date` (`detail_job_create_day_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci' COMMENT='fate job detail statistics';