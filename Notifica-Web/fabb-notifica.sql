-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 23, 2015 at 07:12 PM
-- Server version: 5.5.40-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `fabb-notifica`
--

-- --------------------------------------------------------

--
-- Table structure for table `assignments`
--

DROP TABLE IF EXISTS `assignments`;
CREATE TABLE IF NOT EXISTS `assignments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `faculty_id` int(11) NOT NULL,
  `year` int(11) NOT NULL DEFAULT '-1',
  `groups` varchar(20) NOT NULL DEFAULT '',
  `summary` text NOT NULL,
  `details` text NOT NULL,
  `submission_date` date NOT NULL,
  `poster_id` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `central_authorities`
--

DROP TABLE IF EXISTS `central_authorities`;
CREATE TABLE IF NOT EXISTS `central_authorities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `central_authorities`
--

INSERT INTO `central_authorities` (`id`, `name`, `faculty_id`, `updated_at`, `user_id`) VALUES
(1, 'Ankit Mehta', 500, '2015-03-14 18:15:00', 1);

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
CREATE TABLE IF NOT EXISTS `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `faculty_id` int(11) NOT NULL,
  `year` int(11) NOT NULL DEFAULT '-1',
  `groups` varchar(20) NOT NULL DEFAULT '',
  `summary` text NOT NULL,
  `details` text NOT NULL,
  `event_date` date NOT NULL,
  `poster_id` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `faculty_id`, `year`, `groups`, `summary`, `details`, `event_date`, `poster_id`, `deleted`, `changed_at`) VALUES
(1, 500, -1, '', 'HELLO', 'WORLD', '2015-03-24', 1, 0, '2015-03-24 13:18:29');

-- --------------------------------------------------------

--
-- Table structure for table `faculties`
--

DROP TABLE IF EXISTS `faculties`;
CREATE TABLE IF NOT EXISTS `faculties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(256) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=501 ;

--
-- Dumping data for table `faculties`
--

INSERT INTO `faculties` (`id`, `code`, `name`, `changed_at`) VALUES
(400, 'BEX', 'Electronics', '2015-03-23 18:15:00'),
(500, 'BCT', 'Computer', '2015-03-18 18:15:00');

-- --------------------------------------------------------

--
-- Table structure for table `routines`
--

DROP TABLE IF EXISTS `routines`;
CREATE TABLE IF NOT EXISTS `routines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `faculty_id` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `group` varchar(10) NOT NULL DEFAULT 'A',
  `start_time` int(11) NOT NULL COMMENT 'stored as minutes',
  `end_time` int(11) NOT NULL COMMENT 'stored as minutes',
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `routine_elements`
--

DROP TABLE IF EXISTS `routine_elements`;
CREATE TABLE IF NOT EXISTS `routine_elements` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `routine_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `day` int(11) NOT NULL,
  `start_time` int(11) NOT NULL COMMENT 'stored as minutes',
  `end_time` int(11) NOT NULL COMMENT 'stored as minutes',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `roll` int(11) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `group_id` varchar(10) NOT NULL DEFAULT 'A',
  `pivilege_level` tinyint(1) NOT NULL DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1013 ;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `name`, `roll`, `faculty_id`, `year`, `group_id`, `pivilege_level`, `updated_at`, `user_id`) VALUES
(1000, '__dummy', -1, -1, -1, 'X', 0, '0000-00-00 00:00:00', -1),
(1009, 'Abinash Manandhar', 501, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1009),
(1010, 'Anish Shrestha', 502, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1010),
(1011, 'Anjesh Kafle', 503, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1011),
(1012, 'Ankit Mehata', 504, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1012);

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
CREATE TABLE IF NOT EXISTS `subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(256) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
CREATE TABLE IF NOT EXISTS `teachers` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `contact_number` varchar(100) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `teachers_subjects`
--

DROP TABLE IF EXISTS `teachers_subjects`;
CREATE TABLE IF NOT EXISTS `teachers_subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teacher_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` tinytext NOT NULL,
  `password` char(128) NOT NULL,
  `salt` char(128) NOT NULL,
  `usertype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1013 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `usertype`) VALUES
(1, 'fhx', '87d7de170920dbcabff61520b050dfa35d5cfc68f1ed9327027aeadcc67554c1307b59714fa4412bd80fe0f06fc797bef9d86440afae5d64d7e2cd156f1bb51c', '1cae87da42018b91f828bec5a1452bc2474e1f2e3b2fd0d7137d4048634f9a983f86ce0e9df0788985ed1b6b4440cb4d29aa2de92c196ed5296e14c69e60f129', 3),
(1009, '069BCT501', '39d822c7c5c611d5144ccfb4d693d3857ae56912bc2f9af7aa718b00f5bf7ba0474d4fc2584d29929c305d4bebbadfe4c4e5ae04e280346ddea2ea1ed531c599', 'ca4e61cc48d3c905982b3bdecb83e8daa13e935bb75f722279fae567a4483a381963a5c70be5f847dcbe3973e21d2599de741cc7261e16ca7540b33488a67d07', 1),
(1010, '069BCT502', '07348bf15f2748df0fa584456ec245aa3ef9ea802cce089c42765bea972a0ef7b6e4a06aaa374ee41c596159b6460ce86bd274cf2bbe33acc5aad28a4e220958', '043b5cf3f7500454c51e131d0f33d8cbbc818f5a96d22a93a7066067214f5fb333e319a204f73f1ad73721c3903fa5076cff41aca62ee16b94a471618d7dd2d3', 1),
(1011, '069BCT503', 'ad5bca084a2e2f7fb50e95d5ef0306258ba3b9265120be7c0611e9722fad9445d63f41ae7be7192702a83abe02c7833ea44ba142b93b8548bd042988cd6cc303', '026bbdf0e375ce95aff4ec3fd649af304ce9f35b12de36662efa349294facda72bfcf8cbb65d1b8e02594a4ba5d30dc20f210aee64d301af16afc4961139782d', 1),
(1012, '069BCT504', '438be242289710f2e5c35d2b53f03f445038f4c7d6cbfbc59b53edc01ac331c6775eaee3a9489a217d359f9c2302eac8755f61d0b8f194fbadc9133b15ec5db8', 'cb36e16ca752092a98202303ec617ec976879ed81a46888dcf2c2a9917e0ce161697fd5cbe28d99fb1a1febec567e2d6b7993ef9cae88efdea3ad36661430a18', 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
