
DROP TABLE IF EXISTS `install_log`;
CREATE TABLE `install_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_info_Id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'app_info Id',
  `appName` varchar(80) NOT NULL DEFAULT '' COMMENT '应用名称',
  `udid` varchar(50) NOT NULL DEFAULT '0' COMMENT 'udid',
  `developer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '开发者ID',
  `developerName` varchar(80) NOT NULL DEFAULT '0' COMMENT '开发者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='安装日志';
