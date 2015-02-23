<?php
require_once 'vars.php';
require_once 'classes/User.php';
require_once 'classes/Page.php';

$g_user = new User;

$g_loginPage = new Page;
$g_loginPage->AddStyleSheet("css/loginpage.css");
$g_loginPage->SetView("views/loginpage_view.php");
$g_loginPage->SetController("controls/loginpage_control.php");
$g_loginPage->DisableNavbar();
$g_loginPage->AddJScript("js/sha512.js");
$g_loginPage->AddJScript("js/forms.js");


$g_adminPage = new Page;
if($g_user->LoggedIn()){
    $g_adminPage->SetUser($g_user);
}
$g_adminPage->AddStyleSheet("css/adminpage.css");
$g_adminPage->SetView("views/adminpage_view.php");
$g_adminPage->SetController("controls/adminpage_control.php");
$g_adminPage->AddTab("Home", "index.php?page=adminpage&tab=home");
$g_adminPage->AddTab("Students", "index.php?page=adminpage&tab=students");
$g_adminPage->AddTab("Employees", "index.php?page=adminpage&tab=employees");
$g_adminPage->AddTab("Courses", "index.php?page=adminpage&tab=courses");
$g_adminPage->AddTab("Examination", "index.php?page=adminpage&tab=examination");
$g_adminPage->AddJScript("js/Chart.js");
$g_adminPage->AddJScript("js/adminpage.js");

$g_forbiddenPage = new Page;
$g_forbiddenPage->AddStyleSheet("css/forbiddenpage.css");
$g_forbiddenPage->SetView("views/forbiddenpage_view.php");
//$forbiddenPage->SetController("controls/forbiddenpage_control.php");


?>
