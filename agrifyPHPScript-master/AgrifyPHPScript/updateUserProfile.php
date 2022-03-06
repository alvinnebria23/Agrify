<?php
require_once 'Constants.php';
$server_ip = gethostbyname(gethostname());
$upload_url = 'http://'.$server_ip.'/';
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
	if(isset($_POST['user_id']) AND isset($_POST['profile_image']) AND isset($_POST['description']) AND 
		isset($_POST['action']) AND isset($_POST['firstName']) AND isset($_POST['lastName']) AND isset($_POST['contactNumber']) AND isset($_POST['emailAddress'])){

		$userID 		= $_POST['user_id'];
		$profileImage 	= $_POST['profile_image'];
		$description 	= $_POST['description'];
		$action			= $_POST['action'];
		$firstName		= $_POST['firstName'];
		$lastName		= $_POST['lastName'];
		$contactNumber	= $_POST['contactNumber'];
		$emailAddress	= $_POST['emailAddress'];	
		$imageName1		= uniqid().".jpg";
		$path1 = "D:\\Program Files\\xampp\\htdocs\\profileImages\\".$imageName1;

		$sql2 = "UPDATE `users` SET `first_name` = '$firstName', `last_name` = '$lastName', `contact_no` = '$contactNumber', `email_address` = '$emailAddress' WHERE `user_id` = '$userID'";
			
		if($action == 'newprofilepicture'){
			$sql = "UPDATE `user_profile` SET `profile_pic`= '$imageName1', `profile_description` = '$description' WHERE user_id = '$userID'";
		}elseif($action == 'nonewprofilepicture'){
			$sql = "UPDATE `user_profile` SET `profile_description` = '$description' WHERE user_id = '$userID'";
			
		}
		
		if(mysqli_query($con,$sql)){
			if(mysqli_query($con,$sql2)){
				if($action == 'newprofilepicture'){
				file_put_contents($path1,base64_decode($profileImage));
				}
			}			
		}else{
			echo "error query";
		}		
		mysqli_close($con);
	}
}else{
	echo "Error";
}