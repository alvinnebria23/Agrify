<?php

	class DbOperations{

		private $con;

		function __construct(){

			require_once dirname(__FILE__).'/DbConnect.php';

			$db = new DbConnect();

			$this->con = $db->connect();
		}

		public function createUser($username, $passswordParameter, $firstname, $lastname, $location, $contactNo, $emailAddress, $userType){
			$stmt = $this->con->prepare("SELECT MAX(user_id) as user_id from users");
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_assoc();
			$firstname = strtolower($firstname);
			$lastname = strtolower($lastname);
			$userID = $row['user_id'];
			$userID++;
			$temp = '';
			$stmt->close();
			if($this->isUsernameExist($username)){
				return 1;
			}elseif($this->isEmailAddressExist($emailAddress)){
				return 2;
			}else{
				$password = md5($passswordParameter);
				$stmt	= $this->con->prepare("INSERT INTO users 
												VALUES (? ,? , ?, ?, ?, ?, ?, ?, ?, ?)");
				$stmt->bind_param("ssssssssss", $userID , $username, $password, $firstname, $lastname, $location, $contactNo, $emailAddress, $userType, $temp);

				if($stmt->execute()){
					$stmt->close();
					$this->insertDesignatedTypeOfUser($userID,$username,$userType);

					return 3;
				}else{
					return mysqli_stmt_error($stmt);
				}
			}
		}
		private function isUsernameExist($username){
			$stmt = $this->con->prepare("SELECT user_id 
											FROM users 
											WHERE username = ?");
			$stmt->bind_param("s", $username);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		private function isEmailAddressExist($emailAddress){
			$stmt = $this->con->prepare("SELECT user_id 
											FROM users 
											WHERE email_address = ?");
			$stmt->bind_param("s", $emailAddress);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		private function insertDesignatedTypeOfUser($userID, $username,$userType){			
			if($userType == 'farmer'){
				if($stmt = $this->con->prepare("INSERT INTO farmers 
												VALUES (NULL, ?)")){
					$stmt->bind_param("i", $userID);
					if($stmt->execute()){
						$stmt->close();
					}
				}else{
				   //error !! don't go further
				   var_dump($this->con->error);
				}
			}
			elseif ($userType == 'vendor') {
				if($stmt = $this->con->prepare("INSERT INTO vendors 
												VALUES (NULL, ?, NULL, 'UV')")){
					$stmt->bind_param("i", $userID);
					if($stmt->execute()){
						$stmt->close();
					}
				}else{
				   //error !! don't go further
				   var_dump($this->con->error);
				}
			}
			elseif ($userType == 'provider') {
				if($stmt = $this->con->prepare("INSERT INTO providers 
												VALUES (NULL, ?)")){

					$stmt->bind_param("i", $userID);
					if($stmt->execute()){
						$stmt->close();
					}
				}else{
				   //error !! don't go further
				   var_dump($this->con->error);
				}
			}
			if($stmt = $this->con->prepare("INSERT INTO user_profile 
											VALUES (?, ?, NULL)")){
				$noProfile = 'noprofile.png';
				$stmt->bind_param("is", $userID, $noProfile);
				if($stmt->execute()){
					$stmt->close();
				}
			}
		}

		public function userLogin($username, $password){
			$PASSWORD = substr(md5($password), 0, 20);
			$stmt = $this->con->prepare("SELECT user_id 
											FROM users
												WHERE username = ? AND password = ?");
			$stmt->bind_param("ss", $username, $PASSWORD);
			$stmt->execute();
			$stmt->store_result();
			return $stmt->num_rows > 0;
		}

		public function getUserByUsername($username){
			$stmt = $this->con->prepare("SELECT u.*, up.profile_pic FROM users u, user_profile up WHERE 				username = ? AND up.user_id = u.user_id");
			$stmt->bind_param("s", $username);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}

		public function getStatus($userID){
			$stmt = $this->con->prepare("SELECT v.status FROM vendors v WHERE v.user_id = ?");
			$stmt->bind_param("s", $userID);
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}
	}