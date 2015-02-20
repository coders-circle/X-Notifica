<?php
require_once 'vars.php';
require_once 'classes/Page.php';

$loginPage = new Page;
$loginPage->AddStyleSheet("css/loginpage.css");
$loginPage->SetView("views/loginpage_view.php");
$loginPage->SetController("controls/loginpage_control.php");
$loginPage->DisableNavbar();


$adminPage = new Page;
$adminPage->AddStyleSheet("css/adminpage.css");
$adminPage->SetView("views/adminpage_view.php");
$adminPage->SetController("controls/adminpage_control.php");
$adminPage->AddTab("Home", "index.php?page=adminpage&tab=home");
$adminPage->AddTab("Students", "index.php?page=adminpage&tab=students");
$adminPage->AddTab("Employees", "index.php?page=adminpage&tab=employees");
$adminPage->AddTab("Courses", "index.php?page=adminpage&tab=courses");
$adminPage->AddTab("Examination", "index.php?page=adminpage&tab=examination");
$adminPage->AddJScript("js/Chart.js");
$adminPage->AddJScript("js/adminpage.js");

$forbiddenPage = new Page;
$forbiddenPage->AddStyleSheet("css/forbiddenpage.css");
$forbiddenPage->SetView("views/forbiddenpage_view.php");
//$forbiddenPage->SetController("controls/forbiddenpage_control.php");


?>
