
DROP TABLE IF EXISTS `developer`;
CREATE TABLE `developer` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL DEFAULT '0' COMMENT '帐号',
  `password` varchar(50) NOT NULL DEFAULT '' COMMENT '密码',
  `cert` varchar(80) NOT NULL DEFAULT '0' COMMENT '证书名称',
  `install_limit` int(5) NOT NULL DEFAULT '100' COMMENT '装机数量上限',
  `app_info_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '绑定应用ID',
  `app_name` varchar(80) NOT NULL DEFAULT '' COMMENT '绑定应用名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='开发者';
