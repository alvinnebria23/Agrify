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
	if(isset($_POST['agricultural_sector'])){
		$agriculturalSector = $_POST['agricultural_sector'];
		
		if($agriculturalSector == 'crops'){
			$query = "SELECT crop_id, crop_name FROM crops";
		}elseif($agriculturalSector == 'fisheries'){
			$query = "SELECT fish_type_id, fish_type_name FROM fisheries";
		}elseif($agriculturalSector == 'livestocks'){
			$query = "SELECT livestock_type_id, livestock_type_name FROM livestocks";
		}elseif($agriculturalSector == 'poultries'){
			$query = "SELECT poultry_type_id, poultry_type_name FROM poultries";
		}
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($product_id, $product_name); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$products = array();
		while($stmt->fetch()){
			$temp = array();
			$temp['product_id']			= $product_id;
			$temp['product_name'] 		= $product_name; 
			array_push($products, $temp);
		}
		$stmt->close();
	}
}
//displaying the result in json format 
echo json_encode($products);