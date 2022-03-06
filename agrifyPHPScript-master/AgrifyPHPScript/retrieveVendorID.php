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
	if(isset($_POST['user_id'])){
		$userID = $_POST['user_id'];
		if($stmt = $conn->prepare("SELECT vendor_id FROM vendors WHERE user_id = '$userID'")){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($vendor_id); 
		}else{
			var_dump($stmt1->error);
		}
		$vendor = array();
		while($stmt->fetch()){
			$temp = array();
			$temp['vendor_id']			= $vendor_id;
			array_push($vendor, $temp);
		}
		$stmt->close();
	}
}
//displaying the result in json format 
echo json_encode($vendor);