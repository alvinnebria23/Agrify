<?php
	function sendNotification($tokens, $message){
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
				'registration_ids' => $tokens,
				'data' => $message
		);

		$headers = array(
			'Authorization:key = AAAAL1bptZc:APA91bEQXQKUJNEGd2ygW153n8fLJp20gslBwc3fautd7SlNCZNWRlBR3t3FJUGzs2rri-rYiJ7nC47s-KhMa7tc3TqryrUhd2b8lGPLLRMEeRoKklgrtwjwtVWjL3DcP2yQry3-kak1',
			'Content-Type: application/json'
		);

		$ch = curl_init ();
	    curl_setopt ( $ch, CURLOPT_URL, $url );
	    curl_setopt ( $ch, CURLOPT_POST, true );
	    curl_setopt ( $ch, CURLOPT_HTTPHEADER, $headers );
	    curl_setopt ( $ch, CURLOPT_RETURNTRANSFER, true );
	    curl_setopt ( $ch, CURLOPT_SSL_VERIFYHOST, 0 );
	    curl_setopt ( $ch, CURLOPT_SSL_VERIFYPEER, false );
	    curl_setopt ( $ch, CURLOPT_POSTFIELDS, json_encode($fields));
	    $result = curl_exec ( $ch );
	    if($result == FALSE){
	    	die('Curl failed: ' . curl_error($ch));
	    }
	    curl_close($ch);
	    return $result;
	}
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['user_id']) AND isset($_POST['message']) AND isset($_POST['my_user_id'])){
			$userID = $_POST['user_id'];
			$messageString = $_POST['message'];
			$myUserID = $_POST['my_user_id'];

			$conn = mysqli_connect('127.0.0.1:3306', 'root', '', 'agrify');
			$sql = "select up.profile_pic, concat(u.first_name, ' ', u.last_name) as fullname, u.token from users u, user_profile up where up.user_id = u.user_id AND u.user_id = '$myUserID'";
			$result = mysqli_query($conn,$sql);
			$tokens = array();
			if(mysqli_num_rows($result) > 0){
				while($row = mysqli_fetch_assoc($result)){
					$profilePic = $row['profile_pic']; 
					$fullname 	= $row['fullname'];
				}
			}
			mysqli_close($conn);

			$conn1 = mysqli_connect('127.0.0.1:3306', 'root', '', 'agrify');
			$sql1 = "SELECT token from users where user_id = '$userID'";
			$result1 = mysqli_query($conn1,$sql1);
			$tokens1 = array();
			if(mysqli_num_rows($result1) > 0){
				while($row1 = mysqli_fetch_assoc($result1)){
					$tokens[] = $row1['token'];
				}
			}
			mysqli_close($conn1);
			$message = array(	"user_id" => $myUserID,
								"profile_pic" => $profilePic,
								"fullName" => $fullname,
								"message" => "$messageString");
			$message_status = sendNotification($tokens, $message);
			echo $message_status;
		}
	}
	
?>