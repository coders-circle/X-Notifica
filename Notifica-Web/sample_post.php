<?php
require_once 'classes/User.php';
require_once 'Helpers.php';

header('Content-type: application/json');

$input_data = file_get_contents("php://input");
$input_array = json_decode($input_data, true);

$user_id = $input_array["user_id"];
$encrPassword = $input_array["password"];
$encrPassword = strtolower($encrPassword);


$output_array = array();
$output_array["message_type"] = "Post Response";

$user = new User;
try{
    // Login Successful
    $user->LoginTest($user_id, $encrPassword);
    $db = $user->GetDB();
    if ($input_array["message_type"] == "Post Event" || $input_array["message_type"] == "Post Assignment") {
        $userType = $user->GetUserType();

        $canPost = false;
        if($userType == 2){
            $canPost = true;
        }else if($userType == 1){
            if ($user->GetStudentPrivilage() == 1)
                $canPost = true;
        }
        if (!$canPost)
            throw new Exception("Failed to Post");

        $summary = $input_array["summary"];
        $details = $input_array["details"];
        $faculty_code = $input_array["faculty_code"];
        $faculty_id = GetFacultyId($db, $faculty_code);
        $year = $input_array["year"];
        if ($year == 0)
            $year = -1;
        $groups = strtoupper($input_array["groups"]);
        $date = $input_array["date"];
        
        if ($input_array["message_type"] == "Post Assignment") {
            $subject_code = $input_array["subject_code"];
            $subject_id = GetSubjectId($db, $subject_code);
            if ($insert_stmt = $db->prepare("INSERT INTO assignments (summary, details, faculty_id, subject_id, year, groups, poster_id, submission_date) VALUES (?, ?, ?, ?, ?, ?, ?, FROM_UNIXTIME(?))")) {
                $insert_stmt->bind_param('ssiiisii', $summary, $details, $faculty_id, $subject_id, $year, $groups, $user->GetUserId(), $date);
                if (! $insert_stmt->execute()) {
                    throw new Exception('Failed to add assignment to database');
                }
            }
            else
                throw new Exception('Failed to add assignment to database');
        }
        else {
            if ($insert_stmt = $db->prepare("INSERT INTO events (summary, details, faculty_id, year, groups, poster_id, event_date) VALUES (?, ?, ?, ?, ?, ?, FROM_UNIXTIME(?))")) {
                $insert_stmt->bind_param('ssiisii', $summary, $details, $faculty_id, $year, $groups, $user->GetUserId(), $date);
                if (! $insert_stmt->execute()) {
                    throw new Exception('Failed to add event to database');
                }
            }
            else
                throw new Exception('Failed to add event to database');
        }
        $output_array["post_result"] = "Success";
    }
}
catch(Exception $e){
    $output_array["post_result"] = "Failure";
    $output_array["failure_message"] = $e->getMessage();
}

echo json_encode($output_array);
?>
