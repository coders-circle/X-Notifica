<?php

function GetFacultyId($db, $code) {
    if ($code == "")
        return -1;
    $id = -1;
    if($stmt = $db->prepare("SELECT id from faculties WHERE code = ?")){
        $stmt->bind_param('s', $code);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($id);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $id;
}


function GetUserName($db, $id) {
    $username = "";
    if($stmt = $db->prepare("SELECT username from users WHERE id = ?")){
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($username);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $username;
}

function GetTeacherUserId($db, $id) {
    $userid = -1;
    if($stmt = $db->prepare("SELECT user_id from teachers WHERE id = ?")){
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($userid);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $userid;
}

function GetTeacherUserName($db, $id) {
    return GetUserName($db, GetTeacherUserId($db, $id));
}

function GetSubjectId($db, $code) {
    $id = -1;
    if($stmt = $db->prepare("SELECT id from subjects WHERE code = ?")){
        $stmt->bind_param('s', $code);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($id);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $id;
}

function GetSubjectCode($db, $id) {
    $code = "";
    if($stmt = $db->prepare("SELECT code from subjects WHERE id = ?")){
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($code);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $code;
}

function GetFacultyCode($db, $id) {
    $code = "";
    if($stmt = $db->prepare("SELECT code from faculties WHERE id = ?")){
        $stmt->bind_param('i', $id);;
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($code);
        
        $stmt->fetch();
        $stmt->free_result();
        $stmt->close();
    }
    return $code;
}

$routineSubjects = array();
$routineTeachers = array();
function GetRoutineElements($db, $id, $teacherId=-1) {
    global $routineSubjects, $routineTeachers;
    $elements = array();

    $stmt = null;
    if ($teacherId == -1)
        $stmt = $db->prepare("SELECT * from routine_elements WHERE routine_id = ?");
    else
        $stmt = $db->prepare("SELECT * from routine_elements WHERE routine_id = ? AND teacher_id = ?");
    if($stmt != null){
        if ($teacherId == -1)
            $stmt->bind_param('i', $id);
        else
            $stmt->bind_param('ii', $id, $teacherId);
        $stmt -> execute();
        $result = $stmt->get_result();
        $currentIndex = 0;
        while ($row = $result->fetch_assoc()){
            $element = array();
            $element["start_time"] = $row["start_time"];
            $element["end_time"] = $row["end_time"];
            $element["day"] = $row["day"];
            $element["teacher_user_id"] = GetTeacherUserName($db, $row["teacher_id"]);
            $element["subject_code"] = GetSubjectCode($db, $row["subject_id"]);
            $element["type"] = $row["type"];
            $routineSubjects[] = $row["subject_id"];
            $routineTeachers[] = $row["teacher_id"];
            $elements[$currentIndex++] = $element;
        }
        $stmt->close();
    }
    return $elements;
}

?>
