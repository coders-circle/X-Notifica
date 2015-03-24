-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 24, 2015 at 10:30 PM
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `assignments`
--

INSERT INTO `assignments` (`id`, `faculty_id`, `subject_id`, `year`, `groups`, `summary`, `details`, `submission_date`, `poster_id`, `deleted`, `changed_at`) VALUES
(1, 500, 1, 2069, '', 'test123', 'test345', '2015-03-24', 1017, 0, '2015-03-25 05:35:14'),
(2, 400, 1, -1, '', 'test2', 'test2', '2015-03-23', 1014, 0, '2015-03-24 05:23:44'),
(3, -1, 1, 2069, 'B', 'dijddh', 'djshvs', '2015-04-24', 1016, 0, '2015-03-24 11:55:53'),
(4, -1, 1, 2069, '', 'hsisjvs', 'shsksvjw', '2015-04-24', 1016, 0, '2015-03-24 11:56:35');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `faculty_id`, `year`, `groups`, `summary`, `details`, `event_date`, `poster_id`, `deleted`, `changed_at`) VALUES
(1, 500, -1, '', 'HELLO', 'WORLD', '2015-03-24', 1, 0, '2015-03-24 13:18:29'),
(2, -1, -1, 'B', 'abcd', 'wert', '2016-04-24', 1010, 0, '2015-03-24 06:43:19'),
(3, 500, 2069, 'AB', 'zxcvcx', 'test', '2015-03-23', 1017, 0, '2015-03-24 06:43:27');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `routines`
--

INSERT INTO `routines` (`id`, `faculty_id`, `year`, `group`, `start_time`, `end_time`, `changed_at`) VALUES
(6, 500, 2069, 'A', 0, 1440, '2015-03-24 16:39:54');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=23 ;

--
-- Dumping data for table `routine_elements`
--

INSERT INTO `routine_elements` (`id`, `routine_id`, `subject_id`, `teacher_id`, `day`, `start_time`, `end_time`) VALUES
(6, 6, 3, 2, 0, 615, 660),
(7, 6, 4, 3, 0, 660, 780),
(8, 6, 5, 5, 0, 825, 870),
(9, 6, 6, 4, 0, 870, 915),
(10, 6, 6, 4, 1, 615, 705),
(11, 6, 7, 6, 1, 705, 825),
(12, 6, 7, 6, 2, 645, 660),
(13, 6, 8, 7, 2, 660, 780),
(14, 6, 9, 8, 2, 915, 960),
(15, 6, 8, 7, 3, 615, 705),
(16, 6, 9, 8, 3, 705, 780),
(17, 6, 5, 5, 4, 615, 705),
(18, 6, 4, 3, 4, 705, 780),
(19, 6, 3, 2, 4, 825, 915),
(20, 6, 6, 4, 5, 705, 780),
(21, 6, 3, 2, 5, 825, 795),
(22, 6, 9, 8, 5, 795, 960);

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
(1016, 'Ankit Mehta', 504, 500, 2069, 'A', 1, '0000-00-00 00:00:00', 1016);

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=10 ;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`id`, `code`, `name`, `faculty_id`, `changed_at`) VALUES
(3, 'CT202', 'Computer Graphics', 400, '2015-03-24 16:09:27'),
(4, 'CT101', 'Data Communication', 500, '2015-03-24 16:09:41'),
(5, 'CT508', 'Software Engineering', 500, '2015-03-24 16:10:01'),
(6, 'CT510', 'Computer Organization & Architecture', 500, '2015-03-24 16:10:20'),
(7, 'EL121', 'Instrumentation II', 400, '2015-03-24 16:10:33'),
(8, 'SH506', 'Probability & Statistics', 400, '2015-03-24 16:10:54'),
(9, 'SH 506', 'Communication English II', 500, '2015-03-24 16:11:15');

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
CREATE TABLE IF NOT EXISTS `teachers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `contact_number` varchar(100) NOT NULL,
  `faculty_id` int(11) NOT NULL,
  `changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

--
-- Dumping data for table `teachers`
--

INSERT INTO `teachers` (`id`, `user_id`, `name`, `contact_number`, `faculty_id`, `changed_at`, `updated_at`) VALUES
(1, 1017, 'Aditya Khatri', 'xxxx', 500, '2015-03-24 03:37:18', '0000-00-00 00:00:00'),
(2, 1028, 'Dr. Anil Verma', 'xxxx', 500, '2015-03-24 16:23:08', '0000-00-00 00:00:00'),
(3, 1029, 'Dr. Nanda Bikram Adhikari', 'xxxx', 500, '2015-03-24 16:24:43', '0000-00-00 00:00:00'),
(4, 1030, 'Prof. Dr. Subarna Shakya', 'xxxx', 500, '2015-03-24 16:25:16', '0000-00-00 00:00:00'),
(5, 1031, 'Dr. Arun Timilsina', 'xxxx', 500, '2015-03-24 16:25:37', '0000-00-00 00:00:00'),
(6, 1032, 'Dinesh Baniya Kshatri', 'xxxx', 500, '2015-03-24 16:26:02', '0000-00-00 00:00:00'),
(7, 1033, 'Sajana Shakya', 'xxxx', 500, '2015-03-24 16:26:24', '0000-00-00 00:00:00'),
(8, 1034, 'Prof. Pustun Pradhan', 'xxxx', 500, '2015-03-24 16:26:45', '0000-00-00 00:00:00');

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1035 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `salt`, `usertype`) VALUES
(1, 'fhx', '87d7de170920dbcabff61520b050dfa35d5cfc68f1ed9327027aeadcc67554c1307b59714fa4412bd80fe0f06fc797bef9d86440afae5d64d7e2cd156f1bb51c', '1cae87da42018b91f828bec5a1452bc2474e1f2e3b2fd0d7137d4048634f9a983f86ce0e9df0788985ed1b6b4440cb4d29aa2de92c196ed5296e14c69e60f129', 3),
(1013, '069BCT501', '327cc3388a675f94ff27ea905e85e870c7da1e4860a3aa1ae624c89a842227db730b538862af687c11c41a15837f007958848d6eaa5fdfe7682752a2e23d3525', '67d1ef4271a1221a92ab9ca3ecd8c541bbcb02a6c848e7934fbcf0ca9b775192ec7ca97f8ff7273d0672ab34a30ad5660080c377c8216afd1751066cf883d3f4', 1),
(1014, '069BCT502', 'e43d974aeaa197ac1b1ed785fb89315d99c80be984c7a0870e29eecf0f642319c3f1906324f0099ebc7c40a79656ae7b321ab531f85192dbe2c368067b80dd18', '11cf5cfe484d42127300efe10f8c97dec3d7af718f8dfdeffe70c0037ba6f2c4d6c50029b0f14e700c9abd03b4110505ed1014fddf0ca1a44324d2da2a755965', 1),
(1015, '069BCT503', 'a6d5ba1387ce8b696ec4337e34990385f5d953d6beeea0814e0da13cb8038cc01cfdce97219d6e820e913f988cf8a9c48330710781ee71f1d6fa08186abd5306', '70fb1e2e4dd956586e6c9adcd136cdab0ea24c012f383a3c43328b54a428b8b4f5a20934e473c41aa08fe68150ffe90712b10457088d9553af394913c07ddff3', 1),
(1016, '069BCT504', '82943cc98d2b27dd3ee097162de6756ef4d7a014521ed56a4fae01aeb3bd5b48058ee172207c8fc0c2495af13ac33ee59c4aa6c506c83824228e6447808d5edb', '877653db544a9efdaf783f878bdc842a99d5c8f73f0c6474a1b893cd7f3b176624a2ed3fa3f7c685024d0a3565892922d480f52cd9528f5e29999df00ab934e3', 1),
(1017, 'noob', 'eaaa2cad660e704f51c777f5012fc6a91787be6b0e2ba577555c8150509b639ded57717dd533eeb60cd63d83298d5856d1fb149d680f7a0a46ccbbd3e4a7d6a8', 'd56f7ad9d69006241ab65434b8d7e13a25410fed753d11b83ba3b70af34885899aa11aac884c8741eec88795373fbdd6567f4f477df2cc8b27e9b695f3e7b3b5', 2),
(1028, 'anil_verma', '5d70f5dc8d72ee47df413b925879bbd5b21938bb4b4ee86b01a80640ca20d185a9be35950ba246d4f3abacb1edab136028c9774e89f8066f6ce2ecaa77717d68', '497d37982fb609ac78bb04a1eb819e35cf325d3382e08a84254294a93af400543e57d57a5378e8624a8fa9c606a36a37ee8df3e3c558d20980258de627612f36', 2),
(1029, 'nanda_adhikari', '936c5184bab6eb6e87c4a3151a9ef6dc16a375a95afb3580f5733eb533102b2abffcc7c7710a1939d0b25af3cf02a65c94815cd5869d43dce6e13751dca091be', '5e8c281fe5c728de0cb66454f3e8ee60c875148f0dd9bf934d36222a80abeca4afe5474c251e1c259a43be05c485f9d02db1316f5fc4164355cfa47aef306cc2', 2),
(1030, 'subarna_shakya', 'f8dfbd53217f168cfb98682cf4c0ee130b9446cd3a31b12f20aeb526ec72b10a6c9fdff344947f6901b710ba0d339f93beb8e34c027308f5c39e610509892dd8', '35fd502c8631d8eb84c8dc765e1be151a23566b5016efe743826a6d6666ac16a8aa596e3929e2d117d08c97cea799f1ae3d6bbf459ddd89a4e0f78a6720afcc9', 2),
(1031, 'arun_timilsina', '4b9089d835af0586064a6ff74b462251c2220dd934354cae341b0dfd8994647f2b468094a830b7f7a0abbf8df84d812d04ac23d0e0c09ef4a5d9509ee8578290', 'b1b0c8ceb6512c9fcbfc2396adf4dec074eef6216cc00a1c465d3d11bc0269f30f4b6aef5b44394714ac56efd4ebafb34bc5d12aa47c05b80bfdb8f2edf2bc29', 2),
(1032, 'dinesh_baniya', 'f68915df4726c50dd5580d8e83d94e665ae319ad78807b5e88e6717f0a2bf4666c47ae788b3dd8ca1696483b500ae65f796c4e348d21fe4ea94ce56b30ae83f3', 'd0a3f001ee9cdda0f20d980368c2cc84cff8ad137a0f08783224297e2c580c91bf9a10bcfd627aebda427c61a892a6b0bca9d738f63020bb524ccb40c5b9f0e8', 2),
(1033, 'sajana_shakya', 'dc324673868a75cb6cae100b0345a1c52a69766a64461fe7c49fe3b563e21c5f5e08578b3318c6ba05aabda3481e0ab5372d735339269ce9bb491dffc67aa672', '5eb445d15862a0240ad4618ab4a2ee0b88267d6f3cb3b24e8321c628e5e5887ea4d94fa4b527cc2ad2d555956612a712c5eed11a7a422a8b74daa9403b8dc360', 2),
(1034, 'pustun_pradhan', 'a704d2963970b241bb8a1f1bdf7ff7e83ecce0d6aabd7519bc400826124c3dbfcf117477f0d0ef6991396d9d5b69dd7ce06b47d6ad4913f0dc67fb934fb79689', '7890704b2613c494f279a78ac335b176136fff0203c47425bcfb399dd8f0a77d88a81fdbad3186d58f5525bc50d3cd1154233f1ac98a5855242cdc06332eb575', 2);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
