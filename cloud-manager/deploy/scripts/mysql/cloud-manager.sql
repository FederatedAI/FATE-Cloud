###Cloud-Manager

CREATE TABLE IF NOT EXISTS `t_federated_site_manager` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `site_name` varchar(128) DEFAULT NULL COMMENT 'Organization Name',
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
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `activation_time` timestamp NULL DEFAULT NULL COMMENT 'activation_time',
  `detective_status` tinyint(4) DEFAULT '1' COMMENT 'site status when detect：1：not survie，2: survive',
  `last_detective_time` timestamp NULL DEFAULT NULL COMMENT 'detective time',
  `group_id` bigint(20) NOT NULL COMMENT 'group id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated SITE Manager';

CREATE TABLE IF NOT EXISTS `t_federated_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `name` varchar(128) DEFAULT NULL COMMENT 'Organization Name',
  `institution` varchar(128) DEFAULT NULL COMMENT 'Institution Name',
  `status` tinyint(4) DEFAULT '1' COMMENT 'Federated Organization Status 1,valid 2,delete',
  `descriptions` varchar(256) DEFAULT NULL COMMENT 'Federated Organization Descriptions',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Organization Table';

CREATE TABLE IF NOT EXISTS `t_federated_ip_manager` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `case_id` varchar(50) DEFAULT NULL COMMENT 'case id ，每次改动唯一',
  `site_name` varchar(128) DEFAULT NULL COMMENT 'Organization Name',
  `group_id` int(20) DEFAULT NULL,
  `institutions` varchar(128) DEFAULT NULL COMMENT 'site belongs to institutions',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'role,1:guest,2:host',
  `network_access_entrances` varchar(256) DEFAULT NULL COMMENT 'network access entrances',
  `network_access_entrances_old` varchar(256) DEFAULT NULL COMMENT 'network access entrances',
  `network_access_exits` varchar(256) DEFAULT NULL COMMENT 'network access exits',
  `network_access_exits_old` varchar(256) DEFAULT NULL COMMENT 'network access exits',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'log deal status,0:no deal,1:agreed,2:rejected',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated IP Manager';

CREATE TABLE IF NOT EXISTS `t_federated_group_set` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'group id',
  `group_name` varchar(128) DEFAULT NULL COMMENT 'group name',
  `role` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'role, 1:guest, 2:host',
  `range_info` text COMMENT 'party id range',
  `total` bigint(20) DEFAULT NULL COMMENT 'total party id num',
  `used` bigint(20) DEFAULT '0' COMMENT 'used party id num',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'group status, 1:valid, 2:delete',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Federated Group Set';

CREATE TABLE IF NOT EXISTS `t_federated_group_range_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'group detail id',
  `left_region` bigint(20) NOT NULL DEFAULT '0' COMMENT 'start index',
  `right_region` bigint(20) NOT NULL DEFAULT '0' COMMENT 'end index',
  `size` bigint(20) DEFAULT NULL COMMENT 'total party id num',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'group detail status, 1:valid, 2:delete',
  `use_tag` bigint(20) NOT NULL DEFAULT '0' COMMENT 'use_num,0:not use,use_tag use num',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `group_id` bigint(20) NOT NULL COMMENT 'group id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Group Detail';

CREATE TABLE IF NOT EXISTS `t_federated_site_model` (
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Site model';


create table IF NOT EXISTS `t_federated_site_authority`(
  `authority_id` bigint(20) NOT NULL auto_increment COMMENT 'authority id',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'institution name',
  `authority_institutions` varchar(128) DEFAULT NULL COMMENT 'accessible institutions',
  `create_time` timestamp NOT NULL  COMMENT 'Create Time',
  `update_time` timestamp NOT NULL  COMMENT 'Update Time',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'authority status 1:pending, 2:approve, 3:reject, 4:cancel',
  `generation` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'the generation to work 1:current, 2:old',
  `sequence` bigint(20) NOT NULL  COMMENT 'authority apply sequence',
   PRIMARY KEY (`authority_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='authority information for fate site';


create table IF NOT EXISTS `t_federated_function`(
    `function_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'function id',
    `function_name` varchar(128) UNIQUE NOT NULL COMMENT 'function name',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'authority status 1:available, 2:unavailable',
    `create_time` timestamp NOT NULL  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL  COMMENT 'Update Time',
     PRIMARY KEY (`function_id`)

)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='function information';


create table IF NOT EXISTS `t_cloud_manager_user`(
    `cloud_manager_id` bigint(20) not null auto_increment COMMENT 'primary key',
    `name` varchar(128) NOT NULL UNIQUE COMMENT 'user name for cloud manager',
    `admin_level` tinyint(4) NOT NULL DEFAULT '1' comment 'the level for user of cloud manager',
    `creator` varchar(128) NOT NULL COMMENT 'creator for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 joined, 2 deleted',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(`cloud_manager_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='cloud manager user information';


create table IF NOT EXISTS `t_cloud_manager_origin_user`(
    `cloud_manager_origin_id` bigint(20) not null auto_increment COMMENT 'primary key',
    `name` varchar(128) NOT NULL UNIQUE COMMENT 'user name for cloud manager',
    `password` varchar(256) NOT NULL COMMENT 'password for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 joined, 2 deleted',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(`cloud_manager_origin_id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='cloud manager origin user information';


create table IF NOT EXISTS `t_fate_manager_user`(
    `fate_manager_id` varchar(50) NOT NULL UNIQUE COMMENT 'uuid key',
    `institutions` varchar(128) NOT NULL UNIQUE COMMENT 'institution name for fate manager',
    `secret_info` varchar(256) NOT NULL DEFAULT '' COMMENT 'site appkey,secret',
    `registration_link` text COMMENT 'federated registration link',
    `creator` varchar(128) NOT NULL COMMENT 'creator for user',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1 not activated, 2 activated, 3 deleted',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='fate manager user information';

create table IF NOT EXISTS `t_job_statistics`(
    `site_guest_id` bigint(20) not null COMMENT 'site id to launch the job',
    `site_host_id` bigint(20) NOT NULL  COMMENT 'site id to cooperation',
    `job_success_count` bigint(20) not null COMMENT 'count of successful jobs',
    `job_failed_count` bigint(20) NOT NULL COMMENT 'count of failed jobs',
    `job_running_count` bigint(20) NOT NULL COMMENT 'count of running jobs',
    `job_finish_date` date NOT NULL COMMENT 'type: day',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT 'Create Time',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP COMMENT 'Update Time',
    PRIMARY KEY(site_guest_id,site_host_id,job_finish_date)

)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='jobs statistics for sites';


CREATE TABLE IF NOT EXISTS `t_federated_exchange` (
  `exchange_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `exchange_name` varchar(128) unique DEFAULT NULL COMMENT 'exchagne Name',
  `network_access` varchar(512) DEFAULT NULL COMMENT 'exchange network address',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`exchange_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Exchange Manager';

CREATE TABLE IF NOT EXISTS `t_exchange_details` (
  `exchange_details_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'exchange details id',
  `party_id` varchar(128) unique DEFAULT NULL COMMENT 'party id',
  `network_access` varchar(512) DEFAULT NULL COMMENT 'network address',
  `batch` bigint(20) NOT NULL  COMMENT 'insert-batch-number of route table',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `exchange_id` bigint(20) NOT NULL COMMENT 'exchange id',
  PRIMARY KEY (`exchange_details_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Exchange Detail';


CREATE TABLE IF NOT EXISTS `t_product_version` (
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
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  unique key `name_version` (`product_name`, `product_version`),
  PRIMARY KEY (product_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='product version management';


CREATE TABLE IF NOT EXISTS `t_component_version` (
  `component_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `component_name` varchar(128)  not NULL COMMENT 'component name',
  `component_version` varchar(128)  not NULL COMMENT 'component version',
  `image_repository` varchar(128) DEFAULT NULL COMMENT 'image repository',
  `image_tag` varchar(128) DEFAULT NULL COMMENT 'image tag',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  `product_id` bigint(20) NOT NULL COMMENT ' Product Primary Key',
  PRIMARY KEY (component_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='component version management';
