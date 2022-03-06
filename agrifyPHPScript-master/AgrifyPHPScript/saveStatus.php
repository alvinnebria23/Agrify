<?php
require_once 'Constants.php';
// Create connection
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$message = array();
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	if(isset($_POST['user_id'])){
		$userID = $_POST['user_id'];
		// prepare and bind
		$stmt = $conn->prepare("UPDATE `vendors` SET `status`= 'V' WHERE `user_id` = ?");

		$stmt->bind_param('s', $userID);

		if($stmt->execute()){
			$message['message'] = 'success';
		}else{
			$message['message'] = 'fail';
		}
		$stmt->close();
		$conn->close();
	}
}
echo json_encode($message);
?>