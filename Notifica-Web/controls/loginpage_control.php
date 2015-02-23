<?php

$user = $GLOBALS['g_user'];

if(isset($_POST['un']) && isset($_POST['psss'])){
    $username = $_POST['un'];
	$password = $_POST['psss'];
	if ($user -> Login($username, $password) == true) {
        $GLOBALS['g_adminPage']->SetUser($user);
        header("Location: index.php?page=adminpage");
    }else{echo 'oops! something went wrong :/';}
}else{
}


?>
