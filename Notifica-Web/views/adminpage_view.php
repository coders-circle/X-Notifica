<?php
$tabID = 'home';
if(isset($_GET['tab'])){
    $tabID = $_GET['tab'];
}

$views = array(
    'home'          => 'views/adminpage_hometab_view.php',
    'students'      => 'views/adminpage_studentstab_view.php',
    'employees'     => 'views/adminpage_employeestab_view.php',
    'courses'       => 'views/adminpage_coursestab_view.php',
    'examination'   => 'views/adminpage_examinationtab_view.php'
);

include $views[$tabID];

?>
