CREATE DATABASE IF NOT EXISTS `fate_manager`;
USE `fate_manager`;
CREATE TABLE IF NOT EXISTS `t_fate_account_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `user_id` varchar(50) DEFAULT NULL COMMENT 'user id',
  `user_name` varchar(50) DEFAULT NULL COMMENT 'user name',
  `institutions` varchar(50) DEFAULT NULL COMMENT 'institutions',
  `app_key` varchar(50) DEFAULT NULL COMMENT 'app key',
  `app_secret` varchar(50) DEFAULT NULL COMMENT 'app secret',
  `cloud_user_id` varchar(50) DEFAULT NULL COMMENT 'cloud user id',
  `role` int(10) DEFAULT NULL COMMENT 'role',
  `account_active_url` text COMMENT 'cloud manger account active url',
  `creator` varchar(32) DEFAULT NULL COMMENT 'creator',
  `party_id` int(10) DEFAULT NULL COMMENT 'party id',
  `site_name` varchar(256) DEFAULT NULL,
  `block_msg` varchar(256) DEFAULT NULL COMMENT 'function block',
  `permission_list` varchar(256) DEFAULT NULL COMMENT 'permission list',
  `allow_instituions` text COMMENT 'allow other fate manager to apply',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'user status 0 unvalid，1valid',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_account_info';

CREATE TABLE IF NOT EXISTS `t_fate_apply_site_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `institutions` varchar(256) DEFAULT NULL COMMENT 'institutions',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'user status 0 unvalid，1valid',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_apply_site_info';

CREATE TABLE IF NOT EXISTS `t_fate_auto_test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `federated_id` int(10) DEFAULT NULL COMMENT 'federated id',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `product_type` tinyint(4) unsigned DEFAULT NULL COMMENT '0:fate,1fate-serving',
  `fate_version` varchar(30) DEFAULT NULL COMMENT 'fate version',
  `test_item` varchar(32) DEFAULT NULL COMMENT 'Test Item',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'Start Time',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'End Time',
  `status` tinyint(4) DEFAULT NULL COMMENT '0 not test,1testing,2 test yes,3test failed',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Auto Test';

CREATE TABLE IF NOT EXISTS `t_fate_change_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `case_id` varchar(50) DEFAULT NULL,
  `federated_id` int(10) DEFAULT NULL COMMENT 'federated id',
  `federated_organization` varchar(128) DEFAULT NULL COMMENT 'Federated Organization',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `network_access_entrances` varchar(256) DEFAULT NULL COMMENT 'network access entrances',
  `network_access_exits` varchar(256) DEFAULT NULL COMMENT 'network access exits',
  `status` tinyint(4) DEFAULT '0' COMMENT '0:no deal ,1deal',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Change Log';

CREATE TABLE IF NOT EXISTS `t_fate_component_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `fate_version` varchar(32) DEFAULT NULL COMMENT 'fate version',
  `product_type` tinyint(3) unsigned DEFAULT NULL COMMENT '0fate,1fate-serving',
  `component_name` varchar(32) DEFAULT NULL COMMENT 'component name',
  `image_id` varchar(32) DEFAULT NULL COMMENT 'docker image id',
  `image_name` varchar(32) DEFAULT NULL COMMENT 'docker image name',
  `image_version` varchar(32) DEFAULT NULL COMMENT 'docker image version',
  `image_tag` varchar(32) DEFAULT NULL COMMENT 'docker image tag',
  `image_description` varchar(32) DEFAULT NULL COMMENT 'docker image description',
  `image_size` varchar(20) DEFAULT NULL COMMENT 'image size',
  `image_create_time` timestamp NULL DEFAULT NULL COMMENT 'image create Time',
  `component_version` varchar(32) DEFAULT NULL COMMENT 'component version',
  `version_index` int(10) unsigned DEFAULT NULL COMMENT 'version index from 0',
  `pull_status` tinyint(4) unsigned DEFAULT NULL COMMENT 'image pull state,0no pull,1pulled',
  `package_status` tinyint(4) unsigned DEFAULT NULL COMMENT 'download status,0no,1yes',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated component version';

INSERT INTO `t_fate_component_version` (`id`, `fate_version`, `product_type`, `component_name`, `image_id`, `image_name`, `image_version`, `image_tag`, `image_description`, `image_size`, `image_create_time`, `component_version`, `version_index`, `pull_status`, `package_status`, `create_time`, `update_time`) VALUES
	(43, '1.4.3', 1, 'clustermanager', '146f656d2f63', 'eggroll', '1.4.3-release', '1.4.3-release', '1.4.3-release', '4.77GB', '2020-07-23 22:47:15', '1.4.3', 143, 1, 1, '2020-07-23 22:41:51', '2021-01-06 17:00:17'),
	(44, '1.4.3', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-11-26 18:49:10'),
	(45, '1.4.3', 1, 'nodemanager', '146f656d2f63', 'eggroll', '1.4.3-release', '1.4.3-release', '1.4.3-release', '4.77GB', '2020-07-23 22:47:16', '1.4.3', 143, 1, 1, '2020-07-23 22:42:02', '2021-01-06 17:00:21'),
	(46, '1.4.3', 1, 'fateflow', 'e5d0bd448e63', 'python', '1.4.3-release', '1.4.3-release', '1.4.3-release', '4.5GB', '2020-07-23 22:47:17', '1.4.3', 143, 1, 1, '2020-07-23 22:42:08', '2021-01-06 17:00:19'),
	(47, '1.4.3', 1, 'rollsite', '146f656d2f63', 'eggroll', '1.4.3-release', '1.4.3-release', '1.4.3-release', '4.77GB', '2020-07-23 22:47:17', '1.4.3', 143, 1, 1, '2020-07-23 22:42:15', '2021-01-06 17:00:22'),
	(48, '1.4.3', 1, 'fateboard', 'e5d0bd448e63', 'python', '1.4.3-release', '1.4.3-release', '1.4.3-release', '4.5GB', '2020-07-23 22:47:17', '1.4.3', 143, 1, 1, '2020-09-10 18:29:58', '2021-01-06 17:00:18'),
	(49, '1.4.4', 1, 'clustermanager', 'd2ec04673a5f', 'eggroll', '1.4.4-release', '1.4.4-release', '1.4.4-release', '4.77GB', '2020-07-23 22:47:15', '1.4.4', 144, 1, 1, '2020-07-23 22:41:51', '2021-01-06 17:00:43'),
	(50, '1.4.4', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-11-26 18:49:15'),
	(51, '1.4.4', 1, 'nodemanager', 'd2ec04673a5f', 'eggroll', '1.4.4-release', '1.4.4-release', '1.4.4-release', '4.77GB', '2020-07-23 22:47:16', '1.4.4', 144, 1, 1, '2020-07-23 22:42:02', '2021-01-06 17:00:38'),
	(52, '1.4.4', 1, 'fateflow', 'd6c72382bdae', 'python', '1.4.4-release', '1.4.4-release', '1.4.4-release', '4.5GB', '2020-07-23 22:47:17', '1.4.4', 144, 1, 1, '2020-07-23 22:42:08', '2021-01-06 17:00:39'),
	(53, '1.4.4', 1, 'rollsite', 'd2ec04673a5f', 'eggroll', '1.4.4-release', '1.4.4-release', '1.4.4-release', '4.77GB', '2020-07-23 22:47:17', '1.4.4', 144, 1, 1, '2020-07-23 22:42:15', '2021-01-06 17:00:36'),
	(54, '1.4.4', 1, 'fateboard', 'd6c72382bdae', 'python', '1.4.4-release', '1.4.4-release', '1.4.4-release', '4.5GB', '2020-07-23 22:47:17', '1.4.4', 144, 1, 1, '2020-09-10 18:29:58', '2021-01-06 17:00:41'),
	(55, '1.4.5', 1, 'clustermanager', 'd2ec04673a5f', 'eggroll', '1.4.5-release', '1.4.5-release', '1.4.5-release', '4.77GB', '2020-07-23 22:47:15', '1.4.5', 145, 1, 1, '2020-07-23 22:41:51', '2020-12-15 16:42:16'),
	(56, '1.4.5', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-12-15 16:42:16'),
	(57, '1.4.5', 1, 'nodemanager', 'd2ec04673a5f', 'eggroll', '1.4.5-release', '1.4.5-release', '1.4.5-release', '4.77GB', '2020-07-23 22:47:16', '1.4.5', 145, 1, 1, '2020-07-23 22:42:02', '2020-12-15 16:42:16'),
	(58, '1.4.5', 1, 'fateflow', 'd6c72382bdae', 'python', '1.4.5-release', '1.4.5-release', '1.4.5-release', '4.5GB', '2020-07-23 22:47:17', '1.4.5', 145, 1, 1, '2020-07-23 22:42:08', '2020-12-15 16:42:16'),
	(59, '1.4.5', 1, 'rollsite', 'd2ec04673a5f', 'eggroll', '1.4.5-release', '1.4.5-release', '1.4.5-release', '4.77GB', '2020-07-23 22:47:17', '1.4.5', 145, 1, 1, '2020-07-23 22:42:15', '2020-12-15 16:42:16'),
	(60, '1.4.5', 1, 'fateboard', 'd6c72382bdae', 'python', '1.4.5-release', '1.4.5-release', '1.4.5-release', '4.5GB', '2020-07-23 22:47:17', '1.4.5', 145, 1, 1, '2020-09-10 18:29:58', '2020-12-15 16:42:16'),
	(61, '1.4.0', 1, 'clustermanager', 'd2ec04673a5f', 'eggroll', '1.4.0-release', '1.4.0-release', '1.4.0-release', '4.77GB', '2020-07-23 22:47:15', '1.4.0', 140, 1, 1, '2020-07-23 22:41:51', '2020-12-15 16:42:16'),
	(62, '1.4.0', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-12-15 16:42:16'),
	(63, '1.4.0', 1, 'nodemanager', 'd2ec04673a5f', 'eggroll', '1.4.0-release', '1.4.0-release', '1.4.0-release', '4.77GB', '2020-07-23 22:47:16', '1.4.0', 140, 1, 1, '2020-07-23 22:42:02', '2020-12-15 16:42:16'),
	(64, '1.4.0', 1, 'fateflow', 'd6c72382bdae', 'python', '1.4.0-release', '1.4.0-release', '1.4.0-release', '4.5GB', '2020-07-23 22:47:17', '1.4.0', 140, 1, 1, '2020-07-23 22:42:08', '2020-12-15 16:42:16'),
	(65, '1.4.0', 1, 'rollsite', 'd2ec04673a5f', 'eggroll', '1.4.0-release', '1.4.0-release', '1.4.0-release', '4.77GB', '2020-07-23 22:47:17', '1.4.0', 140, 1, 1, '2020-07-23 22:42:15', '2020-12-15 16:42:16'),
	(66, '1.4.0', 1, 'fateboard', 'd6c72382bdae', 'python', '1.4.0-release', '1.4.0-release', '1.4.0-release', '4.5GB', '2020-07-23 22:47:17', '1.4.0', 140, 1, 1, '2020-09-10 18:29:58', '2020-12-15 16:42:16'),
                (67, '1.5.0', 1, 'clustermanager', 'd2ec04673a5f', 'eggroll', '1.5.0-release', '1.5.0-release', '1.5.0-release', '4.77GB', '2020-07-23 22:47:15', '1.5.0', 150, 1, 1, '2020-07-23 22:41:51', '2020-12-15 16:42:16'),
	(68, '1.5.0', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-12-15 16:42:16'),
	(69, '1.5.0', 1, 'nodemanager', 'd2ec04673a5f', 'eggroll', '1.5.0-release', '1.5.0-release', '1.5.0-release', '4.77GB', '2020-07-23 22:47:16', '1.5.0', 150, 1, 1, '2020-07-23 22:42:02', '2020-12-15 16:42:16'),
	(70, '1.5.0', 1, 'fateflow', 'd6c72382bdae', 'python', '1.5.0-release', '1.5.0-release', '1.5.0-release', '4.5GB', '2020-07-23 22:47:17', '1.5.0', 150, 1, 1, '2020-07-23 22:42:08', '2020-12-15 16:42:16'),
	(71, '1.5.0', 1, 'rollsite', 'd2ec04673a5f', 'eggroll', '1.5.0-release', '1.5.0-release', '1.5.0-release', '4.77GB', '2020-07-23 22:47:17', '1.5.0', 150, 1, 1, '2020-07-23 22:42:15', '2020-12-15 16:42:16'),
	(72, '1.5.0', 1, 'fateboard', 'd6c72382bdae', 'python', '1.5.0-release', '1.5.0-release', '1.5.0-release', '4.5GB', '2020-07-23 22:47:17', '1.5.0', 150, 1, 1, '2020-09-10 18:29:58', '2020-12-15 16:42:16'),
	(73, '1.5.1', 1, 'clustermanager', 'd2ec04673a5f', 'eggroll', '1.5.1-release', '1.5.1-release', '1.5.1-release', '4.77GB', '2020-07-23 22:47:15', '1.5.1', 151, 1, 1, '2020-07-23 22:41:51', '2020-12-15 16:42:16'),
	(74, '1.5.1', 1, 'mysql', '0d64f46acfd1', 'mysql', '8', '8', '8', '544MB', '2020-07-23 22:47:16', '8', 8, 1, 1, '2020-07-23 22:41:57', '2020-12-15 16:42:16'),
	(75, '1.5.1', 1, 'nodemanager', 'd2ec04673a5f', 'eggroll', '1.5.1-release', '1.5.1-release', '1.5.1-release', '4.77GB', '2020-07-23 22:47:16', '1.5.1', 151, 1, 1, '2020-07-23 22:42:02', '2020-12-15 16:42:16'),
	(76, '1.5.1', 1, 'fateflow', 'd6c72382bdae', 'python', '1.5.1-release', '1.5.1-release', '1.5.1-release', '4.5GB', '2020-07-23 22:47:17', '1.5.1', 151, 1, 1, '2020-07-23 22:42:08', '2020-12-15 16:42:16'),
	(77, '1.5.1', 1, 'rollsite', 'd2ec04673a5f', 'eggroll', '1.5.1-release', '1.5.1-release', '1.5.1-release', '4.77GB', '2020-07-23 22:47:17', '1.5.1', 151, 1, 1, '2020-07-23 22:42:15', '2020-12-15 16:42:16'),
	(78, '1.5.1', 1, 'fateboard', 'd6c72382bdae', 'python', '1.5.1-release', '1.5.1-release', '1.5.1-release', '4.5GB', '2020-07-23 22:47:17', '1.5.1', 151, 1, 1, '2020-09-10 18:29:58', '2020-12-15 16:42:16');

CREATE TABLE IF NOT EXISTS `t_fate_deploy_component` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `federated_id` int(10) DEFAULT NULL COMMENT 'federated id',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `site_name` varchar(50) DEFAULT NULL COMMENT 'site name',
  `product_type` tinyint(4) unsigned DEFAULT NULL COMMENT '0fate,1fate-serving',
  `job_id` varchar(50) DEFAULT NULL COMMENT 'jobid',
  `fate_version` varchar(32) DEFAULT NULL COMMENT 'fate version',
  `component_name` varchar(32) DEFAULT NULL COMMENT 'component name',
  `component_version` varchar(32) DEFAULT NULL COMMENT 'component version',
  `address` varchar(128) DEFAULT NULL COMMENT 'service ip and port',
  `label` varchar(128) DEFAULT NULL COMMENT 'node label',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'Start Time',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'End Time',
  `duration` int(10) DEFAULT NULL COMMENT 'Duration',
  `version_index` int(10) DEFAULT NULL COMMENT 'version index,from 1',
  `deploy_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed',
  `deploy_type` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'deploy type ,1k8s,2ansible',
  `status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'component status,-1 unkonwn,0 running,1stopped',
  `is_valid` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '0 unvalid,1valid',
  `finish_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `party_id` (`party_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated component deploy';

CREATE TABLE IF NOT EXISTS `t_fate_deploy_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `job_id` varchar(50) DEFAULT NULL COMMENT 'kubefate uuid',
  `job_type` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'kube job type,-1 unkonwn,0 isntall,1 update,2delete',
  `creator` varchar(30) DEFAULT NULL COMMENT 'kube job creator',
  `status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'job status,-1 unkonwn,0success,1 failed',
  `start_time` timestamp NULL DEFAULT NULL COMMENT 'Start Time',
  `end_time` timestamp NULL DEFAULT NULL COMMENT 'End Time',
  `cluster_id` varchar(50) DEFAULT NULL COMMENT 'kubefate cluster id',
  `federated_id` int(10) DEFAULT NULL COMMENT 'federated id',
  `deploy_type` int(10) DEFAULT NULL COMMENT 'deploy type ,1k8s,2ansible',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `result` varchar(128) DEFAULT NULL COMMENT 'result',
  `product_type` tinyint(4) DEFAULT NULL COMMENT 'product_type,1fate,2fate-serving',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated fate deploy job';

CREATE TABLE IF NOT EXISTS `t_fate_deploy_prepare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `prepare_title` varchar(50) DEFAULT NULL COMMENT 'deploy prepare title',
  `prepare_desc` varchar(256) DEFAULT NULL COMMENT 'deploy prepare description',
  `is_valid` tinyint(4) unsigned DEFAULT NULL COMMENT '0 unvalid,1valid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated deploy prepare';

INSERT INTO `t_fate_deploy_prepare` (`id`, `prepare_title`, `prepare_desc`, `is_valid`, `create_time`, `update_time`) VALUES
	(1, 'kubenetes', 'v1.9+', 1, '2020-06-17 19:36:49', '2020-06-17 19:36:49'),
	(2, 'python', 'v2.7+', 1, '2020-06-17 19:37:13', '2020-06-17 19:37:16'),
	(3, 'mysql', 'v5.7+', 1, '2020-06-17 19:37:52', '2020-06-17 19:37:57'),
	(4, 'helm', 'v3.0+', 1, '2020-07-23 22:29:27', '2020-07-23 22:29:29');

CREATE TABLE IF NOT EXISTS `t_fate_deploy_site` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `federated_id` int(10) DEFAULT NULL COMMENT 'federated id',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `product_type` tinyint(4) unsigned DEFAULT NULL COMMENT '0:fate,1fate-serving',
  `fate_version` varchar(30) DEFAULT NULL COMMENT 'fate version',
  `job_id` varchar(50) DEFAULT NULL COMMENT 'kubefate uuid',
  `name` varchar(50) DEFAULT NULL COMMENT 'kubefate name',
  `name_space` varchar(50) DEFAULT NULL COMMENT 'kubefate name space',
  `revision` int(10) DEFAULT NULL COMMENT 'kubefate revision',
  `deploy_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'deploy status,-1 unkonwn,0 success,1 In pulling image,2 Image Pulled,3 image pulled failed,4Under installation,5Image installed,6Image installed failed,7In testing,8 test passed,9test failed',
  `status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'site status after autotest,-1 unkonwn,1 stoped,2runing',
  `chart` varchar(32) DEFAULT NULL COMMENT 'chart name',
  `chart_version` varchar(32) DEFAULT NULL COMMENT 'chart version',
  `cluster_values` mediumtext COMMENT 'cluster values',
  `cluster_info` mediumtext COMMENT 'cluster info',
  `upgrade_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'site upgrade status,-1 unkonwn,0 no,1yes',
  `version_index` int(11) NOT NULL DEFAULT '-1' COMMENT 'fate version index',
  `fateboard` varchar(50) DEFAULT NULL COMMENT 'kubefate fateboard url',
  `cluster_id` varchar(50) DEFAULT NULL COMMENT 'kubefate cluster id',
  `single_test` tinyint(3) unsigned DEFAULT NULL COMMENT 'single test,0 no test,1testing,2test ok,3test failed',
  `toy_test` tinyint(3) unsigned DEFAULT NULL COMMENT 'toy test,0 no test,1testing,2test ok,3test failed',
  `toy_test_only` tinyint(3) unsigned DEFAULT NULL COMMENT 'toy test only,0no test,1testing,2success,3failed',
  `toy_test_only_read` tinyint(3) unsigned DEFAULT NULL COMMENT 'toy test only read ,0read,1unread',
  `duration` int(10) unsigned DEFAULT NULL COMMENT 'duration',
  `kubenetes_id` int(10) unsigned DEFAULT NULL COMMENT 'kubenetes id',
  `minimize_fast_test` tinyint(3) unsigned DEFAULT NULL COMMENT 'minimize fast test,0 no test,1testing,2test ok,3test failed',
  `minimize_normal_test` tinyint(3) unsigned DEFAULT NULL COMMENT 'minimize normal test,0 no test,1testing,2test ok,3test failed',
  `config` mediumtext COMMENT 'install cofig',
  `rollsite_port` int(10) DEFAULT NULL COMMENT 'yaml proxy port',
  `python_port` int(10) DEFAULT NULL,
  `click_type` int(10) DEFAULT NULL COMMENT 'click type,1 connect success;2page start;3pull;4install;5finish',
  `deploy_type` int(10) DEFAULT NULL COMMENT 'deploy type ,1k8s,2ansible',
  `is_valid` tinyint(4) DEFAULT '-1' COMMENT '-1 unkonw,0unvalid,1valid',
  `finish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `party_id` (`party_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated site  deploy';

CREATE TABLE IF NOT EXISTS `t_fate_fate_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `fate_version` varchar(32) DEFAULT NULL COMMENT 'fate version',
  `product_type` tinyint(3) unsigned DEFAULT NULL COMMENT '0fate,1fate-serving',
  `chart_version` varchar(32) DEFAULT NULL COMMENT 'fate chart version',
  `version_index` int(10) DEFAULT NULL COMMENT 'fate verson index ,increase from 0',
  `pull_status` tinyint(4) unsigned DEFAULT NULL COMMENT 'image pull state,0no pull,1pulled',
  `package_status` tinyint(4) unsigned DEFAULT NULL COMMENT 'package status ,0no,1yes',
  `package_url` text COMMENT 'package download url',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated fate version';

INSERT INTO `t_fate_fate_version` (`id`, `fate_version`, `product_type`, `chart_version`, `version_index`, `pull_status`, `package_status`, `package_url`, `create_time`, `update_time`) VALUES
	(1, '1.4.3', 1, 'v1.4.3', 143, 1, 1, NULL, '2020-07-23 22:31:07', '2020-11-27 10:27:36'),
	(2, '1.4.4', 1, 'v1.4.4', 144, 1, 1, NULL, '2020-07-23 22:31:07', '2020-12-11 13:47:03'),
	(3, '1.4.5', 1, 'v1.4.5', 145, 1, 1, 'https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/ansible_nfate_1.4.5_release-1.0.0.tar.gz', '2020-11-27 10:27:21', '2020-12-15 16:22:37'),
	(4, '1.4.0', 1, 'v1.4.0', 140, 1, 1, 'https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/ansible_nfate_1.4.0_release-1.0.0.tar.gz', '2020-12-15 16:12:27', '2020-12-15 16:22:35'),
	(5, '1.5.0', 1, 'v1.5.0', 150, 1, 1, 'https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/ansible_nfate_1.5.0_release-1.0.0.tar.gz', '2020-11-27 10:27:21', '2020-12-15 16:22:37'),
	(6, '1.5.1', 1, 'v1.5.1', 151, 1, 1, 'https://webank-ai-1251170195.cos.ap-guangzhou.myqcloud.com/ansible_nfate_1.5.1_release-1.0.0.tar.gz', '2020-12-15 16:12:27', '2020-12-15 16:22:35');


CREATE TABLE IF NOT EXISTS `t_fate_federated_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `federation_id` bigint(20) DEFAULT '0' COMMENT 'cloud-manager id',
  `federated_organization` varchar(128) DEFAULT NULL COMMENT 'Federated Organization',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'site belongs to institutions',
  `federated_url` varchar(256) DEFAULT NULL COMMENT 'federated host',
  `status` tinyint(4) DEFAULT '0' COMMENT '0:unvalid,1valid',
  `size` int(10) DEFAULT '0' COMMENT 'federated size',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='Federated Info';

CREATE TABLE IF NOT EXISTS `t_fate_kubenetes_conf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `kubenetes_url` varchar(50) DEFAULT NULL COMMENT 'kubenetes host domain',
  `python_port` int(11) DEFAULT NULL COMMENT 'portlist',
  `rollsite_port` int(11) DEFAULT NULL,
  `node_list` text COMMENT 'node list',
  `deploy_type` tinyint(4) DEFAULT NULL COMMENT 'deploy type ,1 k8s,2ansible',
  `click_type` tinyint(4) DEFAULT NULL COMMENT 'ansible deploy click type',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated kubenetes conf';

CREATE TABLE IF NOT EXISTS `t_fate_monitor_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `ds` int(10) DEFAULT NULL COMMENT '上报日期',
  `guest_party_id` int(10) DEFAULT NULL COMMENT 'guest partyid',
  `guest_site_name` varchar(50) DEFAULT NULL COMMENT 'guest site name',
  `guest_institution` varchar(64) DEFAULT NULL COMMENT 'guest institution',
  `host_party_id` int(10) DEFAULT NULL COMMENT 'host partyid',
  `host_site_name` varchar(50) DEFAULT NULL COMMENT 'guest site name',
  `host_institution` varchar(64) DEFAULT NULL COMMENT 'host institution',
  `arbiter_party_id` int(10) DEFAULT NULL COMMENT 'arbiter party id',
  `arbiter_site_name` int(10) DEFAULT NULL COMMENT 'artbiter site name',
  `arbiter_institution` varchar(50) DEFAULT NULL COMMENT 'arbiter institution',
  `job_id` varchar(64) DEFAULT NULL COMMENT 'job id',
  `status` varchar(50) NOT NULL DEFAULT '0' COMMENT 'model status,0 success,1failed',
  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT 'start time',
  `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT 'end tie',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Create Time',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT 'update time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Site Info';

CREATE TABLE IF NOT EXISTS `t_fate_other_site_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `site_id` bigint(12) DEFAULT NULL COMMENT 'cloud manger autocremetid',
  `site_name` varchar(128) DEFAULT NULL COMMENT 'Site Name',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'site belongs to institutions',
  `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'role,1:guest,2:host',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'site status,1 not joined,2 joined,3 removed',
  `service_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'service status,0 unavailable,1available',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `is_valid` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'is valid',
  `acativation_time` timestamp NULL DEFAULT NULL COMMENT 'activation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Other Site Info';

CREATE TABLE IF NOT EXISTS `t_fate_report_institution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `ds` int(10) DEFAULT NULL COMMENT '上报日期',
  `institution` varchar(64) DEFAULT NULL COMMENT 'institution',
  `total` int(10) DEFAULT NULL COMMENT 'total',
  `success` int(10) DEFAULT NULL COMMENT 'success',
  `running` int(10) DEFAULT NULL COMMENT 'running',
  `waiting` int(10) DEFAULT NULL COMMENT 'waiting',
  `timeout` int(10) DEFAULT NULL COMMENT 'timeout',
  `failed` int(10) DEFAULT NULL COMMENT 'failed',
  `canceled` int(10) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_report_institution';

CREATE TABLE IF NOT EXISTS `t_fate_report_site` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `ds` int(10) DEFAULT NULL COMMENT '上报日期',
  `institution` varchar(64) DEFAULT NULL COMMENT 'institution',
  `institution_site_name` varchar(64) DEFAULT NULL COMMENT 'institution site name',
  `site_name` varchar(64) DEFAULT NULL COMMENT ' site name',
  `total` int(10) DEFAULT NULL COMMENT 'total',
  `success` int(10) DEFAULT NULL COMMENT 'success',
  `running` int(10) DEFAULT NULL COMMENT 'running',
  `waiting` int(10) DEFAULT NULL COMMENT 'waiting',
  `timeout` int(10) DEFAULT NULL COMMENT 'timeout',
  `failed` int(10) DEFAULT NULL COMMENT 'failed',
  `canceled` int(10) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_report_site';

CREATE TABLE IF NOT EXISTS `t_fate_site_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `federated_id` int(10) DEFAULT NULL COMMENT '联邦组织ID',
  `federated_organization` varchar(128) DEFAULT NULL COMMENT 'Federated Organization',
  `party_id` bigint(12) DEFAULT NULL COMMENT 'party_id',
  `site_id` bigint(12) DEFAULT NULL COMMENT 'cloud manger autocremetid',
  `site_name` varchar(128) DEFAULT NULL COMMENT 'Site Name',
  `institutions` varchar(128) DEFAULT NULL COMMENT 'site belongs to institutions',
  `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'role,1:guest,2:host',
  `app_key` varchar(64) DEFAULT NULL COMMENT 'Federation  Key',
  `app_secret` varchar(64) DEFAULT NULL COMMENT 'Federation  Secret',
  `registration_link` text COMMENT 'registration_link',
  `network_access_entrances` varchar(256) DEFAULT NULL COMMENT 'network access entrances',
  `network_access_exits` varchar(256) DEFAULT NULL COMMENT 'network access exits',
  `fate_version` varchar(10) DEFAULT NULL COMMENT 'fate version',
  `fate_serving_version` varchar(10) DEFAULT NULL COMMENT 'fate serving version',
  `component_version` varchar(512) DEFAULT NULL COMMENT 'fate component version',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'site status,1 not joined,2 joined,3 removed',
  `deploy_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'deploy type ,1k8s,2ansible',
  `service_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'service status,0 unavailable,1available',
  `edit_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'edit status,-1 unkonwn,1 unedit,2 edit',
  `read_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT 'read status,-1 unkonwn,3 read,1agreed ,2rejected',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `acativation_time` timestamp NULL DEFAULT NULL COMMENT 'activation Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='Federated Site Info';

CREATE TABLE IF NOT EXISTS `t_fate_token_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `user_id` varchar(50) DEFAULT NULL COMMENT 'user id',
  `user_name` varchar(50) DEFAULT NULL COMMENT 'user name',
  `token` varchar(256) DEFAULT NULL COMMENT 'token',
  `expire_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'expire_time',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_token_info';

CREATE TABLE IF NOT EXISTS `t_fate_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `user_id` varchar(50) DEFAULT NULL COMMENT 'user id',
  `user_name` varchar(128) DEFAULT NULL COMMENT 'user name',
  `password` varchar(128) DEFAULT NULL COMMENT 'user name',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='t_fate_user_info';

INSERT INTO `t_fate_user_info` (`id`, `user_id`, `user_name`, `password`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'admin', '2020-08-25 11:01:41', '2020-08-25 11:01:41'),
	(2, 'developer', 'developer', 'developer', '2020-08-26 10:05:15', '2020-08-26 10:05:15'),
	(3, 'business', 'business', 'business', '2020-08-26 10:05:22', '2020-08-26 10:05:22'),
	(11, '111', '111', '111', '2020-10-20 14:23:53', '2020-10-20 14:23:53'),
	(12, '222', '222', '222', '2020-10-23 19:01:58', '2020-10-23 19:02:01'),
	(13, '333', '333', '333', '2020-10-23 19:02:50', '2020-10-23 19:03:25'),
	(14, '9999', 'stu9999', 'stu9999', '2020-10-26 14:28:13', '2020-10-26 14:28:13'),
	(15, '10000', 'stu10000', 'stu10000', '2020-10-26 14:28:29', '2020-10-26 14:28:29');
