-- 部门表
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
   `id` int(11) NOT NULL auto_increment,
   `title` varchar(20) NOT NULL,
   `intro` varchar(50) NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 员工表
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
    `id` int(11) NOT NULL auto_increment,
    `title` varchar(20) NOT NULL,
    `email` varchar(50) NOT NULL,
    `gender` char(1) NOT NULL,
    `dep_id` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;