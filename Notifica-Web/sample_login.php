<?php

header('Content-type: application/json');

$input_data = file_get_contents("php://input");
$input_array = json_decode($input_data, true);

$user_id = $input_array["user_id"];
$encrPassword = $input_array["password"];

// For test purposes: user-id = bibek, password = dahal

$output_array = array();
// Make sure these responses are exactly like these, since client app depends on them
$output_array["message_type"] = "Login Result";
if ($user_id != "bibek") {
    $output_array["login_result"] = "Failure";
    $output_array["failure_message"] = "Invalid User";
}
elseif (strtoupper($encrPassword) != strtoupper(hash("sha512", "dahal"))) { // since hashed value is hex string, make sure 0xab == 0xAB
    $output_array["login_result"] = "Failure";
    $output_array["failure_message"] = "Invalid Password";
    $output_array["password"] = hash("sha512", "dahal");
}
else {
    $output_array["login_result"] = "Success";
    $output_array["user_type"] = "Student";  // Or Teacher
    $output_array["name"] = "Bibek Dahal";   // Full name of user
}
echo json_encode($output_array)
?>
