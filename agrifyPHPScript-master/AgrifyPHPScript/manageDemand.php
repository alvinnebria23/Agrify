<?php
require_once 'Constants.php';
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die('Unable to Connect');
	if(isset($_POST['demand_id']) AND isset($_POST['action'])){
		$demandID 		= $_POST['demand_id'];
		$action			= $_POST['action'];	
		$response 		= null;
		if($action == 'markasdone'){
			$sql = "UPDATE `demands` SET `status`= 'COM' WHERE demand_id = '$demandID'";
		}else if($action == 'markasdelete'){
			$sql = "UPDATE `demands` SET `status`= 'DEL' WHERE demand_id = '$demandID'";
		}else if($action == 'markasremove'){
			$sql = "DELETE FROM demands WHERE demand_id = '$demandID'";
		}else if($action == 'savereceivedkilograms'){
			$receivedKilograms = $_POST['received_kilograms'];
			$sql = "UPDATE `demands` SET `received_kilograms` = '$receivedKilograms' WHERE demand_id = '$demandID'";
		}		
		if(mysqli_query($con,$sql)){
			$response = 'success';
		}else{
			echo "error query";
		}		
		mysqli_close($con);
	}
}else{
	echo "Error";
}