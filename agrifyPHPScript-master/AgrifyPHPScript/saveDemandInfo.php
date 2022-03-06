<?php
require_once 'Constants.php';
// Create connection
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
date_default_timezone_set('Etc/GMT+7');
$dateDemanded		=	date('Ymd',strtotime("0 days"));
$response = array();
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	if(isset($_POST['agricultural_sector']) AND isset($_POST['product_id']) AND isset($_POST['variety_id']) AND isset($_POST['vendor_id']) AND isset($_POST['price']) AND isset($_POST['needed_kilograms']) AND 
		isset($_POST['duration_end']) AND isset($_POST['description'])){
		$demandID 			= null;
		$agriculturalSector = $_POST['agricultural_sector'];
		$productID 			= $_POST['product_id'];
		$varietyID 			= $_POST['variety_id'];
		$vendorID 			= $_POST['vendor_id'];
		$price 				= $_POST['price'];
		$neededKilograms 	= $_POST['needed_kilograms'];
		$receivedKilograms 	= 0;
		$durationEnd 		= $_POST['duration_end'];
		$description 		= $_POST['description'];
		$status 			= 'INC';
		// prepare and bind
		$stmt = $conn->prepare("INSERT INTO demands (demand_id, agricultural_sector, product_id, variety_id, vendor_id, price, needed_kilograms, received_kilograms, date_demanded, duration_end, description, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		$stmt->bind_param('ssssssssssss', $demandID, $agriculturalSector, $productID, $varietyID, $vendorID, $price, $neededKilograms, $receivedKilograms, $dateDemanded, $durationEnd, $description, $status);
		if($stmt->execute()){
			$response['error'] = false;
		}else{
			$response['error'] = true;
		}
		$stmt->close();
		$conn->close();
	}
}
echo json_encode($response);
?>