-- t_federated_exchange vip update vip_entrance
ALTER TABLE `t_federated_exchange`
	CHANGE COLUMN `vip` `vip_entrance` VARCHAR(512) NULL DEFAULT NULL COMMENT 'exchange vip entrance address' COLLATE 'utf8_general_ci' AFTER `exchange_name`;
-- t_roll_site add network_access_exit field
ALTER TABLE `t_roll_site`
	ADD COLUMN `network_access_exit` VARCHAR(512) NULL DEFAULT NULL COMMENT 'network address exit' COLLATE 'utf8_general_ci' AFTER `network_access`;
-- update t_party. add party_id unique index, delete roll_site_id
ALTER TABLE `t_party`
	DROP COLUMN `roll_site_id`,
	ADD UNIQUE INDEX `party_id` (`party_id`);
-- add t_roll_site-t_party associate table t_roll_site_party
CREATE TABLE IF NOT EXISTS `t_roll_site_party` (
	`roll_site_id` BIGINT(20) NOT NULL,
	`party_id` VARCHAR(128) NOT NULL COLLATE 'utf8_general_ci',
	PRIMARY KEY (`roll_site_id`, `party_id`) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
-- add t_federated_ip_manager history new field
ALTER TABLE `t_federated_ip_manager`
	ADD COLUMN `exchange_name` VARCHAR(256) NULL DEFAULT NULL AFTER `network_access_exits_old`,
	ADD COLUMN `exchange_name_old` VARCHAR(256) NULL DEFAULT NULL AFTER `exchange_name`,
	ADD COLUMN `vip_entrance` VARCHAR(256) NULL DEFAULT NULL AFTER `exchange_name_old`,
	ADD COLUMN `vip_entrance_old` VARCHAR(256) NULL DEFAULT NULL AFTER `vip_entrance`,
	ADD COLUMN `secure_status` TINYINT(4) NOT NULL DEFAULT '0' AFTER `exchange_name_old`,
	ADD COLUMN `secure_status_old` TINYINT(4) NOT NULL DEFAULT '0' AFTER `secure_status`,
	ADD COLUMN `polling_status` TINYINT(4) NOT NULL DEFAULT '0' AFTER `secure_status_old`,
	ADD COLUMN `polling_status_old` TINYINT(4) NOT NULL DEFAULT '0' AFTER `polling_status`,
	CHANGE COLUMN `status` `status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT 'log deal status,0:no deal,1:agreed,2:rejected' AFTER `polling_status_old`;


