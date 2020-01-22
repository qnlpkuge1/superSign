ALTER TABLE `app_info` ADD COLUMN `owner_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属用户';
ALTER TABLE `install_log` ADD COLUMN `owner_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属用户';
