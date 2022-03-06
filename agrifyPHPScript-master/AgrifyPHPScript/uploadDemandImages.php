<?php
require_once 'Constants.php';
$server_ip = gethostbyname(gethostname());
$upload_url = 'http://'.$server_ip.'/';
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
	if(isset($_POST['demand_id']) AND isset($_POST['image_1']) AND isset($_POST['image_2']) 
		AND isset($_POST['image_3']) AND isset($_POST['image_4']) AND isset($_POST['action'])){

		$demandID 	= $_POST['demand_id'];
		$image1 	= $_POST['image_1'];
		$image2 	= $_POST['image_2'];
		$image3 	= $_POST['image_3'];
		$image4 	= $_POST['image_4'];
		$action		= $_POST['action'];

		$imageName1	=	uniqid().".jpg";
		$imageName2	=	uniqid().".jpg";
		$imageName3	=	uniqid().".jpg";
		$imageName4	=	uniqid().".jpg";

		$path1 = "D:\\Program Files\\xampp\\htdocs\\DemandImages\\".$imageName1;
		$path2 = "D:\\Program Files\\xampp\\htdocs\\DemandImages\\".$imageName2;
		$path3 = "D:\\Program Files\\xampp\\htdocs\\DemandImages\\".$imageName3;
		$path4 = "D:\\Program Files\\xampp\\htdocs\\DemandImages\\".$imageName4;
		
		if($action == 'update'){
			$sql = "UPDATE `demand_images` SET `image_1` = '$imageName1', `image_2` = '$imageName2', `image_3` = '$imageName3', `image_4` = '$imageName4' WHERE demand_id = '$demandID'";
		}elseif ($action == 'addDemand') {
			$sql = "INSERT INTO demand_images (demand_id, image_1, image_2, image_3, image_4) VALUES ( '$demandID', '$imageName1', '$imageName2', '$imageName3', '$imageName4')";
		}			
		if(mysqli_query($con,$sql)){
			file_put_contents($path1,base64_decode($image1));
			file_put_contents($path2,base64_decode($image2));
			file_put_contents($path3,base64_decode($image3));
			file_put_contents($path4,base64_decode($image4));
		}		
		mysqli_close($con);
	}
}else{
	echo "Error";
}