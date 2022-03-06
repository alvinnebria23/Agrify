<?php 
require_once 'Constants.php';
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['receiver_id'])){
			$receiverID  	 = $_POST['receiver_id'];
		if($stmt = $conn->prepare("SELECT m.conversation_id, up.profile_pic, concat(u.first_name,' ',u.last_name) as fullname, u.user_id FROM messages m, user_profile up, users u WHERE m.receiver_id = '$receiverID' AND m.sender_id = up.user_id AND u.user_id = up.user_id GROUP by m.conversation_id")){
			$stmt->execute();
			$stmt->bind_result($conversation_id, $profile_pic, $fullname, $user_id); 
		}else{
			var_dump($stmt->error);
		}
		$userInfo = array();	
		while($stmt->fetch()){
			$temp = array();
			if($profile_pic == null){
				$profile_pic = 'noprofile.png';
			}
			$temp['conversation_id']	= $conversation_id;
			$temp['profile_pic']		= $profile_pic;
			$temp['fullname'] 			= $fullname; 
			$temp['user_id'] 			= $user_id;
			array_push($userInfo, $temp);
		}
	}
	$stmt->close();
}
echo json_encode($userInfo);