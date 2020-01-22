
DROP TABLE IF EXISTS `developer_ping_log`;
CREATE TABLE `developer_ping_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `developer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '开发者ID',
  `developerName` varchar(80) NOT NULL DEFAULT '0' COMMENT '开发者',
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '检测状态 0：禁用； 1：正常 ；',
  `result` varchar(256) NOT NULL DEFAULT '' COMMENT '检测结果',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='检测日志';
