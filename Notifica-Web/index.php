<?php

include 'pages.php';


$pageID = 'loginpage';
if(isset($_GET['page'])){
    $pageID = $_GET['page'];
}

if($pageID == 'loginpage'){
    $loginPage->GeneratePage();
}else if($pageID == 'adminpage'){
	$adminPage->GeneratePage();
}
else{
    $forbiddenPage->GeneratePage();
}


?>
