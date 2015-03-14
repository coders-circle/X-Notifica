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
    if(isset($_POST['studentname'])){
        $studentname = $_POST['studentname'];
        $faculty = $_POST['faculty'];
        $batch = $_POST['batch'];
        $roll = $_POST['roll'];
        $user = $GLOBALS['g_user'];
        $user->AddStudent($studentname, $faculty, $roll, $batch);
    }
}

else if($tabID == 'employees') {
    $adminPage->SetActiveTab(2);
}

else if($tabID == 'courses'){
    $adminPage->SetActiveTab(3);
}

else if($tabID == 'examination'){
    $adminPage->SetActiveTab(4);
}


?>
