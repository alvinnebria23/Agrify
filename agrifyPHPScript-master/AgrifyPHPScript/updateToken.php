<?php 
require_once 'Constants.php';
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	if(isset($_POST['user_id']) AND isset($_POST['token'])){
		$userID  = $_POST['user_id'];
		$token	 = $_POST['token'];

		$stmt = $conn->prepare("UPDATE `users` SET token = ? WHERE user_id = ?");

		$stmt->bind_param("ss", $token, $userID);

		if($stmt->execute()){
			$message['message'] = 'success';
		}else{
			$message['message'] = 'fail';
		}
		$stmt->close();
		$conn->close();
	}	
}
?>