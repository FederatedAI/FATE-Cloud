SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_fate_secret_info
-- ----------------------------
DROP TABLE IF EXISTS `t_fate_secret_info`;
CREATE TABLE `t_fate_secret_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
  `node` varchar(32) NOT NULL COMMENT 'role:guest or host',
  `party_id` varchar(32) DEFAULT NULL COMMENT 'partyId',
  `app_key` varchar(32) NOT NULL COMMENT 'Federation Key',
  `app_secret` varchar(50) DEFAULT NULL COMMENT 'Federation Secret',
  `role_type` varchar(32) DEFAULT NULL COMMENT 'Role：-1:Unknown,1:Guest,2:Host',
  `node_status` int(10) DEFAULT NULL COMMENT 'Status：1:not join,2:joined,3:remove',
  `host_domain` varchar(255) DEFAULT NULL COMMENT 'Federated Organization Domain,ip:port',
  `export_url` varchar(255) DEFAULT NULL COMMENT 'Network Access Exits',
  `portal_url` varchar(255) DEFAULT NULL COMMENT 'Network Access Entrances',
  `fed_org_url` varchar(255) DEFAULT NULL COMMENT 'Federated Organization Url',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'createtime',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'updatetime',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='fate-manager_secret_table';