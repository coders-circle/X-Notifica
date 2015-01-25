<?php

$tabID = 'home';
if(isset($_GET['tab'])){
    $tabID = $_GET['tab'];
}

$adminPage = $GLOBALS['adminPage'];

if($tabID == 'home') {
    $adminPage->SetActiveTab(0);
}

else if($tabID == 'students') {
    $adminPage->SetActiveTab(1);
}

else if($tabID == 'employees') {
    $adminPage->SetActiveTab(2);
}

else if($tabID == 'courses'){
    $adminPage->SetActiveTab(3);
}

else if($tabID == 'examination'){
    $adminPage->SetActiveTab(4);
}


?>
