<?php

$user = $GLOBALS['g_user'];

if(isset($_POST['un']) && isset($_POST['psss'])){
    $username = $_POST['un'];
	$password = $_POST['psss'];
	if ($user -> Login($username, $password) == true) {
        //$GLOBALS['g_adminPage']->SetUser($user);
        $userType = $user->GetUserType();
        if($userType == 3){
            header("Location: index.php?page=adminpage");
        }else if($userType == 2){
            header("Location: index.php?page=teacherpage");
        }else if($userType == 1){
            header("Location: index.php?page=studentpage");
        }else{

        }
    }else{echo 'oops! something went wrong :/';}
}else{
}


?>
