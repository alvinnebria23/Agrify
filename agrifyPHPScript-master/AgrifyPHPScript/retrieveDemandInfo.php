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
		if(isset($_POST['demand_id'])){
			$demandID  = $_POST['demand_id'];
		
		$query = "select di.image_1, di.image_2, di.image_3, di.image_4, d.demand_id, d.agricultural_sector, d.product_id, d.variety_id, d.vendor_id, d.price, d.needed_kilograms, d.received_kilograms,d.date_demanded, d.duration_end, d.description FROM demand_images di, demands d WHERE d.demand_id = di.demand_id AND d.demand_id = '$demandID';";
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($image_1, $image_2, $image_3, $image_4, $demand_id, $agricultural_sector, $product_id, $variety_id, $vendor_id, $price, $needed_kilograms, $received_kilograms, $date_demanded, $duration_end, $description); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$demand = array();
		while($stmt->fetch()){
			$temp['image_1']			= $image_1;
			$temp['image_2'] 			= $image_2; 
			$temp['image_3'] 			= $image_3; 
			$temp['image_4'] 			= $image_4; 
			$temp['demand_id']			= $demand_id; 
			$temp['agricultural_sector']= $agricultural_sector;
			$temp['product_id'] 		= $product_id; 
			$temp['variety_id']			= $variety_id; 
			$temp['vendor_id']			= $vendor_id; 
			$temp['price']				= $price; 
			$temp['needed_kilograms']	= $needed_kilograms;
			$temp['received_kilograms']	= $received_kilograms; 
			$temp['date_demanded'] 		= $date_demanded; 
			$temp['duration_end'] 		= $duration_end; 
			$temp['description']		= $description; 
			array_push($demand, $temp);
		}
	}
	$stmt->close();
}

//displaying the result in json format 
echo json_encode($demand);