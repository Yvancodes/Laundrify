-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 17, 2026 at 03:42 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `laundrify_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `add_ons`
--

CREATE TABLE `add_ons` (
  `id` int(11) NOT NULL,
  `addon_name` varchar(100) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `add_ons`
--

INSERT INTO `add_ons` (`id`, `addon_name`, `price`) VALUES
(1, 'Bleach', 10),
(2, 'Detergent Soap', 15),
(3, 'Express Service', 50),
(4, 'Extra Rinse', 30),
(5, 'Fabric Conditioner', 20),
(6, 'Fold Service', 40),
(7, 'Perfume / Freshener', 15),
(8, 'Stain Remover', 25);

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`id`, `name`, `phone`) VALUES
(1, 'raf', '09053549846'),
(2, 'Yvan', '0993491293'),
(3, 'rafael Canalda', '09012912381'),
(4, 'Rafael Jr. Canalda', '09912836108'),
(6, 'Raff', '09912836108'),
(7, 'Ardell Joy Francicsco', '09678437501'),
(8, 'Rose', '09064359346'),
(9, 'James', '09950169768');

-- --------------------------------------------------------

--
-- Table structure for table `prices`
--

CREATE TABLE `prices` (
  `service_name` varchar(50) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prices`
--

INSERT INTO `prices` (`service_name`, `price`) VALUES
('Dry', 50),
('Fold', 30),
('Full Service', 150),
('Self-Serve', 80),
('Wash', 60),
('Wash & Dry', 100),
('Weight (per kg)', 10);

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `id` int(11) NOT NULL,
  `invoice_number` varchar(50) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `cashier_id` int(11) DEFAULT NULL,
  `total_amount` double NOT NULL,
  `amount_paid` double NOT NULL,
  `change_amount` double NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `sale_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`id`, `invoice_number`, `customer_id`, `cashier_id`, `total_amount`, `amount_paid`, `change_amount`, `status`, `sale_date`) VALUES
(1, 'INV-FDF74F0E', 9, NULL, 355, 500, 145, 'Claimed', '2026-05-16 11:30:18'),
(2, 'INV-94A69968', 9, NULL, 280, 300, 20, 'Unclaimed', '2026-05-17 12:37:47'),
(3, 'INV-B33715EF', 9, NULL, 200, 200, 0, 'Pending', '2026-05-17 12:53:12'),
(4, 'INV-D76FCC0A', 9, 4, 230, 500, 270, 'Pending', '2026-05-17 13:37:22');

-- --------------------------------------------------------

--
-- Table structure for table `sales_details`
--

CREATE TABLE `sales_details` (
  `id` int(11) NOT NULL,
  `sale_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `price` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales_details`
--

INSERT INTO `sales_details` (`id`, `sale_id`, `item_name`, `price`) VALUES
(1, 1, 'Full Service', 150),
(2, 1, 'Bleach (x1)', 10),
(3, 1, 'Detergent Soap (x1)', 15),
(4, 1, 'Express Service (x1)', 50),
(5, 1, 'Extra Rinse (x1)', 30),
(6, 1, 'Weight (10.0 kg)', 100),
(7, 2, 'Full Service', 150),
(8, 2, 'Weight (8.0 kg)', 80),
(9, 2, 'Bleach (x2)', 20),
(10, 2, 'Detergent Soap (x2)', 30),
(11, 3, 'Full Service', 150),
(12, 3, 'Weight (5.0 kg)', 50),
(13, 4, 'Full Service', 150),
(14, 4, 'Weight (8.0 kg)', 80);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`) VALUES
(1, 'Admin', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'Admin'),
(2, 'Yvan', 'n97bATx3Y7+jqjv7WhmfBpaUy9Qgqyk9Qg6dVQLM4VY=', 'Admin'),
(3, 'Rafael', 'YPbD/djy78mq2oG2Y6UTT0szi2+4Ctcco20sNf3/7vg=', 'Cashier'),
(4, 'Jerry', 'qjiAIiLJZMMbU+lCP//Jsyyz6YUrbVrW1AaSBB4T9IQ=', 'Cashier');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `add_ons`
--
ALTER TABLE `add_ons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `addon_name` (`addon_name`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `prices`
--
ALTER TABLE `prices`
  ADD PRIMARY KEY (`service_name`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invoice_number` (`invoice_number`),
  ADD KEY `fk_sales_customers` (`customer_id`),
  ADD KEY `fk_sales_users` (`cashier_id`);

--
-- Indexes for table `sales_details`
--
ALTER TABLE `sales_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_details_sales` (`sale_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `add_ons`
--
ALTER TABLE `add_ons`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `sales_details`
--
ALTER TABLE `sales_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `sales`
--
ALTER TABLE `sales`
  ADD CONSTRAINT `fk_sales_customers` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_sales_users` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `sales_details`
--
ALTER TABLE `sales_details`
  ADD CONSTRAINT `fk_details_sales` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
