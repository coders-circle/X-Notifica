<?php
function GetTime($str) {
    $min = 0;
    $hr = 0;
    sscanf($str, "%d:%d", $hr, $min);
    return $hr*60+$min;
}

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

else if($tabID == 'routine'){
    $dayarr = [ 'sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat' ];
    if (isset($_POST['faculty']) && isset($_POST['batch'])) {
        for ($day = 0; $day < 7; ++$day) {
            $srs[$day] = $_POST['#'.$dayarr[$day].'_routinestart'];
            $sre[$day] = $_POST['#'.$dayarr[$day].'_routineend'];
            $sru[$day] = $_POST['#'.$dayarr[$day].'_routinesubject'];
            $srt[$day] = $_POST['#'.$dayarr[$day].'_routineteacher'];
        }

        $batch = intval($_POST['batch']);
        $group = $_POST['group'];
        $user = $GLOBALS['g_user'];

        $elements = array();
        $count = 0;
        for ($day=0; $day<7; $day++) {
        for ($i=0; $i<count($sru[$day]); $i++) {
            $element = array();
            $element['subject_id']  = intval($sru[$day][$i]);
            $element['teacher_id']  = intval($srt[$day][$i]);
            $element['day'] = $day;
            $element['starttime']   = GetTime($srs[$day][$i]);
            $element['endtime']     = GetTime($sre[$day][$i]);
            $elements[$count++] = $element;
            $element = array();
        }
        }
        
   
//        for ($i=0; $i<$count; $i++) {
//            $arr = $elements[$i];
//            foreach($arr as $key => $value)
//                echo $key . " = ". $value . "<br/>";
//        }      
        $user->AddRoutine($batch, $group, 0, 24*60, $elements);
    }
    $adminPage->SetActiveTab(4);
}


?>
