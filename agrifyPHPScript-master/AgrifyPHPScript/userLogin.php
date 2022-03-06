<?php
	
require_once 'DbOperations.php';

	$response = array();

	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['username']) and isset($_POST['password'])){
			$db = new DbOperations();

			if($db->userLogin($_POST['username'], $_POST['password'])){
				$user = $db->getUserByUsername($_POST['username']);
				if($user['profile_pic'] == null){
					$user['profile_pic'] = 'noprofile.png';
				}
				if($user['user_type'] == 'farmer' || $user['user_type'] == 'provider'){
					$response['status'] = 'UV';
				}elseif($user['user_type'] == 'vendor'){
					$vendor = $db->getStatus($user['user_id']);
					$response['status'] = $vendor['status'];
				}
				$response['error'] 			= false;
				$response['user_id']		= $user['user_id'];
				$response['username']		= $user['username'];
				$response['first_name']		= $user['first_name'];
				$response['last_name']		= $user['last_name'];
				$response['location']		= $user['location'];
				$response['email_address']	= $user['email_address'];
				$response['user_type']		= $user['user_type'];
				$response['profile_pic']	= $user['profile_pic'];

			}else{
				$response['error'] = true;
				$response['message'] = "Invalid username or password";
			}
		}else{
			$response['error'] = true;
			$response['message'] = "Required fields are missing";
		}
	}

echo json_encode($response);