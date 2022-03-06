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

			$query = "select v.permit_number from vendors v where user_id = '$userID'";	
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($pn); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result		
		while($stmt->fetch()){
			$response['permit_number']			= $pn;
		}
	}
	$stmt->close();
}

//displaying the result in json format 
echo json_encode($response);