<?php

require_once 'classes/User.php';
require_once 'Helpers.php';

header('Content-type: application/json');

$input_data = file_get_contents("php://input");
$input_array = json_decode($input_data, true);


/*
We get two types of messages:
1. Update Request
 - Send new data to the client

2. Updated Info
 - Client is notifying that an update has been successful, the time of update is sent by the client
*/

$output_array = array();
if ($input_array["message_type"] == "Update Request") {

    $user_id = $input_array["user_id"];
    $password = $input_array["password"];
    $password = strtolower($password);
    $client_updated_at = $input_array["updated_at"];

    $output_array["message_type"] = "Database Update";

    $user = new User;

    // Get a list of changed data since $updated_at time from the database
    // Note that $updated_at is integer in seconds

    // Expected arrays are:
    //  - faculties
    //  - subjects
    //  - teachers
    //  - routine
    //  - assignments
    //  - events


    try{
        $user->LoginTest($user_id, $password);
        $db = $user->GetDB();
        $userType = $user->GetUserType();
        if($userType == 1 or $userType == 2){
            
            if($stmt = $db->prepare("SELECT * from faculties WHERE UNIX_TIMESTAMP(changed_at) > ?")){
                $stmt->bind_param('i', $client_updated_at);
                $stmt -> execute();
                $result = $stmt->get_result();
                $currentIndex = 0;
                $faculties = array();
                while ($row = $result->fetch_assoc()){
                    $faculty = array();
                    $faculty["code"] = $row["code"];
                    $faculty["name"] = $row["name"];
                    $faculties[$currentIndex++] = $faculty;
                }
                if ($currentIndex > 0)
                    $output_array["faculties"] = $faculties;
                $stmt->close();
            }
            if ($userType == 1) {
                if($stmt = $db->prepare("SELECT * from routines WHERE UNIX_TIMESTAMP(changed_at) > ? AND faculty_id = ? AND year = ?")){
                    $stmt->bind_param('iii', $client_updated_at, $user->GetFacultyID(), $user->GetStudentBatch());
                    $stmt -> execute();
                    $result = $stmt->get_result();
                    $currentIndex = 0;
                    if ($row = $result->fetch_assoc()){
                        $routine = array();
                        $routine["start_time"] = $row["start_time"];
                        $routine["end_time"] = $row["end_time"];
                        $routine["elements"] = GetRoutineElements($db, $row["id"]);
                        $output_array["routine"] = $routine;
                    }
                    $stmt->close();
                }
            } elseif ($userType == 2) {
                if($stmt = $db->prepare("SELECT * from routines WHERE UNIX_TIMESTAMP(changed_at) > ?")){
                    $stmt->bind_param('i', $client_updated_at);
                    $stmt -> execute();
                    $result = $stmt->get_result();
                    $currentIndex = 0;
                    $routines = array();
                    while ($row = $result->fetch_assoc()){
                        $elements = GetRoutineElements($db, $row["id"], $user->GetID());
                        $routine = array();
                        $routine["year"] = $row["year"];
                        $routine["group"] = $row["group"];
                        $routine["faculty_code"] = GetFacultyCode($db, $row["faculty_id"]);
                        $routine["elements"] = $elements;
                        $routines[$currentIndex++] = $routine;
                    }
                    if ($currentIndex > 0)
                        $output_array["routines"] = $routines;
                    $stmt->close();
                }
            }
            
            if (count($routineSubjects) > 0)
            if($stmt = $db->prepare("SELECT * from subjects WHERE id IN (".implode(',',array_map('intval', $routineSubjects)).") AND UNIX_TIMESTAMP(changed_at) > ?")){
                $stmt->bind_param('i', $client_updated_at);
                $stmt -> execute();
                $result = $stmt->get_result();
                $currentIndex = 0;
                $subjects = array();
                while ($row = $result->fetch_assoc()){
                    $subject = array();
                    $subject["code"] = $row["code"];
                    $subject["name"] = $row["name"];
                    $subject["faculty_code"] = GetFacultyCode($db, $row["faculty_id"]);
                    $subjects[$currentIndex++] = $subject;
                }
                if ($currentIndex > 0)
                    $output_array["subjects"] = $subjects;
                $stmt->close();
            }
            
            if ($userType == 1 && count($routineTeachers) > 0)
            if($stmt = $db->prepare("SELECT * from teachers WHERE id IN (".implode(',',array_map('intval', $routineTeachers)).") AND UNIX_TIMESTAMP(changed_at) > ?")){
                $stmt->bind_param('i', $client_updated_at);
                $stmt -> execute();
                $result = $stmt->get_result();
                $currentIndex = 0;
                $teachers = array();
                while ($row = $result->fetch_assoc()){
                    $teacher = array();
                    $teacher["name"] = $row["name"];
                    $teacher["user_id"] = $row["user_id"];
                    $teacher["contact"] = $row["contact_number"];
                    $teacher["faculty_code"] = GetFacultyCode($db, $row["faculty_id"]);
                    $teachers[$currentIndex++] = $teacher;
                }
                if ($currentIndex > 0)
                    $output_array["teachers"] = $subjects;
                $stmt->close();
            }
            $stmt = null;
            if ($userType == 1)
                $stmt = $db->prepare("SELECT * from assignments WHERE UNIX_TIMESTAMP(changed_at) > ? AND (faculty_id = -1 OR faculty_id = ?) AND (year = -1 OR year = ?)");
            else
                $stmt = $db->prepare("SELECT * from assignments WHERE UNIX_TIMESTAMP(changed_at) > ? AND poster_id = ?");
            if ($stmt != null) {
                if ($userType == 1)
                    $stmt->bind_param('iii', $client_updated_at, $user->GetFacultyID(), $user->GetStudentBatch());
                else
                    $stmt->bind_param('ii', $client_updated_at, GetTeacherUserId($db, $user->GetID()));
                $stmt -> execute();
                $result = $stmt->get_result();
                $currentIndex = 0;
                $assignments = array();
                while ($row = $result->fetch_assoc()){
                    if ($row["groups"] != "") {
                        if (strpos($row["groups"], $user->GetStudentGroup()) == false)
                            continue;
                    }
                    $assignment = array();
                    $assignment["id"] = $row["id"];
                    $assignment["subject_code"] = GetSubjectCode($db, $row["subject_id"]);
                    $assignment["poster_id"] = GetUserName($db, $row["poster_id"]);
                    $assignment["date"] = strtotime($row["submission_date"]);
                    $assignment["summary"] = $row["summary"];
                    $assignment["details"] = $row["details"];
                    if ($userType == 2) {
                        $assignment["year"] = $row["year"];
                        $assignment["faculty_code"] = GetFacultyCode($db, $row["faculty_id"]);
                        $assignment["groups"] = $row["groups"];
                    }
                    $assignments[$currentIndex++] = $assignment;
                }
                if ($currentIndex > 0)
                    $output_array["assignments"] = $assignments;
                $stmt->close();
            }
            $stmt = null;
            if ($userType == 1)
                $stmt = $db->prepare("SELECT * from events WHERE UNIX_TIMESTAMP(changed_at) > ? AND (faculty_id = -1 OR faculty_id = ?) AND (year = -1 OR year = ?)");
            else
                $stmt = $db->prepare("SELECT * from events WHERE UNIX_TIMESTAMP(changed_at) > ? AND poster_id = ?");
            if ($stmt != null) {
                if ($userType == 1)
                    $stmt->bind_param('iii', $client_updated_at, $user->GetFacultyID(), $user->GetStudentBatch());
                else
                    $stmt->bind_param('ii', $client_updated_at, GetTeacherUserId($db, $user->GetID()));
                $stmt -> execute();
                $result = $stmt->get_result();
                $currentIndex = 0;
                $events = array();
                while ($row = $result->fetch_assoc()){
                    if ($row["groups"] != "") {
                        if (strpos($row["groups"], $user->GetStudentGroup()) == false)
                            continue;
                    }
                    $event = array();
                    $event["id"] = $row["id"];
                    $event["poster_id"] = GetUserName($db, $row["poster_id"]);
                    $event["date"] = strtotime($row["event_date"]);
                    $event["summary"] = $row["summary"];
                    $event["details"] = $row["details"];
                    if ($userType == 2) {
                        $event["year"] = $row["year"];
                        $event["faculty_code"] = GetFacultyCode($db, $row["faculty_id"]);
                        $event["groups"] = $row["groups"];
                    }
                    $events[$currentIndex++] = $event;
                }
                if ($currentIndex > 0)
                    $output_array["events"] = $events;
                $stmt->close();
            }
        }
        // Finally the latest time of update that the client will record; this time is later returned in "Updated Info" message
        $tm = time();
        $output_array["time"] = $tm;
        $output_array["update_result"] = "Success";

    }
    catch(Exception $e){
        $output_array["update_result"] = "Failure";
        $output_array["failure_message"] = $e->getMessage();
    }
}
elseif ($input_array["message_type"] == "Updated Info") {
    $user_id = $input_array["user_id"];
    $updated_at = $input_array["updated_at"];
    // Store the $updated_at integer in database of user (user_id) at column "updated_at"
    // Note that $updated_at is integer in seconds
}
else {
    $output_array["message_type"] = "Invalid Request";
}


echo json_encode($output_array);
?>
