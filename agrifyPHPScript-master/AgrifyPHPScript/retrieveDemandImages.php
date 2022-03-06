<?php 
require_once 'Constants.php';
$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
if (mysqli_connect_errno()) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
	die();
}

if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
		if(isset($_POST['demand_id'])){
			$demandID  = $_POST['demand_id'];
		if($stmt = $conn->prepare("select image_1, image_2, image_3, image_4 FROM demand_images WHERE demand_id = '$demandID'")){
			$stmt->execute();
			$stmt->bind_result($image_1, $image_2, $image_3, $image_4); 
		}else{
			var_dump($stmt->error);
		}
		$images = array();
		while($stmt->fetch()){
			$temp = array();
			$temp['image_1']	= $image_1;
			$temp['image_2']	= $image_2;
			$temp['image_3'] 	= $image_3; 
			$temp['image_4'] 	= $image_4;
			array_push($images, $temp);
		}
	}
	$stmt->close();
}
echo json_encode($images);