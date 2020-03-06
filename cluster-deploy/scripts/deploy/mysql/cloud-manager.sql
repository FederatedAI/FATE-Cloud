SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ai_fdn_data_node
-- ----------------------------
DROP TABLE IF EXISTS `ai_fdn_data_node`;
CREATE TABLE `ai_fdn_data_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `portal_url` varchar(128) DEFAULT NULL COMMENT 'Network Access Entrances',
  `export_url` varchar(128) DEFAULT NULL COMMENT 'Network Access Exits',
  `secret_info` varchar(512) DEFAULT NULL COMMENT 'Federation Key&Secret Info',
  `role_type` tinyint(4) DEFAULT '1' COMMENT 'Role：-1:Unknown,1:Guest,2:Host',
  `node_status` tinyint(4) DEFAULT '1' COMMENT 'Status：1:not join,2:joined,3:remove',
  `node_public` tinyint(4) DEFAULT '1' COMMENT 'Dentity Public：1：yes，2：no',
  `detective_status` tinyint(4) DEFAULT '2' COMMENT 'Survival Status：1:Survival，2:Non Survival',
  `last_detective_time` timestamp NULL DEFAULT NULL COMMENT 'Check Survival Time',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Createtime',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updatetime',
  `f_description` varchar(512) DEFAULT NULL COMMENT 'node description',
  `organization_desc` varchar(512) DEFAULT NULL COMMENT 'Federated Organization,Multiple can be supported, separated by ";"',
  PRIMARY KEY (`id`),
  KEY `idx_sunion` (`node_status`,`role_type`,`node_public`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='cloud-manager_node_table';
