<?php
	
require_once 'DbOperations.php';

	$response = array();

	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['username']) AND 
				isset($_POST['password']) AND 
					isset($_POST['first_name']) AND 
						isset($_POST['last_name']) AND 
							isset($_POST['location']) AND 
								isset($_POST['contact_no']) AND 
									isset($_POST['email_address']) AND 
										isset($_POST['user_type']))
			{

			$db = new DbOperations();

			$result = $db->createUser(	$_POST['username'],
										$_POST['password'],
										$_POST['first_name'],
										$_POST['last_name'],
										$_POST['location'],
										$_POST['contact_no'],
										$_POST['email_address'],
										$_POST['user_type']
									);
			if($result == 3){
				$response['error'] = false;
				$response['message'] = "User registered successfully";
			}elseif($result == 4){
				$response['error'] = true;
				$response['message'] = "Some error occurred please try again";
			}elseif($result == 1) {
				$response['error'] = true;
				$response['message'] = "Username is unavailable";
			}elseif($result == 2) {
				$response['error'] = true;
				$response['message'] = "Email address is unavailable";
			}
		}else{
			$response['error'] = true;
			$response['message'] = "Required fields are missing";
		}
	}else{
		$response['error'] = true;
		$response['message'] = "Invalid Request";
	}

	echo json_encode($response);