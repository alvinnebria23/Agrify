<?php 

require_once 'Constants.php';

//connecting to database and getting the connection object
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

//Checking if any error occured while connecting
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['user_id'])){
			$userID  = $_POST['user_id'];
		
		$query = "select up.profile_pic, n.notification_id, n.notification_receiver_id, n.title, n.date, n.notification_sender_id from user_profile up, notifications n WHERE n.notification_sender_id = up.user_id AND n.notification_receiver_id = '$userID';";
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($profile_pic, $notification_id, $notification_receiver_id, $title, $date, $notification_sender_id); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$notifications = array();
		while($stmt->fetch()){
			$temp['profile_pic']				= $profile_pic;
			$temp['notification_id'] 			= $notification_id; 
			$temp['notification_receiver_id']	= $notification_receiver_id; 
			$temp['title'] 						= $title; 
			$temp['date']						= $date; 
			$temp['notification_sender_id']		= $notification_sender_id;
			array_push($notifications, $temp);
		}
	}
	$stmt->close();
}

//displaying the result in json format 
echo json_encode($notifications);