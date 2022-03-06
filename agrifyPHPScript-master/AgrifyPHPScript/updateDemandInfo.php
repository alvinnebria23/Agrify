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
	if(isset($_POST['demand_id']) AND isset($_POST['agricultural_sector']) AND isset($_POST['product_id']) AND isset($_POST['variety_id']) AND isset($_POST['price']) AND isset($_POST['needed_kilograms']) AND isset($_POST['duration_end']) AND isset($_POST['description'])){
		$demandID 			= $_POST['demand_id'];
		$agriculturalSector = $_POST['agricultural_sector'];
		$productID 			= $_POST['product_id'];
		$varietyID 			= $_POST['variety_id'];
		$price 				= $_POST['price'];
		$neededKilograms 	= $_POST['needed_kilograms'];
		$durationEnd 		= $_POST['duration_end'];
		$description 		= $_POST['description'];
		// prepare and bind
		$stmt = $conn->prepare("UPDATE `demands` SET `agricultural_sector`= ?,`product_id` = ?,`variety_id`= ?, `price`= ?,`needed_kilograms`= ?, `duration_end`= ?, `description`= ? WHERE `demand_id` = ?");

		$stmt->bind_param('ssssssss', $agriculturalSector, $productID, $varietyID, $price, $neededKilograms, $durationEnd, $description, $demandID);
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