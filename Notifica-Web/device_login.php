<?php

require_once 'classes/User.php';

header('Content-type: application/json');

$input_data = file_get_contents("php://input");
$input_array = json_decode($input_data, true);

$user_id = $input_array["user_id"];
$encrPassword = $input_array["password"];
$encrPassword = strtolower($encrPassword);


$output_array = array();
// Make sure these responses are exactly like these, since client app depends on them
$output_array["message_type"] = "Login Result";

$user = new User;
try{
//if($user->Login($user_id, $encrPassword)){
    // Login Successful
    $user->LoginTest($user_id, $encrPassword);
    $output_array["login_result"] = "Success";
    $userType = $user->GetUserType();
    if($userType == 2){

    }else if($userType == 1){
        $output_array["user_type"] = "Student";  // Or Teacher
    }
    $output_array["name"] = $user->GetFullName();
    //$user->Logout();
    //setcookie("bs", "", time()-3600);
}
//else{
catch(Exception $e){
    $output_array["login_result"] = "Failure";
    $output_array["failure_message"] = $e->getMessage();
}

echo json_encode($output_array)
?>
