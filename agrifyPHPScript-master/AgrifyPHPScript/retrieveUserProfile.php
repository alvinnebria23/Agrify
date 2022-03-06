<?php 
require_once 'Constants.php';
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['user_id'])){
			$userID  = $_POST['user_id'];
		if($stmt = $conn->prepare("select up.user_id, up.profile_pic, CONCAT(u.first_name,' ',u.last_name) as fullname, up.profile_description, u.location, u.user_type, u.contact_no, u.email_address from users u, user_profile up where u.user_id = up.user_id and u.user_id = '$userID'")){
			$stmt->execute();
			$stmt->bind_result($user_id, $profile_pic, $fullname, $profile_description, $location, $user_type, $contact_no, $email_address); 
		}else{
			var_dump($stmt->error);
		}
		$userInfo = array();	
		while($stmt->fetch()){
			$temp = array();
			if($profile_pic == null){
				$profile_pic = 'noprofile.png';
			}
			if($profile_description == null){
				$profile_description = 'No profile description.';
			}
			$temp['user_id']				= $user_id;
			$temp['profile_pic']			= $profile_pic;
			$temp['fullname'] 				= $fullname; 
			$temp['profile_description'] 	= $profile_description;
			$temp['location']				= $location;
			$temp['user_type']				= $user_type;
			$temp['contact_no'] 			= $contact_no; 
			$temp['email_address']			= $email_address;
			array_push($userInfo, $temp);
		}
	}
	$stmt->close();
}
echo json_encode($userInfo);