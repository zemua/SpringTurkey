-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: localhost    Database: turkeydb
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `turkeydb`
--

/*!40000 DROP DATABASE IF EXISTS `turkeydb`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `turkeydb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `turkeydb`;

--
-- Table structure for table `turkey_activities`
--

DROP TABLE IF EXISTS `turkey_activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_activities` (
  `id` binary(16) NOT NULL,
  `activity_name` varchar(255) DEFAULT NULL,
  `activity_type` smallint NOT NULL,
  `category_type` smallint NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `turkey_group` binary(16) DEFAULT NULL,
  `user` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk__activity__name_and_type` (`user`,`activity_name`,`activity_type`),
  KEY `device_to_user_index` (`user`),
  KEY `FK4hrj7xupqs6tnd9mvk23eaqjo` (`turkey_group`),
  CONSTRAINT `FK4hrj7xupqs6tnd9mvk23eaqjo` FOREIGN KEY (`turkey_group`) REFERENCES `turkey_group` (`id`),
  CONSTRAINT `FKkxxk8orgg3oy7h73ob23o1iox` FOREIGN KEY (`user`) REFERENCES `turkey_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_condition`
--

DROP TABLE IF EXISTS `turkey_condition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_condition` (
  `id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `last_days_to_consider` int NOT NULL,
  `required_usage_ms` bigint NOT NULL,
  `conditional_group` binary(16) NOT NULL,
  `target_group` binary(16) NOT NULL,
  `user` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `condition_to_user_index` (`user`),
  KEY `conditional_group_same_user` (`conditional_group`,`user`),
  KEY `target_group_same_user` (`target_group`,`user`),
  CONSTRAINT `conditional_group_same_user` FOREIGN KEY (`conditional_group`, `user`) REFERENCES `turkey_group` (`id`, `user`),
  CONSTRAINT `FK7g986qug3pg47m9h2bl28uvpy` FOREIGN KEY (`user`) REFERENCES `turkey_user` (`id`),
  CONSTRAINT `FKi8xi90dq0utoo49e32ye0sujh` FOREIGN KEY (`conditional_group`) REFERENCES `turkey_group` (`id`),
  CONSTRAINT `FKogr6w80ko57w21qk3552ys11` FOREIGN KEY (`target_group`) REFERENCES `turkey_group` (`id`),
  CONSTRAINT `target_group_same_user` FOREIGN KEY (`target_group`, `user`) REFERENCES `turkey_group` (`id`, `user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_device`
--

DROP TABLE IF EXISTS `turkey_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_device` (
  `id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `device_type` smallint NOT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `usage_time` bigint NOT NULL,
  `user` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `device_to_user_index` (`user`),
  CONSTRAINT `FKgsc9t56ld4h4lg2j7lic0tvd7` FOREIGN KEY (`user`) REFERENCES `turkey_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_group`
--

DROP TABLE IF EXISTS `turkey_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_group` (
  `id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `prevent_close` bit(1) DEFAULT NULL,
  `type` smallint NOT NULL,
  `user` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `group_to_user_index` (`user`),
  KEY `group_and_user_index` (`id`,`user`),
  CONSTRAINT `FKm6023dxsnrc7ssxwbh2lxwwuo` FOREIGN KEY (`user`) REFERENCES `turkey_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_setting`
--

DROP TABLE IF EXISTS `turkey_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_setting` (
  `id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `platform` smallint DEFAULT NULL,
  `setting_key` varchar(255) DEFAULT NULL,
  `setting_value` varchar(255) DEFAULT NULL,
  `user` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk__platform__setting` (`user`,`setting_key`,`platform`),
  KEY `setting_to_user_index` (`user`),
  CONSTRAINT `FKl1w6j8fpiq93ehe32pthgsks9` FOREIGN KEY (`user`) REFERENCES `turkey_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_uncloseable`
--

DROP TABLE IF EXISTS `turkey_uncloseable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_uncloseable` (
  `activity_id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `prevent_closing` bit(1) DEFAULT NULL,
  PRIMARY KEY (`activity_id`),
  CONSTRAINT `FKbkre6sc1yl5aapubfngjkwyqs` FOREIGN KEY (`activity_id`) REFERENCES `turkey_activities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `turkey_user`
--

DROP TABLE IF EXISTS `turkey_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turkey_user` (
  `id` binary(16) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `deleted` datetime(6) DEFAULT NULL,
  `edited` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email_index` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-10 11:15:15



ALTER TABLE turkey_condition
    ADD CONSTRAINT conditional_group_same_user FOREIGN KEY
    (conditional_group, user)
    REFERENCES turkey_group (id, user);
    
ALTER TABLE turkey_condition
    ADD CONSTRAINT target_group_same_user FOREIGN KEY
    (target_group, user)
    REFERENCES turkey_group (id, user);
