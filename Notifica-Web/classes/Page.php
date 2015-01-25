<?php

include_once 'Navbar.php';

class Page extends Navbar{
    // required user level to access the page
    private $requiredAccessLevel;
    private $styleSheetURIs;
    private $jsURIs;
    private $viewURI;
    private $controllerURI;
    private $navbarEnabled;
    private $viewSet;
    private $controllerSet;
    public function Page(){
        // 0 = general public, anyone can visit the page
        $this->styleSheetURIs = array("css/bootstrap.min.css");
        $this->jsURIs = array("js/jquery.min.js", "js/bootstrap.min.js");
        $this->bodyURI = "";
        $this->requiredAccessLevel = 0;
        $this->navbarEnabled = true;
        $this->viewSet = false;
        $this->controllerSet = false;
    }
    public function SetRequiredAccessLevel($accessLevel){
        $this->requiredAccessLevel = $accessLevel;
    }
    public function GetRequiredAccessLevel(){
        return $this->requiredAccessLevel;
    }
    public function SetView($viewURI){
        $this->viewURI = $viewURI;
        $this->viewSet = true;
    }
    public function SetController($controllerURI){
        $this->controllerURI = $controllerURI;
        $this->controllerSet = true;
    }
    public function DisableNavbar(){
        $this->navbarEnabled = false;
    }

    public function GeneratePage(){
        $pageTitle = "test";
        echo '<!DOCTYPE HTML><html>';
        echo '<head>';
        echo '<title>' . $pageTitle . '</title>';
        echo '<meta charset="utf-8">';
        echo '<meta http-equiv="X-UA-Compatible" content="IE=edge">';
        echo '<meta name="viewport" content="width=device-width, initial-scale=1">';
        foreach($this->styleSheetURIs as $styleSheetURI){
            echo '<link rel="stylesheet" type="text/css" href="' . $styleSheetURI . '" />';
        }
        echo '</head>';
        echo '<body>';
        if($this->controllerSet) include $this->controllerURI;
        if($this->navbarEnabled) $this->GenerateNavbar();
        if($this->viewSet) include $this->viewURI;
        foreach($this->jsURIs as $jsURI){
            echo '<script language="javascript" type="text/javascript" src="' . $jsURI . '"></script>';
        }
        echo '</body>';
        echo '</html>';
    }



    public function AddStyleSheet($styleSheetURI){
        $this->styleSheetURIs[count($this->styleSheetURIs)] = $styleSheetURI;
    }
    public function AddJScript($jsURI){
        $this->jsURIs[count($this->jsURIs)] = $jsURI;
    }
}

?>
