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
	if(isset($_POST['vendor_id'])){
		$vendorID = $_POST['vendor_id'];
		if($stmt = $conn->prepare("SELECT MAX(demand_id) FROM demands WHERE vendor_id = '$vendorID'")){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($demand_id); 
		}else{
			var_dump($stmt1->error);
		}
		$demand = array();
		while($stmt->fetch()){
			$temp = array();
			$temp['demand_id']			= $demand_id;
			array_push($demand, $temp);
		}
		$stmt->close();
	}
}
//displaying the result in json format 
echo json_encode($demand);