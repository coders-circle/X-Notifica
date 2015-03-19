<?php

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
    $updated_at = $input_array["updated_at"];

    $output_array["message_type"] = "Database Update";

    // Get a list of changed data since $updated_at time from the database
    // Note that $updated_at is integer in seconds

    // Expected arrays are:
    //  - faculties
    //  - subjects
    //  - teachers
    //  - routine
    //  - assignments
    //  - events

    $faculties = array();
    $faculties[0] = array();
    $faculties[0]["code"] = "BCT";
    $faculties[0]["name"] = "Bachelor of Computer Engineering";

    $teachers = array();
    $teachers[0] = array();
    $teachers[0]["user_id"] = "bibek_khtramanxe";
    $teachers[0]["name"] = "Bibek Dahal";
    $teachers[0]["contact"] = "+977-98666333";

    $subjects = array();
    $subjects[0] = array();
    $subjects[0]["code"] = "CT234";
    $subjects[0]["name"] = "Software Engineering";
    $subjects[1] = array();
    $subjects[1]["code"] = "EX456";
    $subjects[1]["name"] = "Computer Graphics";

    if (true /*replace this condition by testing if routine of user_id has changed */){
        $routine = array();
        $routine["start_time"] = 10*60+15; // 10:15
        $routine["end_time"] = 17*60+0;    // 17:00

        // Get each routine element and store in $elements
        $elements = array();
        $elements[0] = array();
        $elements[0]["subject_code"] = "CT234";
        $elements[0]["teacher_user_id"] = "bibek_khtramanxe";
        $elements[0]["day"] = 1;              // Monday
        $elements[0]["start_time"] = 11*60+5; // 11:05
        $elements[0]["end_time"] = 11*60+55;  // 11:55
        //...

        $routine["elements"] = $elements;
    }

    $assignments = array();
    $assignments[0] = array();
    $assignments[0]["id"] = 2; // NOTE: This id is directly fetched from the SQL database and is synchronized with SQL database in client
    $assignments[0]["date"] = 1233456;  // Time in "SECONDS";
    $assignments[0]["subject_code"] = "EX456";
    $assignments[0]["summary"] = "Summary";
    $assignments[0]["details"] = "Details";

    $events = array();
    $events[0] = array();
    $events[0]["id"] = 5; // NOTE: This id is directly fetched from the SQL database and is synchronized with SQL database in client
    $events[0]["date"] = 1233456;  // Time in "SECONDS";
    $events[0]["summary"] = "Summary";
    $events[0]["details"] = "Details";


    // Finally the latest time of update that the client will record; this time is later returned in "Updated Info" message
    $tm = time();

    // Now form the json
    $output_array["faculties"] = $faculties;
    $output_array["teachers"] = $teachers;
    $output_array["subjects"] = $subjects;
    $output_array["routine"] = $routine;
    $output_array["assignments"] = $assignments;
    $output_array["events"] = $events;
//    $output_array["routine"] = $routine;
    $output_array["time"] = $tm;
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
