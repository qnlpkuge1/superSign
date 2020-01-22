
DROP TABLE IF EXISTS `app_info`;
CREATE TABLE `app_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `appId` varchar(200) NOT NULL DEFAULT '' COMMENT '应用bundle wildcard Id',
  `appIdReal` varchar(200) NOT NULL DEFAULT '' COMMENT '应用bunid Id',
  `appName` varchar(80) NOT NULL DEFAULT '' COMMENT '应用名称',
  `version` varchar(50) NOT NULL DEFAULT '' COMMENT '版本号',
  `versionCode` varchar(80) NOT NULL DEFAULT '' COMMENT '版本编码',
  `path` varchar(255) NOT NULL DEFAULT '0' COMMENT 'IPA路径',
  `icon_path` varchar(255) NOT NULL DEFAULT '0' COMMENT 'icon路径',
  `mbconfig` varchar(255) NOT NULL DEFAULT '0' COMMENT 'mobileconfig路径',
  `install_limit` int(5) NOT NULL DEFAULT '0' COMMENT '装机数量上限',
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '用户状态 0：禁用； 1：正常 ；',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='应用信息表';
