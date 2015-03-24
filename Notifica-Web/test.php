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

            $stmt->close();
        }
        echo "</br>";
        echo $user->GetFacultyID();
        echo "</br>";
        echo $user->GetStudentBatch();
        if($stmt = $db->prepare("SELECT * from assignments WHERE UNIX_TIMESTAMP(changed_at) > ? AND (faculty_id = -1 OR faculty_id = ?) AND (year = -1 OR year = ?)")){
            echo "hello world";
            $stmt->bind_param('iii', $client_updated_at, $user->GetFacultyID(), $user->GetStudentBatch());
            $stmt -> execute();
            $result = $stmt->get_result();
            $currentIndex = 0;
            $assignments = array();
            while ($row = $result->fetch_assoc()){
                var_dump($row);
            }
        }$stmt->close();
    }
}catch(Exception $e){
}

?>
