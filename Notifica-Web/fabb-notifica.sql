-- phpMyAdmin SQL Dump
-- version 4.0.4.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Mar 24, 2015 at 04:46 AM
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
-- Table structure for table `assignments`
--

CREATE TABLE IF NOT EXISTS `assignments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `faculty_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `year` int(11) NOT NULL DEFAULT '-1',
  `groups` varchar(20) NOT NULL DEFAULT '',
  `summary` text NOT NULL,
  `details` text NOT NULL,
  `submission_date` date NOT NULL,
  `poster_id` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `assignments`
--

INSERT INTO `assignments` (`id`, `faculty_id`, `subject_id`, `year`, `groups`, `summary`, `details`, `submission_date`, `poster_id`, `deleted`, `changed_at`) VALUES
(1, 500, 1, 2069, '', 'test123', 'test345', '2015-03-24', 1010, 0, '2015-03-25 17:17:26'),
(2, 400, 1, -1, '', 'test2', 'test2', '2015-03-23', 1010, 0, '2015-03-23 17:06:41');

-- --------------------------------------------------------

--
-- Table structure for table `central_authorities`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `faculty_id`, `year`, `groups`, `summary`, `details`, `event_date`, `poster_id`, `deleted`, `changed_at`) VALUES
(1, 500, -1, '', 'HELLO', 'WORLD', '2015-03-24', 1, 0, '2015-03-24 13:18:29'),
(2, -1, -1, 'AB', 'abcd', 'wert', '2016-04-24', 1010, 0, '2015-03-23 16:34:37'),
(3, 500, 2069, '', 'zxcvcx', 'test', '2015-03-23', 1010, 0, '2015-03-26 16:37:35');

-- --------------------------------------------------------

--
-- Table structure for table `faculties`
--

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

CREATE TABLE IF NOT EXISTS `students` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `roll` int(11) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `group_id` varchar(10) NOT NULL DEFAULT 'A',
  `privilege_level` tinyint(1) NOT NULL DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1017 ;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `name`, `roll`, `faculty_id`, `year`, `group_id`, `privilege_level`, `updated_at`, `user_id`) VALUES
(1013, 'Abinash Manandhar', 501, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1013),
(1014, 'Anish Shrestha', 502, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1014),
(1015, 'Anjesh Kafle', 503, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1015),
(1016, 'Ankit Mehta', 504, 500, 2069, 'A', 0, '0000-00-00 00:00:00', 1016);

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE IF NOT EXISTS `subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(256) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`id`, `code`, `name`, `faculty_id`, `changed_at`) VALUES
(1, 'CT111', 'TEST SUBJECT', 500, '2015-03-23 16:54:52');

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

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

--
-- Dumping data for table `teachers`
--

INSERT INTO `teachers` (`id`, `user_id`, `name`, `contact_number`, `faculty_id`, `changed_at`, `updated_at`) VALUES
(0, 1017, 'Aditya Khatri', 'xxxx', 500, '2015-03-24 03:37:18', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `teachers_subjects`
--

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

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` tinytext NOT NULL,
  `password` char(128) NOT NULL,
  `salt` char(128) NOT NULL,
  `usertype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1018 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `usertype`) VALUES
(1, 'fhx', '87d7de170920dbcabff61520b050dfa35d5cfc68f1ed9327027aeadcc67554c1307b59714fa4412bd80fe0f06fc797bef9d86440afae5d64d7e2cd156f1bb51c', '1cae87da42018b91f828bec5a1452bc2474e1f2e3b2fd0d7137d4048634f9a983f86ce0e9df0788985ed1b6b4440cb4d29aa2de92c196ed5296e14c69e60f129', 3),
(1013, '069BCT501', '327cc3388a675f94ff27ea905e85e870c7da1e4860a3aa1ae624c89a842227db730b538862af687c11c41a15837f007958848d6eaa5fdfe7682752a2e23d3525', '67d1ef4271a1221a92ab9ca3ecd8c541bbcb02a6c848e7934fbcf0ca9b775192ec7ca97f8ff7273d0672ab34a30ad5660080c377c8216afd1751066cf883d3f4', 1),
(1014, '069BCT502', 'e43d974aeaa197ac1b1ed785fb89315d99c80be984c7a0870e29eecf0f642319c3f1906324f0099ebc7c40a79656ae7b321ab531f85192dbe2c368067b80dd18', '11cf5cfe484d42127300efe10f8c97dec3d7af718f8dfdeffe70c0037ba6f2c4d6c50029b0f14e700c9abd03b4110505ed1014fddf0ca1a44324d2da2a755965', 1),
(1015, '069BCT503', 'a6d5ba1387ce8b696ec4337e34990385f5d953d6beeea0814e0da13cb8038cc01cfdce97219d6e820e913f988cf8a9c48330710781ee71f1d6fa08186abd5306', '70fb1e2e4dd956586e6c9adcd136cdab0ea24c012f383a3c43328b54a428b8b4f5a20934e473c41aa08fe68150ffe90712b10457088d9553af394913c07ddff3', 1),
(1016, '069BCT504', '82943cc98d2b27dd3ee097162de6756ef4d7a014521ed56a4fae01aeb3bd5b48058ee172207c8fc0c2495af13ac33ee59c4aa6c506c83824228e6447808d5edb', '877653db544a9efdaf783f878bdc842a99d5c8f73f0c6474a1b893cd7f3b176624a2ed3fa3f7c685024d0a3565892922d480f52cd9528f5e29999df00ab934e3', 1),
(1017, 'noob', 'eaaa2cad660e704f51c777f5012fc6a91787be6b0e2ba577555c8150509b639ded57717dd533eeb60cd63d83298d5856d1fb149d680f7a0a46ccbbd3e4a7d6a8', 'd56f7ad9d69006241ab65434b8d7e13a25410fed753d11b83ba3b70af34885899aa11aac884c8741eec88795373fbdd6567f4f477df2cc8b27e9b695f3e7b3b5', 2);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
