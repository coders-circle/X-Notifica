<?php

require_once 'User.php';

class Navbar{
    protected $tabs;
    protected $tabActions;
    protected $activeTab;
    protected $user;
    public function Navbar() {
        $this->tabs = array();
        $this->tabActions = array();
        $this->activeTab = 0;
        $this->user = null;
    }
    public function AddTab($tabName, $actionOnClick){
        $this->tabs[count($this->tabs)] = $tabName;
        $this->tabActions[count($this->tabActions)] = $actionOnClick;
    }
    public function SetActiveTab($tabID){
        $this->activeTab = $tabID;
    }
    protected function GenerateNavbar(){
        echo '<nav class="navbar navbar-default navbar-fixed-top">';
        echo '<div class="container">';
        echo '<div class="navbar-header">';

        echo '<a class="navbar-brand" href="#"><strong>'.$GLOBALS['g_appName'].'</strong></a>';
        echo '</div>';
        echo '<div id="navbar" class="navbar-collapse collapse">';
        echo '<ul class="nav navbar-nav navbar-right">';
        $currentTab = 0;
        if(count($this->tabs) > 0){
            foreach($this->tabs as $tab){
                if($this->activeTab == $currentTab){
                    echo '<li class="active"><a href="'.$this->tabActions[$currentTab].'">'.$tab.'</a></li>';
                }else{
                    echo '<li><a href="'.$this->tabActions[$currentTab].'">'.$tab.'</a></li>';
                }
                ++$currentTab;
            }
        }
        if(isset($this->user)){
            //$firstname = strstr($this->user->GetUsername(), ' ', true);
            $firstname = $this->user->GetUsername();
            echo '<li class="dropdown">';
            echo '<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">';
            echo $firstname.'<span class="caret"></span></a>';
            echo '<ul class="dropdown-menu" role="menu">';
            echo '<li><a href="logout.php">Logout</a></li>';
            echo '</ul> </li>';
        }

        echo '</ul>';
        echo '</div>';
        echo '</div>';
        echo '</nav>';
    }
    public function SetUser($_user)
    {
        $this->user = $_user;
    }

}


?>
