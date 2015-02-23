<?php

include 'pages.php';

$pageID = 'loginpage';
if(isset($_GET['page'])){
    $pageID = $_GET['page'];
}

if($pageID == 'loginpage'){
    $g_loginPage->GeneratePage();
}else if($pageID == 'adminpage'){
	$g_adminPage->GeneratePage();
}
else{
    $g_forbiddenPage->GeneratePage();
}


?>
