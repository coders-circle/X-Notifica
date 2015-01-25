<?php

//include_once 'jpt.php';

class Navbar{
    protected $tabs;
    protected $tabActions;
    protected $activeTab;
    public function Navbar() {
        $this->tabs = array();
        $this->tabActions = array();
        $this->activeTab = 0;
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

        echo '<a class="navbar-brand" href="#">'.$GLOBALS['g_appName'].'</a>';
        echo '</div>';
        echo '<div id="navbar" class="navbar-collapse collapse">';
        echo '<ul class="nav navbar-nav">';
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
        echo '</ul>';
        echo '</div>';
        echo '</div>';
        echo '</nav>';
    }

}

?>
