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
	if(isset($_POST['agricultural_sector']) AND isset($_POST['product_id'])){
		$agriculturalSector = $_POST['agricultural_sector'];
		$productID			= $_POST['product_id'];
		if($agriculturalSector == 'crops'){
			$query = "SELECT crop_variety_id, crop_variety_name FROM crop_varieties WHERE crop_id = '$productID'";
		}elseif($agriculturalSector == 'fisheries'){
			$query = "SELECT fish_type_variety_id, fish_type_variety_name FROM fish_type_varieties WHERE fish_type_id = '$productID'";
		}elseif($agriculturalSector == 'livestocks'){
			$query = "SELECT livestock_type_variety_id, livestock_type_variety_name FROM livestock_type_varieties WHERE livestock_type_id = '$productID'";
		}elseif($agriculturalSector == 'poultries'){
			$query = "SELECT poultry_type_variety_id, poultry_type_variety_name FROM poultry_type_varieties WHERE
				poultry_type_id = '$productID'";
		}
		if($stmt = $conn->prepare($query)){
			//executing the query 
			$stmt->execute();

			//binding results to the query 
			$stmt->bind_result($variety_id, $variety_name); 
		}else{
			var_dump($stmt1->error);
		}
		//traversing through all the result 
		$varieties = array();
		while($stmt->fetch()){
			$temp = array();
			$temp['variety_id']			= $variety_id;
			$temp['variety_name'] 		= $variety_name; 
			array_push($varieties, $temp);
		}
		$stmt->close();
	}
}
//displaying the result in json format 
echo json_encode($varieties);