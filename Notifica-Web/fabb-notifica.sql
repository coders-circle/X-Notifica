-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Feb 23, 2015 at 10:34 AM
-- Server version: 5.5.32
-- PHP Version: 5.4.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `fabb-notifica`
--
CREATE DATABASE IF NOT EXISTS `fabb-notifica` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `fabb-notifica`;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` tinytext NOT NULL,
  `password` char(128) NOT NULL,
  `salt` char(128) NOT NULL,
  `usertype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `usertype`) VALUES
(1, 'Ankit', '87d7de170920dbcabff61520b050dfa35d5cfc68f1ed9327027aeadcc67554c1307b59714fa4412bd80fe0f06fc797bef9d86440afae5d64d7e2cd156f1bb51c', '1cae87da42018b91f828bec5a1452bc2474e1f2e3b2fd0d7137d4048634f9a983f86ce0e9df0788985ed1b6b4440cb4d29aa2de92c196ed5296e14c69e60f129', 3);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
