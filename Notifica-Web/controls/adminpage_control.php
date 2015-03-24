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
    if (isset($_POST['faculty']) && isset($_POST['batch'])) {
        $srs = $_POST['#sun_routinestart'];
        $sre = $_POST['#sun_routineend'];
        $sru = $_POST['#sun_routinesubject'];
        $srt = $_POST['#sun_routineteacher'];
        $mrs = $_POST['#mon_routinestart'];
        $mre = $_POST['#mon_routineend'];
        $mru = $_POST['#mon_routinesubject'];
        $mrt = $_POST['#mon_routineteacher'];
        $trs = $_POST['#tue_routinestart'];
        $tre = $_POST['#tue_routineend'];
        $tru = $_POST['#tue_routinesubject'];
        $trt = $_POST['#tue_routineteacher'];
        $wrs = $_POST['#wed_routinestart'];
        $wre = $_POST['#wed_routineend'];
        $wru = $_POST['#wed_routinesubject'];
        $wrt = $_POST['#wed_routineteacher'];
        $ttrs = $_POST['#thu_routinestart'];
        $ttre = $_POST['#thu_routineend'];
        $ttru = $_POST['#thu_routinesubject'];
        $ttrt = $_POST['#thu_routineteacher'];
        $frs = $_POST['#fri_routinestart'];
        $fre = $_POST['#fri_routineend'];
        $fru = $_POST['#fri_routinesubject'];
        $frt = $_POST['#fri_routineteacher'];
        $strs = $_POST['#sat_routinestart'];
        $stre = $_POST['#sat_routineend'];
        $stru = $_POST['#sat_routinesubject'];
        $strt = $_POST['#sat_routineteacher'];

        $faculty_id = intval($POST['faculty']);
        $batch = intval($POST['batch']);
        $group = $POST['group'];
        $user = $GLOBALS['g_user'];

        $elements = array();
        $count = 0;
        for ($i=0; $i<count($sru); $i++) {
            $element = array();
            $element['subject_id']  = intval($sru[$i]);
            $element['teacher_id']  = intval($srt[$i]);
            $element['day'] = 0;
            $element['starttime']   = GetTime($srs[$i]);
            $element['endtime']     = GetTime($sre[$i]);
            $elements[$count++] = $element;
            $element = array();
        }
        for ($i=0; $i<count($mru); $i++) {
            $element['subject_id']  = intval($mru[$i]);
            $element['teacher_id']  = intval($mrt[$i]);
            $element['day'] = 1;
            $element['starttime']   = GetTime($mrs[$i]);
            $element['endtime']     = GetTime($mre[$i]);
            $elements[$count++] = $element;
        }
        for ($i=0; $i<count($tru); $i++) {
            $element = array();
            $element['subject_id']  = intval($tru[$i]);
            $element['teacher_id']  = intval($trt[$i]);
            $element['day'] = 2;
            $element['starttime']   = GetTime($trs[$i]);
            $element['endtime']     = GetTime($tre[$i]);
            $elements[$count++] = $element;
        }
        for ($i=0; $i<count($wru); $i++) {
            $element = array();
            $element['subject_id']  = intval($wru[$i]);
            $element['teacher_id']  = intval($wrt[$i]);
            $element['day'] = 3;
            $element['starttime']   = GetTime($wrs[$i]);
            $element['endtime']     = GetTime($wre[$i]);
            $elements[$count++] = $element;
        }
        for ($i=0; $i<count($ttru); $i++) {
            $element = array();
            $element['subject_id']  = intval($ttru[$i]);
            $element['teacher_id']  = intval($ttrt[$i]);
            $element['day'] = 4;
            $element['starttime']   = GetTime($ttrs[$i]);
            $element['endtime']     = GetTime($ttre[$i]);
            $elements[$count++] = $element;
        }
        for ($i=0; $i<count($fru); $i++) {
            $element = array();
            $element['subject_id']  = intval($fru[$i]);
            $element['teacher_id']  = intval($frt[$i]);
            $element['day'] = 5;
            $element['starttime']   = GetTime($frs[$i]);
            $element['endtime']     = GetTime($fre[$i]);
            $elements[$count++] = $element;
        }
        for ($i=0; $i<count($stru); $i++) {
            $element = array();
            $element['subject_id']  = intval($stru[$i]);
            $element['teacher_id']  = intval($strt[$i]);
            $element['day'] = 6;
            $element['starttime']   = GetTime($strs[$i]);
            $element['endtime']     = GetTime($stre[$i]);
            $elements[$count++] = $element;
        }
   
 //       for ($i=0; $i<$count; $i++) {
 //           $arr = $elements[$i];
 //           foreach($arr as $key => $value)
 //               echo $key . " = ". $value . "<br/>";
 //       }      
        $user->AddRoutine($faculty_id, $batch, $group, 0, 24*60, $elements);
    }
    $adminPage->SetActiveTab(4);
}


?>
