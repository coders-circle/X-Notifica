<?php

$tabID = 'home';
if(isset($_GET['tab'])){
    $tabID = $_GET['tab'];
}

$adminPage = $GLOBALS['g_adminPage'];

if($tabID == 'home') {
    $adminPage->SetActiveTab(0);
}

else if($tabID == 'students') {
    $adminPage->SetActiveTab(1);
    if(isset($_POST['studentname']) && isset($_POST['batch']) && isset($_POST['roll'])){
        $studentname = $_POST['studentname'];
        $batch = $_POST['batch'];
        $roll = $_POST['roll'];
        $user = $GLOBALS['g_user'];
        $user->AddStudent($studentname, $roll, $batch);
    }
}

else if($tabID == 'employees') {
    $adminPage->SetActiveTab(2);
    if(isset($_POST['employeename']) && isset($_POST['employeeun']) && isset($_POST['contact'])){
        $employeename = $_POST['employeename'];
        $employeeun = $_POST['employeeun'];
        $contact = $_POST['contact'];
        $user = $GLOBALS['g_user'];
        $user->AddTeacher($employeename, $employeeun, $contact);
    }
}

else if($tabID == 'courses'){
    $adminPage->SetActiveTab(3);
    if(isset($_POST['subjectname']) && isset($_POST['subjectcode']) && isset($_POST['faculty'])){
        $subjectname = $_POST['subjectname'];
        $subjectcode = $_POST['subjectcode'];
        $facultyid = $_POST['faculty'];
        $user = $GLOBALS['g_user'];
        $user->AddCourse($subjectname, $subjectcode, $facultyid);
    }
}

else if($tabID == 'examination'){
    $adminPage->SetActiveTab(4);
}


?>
