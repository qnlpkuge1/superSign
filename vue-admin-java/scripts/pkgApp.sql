
DROP TABLE IF EXISTS `pkg_app`;
CREATE TABLE `pkg_app` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL DEFAULT '' COMMENT '应用名称',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '链接地址',
  `type` varchar(80) NOT NULL DEFAULT '' COMMENT '类型',
  `removePassword` varchar(30) NOT NULL DEFAULT '0' COMMENT '删除密码',
  `icon_path` varchar(255) NOT NULL DEFAULT '0' COMMENT 'icon路径',
  `mbconfig` varchar(255) NOT NULL DEFAULT '0' COMMENT 'mobileconfig路径',
  `owner_id` bigint(4) NOT NULL DEFAULT '0' COMMENT '所属用户',
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '状态 0：禁用；1：正常;',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='封包应用';
