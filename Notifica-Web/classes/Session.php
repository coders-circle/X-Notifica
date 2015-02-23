<?php

class Session {
	private $m_sessionName;
	private $m_secure;
	private $m_httpOnly;
	public function __construct() {
		$this->m_sessionName = 'sec_session';
		$this->m_secure = false;
		$this->m_httpOnly = true;
		$this->Start();
	}
	public function Start() {
		if (ini_set('session.use_only_cookies', 1) === false) {
			throw Exception("can't initiate a safe session");
		}
		$cookieParams = session_get_cookie_params();
		session_set_cookie_params($cookieParams["lifetime"], $cookieParams["path"], $cookieParams["domain"], $this->m_secure, $this->m_httpOnly);
		session_name($this->m_sessionName);
		session_start();
		session_regenerate_id();
	}
}
?>
