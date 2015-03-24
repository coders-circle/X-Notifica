<?php

require_once 'Session.php';
require_once 'Database.php';


// @TODO: move these definitions to vars.php
define("HOST", "localhost");
define("USER", "root");
define("PASSWORD", "root");
define("DATABASE", "fabb-notifica");
define("SECURE", false);	// make true to enable https

class AccountLockedException extends Exception{
    public function __construct(){
        parent::__construct("This account is locked.");
    }
}

class PermissionDeniedException extends Exception{
    public function __construct(){
        parent::__construct("Not enough permission.");
    }
}

class User {
	protected $session;
    protected $db;
	protected $userid;
	protected $userType;
	protected $loggedIn;
	protected $username;
    protected $facultyid;
    protected $name;

    protected $year;
    protected $roll;
    protected $group;
    protected $privilaged;

    protected $id;

	public function __construct() {
		$this -> session = new Session;
        $this -> db = new Database(HOST, USER, PASSWORD, DATABASE);
		$this -> userType = 0;
		$this -> loggedIn = false;
        $this->facultyid = 0;
        $this->name = "";
	}

    public function GetDB(){
        return $this->db;
    }

	public function StartSession() {
		$this -> session -> Start();
	}

	public function DeleteUser($userid){

        //if($this -> db -> query("DELETE FROM users WHERE id = '$userid'") == FALSE){
            // failed to delete :/
        //}
	}

	public function Logout(){
		$_SESSION = array();
		$params = session_get_cookie_params();
		setcookie(session_name(), '', time() - 42000, $params["path"],  $params["domain"],  $params["secure"],  $params["httponly"]);
		session_destroy();
		$this->userid = 0;
		$this->userType = 0;
		$this->loggedIn = false;
	}

	public function GetUsername(){
		return $this->username;
	}

    public function LoginTest($username, $password){
        $mysqli = $this -> db;
        if ($stmt = $mysqli -> prepare("SELECT id, password, salt, usertype FROM users WHERE username = ? LIMIT 1")) {
            $stmt -> bind_param('s', $username);
            $stmt -> execute();
            $stmt -> store_result();
            $stmt -> bind_result($this->userid, $db_password, $salt, $this->userType);
            $stmt -> fetch();
            $password = hash('sha512', $password . $salt);

            if ($stmt->num_rows == 1 ) {
                if ($db_password == $password) {
                    //$this->userid = preg_replace("/[^0-9]+/", "", $this->userid);
                    //$username = preg_replace("/[^a-zA-Z0-9_\-]+/", "", $username);
                    $this->loggedIn = true;
                    $this->username = $username;
                    $stmt->free_result();
                    $stmt->close();
                    $stmt = null;

                    if($this->userType == 3){
                        $stmt = $this->db->prepare("SELECT id, name, faculty_id FROM central_authorities WHERE user_id = ? LIMIT 1");
                    }else if($this->userType == 2){
                        $stmt = $this->db->prepare("SELECT id, name, faculty_id FROM teachers WHERE user_id = ? LIMIT 1");
                    }else if($this->userType == 1){
                        $stmt = $this->db->prepare("SELECT id, name, faculty_id, roll, year, group_id, privilege_level FROM students WHERE user_id = ? LIMIT 1");
                    }

                    if($stmt != null){
                        $stmt->bind_param('i', $this->userid);
                        $stmt->execute();
                        $stmt->store_result();
                        if($this->userType == 1){
                            $stmt->bind_result($this->id, $this->name, $this->facultyid, $this->roll, $this->year, $this->group, $this->privilaged);
                        }else{
                            $stmt->bind_result($this->id, $this->name, $this->facultyid);
                        }

                        $stmt->fetch();
                        $stmt->free_result();
                        $stmt->close();
                    }
                } else {
                throw new Exception("Invalid Password");
                }
            } else {
                throw new Exception("Invalid User");
                //return false;
            }
        }
    }
    public function GetID(){
        return $this->id;
    }

	public function Login($username, $password) {
		$mysqli = $this -> db;
		if ($stmt = $mysqli -> prepare("SELECT id, password, salt, usertype FROM users WHERE username = ? LIMIT 1")) {
			$stmt -> bind_param('s', $username);
			$stmt -> execute();
            $stmt -> store_result();
			$stmt -> bind_result($this->userid, $db_password, $salt, $this->userType);
			$stmt -> fetch();
			$password = hash('sha512', $password . $salt);

			if ($stmt->num_rows == 1 ) {
				if ($this -> CheckBrute() == true) {
					throw new AccountLockedException;
				} else {
					if ($db_password == $password) {
						$user_browser = $_SERVER['HTTP_USER_AGENT'];
						$this->userid = preg_replace("/[^0-9]+/", "", $this->userid);
						$_SESSION['user_id'] = $this->userid;
						$username = preg_replace("/[^a-zA-Z0-9_\-]+/", "", $username);
						$_SESSION['username'] = $username;
						$_SESSION['login_string'] = hash('sha512', $password . $user_browser);
						$this->loggedIn = true;
						$this->username = $username;
                        $stmt->free_result();
                        $stmt->close();

                        $stmt = null;


                        if($this->userType == 3){
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM central_authorities WHERE id = ? LIMIT 1");
                        }else if($this->userType == 2){
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM teachers WHERE id = ? LIMIT 1");
                        }else{
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM students WHERE id = ? LIMIT 1");
                        }

                        if($stmt != null){
                            $stmt->bind_param('i', $this->userid);
                            $stmt->execute();
                            $stmt->store_result();
                            $stmt->bind_result($this->name, $this->facultyid);

                            $stmt->fetch();
                            $stmt->free_result();
                            $stmt->close();
                        }


						return true;
					} else {
						//$now = time();
						//$mysqli -> query("INSERT INTO login_attempts(user_id, time) VALUES ('$this->userid', '$now')");
                        //throw new Exception("Invalid Password")
						return false;
					}
				}
			} else {
                //throw new Exception("Invalid User")
				return false;
			}
		}
	}

	function GetUserType(){
		// 0 = guest, 1 = normal user, 2 = admin, 3 = central authority
		return $this->userType;
	}

	function GetUserId(){
		return $this->userid;
	}

	function CheckBrute() {
        return false;
		$now = time();
		$valid_attempts = $now - (2 * 60 * 60);
		if ($stmt = $this->db -> prepare("SELECT time FROM login_attempts WHERE user_id = ? AND time > '$valid_attempts'")) {
			$stmt -> bind_param('i', $this->userid);
			$stmt -> execute();
			$stmt -> store_result();
			if ($stmt -> num_rows > 5) {
				//return true;  // to enable account lock (protection against brute force)
				return false;   // currently disabled
			} else {
				return false;
			}
		}
	}
	function LoggedIn() {
		$this->loggedIn = false;
		$mysqli = $this -> db;
		if (isset($_SESSION['user_id'], $_SESSION['username'], $_SESSION['login_string'])) {
			$this->userid = $_SESSION['user_id'];
			$login_string = $_SESSION['login_string'];
			$username = $_SESSION['username'];
			$user_browser = $_SERVER['HTTP_USER_AGENT'];
			if ($stmt = $mysqli -> prepare("SELECT username, password, usertype FROM users WHERE id = ? LIMIT 1")) {
				$stmt -> bind_param('i', $this->userid);
				$stmt -> execute();
				$stmt -> store_result();
				if ($stmt -> num_rows == 1) {
					$stmt -> bind_result($this->username, $password, $this->userType);
					$stmt -> fetch();
					$login_check = hash('sha512', $password . $user_browser);
					if ($login_check == $login_string) {

                        $stmt->free_result();
                        $stmt->close();

                        $stmt = null;
                        if($this->userType == 3){
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM central_authorities WHERE id = ? LIMIT 1");
                        }else if($this->userType == 2){
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM teachers WHERE id = ? LIMIT 1");
                        }else{
                            $stmt = $this->db->prepare("SELECT name, faculty_id FROM students WHERE id = ? LIMIT 1");
                        }
                        if($stmt != null){
                            $stmt->bind_param('i', $this->userid);
                            $stmt->execute();
                            $stmt->store_result();
                            $stmt->bind_result($this->name, $this->facultyid);

                            $stmt->fetch();
                            $stmt->free_result();
                            $stmt->close();
                        }

						$this->loggedIn = true;
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
    function AddUserRaw($username, $password, $usertype)
    {
        $newpass = hash('sha512', $password);
        $this->AddUser($username, $newpass, $usertype);
    }
    function GetFacultyID(){
        return $this->facultyid;
    }
    function GetStudentRoll(){
        return $this->roll;
    }
    function GetStudentBatch(){
        return $this->year;
    }
    function GetStudentGroup(){
        return $this->group;
    }
    function GetStudentPrivilage(){
        return $this->privilaged;
    }

    function AddRoutineElement($routineid, $subjectid, $teacherid, $day, $starttime, $endtime){
        $mysqli = $this->db;
        if ($insert_stmt = $mysqli->prepare("INSERT INTO routine_elements (routine_id, subject_id, teacher_id, day, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)")) {
            $insert_stmt->bind_param('iiiiii', $routineid, $subjectid, $teacherid, $day, $starttime, $endtime);
            if (! $insert_stmt->execute()) {
                throw new Exception("Failed to add routine element :/");
            }
        }
    }
    
    function AddRoutine($facultyid, $year, $group, $starttime, $endtime, $routine_elements){
        $mysqli = $this->db;
        if ($insert_stmt = $mysqli->prepare("INSERT INTO routines (faculty_id, year, `group`, start_time, end_time) VALUES (?, ?, ?, ?, ?)")) {
            $insert_stmt->bind_param('iisii', $facultyid, $year, $group, $starttime, $endtime);
            if (!$insert_stmt->execute()) {
                throw new Exception("Failed to add routine :/");
            }
            $id = $mysqli->insert_id;
            foreach($routine_elements as $re){
                $this->AddRoutineElement($id, $re["subject_id"], $re["teacher_id"], $re["day"], $re["starttime"], $re["endtime"]);
            }
        }
    }




    function AddStudent($studentname, $roll, $batch){
        if($this->LoggedIn() == false && $this->GetUserType() != 3){
            throw new PermissionDeniedException;
        }

        $faculty_id = $this->facultyid;
        $group = "A";

        $mysqli = $this->db;

        $yearstr = strval($batch);
        $yearstr = substr($yearstr, -3);
        $stmt = $this->db->prepare("SELECT code FROM faculties WHERE id = ? LIMIT 1");
        if($stmt == null){throw new Exception($this->db->error);}
        $stmt->bind_param('i', $faculty_id);
        $stmt->execute();
        $stmt->store_result();
        $dep_code = null;
        if ($stmt->num_rows == 1) {
            $stmt -> bind_result($dep_code);
            $stmt -> fetch();
        }else {throw new Exception("Unable to fetch Department Code");}

        $username = $yearstr.$dep_code.strval($roll);
        $this->AddUserRaw($username, $username, 1);    // 1 = student
        $user_id = $this->db->insert_id;

        if ($insert_stmt = $mysqli->prepare("INSERT INTO students (user_id, name, roll, faculty_id, year, group_id) VALUES (?, ?, ?, ?, ?, ?)")) {
            $insert_stmt->bind_param('isiiis', $user_id, $studentname, $roll, $faculty_id, $batch, $group);
            if (! $insert_stmt->execute()) {
                // TODO: may be also delete created user  @@ So so lazy -_-
                throw new Exception('Failed to add the Student');

            }
        }
    }
    function AddTeacher($teachername, $teacherun, $contact){
        $this->AddUserRaw($teacherun, $teacherun, 2);    // 2 = teacher
        $user_id = $this->db->insert_id;
        $mysqli = $this->db;
        $faculty_id = $this->facultyid;
        if ($insert_stmt = $mysqli->prepare("INSERT INTO teachers (user_id, name, faculty_id, contact_number) VALUES (?, ?, ?, ?)")) {
            $insert_stmt->bind_param('isis', $user_id, $teachername, $faculty_id, $contact);
            if (! $insert_stmt->execute()) {
                //throw new Exception('Failed to add the teacher');
            }
        }
    }
    function AddCourse($subjectname, $subjectcode, $facultyid){
        if($insert_stmt = $this->db->prepare("INSERT INTO subjects (code, name, faculty_id) VALUES (?, ?, ?)")){
            $insert_stmt->bind_param('ssi', $subjectcode, $subjectname, $facultyid);
            if(!$insert_stmt->execute()){
                throw new Exception('Failed to add the course');
            }
        }
    }
    function GetName(){
        $piece = explode(" ", $this->name);
        return $piece[0];
    }
    function GetFullName(){
        return $this->name;
    }
    function GetStudents(){
        if ($stmt = $this -> db -> prepare("SELECT * FROM students WHERE roll > 0")) {
            $stmt -> execute();
            return $stmt->get_result();
        }
    }
    function GetFaculties(){
        if ($stmt = $this -> db -> prepare("SELECT * FROM faculties")) {
            $stmt -> execute();
            return $stmt->get_result();
        }
    }
    function GetTeachers(){
        if ($stmt = $this -> db -> prepare("SELECT * FROM teachers")) {
            $stmt -> execute();
            return $stmt->get_result();
        }
    }
    function GetCourses(){
        if ($stmt = $this -> db -> prepare("SELECT * FROM subjects")) {
            $stmt -> execute();
            return $stmt->get_result();
        }
    }

	function AddUser($username, $password, $usertype)
	{
        if($this->LoggedIn() == false && $this->GetUserType() != 3)
        {
            throw new PermissionDeniedException;
        }

		$mysqli = $this->db;
		if (strlen($password) != 128) {
			 throw new Exception('Invalid password configuration');
		}
		$stmt = $mysqli->prepare("SELECT * FROM users WHERE username = ? LIMIT 1");
		if ($stmt) {
			$stmt->bind_param('s', $username);
			$stmt->execute();
			$stmt->store_result();
			if ($stmt->num_rows == 1) {
				$stmt->close();
				throw new Exception('A user with this Username already exists.');
			}
			$stmt->close();
		} else {
			$stmt->close();
			throw new Exception("database error");
		}
		$random_salt = hash('sha512', uniqid(mt_rand(1, mt_getrandmax()), true));
		$password = hash('sha512', $password . $random_salt);
		if ($insert_stmt = $mysqli->prepare("INSERT INTO users (username, password, salt, usertype) VALUES (?, ?, ?, ?)")) {
			$insert_stmt->bind_param('sssi', $username, $password, $random_salt, $usertype);
			if (! $insert_stmt->execute()) {
				throw new Exception('Failed to execute the query');
			}
		}else{throw new Exception("Failed to prepare user insert statement");}
	}
}
?>
