<?php

require_once('User.php');

class Student extends User{
    private $id;
    private $name;
    private $roll;
    private $faculty_id;
    private $year;
    private $group_number;
    private $section;
    private $is_privilaged;

    public function __construct(){
        parent::__construct();
        $this->is_privilaged = false;
    }
}

?>
