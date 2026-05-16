-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: laundrify_db
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'raf','09053549846'),(2,'Yvan','0993491293'),(3,'rafael Canalda','09012912381'),(4,'Rafael Jr. Canalda','09912836108'),(6,'Raff','09912836108'),(7,'Ardell Joy Francicsco','09678437501'),(8,'Rose','09064359346');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prices`
--

DROP TABLE IF EXISTS `prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prices` (
  `service_name` varchar(50) NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`service_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prices`
--

LOCK TABLES `prices` WRITE;
/*!40000 ALTER TABLE `prices` DISABLE KEYS */;
INSERT INTO `prices` VALUES ('Dry',50),('Fold',30),('Full Service',150),('Self-Serve',80),('Wash',60),('Wash & Dry',100),('Weight (per kg)',10);
/*!40000 ALTER TABLE `prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sales` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `invoice_number` varchar(50) NOT NULL,
  `customer_name` varchar(100) DEFAULT '',
  `customer_phone` varchar(50) DEFAULT '',
  `items` text NOT NULL,
  `total_amount` double NOT NULL,
  `amount_paid` double NOT NULL,
  `change_amount` double NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `sale_date` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales`
--

LOCK TABLES `sales` WRITE;
/*!40000 ALTER TABLE `sales` DISABLE KEYS */;
INSERT INTO `sales` VALUES (1,'INV-B0214702','Kalell','321213213','Weight (5.0 kg), Full Service',15,20,5,'Claimed','2026-05-05 12:22:30'),(2,'INV-1E345150','','','Wash, Dry, Fold, Weight (1.0 kg)',13,100,87,'Claimed','2026-05-08 02:49:05'),(3,'INV-BD2A93DE','','','Wash, Dry, Fold, Weight (8.0 kg)',220,300,80,'Claimed','2026-05-08 03:06:40'),(4,'INV-30B410E5','','','Wash, Dry, Fold, Wash & Dry, Self-Serve, Weight (20.0 kg)',520,550,30,'Claimed','2026-05-09 17:25:04'),(5,'INV-E598B19E','','','Weight (8.0 kg), Wash, Fold, Dry, Full Service',370,370,0,'Claimed','2026-05-09 17:26:13'),(6,'INV-CFBAB264','raf','123123213123213','Full Service, Weight (8.0 kg)',230,250,20,'Claimed','2026-05-09 17:28:21'),(7,'INV-10634E98','raf','','Wash, Self-Serve, Weight (8.0 kg)',220,250,30,'Claimed','2026-05-09 17:30:47'),(8,'INV-D8D66DEB','','','Weight (5.0 kg)',50,1000,950,'Claimed','2026-05-10 04:06:23'),(9,'INV-1520252C','','','Fold, Wash, Dry, Weight (8.0 kg)',220,500,280,'Claimed','2026-05-10 04:07:20'),(10,'INV-084869DE','','','Fold, Wash, Wash & Dry, Weight (8.0 kg)',270,500,230,'Claimed','2026-05-10 04:07:37'),(11,'INV-E4BF2D81','','','Weight (10.0 kg)',100,500,400,'Claimed','2026-05-10 04:08:09'),(12,'INV-85206AED','','','Weight (8.0 kg)',80,80,0,'Claimed','2026-05-10 04:09:22'),(13,'INV-9EF18711','','','Fold, Dry, Wash & Dry, Weight (8.0 kg)',260,10000,9740,'Claimed','2026-05-10 04:39:27'),(14,'INV-85ACCF0C','','','Wash, Fold, Dry, Wash & Dry, Weight (5.0 kg)',290,500,210,'Claimed','2026-05-10 04:47:28'),(15,'INV-ECB76DE5','simon','','Fold, Wash & Dry, Self-Serve, Weight (5.0 kg)',260,300,40,'Claimed','2026-05-10 04:48:07'),(16,'INV-2D2DBFEA','raf','09053549846','Fold, Wash, Dry, Weight (8.0 kg)',220,500,280,'Claimed','2026-05-11 05:00:56'),(17,'INV-CBC9C862','raf','09053549846','Wash, Dry, Fold, Wash & Dry, Weight (8.0 kg)',320,500,180,'Claimed','2026-05-11 05:01:58'),(18,'INV-F72174E5','rafael Canalda','09012912381','Fold, Weight (123.0 kg)',1260,1520,260,'Claimed','2026-05-11 05:37:00'),(19,'INV-71323DA7','','','Wash, Fold, Wash & Dry, Full Service, Weight (8.0 kg)',420,500,80,'Claimed','2026-05-11 06:18:33'),(20,'INV-9F1A5EE3','Rafael Jr. Canalda','09912836108','Full Service, Weight (8.0 kg)',230,500,270,'Claimed','2026-05-11 07:30:51'),(21,'INV-F11ED9F6','Rafael Jr. Canalda','09912836108','Fold, Wash, Weight (3.0 kg)',120,500,380,'Claimed','2026-05-11 07:36:50'),(22,'INV-6EB33977','raf','09053549846','Fold, Wash, Dry, Self-Serve, Weight (8.0 kg)',300,500,200,'Unclaimed','2026-05-11 07:40:11'),(23,'INV-270D84F4','Rafael Jr. Canalda','09912836108','Full Service, Detergent Soap (x1), Fabric Conditioner (x1), Weight (8.0 kg)',265,500,235,'Unclaimed','2026-05-11 08:12:41'),(24,'INV-A5E7CB59','Raff','09912836108','Wash, Dry, Fold, Weight (8.0 kg)',220,500,280,'Unclaimed','2026-05-11 09:06:25'),(25,'INV-6C0E474A','','','Wash, Dry, Fold, Weight (8.0 kg), Detergent Soap (x1), Fabric Conditioner (x1)',255,500,245,'Pending','2026-05-11 09:08:43'),(26,'INV-944E03DD','Rafael Jr. Canalda','09912836108','Wash, Dry, Fold, Weight (8.0 kg), Detergent Soap (x1), Fabric Conditioner (x1), Bleach (x1)',265,500,235,'Unclaimed','2026-05-11 09:09:07'),(27,'INV-B5852055','Ardell Joy Francicsco','09678437501','Full Service, Weight (8.0 kg), Detergent Soap (x1), Fabric Conditioner (x1)',265,500,235,'Unclaimed','2026-05-11 09:12:31'),(28,'INV-B26B161E','Rafael Jr. Canalda','09912836108','Full Service, Detergent Soap (x1), Fabric Conditioner (x1), Bleach (x1), Perfume / Freshener (x1), Fold Service (x1), Weight (8.0 kg)',330,500,170,'Unclaimed','2026-05-13 13:05:34'),(29,'INV-12C79400','','','Wash, Fold, Wash & Dry, Detergent Soap (x1), Fabric Conditioner (x1), Bleach (x1)',235,500,265,'Pending','2026-05-13 13:39:52'),(30,'INV-AB88908B','Rose','09064359346','Full Service, Detergent Soap (x1), Fabric Conditioner (x1), Bleach (x1), Weight (8.0 kg)',275,500,225,'Unclaimed','2026-05-13 14:00:15'),(31,'INV-76FBA60F','','','Fold, Wash, Dry, Wash & Dry, Self-Serve, Fold',350,1000,650,'Pending','2026-05-13 14:10:51'),(32,'INV-6A3F7D9B','','','Fold, Weight (8.0 kg), Full Service',260,500,240,'Pending','2026-05-15 14:55:15'),(33,'INV-065FDC24','Raff','09912836108','Wash, Fold, Self-Serve, Full Service, Bleach (x1), Weight (8.0 kg)',410,500,90,'Pending','2026-05-16 04:50:00'),(34,'INV-5F3F3468','','','Dry',50,500,450,'Pending','2026-05-16 04:55:43'),(35,'INV-10DA8EF9','','','Wash',60,500,440,'Pending','2026-05-16 05:07:11'),(36,'INV-870F0041','Rafael Jr. Canalda','09912836108','Full Service, Weight (8.0 kg), Detergent Soap (x1), Fabric Conditioner (x1), Bleach (x1)',275,500,225,'Pending','2026-05-16 05:27:41');
/*!40000 ALTER TABLE `sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','password','admin'),(2,'cashier','password','cashier'),(3,'manager','password','manager'),(4,'owner','password','owner'),(5,'raf','rafael','Cashier'),(8,'yvan','bading','Cashier'),(10,'tomboyyvan','bading','Cashier'),(11,'egan','egan123','Admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-16 13:56:25
