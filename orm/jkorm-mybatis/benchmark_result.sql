-- MySQL dump 10.13  Distrib 5.6.30, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: test
-- ------------------------------------------------------
-- Server version	5.6.30-1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `benchmark_result`
--

DROP TABLE IF EXISTS `benchmark_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `benchmark_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '结果id',
  `app` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名',
  `player` varchar(50) NOT NULL DEFAULT '' COMMENT '玩家名',
  `action` varchar(50) NOT NULL DEFAULT '' COMMENT '动作',
  `concurrents` int(11) unsigned NOT NULL COMMENT '并发数',
  `requests` int(11) unsigned NOT NULL COMMENT '请求数',
  `async` tinyint(3) unsigned NOT NULL COMMENT '是否异步',
  `run_time` double(64,2) unsigned NOT NULL COMMENT '运行时间',
  `tps` double(64,2) unsigned NOT NULL COMMENT '吞吐量',
  `rt` double(64,2) unsigned NOT NULL COMMENT '响应时间',
  `err_pct` int(11) unsigned NOT NULL COMMENT '错误百分比',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 COMMENT='性能测试结果';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `benchmark_result`
--

LOCK TABLES `benchmark_result` WRITE;
/*!40000 ALTER TABLE `benchmark_result` DISABLE KEYS */;
INSERT INTO `benchmark_result` VALUES (4,'orm','jkorm-druid','update',1,10000,0,10941.35,913.96,1.09,0),(5,'orm','jkorm-druid','update',1,50000,0,54826.59,911.97,1.10,0),(6,'orm','jkorm-druid','update',1,100000,0,113009.61,884.88,1.13,0),(7,'orm','jkorm-druid','delete',1,10000,0,11206.02,892.38,1.12,0),(8,'orm','jkorm-druid','delete',1,50000,0,56035.35,892.29,1.12,0),(9,'orm','jkorm-druid','delete',1,100000,0,113810.75,878.65,1.14,0),(13,'orm','jkorm-druid','getEmpsByConditionIf',1,10000,0,752.79,13283.99,0.08,0),(14,'orm','jkorm-druid','getEmpsByConditionIf',1,50000,0,3748.45,13338.83,0.07,0),(15,'orm','jkorm-druid','getEmpsByConditionIf',1,100000,0,7512.75,13310.70,0.07,0),(16,'orm','jkorm-druid','updateEmpOnDynFields',1,10000,0,11458.73,872.70,1.15,0),(17,'orm','jkorm-druid','updateEmpOnDynFields',1,50000,0,57058.38,876.30,1.14,0),(18,'orm','jkorm-druid','updateEmpOnDynFields',1,100000,0,113698.12,879.52,1.14,0),(19,'orm','jkorm-druid','getEmpsByIds',1,10000,0,718.64,13915.13,0.07,0),(20,'orm','jkorm-druid','getEmpsByIds',1,50000,0,3595.19,13907.48,0.07,0),(21,'orm','jkorm-druid','getEmpsByIds',1,100000,0,7206.65,13876.07,0.07,0),(25,'orm','mybatis','update',1,10000,0,11463.48,872.34,1.15,0),(26,'orm','mybatis','update',1,50000,0,57073.62,876.06,1.14,0),(27,'orm','mybatis','update',1,100000,0,114775.68,871.26,1.16,0),(28,'orm','mybatis','delete',1,10000,0,11584.20,863.24,1.16,0),(29,'orm','mybatis','delete',1,50000,0,57787.59,865.24,1.16,0),(30,'orm','mybatis','delete',1,100000,0,113868.56,878.21,1.14,0),(34,'orm','mybatis','getEmpsByConditionIf',1,10000,0,771.94,12954.31,0.08,0),(35,'orm','mybatis','getEmpsByConditionIf',1,50000,0,3853.58,12974.95,0.08,0),(36,'orm','mybatis','getEmpsByConditionIf',1,100000,0,7713.51,12964.26,0.08,0),(37,'orm','mybatis','updateEmpOnDynFields',1,10000,0,10874.80,919.56,1.09,0),(38,'orm','mybatis','updateEmpOnDynFields',1,50000,0,56977.28,877.54,1.14,0),(39,'orm','mybatis','updateEmpOnDynFields',1,100000,0,115276.39,867.48,1.15,0),(40,'orm','mybatis','getEmpsByIds',1,10000,0,797.75,12535.21,0.08,0),(41,'orm','mybatis','getEmpsByIds',1,50000,0,4021.08,12434.48,0.08,0),(42,'orm','mybatis','getEmpsByIds',1,100000,0,8016.73,12473.92,0.08,0),(43,'orm','jkorm-druid','add',1,10000,0,12189.02,820.41,1.22,0),(44,'orm','jkorm-druid','add',1,50000,0,59320.53,842.88,1.19,0),(45,'orm','jkorm-druid','add',1,100000,0,111431.50,897.41,1.11,0),(46,'orm','jkorm-druid','getDepWithEmps',1,10000,0,1192.39,8386.55,0.12,0),(47,'orm','jkorm-druid','getDepWithEmps',1,50000,0,6028.82,8293.50,0.12,0),(48,'orm','jkorm-druid','getDepWithEmps',1,100000,0,12069.07,8285.65,0.12,0),(49,'orm','mybatis','add',1,10000,0,11152.81,896.64,1.11,0),(50,'orm','mybatis','add',1,50000,0,55456.16,901.61,1.11,0),(51,'orm','mybatis','add',1,100000,0,112779.79,886.68,1.13,0),(52,'orm','mybatis','getDepWithEmps',1,10000,0,1187.60,8420.32,0.12,0),(53,'orm','mybatis','getDepWithEmps',1,50000,0,5925.06,8438.73,0.12,0),(54,'orm','mybatis','getDepWithEmps',1,100000,0,11811.24,8466.51,0.12,0);
/*!40000 ALTER TABLE `benchmark_result` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-16 14:14:22
