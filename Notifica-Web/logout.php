<?php
require_once 'pages.php';

$user = $GLOBALS['g_user'];
$user->Logout();
setcookie("bs", "", time()-3600);
header('Location: index.php');
?>
