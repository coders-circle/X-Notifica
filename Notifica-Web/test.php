<?php
include_once('classes/User.php');



try{

    $user = new User;
    $user->LoginTest("069BCT501", hash('sha512', "069BCT501"));
    $db = $user->GetDB();
    $userType = $user->GetUserType();
    $updated_at = strtotime("10 September 2000");
    if($userType == 2){
    }else if($userType == 1){
        echo "hello world";
        if($stmt = $db->prepare("SELECT * from faculties WHERE changed_at > ?")){
            $stmt->bind_param('i', $updated_at);
            $stmt -> execute();
            $result = $stmt->get_result();
            $currentIndex = 0;
            $faculties = array();

            while ($row = $result->fetch_assoc()){
                $faculties[$currentIndex++] = $row;
                var_dump($row);
            }
        }
    }
}catch(Exception $e){
}

?>
