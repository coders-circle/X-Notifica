<?php
require_once 'vars.php';
require_once 'classes/User.php';
require_once 'classes/Page.php';

$g_user = new User;

$g_adminPage = null;
$g_teacherPage = null;
$g_studentPage = null;
$g_loginPage = null;

if($g_user->LoggedIn()){
    $userType = $g_user->GetUserType();
    if($userType == 3){
        $g_adminPage = new Page;
        $g_adminPage->SetUser($g_user);
        $g_adminPage->AddStyleSheet("css/adminpage.css");
        $g_adminPage->SetView("views/adminpage_view.php");
        $g_adminPage->SetController("controls/adminpage_control.php");
        $g_adminPage->AddTab("Home", "index.php?page=adminpage&tab=home");
        $g_adminPage->AddTab("Students", "index.php?page=adminpage&tab=students");
        $g_adminPage->AddTab("Employees", "index.php?page=adminpage&tab=employees");
        $g_adminPage->AddTab("Courses", "index.php?page=adminpage&tab=courses");
        $g_adminPage->AddTab("Routine", "index.php?page=adminpage&tab=routine");
        $g_adminPage->AddJScript("js/Chart.js");
        $g_adminPage->AddJScript("js/adminpage.js");
    } else if($userType == 2){
        $g_teacherPage = new Page;
    } else if($userType == 1){
        $g_studentPage = new Page;
        $g_studentPage->SetUser($g_user);
        $g_studentPage->AddStyleSheet("css/studentpage.css");
        $g_studentPage->SetView("views/studentpage_view.php");
        $g_studentPage->SetController("controls/studentpage_control.php");
    } else {
    }
} else {
    $g_loginPage = new Page;
    $g_loginPage->AddStyleSheet("css/loginpage.css");
    $g_loginPage->SetView("views/loginpage_view.php");
    $g_loginPage->SetController("controls/loginpage_control.php");
    $g_loginPage->DisableNavbar();
    $g_loginPage->AddJScript("js/sha512.js");
    $g_loginPage->AddJScript("js/forms.js");
}

$g_forbiddenPage = new Page;
$g_forbiddenPage->AddStyleSheet("css/forbiddenpage.css");
$g_forbiddenPage->SetView("views/forbiddenpage_view.php");
//$forbiddenPage->SetController("controls/forbiddenpage_control.php");


?>
