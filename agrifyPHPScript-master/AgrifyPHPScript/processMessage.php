<?php
require_once 'Constants.php';
// Create connection
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$response = array();
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	if(isset($_POST['action'])){

		$action		= $_POST['action'];				
		// prepare and bind
		if($action == 'saveMessage'){
			$messageID 		= " ";
			$conversationID = $_POST['conversation_id'];
			$receiverID		= $_POST['receiver_id'];
			$message 		= $_POST['message'];
			$senderID 		= $_POST['sender_id'];
			$date_time_sent = $_POST['date_time_sent'];
			$status			= 'unread';
			$temp_timestamp = null;
			$stmt = $conn->prepare("INSERT INTO messages (message_id, conversation_id, receiver_id, message, sender_id, date_time_sent, status, temp_timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, NULL)");
			$stmt->bind_param('sssssss', $messageID, $conversationID, $receiverID, $message, $senderID, $date_time_sent, $status);
			if($stmt->execute()){
				$response['error'] = false;
			}else{
				$response['error'] = true;
			}
			$stmt->close();
		}elseif ($action == 'retrieveMessages') {
			$conversationID = $_POST['conversation_id'];
			$query = "SELECT t1.conversation_id, t1.message_id, t2.message, t2.sender_id, t2.date_time_sent, t2.status FROM (SELECT MAX(m.temp_timestamp) as temp_timestamp , m.conversation_id, max(message_id) as message_id from messages m WHERE m.conversation_id = '$conversationID' ) as t1 LEFT JOIN (SELECT m.message_id, m.message, m.sender_id, m.date_time_sent, m.status FROM messages m) as t2 ON t1.message_id = t2.message_id";
				if($stmt = $conn->prepare($query)){

				//executing the query 
				$stmt->execute();

				//binding results to the query 
				$stmt->bind_result($conversation_id, $message_id, $message, $sender_id, $date_time_sent, $status); 
			}else{
				var_dump($stmt1->error);
			}
			//traversing through all the result 
			$inbox = array();
			while($stmt->fetch()){
				$temp = array();
				$temp['conversation_id']	= $conversation_id;
				$temp['message_id']			= $message_id;
				$temp['message'] 			= $message; 
				$temp['sender_id']			= $sender_id;
				$temp['date_time_sent']		= $date_time_sent; 
				$temp['status']				= $status; 
				array_push($inbox, $temp);
			}
			$stmt->close();
		}elseif ($action == 'populateThread') {
			$conversationID = $_POST['conversation_id'];
			$query = "SELECT t1.message_id, t2.message, t2.sender_id, t2.date_time_sent, t2.status, t1.temp_timestamp FROM (SELECT m.temp_timestamp as temp_timestamp , m.conversation_id,message_id as message_id from messages m WHERE m.conversation_id = '$conversationID' ) as t1 LEFT JOIN (SELECT m.message_id, m.message, m.sender_id, m.date_time_sent, m.status FROM messages m) as t2 ON t1.message_id = t2.message_id order by t1.temp_timestamp";
				if($stmt = $conn->prepare($query)){

				//executing the query 
				$stmt->execute();

				//binding results to the query 
				$stmt->bind_result($message_id, $message, $sender_id, $date_time_sent, $status, $temp_timestamp); 
			}else{
				var_dump($stmt1->error);
			}
			//traversing through all the result 
			$inbox = array();
			while($stmt->fetch()){
				$temp = array();
				$temp['message_id']			= $message_id;
				$temp['message'] 			= $message; 
				$temp['sender_id']			= $sender_id;
				$temp['date_time_sent']		= $date_time_sent; 
				$temp['status']				= $status; 
				$temp['temp_timestamp']		= $temp_timestamp;
				array_push($inbox, $temp);
			}
			$stmt->close();
		}		
		$conn->close();
		echo json_encode($inbox);
	}
}
?>