<?php

include 'pages.php';

$pageID = 'loginpage';
if(isset($_GET['page'])){
    $pageID = $_GET['page'];
}

if($pageID == 'loginpage'){
    if($g_loginPage != null){
        $g_loginPage->GeneratePage();
    }else{
        $userType = $g_user->GetUserType();
        if($userType == 3){
            header("Location: index.php?page=adminpage");
        }else if($userType == 2){
            header("Location: index.php?page=teacherpage");
        }else if($userType == 1){
            header("Location: index.php?page=studentpage");
        }else{
            // very unexpected place to be
            // may be throw some exception
        }
    }
}else if($pageID == 'adminpage'){
    if($g_adminPage != null){
	    $g_adminPage->GeneratePage();
    }else{
        $g_forbiddenPage->GeneratePage();
    }
}else if($pageID == 'teacherpage'){
    if($g_teacherPage != null){
        $g_teacherPage->GeneratePage();
    }else{
        $g_forbiddenPage->GeneratePage();
    }
}else if($pageID == 'studentpage'){
    if($g_studentPage != null){
        $g_studentPage->GeneratePage();
    }else{
        $g_forbiddenPage->GeneratePage();
    }
}else{
    $g_forbiddenPage->GeneratePage();
}


?>
