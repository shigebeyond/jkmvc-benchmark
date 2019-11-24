-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `from_uid` int(11) unsigned NOT NULL COMMENT '发送人id',
  `to_uid` int(11) unsigned NOT NULL COMMENT '接收人id',
  `content` varchar(50) NOT NULL DEFAULT '' COMMENT '消息内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息';